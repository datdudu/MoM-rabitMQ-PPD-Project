package com.MoMPPD.demo.user;
import com.MoMPPD.demo.broker.BrokerManager;
import com.MoMPPD.demo.models.Message;
import com.MoMPPD.demo.models.User;
import com.MoMPPD.demo.utils.RabbitMQConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.List;

@Component
public class UserGUI {

    @Autowired
    private BrokerManager brokerManager;

    @Autowired
    private RabbitMQConnection rabbitMQConnection;

    private User currentUser;
    private JFrame frame;
    private JTextArea messageArea;
    private DefaultListModel<String> topicListModel;
    private JList<String> topicList;

    @PostConstruct
    public void init() {
        String appType = System.getProperty("app.type", "client");
        if ("client".equals(appType)) {
            SwingUtilities.invokeLater(this::createAndShowGUI);
        }
    }

    private void createAndShowGUI() {
        frame = new JFrame("Cliente MOM PPD");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);

        JPanel loginPanel = new JPanel();
        JTextField userField = new JTextField(15);
        JButton loginBtn = new JButton("Entrar");
        loginPanel.add(new JLabel("Usuário:"));
        loginPanel.add(userField);
        loginPanel.add(loginBtn);

        frame.add(loginPanel, BorderLayout.NORTH);

        // Painel principal (aparece após login)
        JPanel mainPanel = new JPanel(new BorderLayout());
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        mainPanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        // Painel de tópicos
        JPanel topicPanel = new JPanel(new BorderLayout());
        topicListModel = new DefaultListModel<>();
        topicList = new JList<>(topicListModel);
        topicPanel.add(new JLabel("Tópicos disponíveis:"), BorderLayout.NORTH);
        topicPanel.add(new JScrollPane(topicList), BorderLayout.CENTER);

        JButton subscribeBtn = new JButton("Assinar");
        JButton unsubscribeBtn = new JButton("Desassinar");
        JButton refreshTopicsBtn = new JButton("Atualizar Tópicos");

        JPanel topicBtnPanel = new JPanel();
        topicBtnPanel.add(subscribeBtn);
        topicBtnPanel.add(unsubscribeBtn);
        topicPanel.add(topicBtnPanel, BorderLayout.SOUTH);
        topicBtnPanel.add(refreshTopicsBtn);

        mainPanel.add(topicPanel, BorderLayout.WEST);

        // Painel de envio de mensagens
        JPanel sendPanel = new JPanel(new GridLayout(3, 1));

        // Mensagem direta
        JPanel directPanel = new JPanel();
        JTextField toUserField = new JTextField(10);
        JTextField directMsgField = new JTextField(20);
        JButton sendDirectBtn = new JButton("Enviar Direto");
        directPanel.add(new JLabel("Para:"));
        directPanel.add(toUserField);
        directPanel.add(directMsgField);
        directPanel.add(sendDirectBtn);

        // Mensagem para tópico
        JPanel topicMsgPanel = new JPanel();
        JTextField topicMsgField = new JTextField(20);
        JButton sendTopicBtn = new JButton("Enviar para Tópico Selecionado");
        topicMsgPanel.add(topicMsgField);
        topicMsgPanel.add(sendTopicBtn);

        // Mensagem para fila genérica
        JPanel genericQueuePanel = new JPanel();
        JTextField genericQueueField = new JTextField(10);
        JTextField genericQueueMsgField = new JTextField(20);
        JButton sendGenericQueueBtn = new JButton("Enviar para Fila Genérica");
        genericQueuePanel.add(new JLabel("Fila:"));
        genericQueuePanel.add(genericQueueField);
        genericQueuePanel.add(genericQueueMsgField);
        genericQueuePanel.add(sendGenericQueueBtn);

        sendPanel.add(genericQueuePanel);
        sendPanel.add(directPanel);
        sendPanel.add(topicMsgPanel);

        mainPanel.add(sendPanel, BorderLayout.SOUTH);

