package com.MoMPPD.demo.utils;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class RabbitMQConnection {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    // ========== GERENCIAMENTO DE FILAS ==========

    /**
     * Cria uma nova fila
     */
    public boolean createQueue(String queueName) {
        try {
            Queue queue = new Queue(queueName, true); // durável
            rabbitAdmin.declareQueue(queue);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao criar fila: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove uma fila
     */
    public boolean deleteQueue(String queueName) {
        try {
            rabbitAdmin.deleteQueue(queueName);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao remover fila: " + e.getMessage());
            return false;
        }
    }

    // ========== GERENCIAMENTO DE TÓPICOS ==========

    /**
     * Cria um novo tópico (exchange do tipo topic)
     */
    public boolean createTopic(String topicName) {
        try {
            TopicExchange exchange = new TopicExchange(topicName);
            rabbitAdmin.declareExchange(exchange);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao criar tópico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove um tópico
     */
    public boolean deleteTopic(String topicName) {
        try {
            rabbitAdmin.deleteExchange(topicName);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao remover tópico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Vincula uma fila a um tópico com routing key
     */
    public boolean bindQueueToTopic(String queueName, String topicName, String routingKey) {
        try {
            Queue queue = new Queue(queueName);
            TopicExchange exchange = new TopicExchange(topicName);
            Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey);
            rabbitAdmin.declareBinding(binding);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao vincular fila ao tópico: " + e.getMessage());
            return false;
        }
    }

    // ========== ENVIO E RECEBIMENTO DE MENSAGENS ==========

    /**
     * Envia mensagem para uma fila específica
     */
    public boolean sendMessageToQueue(String queueName, String message) {
        try {
            rabbitTemplate.convertAndSend(queueName, message);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem para fila: " + e.getMessage());
            return false;
        }
    }

    /**
     * Envia mensagem para um tópico
     */
    public boolean sendMessageToTopic(String topicName, String routingKey, String message) {
        try {
            rabbitTemplate.convertAndSend(topicName, routingKey, message);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem para tópico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recebe mensagem de uma fila (não-bloqueante)
     */
    public String receiveMessageFromQueue(String queueName) {
        try {
            Object message = rabbitTemplate.receiveAndConvert(queueName);
            return message != null ? message.toString() : null;
        } catch (Exception e) {
            System.err.println("Erro ao receber mensagem da fila: " + e.getMessage());
            return null;
        }
    }

    // ========== INFORMAÇÕES ==========

    /**
     * Obtém informações sobre uma fila (incluindo quantidade de mensagens)
     */
    public Properties getQueueInfo(String queueName) {
        try {
            return rabbitAdmin.getQueueProperties(queueName);
        } catch (Exception e) {
            System.err.println("Erro ao obter informações da fila: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtém quantidade de mensagens em uma fila
     */
    public long getMessageCount(String queueName) {
        try {
            Properties props = rabbitAdmin.getQueueProperties(queueName);
            if (props != null) {
                return (Long) props.get(RabbitAdmin.QUEUE_MESSAGE_COUNT);
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Erro ao obter contagem de mensagens: " + e.getMessage());
            return 0;
        }
    }

    public boolean unbindQueueFromTopic(String queueName, String topicName, String routingKey) {
        try {
            Queue queue = new Queue(queueName);
            TopicExchange exchange = new TopicExchange(topicName);
            Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey);
            rabbitAdmin.removeBinding(binding);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao desvincular fila do tópico: " + e.getMessage());
            return false;
        }
    }

    public boolean userQueueExists(String queueName) {
        try {
            Properties props = rabbitAdmin.getQueueProperties(queueName);
            return props != null;
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> listTopicsFromRabbitMQ() {
        List<String> topics = new ArrayList<>();
        try {
            String apiUrl = "http://localhost:15672/api/exchanges/%2F";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String basicAuth = "Basic " + Base64.getEncoder().encodeToString("guest:guest".getBytes());
            conn.setRequestProperty("Authorization", basicAuth);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            // Parse JSON (simples, sem biblioteca externa)
            String json = content.toString();
            String[] exchanges = json.split("\\},\\{");
            for (String exchange : exchanges) {
                if (exchange.contains("\"type\":\"topic\"")) {
                    // Pega o nome do exchange
                    String[] parts = exchange.split("\"name\":\"");
                    if (parts.length > 1) {
                        String name = parts[1].split("\"")[0];
                        // Ignora exchanges padrão do RabbitMQ
                        if (!name.isEmpty() && !name.startsWith("amq.")) {
                            topics.add(name);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao consultar tópicos do RabbitMQ: " + e.getMessage());
        }
        return topics;
    }

    public List<String> listUserQueuesFromRabbitMQ() {
        List<String> userQueues = new ArrayList<>();
        try {
            String apiUrl = "http://localhost:15672/api/queues/%2F";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String basicAuth = "Basic " + Base64.getEncoder().encodeToString("guest:guest".getBytes());
            conn.setRequestProperty("Authorization", basicAuth);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            // Parse JSON (simples, sem biblioteca externa)
            String json = content.toString();
            String[] queues = json.split("\\},\\{");
            for (String queue : queues) {
                String[] parts = queue.split("\"name\":\"");
                if (parts.length > 1) {
                    String name = parts[1].split("\"")[0];
                    if (name.startsWith("user_")) {
                        userQueues.add(name);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao consultar filas do RabbitMQ: " + e.getMessage());
        }
        return userQueues;
    }

    public boolean createGenericQueue(String queueName) {
        return createQueue(queueName);
    }

    public boolean deleteGenericQueue(String queueName) {
        return deleteQueue(queueName);
    }

    public List<String> listAllQueuesFromRabbitMQ() {
        List<String> allQueues = new ArrayList<>();
        try {
            String apiUrl = "http://localhost:15672/api/queues/%2F";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String basicAuth = "Basic " + Base64.getEncoder().encodeToString("guest:guest".getBytes());
            conn.setRequestProperty("Authorization", basicAuth);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            // Parse JSON (simples, sem biblioteca externa)
            String json = content.toString();
            String[] queues = json.split("\\},\\{");
            for (String queue : queues) {
                String[] parts = queue.split("\"name\":\"");
                if (parts.length > 1) {
                    String name = parts[1].split("\"")[0];
                    // Retorna TODAS as filas, sem filtrar
                    allQueues.add(name);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao consultar todas as filas do RabbitMQ: " + e.getMessage());
        }
        return allQueues;
    }
}