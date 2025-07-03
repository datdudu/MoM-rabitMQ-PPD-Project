package com.MoMPPD.demo.models;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String personalQueueName;
    private List<String> subscribedTopics;
    private boolean isOnline;

    public User(String username) {
        this.username = username;
        this.personalQueueName = "user_" + username; // fila pessoal do usuário
        this.subscribedTopics = new ArrayList<>();
        this.isOnline = false;
    }

    // ========== GETTERS E SETTERS ==========

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPersonalQueueName() {
        return personalQueueName;
    }

    public void setPersonalQueueName(String personalQueueName) {
        this.personalQueueName = personalQueueName;
    }

    public List<String> getSubscribedTopics() {
        return subscribedTopics;
    }

    public void setSubscribedTopics(List<String> subscribedTopics) {
        this.subscribedTopics = subscribedTopics;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    // ========== MÉTODOS UTILITÁRIOS ==========

    /**
     * Adiciona um tópico à lista de inscrições
     */
    public boolean subscribeToTopic(String topicName) {
        if (!subscribedTopics.contains(topicName)) {
            subscribedTopics.add(topicName);
            return true;
        }
        return false; // já estava inscrito
    }

    /**
     * Remove um tópico da lista de inscrições
     */
    public boolean unsubscribeFromTopic(String topicName) {
        return subscribedTopics.remove(topicName);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", personalQueueName='" + personalQueueName + '\'' +
                ", subscribedTopics=" + subscribedTopics +
                ", isOnline=" + isOnline +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}