package com.MoMPPD.demo.broker;
import com.MoMPPD.demo.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.List;

@Component
public class BrokerGUI {

    @Autowired
    private BrokerManager brokerManager;

    private JFrame frame;
    private DefaultListModel<String> userListModel;
    private DefaultListModel<String> topicListModel;
    private DefaultListModel<String> genericQueueListModel;

    @PostConstruct
    public void init() {
        String appType = System.getProperty("app.type", "broker");
        if ("broker".equals(appType)) {
            SwingUtilities.invokeLater(this::createAndShowGUI);
        }
    }

    private void createAndShowGUI() {
        frame = new JFrame("Broker Manager - MOM PPD");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Usuários
        JPanel userPanel = new JPanel(new BorderLayout());
        userListModel = new DefaultListModel<>();
        JList<String> userList = new JList<>(userListModel);
        userPanel.add(new JScrollPane(userList), BorderLayout.CENTER);

        JPanel userInputPanel = new JPanel();
        JTextField userField = new JTextField(15);
        JButton addUserBtn = new JButton("Adicionar Usuário");
        JButton removeUserBtn = new JButton("Remover Usuário");
        userInputPanel.add(userField);
        userInputPanel.add(addUserBtn);
        userInputPanel.add(removeUserBtn);
        userPanel.add(userInputPanel, BorderLayout.SOUTH);

        addUserBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            if (!username.isEmpty()) {
                if (brokerManager.addUser(username)) {
                    updateUserList();
                    JOptionPane.showMessageDialog(frame, "Usuário adicionado!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Usuário já existe!");
                }
            }else{
                JOptionPane.showMessageDialog(frame, "Digite um nome de usuário para ser adicionado!");
            }
        });

        removeUserBtn.addActionListener(e -> {
            String selected = userList.getSelectedValue();
            if (selected != null) {
                String username = selected.split(" ")[0];
                String queueName = "user_" + username;
                if (brokerManager.removeGenericQueue(queueName)) { // Use o método de remover fila genérica
                    updateUserList();
                    JOptionPane.showMessageDialog(frame, "Usuário removido!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Erro ao remover usuário!");
                }
            }
        });

        tabbedPane.addTab("Usuários", userPanel);

        // Tópicos
        JPanel topicPanel = new JPanel(new BorderLayout());
        topicListModel = new DefaultListModel<>();
        JList<String> topicList = new JList<>(topicListModel);
        topicPanel.add(new JScrollPane(topicList), BorderLayout.CENTER);

        JPanel topicInputPanel = new JPanel();
        JTextField topicField = new JTextField(15);
        JButton addTopicBtn = new JButton("Adicionar Tópico");
        JButton removeTopicBtn = new JButton("Remover Tópico");
        topicInputPanel.add(topicField);
        topicInputPanel.add(addTopicBtn);
        topicInputPanel.add(removeTopicBtn);
        topicPanel.add(topicInputPanel, BorderLayout.SOUTH);

        addTopicBtn.addActionListener(e -> {
            String topic = topicField.getText().trim();
            if (!topic.isEmpty()) {
                if (brokerManager.addTopic(topic)) {
                    updateTopicList();
                    JOptionPane.showMessageDialog(frame, "Tópico adicionado!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Tópico já existe!");
                }
            }
        });

        removeTopicBtn.addActionListener(e -> {
            String selected = topicList.getSelectedValue();
            if (selected != null) {
                if (brokerManager.removeTopic(selected)) {
                    updateTopicList();
                    JOptionPane.showMessageDialog(frame, "Tópico removido!");
                }
            }
        });

        tabbedPane.addTab("Tópicos", topicPanel);

        // Mensagens nas filas dos usuários
        JPanel queuePanel = new JPanel(new BorderLayout());
        DefaultListModel<String> queueListModel = new DefaultListModel<>();
        JList<String> queueList = new JList<>(queueListModel);
        queuePanel.add(new JScrollPane(queueList), BorderLayout.CENTER);

        JButton refreshQueuesBtn = new JButton("Atualizar Filas");
        queuePanel.add(refreshQueuesBtn, BorderLayout.SOUTH);

        refreshQueuesBtn.addActionListener(e -> {
            queueListModel.clear();
            List<String> userQueues = brokerManager.listUserQueuesFromRabbitMQ();
            for (String queueName : userQueues) {
                String username = queueName.replaceFirst("user_", "");
                long count = brokerManager.getQueueMessageCount(queueName);
                queueListModel.addElement(username + " - Mensagens na fila: " + count);
            }
        });

        tabbedPane.addTab("Filas de Usuários", queuePanel);

        // Filas Genéricas
        JPanel genericQueuePanel = new JPanel(new BorderLayout());
        genericQueueListModel = new DefaultListModel<>(); // REMOVA o "DefaultListModel<String>" aqui
        JList<String> genericQueueList = new JList<>(genericQueueListModel);
        genericQueuePanel.add(new JScrollPane(genericQueueList), BorderLayout.CENTER);

        JPanel genericQueueInputPanel = new JPanel();
        JTextField genericQueueField = new JTextField(15);
        JButton addGenericQueueBtn = new JButton("Adicionar Fila");
        JButton removeGenericQueueBtn = new JButton("Remover Fila");
        JButton refreshGenericQueuesBtn = new JButton("Atualizar Filas");
        genericQueueInputPanel.add(genericQueueField);
        genericQueueInputPanel.add(addGenericQueueBtn);
        genericQueueInputPanel.add(removeGenericQueueBtn);
        genericQueueInputPanel.add(refreshGenericQueuesBtn);
        genericQueuePanel.add(genericQueueInputPanel, BorderLayout.SOUTH);

        addGenericQueueBtn.addActionListener(e -> {
            String queueName = genericQueueField.getText().trim();
            if (!queueName.isEmpty()) {
                if (brokerManager.addGenericQueue(queueName)) {
                    updateGenericQueueList();
                    JOptionPane.showMessageDialog(frame, "Fila criada!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Fila já existe!");
                }
            }
        });

        removeGenericQueueBtn.addActionListener(e -> {
            String selected = genericQueueList.getSelectedValue();
            if (selected != null) {
                if (brokerManager.removeGenericQueue(selected)) {
                    updateGenericQueueList();
                    JOptionPane.showMessageDialog(frame, "Fila removida!");
                }
            }
        });

        refreshGenericQueuesBtn.addActionListener(e -> updateGenericQueueList());

        tabbedPane.addTab("Filas Genéricas", genericQueuePanel);


        frame.add(tabbedPane);
        updateUserList();
        updateTopicList();
        updateGenericQueueList();
        frame.setVisible(true);
    }

    private void updateUserList() {
        userListModel.clear();
        List<String> userQueues = brokerManager.listUserQueuesFromRabbitMQ();
        for (String queueName : userQueues) {
            // Extrai o nome do usuário do padrão user_nome
            String username = queueName.replaceFirst("user_", "");
            userListModel.addElement(username + " (fila: " + queueName + ")");
        }
    }

    private void updateTopicList() {
        topicListModel.clear();
        List<String> topics = brokerManager.listTopicsFromRabbitMQ();
        for (String topic : topics) {
            topicListModel.addElement(topic);
        }
    }

    private void updateGenericQueueList() {
        genericQueueListModel.clear();
        List<String> allQueues = brokerManager.listAllQueuesFromRabbitMQ(); // Este método deve retornar TODAS as filas
        for (String queue : allQueues) {
            if (!queue.startsWith("user_")) {
                genericQueueListModel.addElement(queue);
            }
        }
    }
}