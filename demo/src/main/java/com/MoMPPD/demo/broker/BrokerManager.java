package com.MoMPPD.demo.broker;

import com.MoMPPD.demo.models.User;
import com.MoMPPD.demo.utils.RabbitMQConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BrokerManager {

    @Autowired
    private RabbitMQConnection rabbitMQConnection;

    // Mapa de usuários cadastrados (username -> User)
    private final Map<String, User> users = new HashMap<>();

    // Lista de tópicos criados
    private final Set<String> topics = new HashSet<>();

    // ========== USUÁRIOS ==========

    public boolean addUser(String username) {
        String queueName = "user_" + username;
        // Verifica se a fila já existe
        if (rabbitMQConnection.userQueueExists(queueName)) {
            return false; // Usuário já existe
        }
        // Cria a fila para o novo usuário
        return rabbitMQConnection.createQueue(queueName);
    }

    public boolean removeUser(String username) {
        User user = users.remove(username);
        if (user != null) {
            rabbitMQConnection.deleteQueue(user.getPersonalQueueName());
            return true;
        }
        return false;
    }

    public List<User> listUsers() {
        return new ArrayList<>(users.values());
    }

    // ========== FILAS ==========

    public boolean addQueue(String queueName) {
        return rabbitMQConnection.createQueue(queueName);
    }

    public boolean removeQueue(String queueName) {
        return rabbitMQConnection.deleteQueue(queueName);
    }

    public long getQueueMessageCount(String queueName) {
        return rabbitMQConnection.getMessageCount(queueName);
    }

    // ========== TÓPICOS ==========

    public boolean addTopic(String topicName) {
        if (topics.contains(topicName)) {
            return false; // Tópico já existe
        }
        boolean created = rabbitMQConnection.createTopic(topicName);
        if (created) {
            topics.add(topicName);
        }
        return created;
    }

    public boolean removeTopic(String topicName) {
        boolean removed = rabbitMQConnection.deleteTopic(topicName);
        if (removed) {
            topics.remove(topicName);
        }
        return removed;
    }

    public List<String> listTopics() {
        return new ArrayList<>(topics);
    }

    // ========== OUTROS MÉTODOS ÚTEIS ==========

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public List<String> listTopicsFromRabbitMQ() {
        return rabbitMQConnection.listTopicsFromRabbitMQ();
    }

    public List<String> listUserQueuesFromRabbitMQ() {
        return rabbitMQConnection.listUserQueuesFromRabbitMQ();
    }

    public boolean addGenericQueue(String queueName) {
        // Não permite duplicidade
        if (rabbitMQConnection.userQueueExists(queueName)) {
            return false;
        }
        return rabbitMQConnection.createGenericQueue(queueName);
    }

    public boolean removeGenericQueue(String queueName) {
        return rabbitMQConnection.deleteGenericQueue(queueName);
    }

    public List<String> listAllQueuesFromRabbitMQ() {
        return rabbitMQConnection.listAllQueuesFromRabbitMQ();
    }
}