        // Ações de login
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            if (!username.isEmpty()) {
                String queueName = "user_" + username;
                if (!rabbitMQConnection.userQueueExists(queueName)) {
                    JOptionPane.showMessageDialog(frame, "Usuário não existe! Peça ao administrador para criar.");
                    return;
                }

                currentUser = new User(username); // ou recupere do Broker se quiser mais dados
                frame.remove(loginPanel);
                frame.add(mainPanel, BorderLayout.CENTER);
                frame.setTitle("Cliente MOM PPD - Usuário: " + username);
                frame.revalidate();
                frame.repaint();
                frame.setTitle("Cliente MOM PPD - Usuário: " + username);
                updateTopicList();
                startMessageListener();
            }
        });

        // Assinar tópico
        subscribeBtn.addActionListener(e -> {
            String selectedTopic = topicList.getSelectedValue();
            if (selectedTopic != null && currentUser != null) {
                if (currentUser.subscribeToTopic(selectedTopic)) {
                    rabbitMQConnection.bindQueueToTopic(currentUser.getPersonalQueueName(), selectedTopic, "#");
                    appendMessage("Assinado ao tópico: " + selectedTopic);
                } else {
                    appendMessage("Já está assinado ao tópico: " + selectedTopic);
                }
            }
        });

        // Desassinar tópico
        unsubscribeBtn.addActionListener(e -> {
            String selectedTopic = topicList.getSelectedValue();
            if (selectedTopic != null && currentUser != null) {
                if (currentUser.unsubscribeFromTopic(selectedTopic)) {
                    rabbitMQConnection.unbindQueueFromTopic(currentUser.getPersonalQueueName(), selectedTopic, "#");
                    appendMessage("Desassinado do tópico: " + selectedTopic);
                } else {
                    appendMessage("Não estava assinado ao tópico: " + selectedTopic);
                }
            }
        });

        //Atualizar tópico
        refreshTopicsBtn.addActionListener(e -> updateTopicList());

        // Enviar mensagem direta
        sendDirectBtn.addActionListener(e -> {
            String toUser = toUserField.getText().trim();
            String msg = directMsgField.getText().trim();
            if (!toUser.isEmpty() && !msg.isEmpty() && currentUser != null) {
                String destQueue = "user_" + toUser;
                if (rabbitMQConnection.userQueueExists(destQueue)) {
                    Message message = new Message(currentUser.getUsername(), toUser, msg);
                    rabbitMQConnection.sendMessageToQueue(destQueue, message.toJson());
                    appendMessage("Mensagem enviada para " + toUser + ": " + msg);
                } else {
                    appendMessage("Usuário de destino não existe.");
                }
            }
        });

        // Enviar mensagem para tópico
        sendTopicBtn.addActionListener(e -> {
            String selectedTopic = topicList.getSelectedValue();
            String msg = topicMsgField.getText().trim();
            if (selectedTopic != null && !msg.isEmpty() && currentUser != null) {
                Message message = new Message(currentUser.getUsername(), selectedTopic, msg, Message.MessageType.TOPIC);
                rabbitMQConnection.sendMessageToTopic(selectedTopic, "#", message.toJson());
                appendMessage("Mensagem enviada para tópico " + selectedTopic + ": " + msg);
            }
        });

        sendGenericQueueBtn.addActionListener(e -> {
            String queueName = genericQueueField.getText().trim();
            String msg = genericQueueMsgField.getText().trim();
            if (!queueName.isEmpty() && !msg.isEmpty() && currentUser != null) {
                if (rabbitMQConnection.userQueueExists(queueName)) {
                    // Envia mensagem simples em formato JSON
                    String jsonMsg = String.format(
                            "{\"from\":\"%s\",\"to\":\"%s\",\"content\":\"%s\",\"type\":\"GENERIC\",\"timestamp\":\"%s\",\"topicName\":\"\"}",
                            currentUser.getUsername(), queueName, msg, java.time.LocalDateTime.now().toString()
                    );
                    rabbitMQConnection.sendMessageToQueue(queueName, jsonMsg);
                    appendMessage("Mensagem enviada para fila " + queueName + ": " + msg);
                } else {
                    appendMessage("Fila genérica não existe.");
                }
            }
        });

        frame.setVisible(true);
    }

    private void updateTopicList() {
        topicListModel.clear();
        List<String> topics = rabbitMQConnection.listTopicsFromRabbitMQ();
        for (String topic : topics) {
            topicListModel.addElement(topic);
        }
    }
    private void appendMessage(String msg) {
        if (msg.trim().startsWith("{") && msg.contains("\"from\"")) {
            try {
                String from = msg.split("\"from\":\"")[1].split("\"")[0];
                String to = msg.split("\"to\":\"")[1].split("\"")[0];
                String content = msg.split("\"content\":\"")[1].split("\"")[0];
                String type = msg.split("\"type\":\"")[1].split("\"")[0];
                String topic = msg.split("\"topicName\":\"")[1].split("\"")[0];
                String timestamp = msg.split("\"timestamp\":\"")[1].split("\"")[0];

                if ("DIRECT".equals(type)) {
                    messageArea.append(
                            "Recebido direto (" + from + ") às " + timestamp + ":\n" +
                                    content + "\n\n"
                    );
                } else if ("TOPIC".equals(type)) {
                    messageArea.append(
                            "Recebido no tópico " + topic + " (" + from + ") às " + timestamp + ":\n" +
                                    content + "\n\n"
                    );
                } else {
                    messageArea.append(
                            "Recebido (" + from + ") às " + timestamp + ":\n" +
                                    content + "\n\n"
                    );
                }
            } catch (Exception e) {
                messageArea.append(msg + "\n");
            }
        } else {
            messageArea.append(msg + "\n");
        }
    }

    // Thread simples para buscar mensagens da fila do usuário
    private void startMessageListener() {
        new Thread(() -> {
            while (true) {
                if (currentUser != null) {
                    String msg = rabbitMQConnection.receiveMessageFromQueue(currentUser.getPersonalQueueName());
                    if (msg != null) {
                        appendMessage(msg);
                    }
                }
                try {
                    Thread.sleep(1000); // verifica a cada 1 segundo
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }
}