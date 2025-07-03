package com.MoMPPD.demo.models;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Data
public class Message {
    private String from;
    private String to;
    private String content;
    private MessageType type;
    private String timestamp;
    private String topicName; // usado quando type = TOPIC

    public enum MessageType {
        DIRECT,  // mensagem direta entre usuários
        TOPIC    // mensagem para tópico
    }

    // ========== CONSTRUTORES ==========

    /**
     * Construtor para mensagem direta
     */
    public Message(String from, String to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
        this.type = MessageType.DIRECT;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Construtor para mensagem de tópico
     */
    public Message(String from, String topicName, String content, MessageType type) {
        this.from = from;
        this.topicName = topicName;
        this.content = content;
        this.type = type;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // ========== GETTERS E SETTERS ==========

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    // ========== MÉTODOS UTILITÁRIOS ==========

    /**
     * Converte a mensagem para formato JSON simples
     */
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"from\":\"").append(from).append("\",");
        json.append("\"to\":\"").append(to != null ? to : "").append("\",");
        json.append("\"content\":\"").append(content).append("\",");
        json.append("\"type\":\"").append(type).append("\",");
        json.append("\"timestamp\":\"").append(timestamp).append("\",");
        json.append("\"topicName\":\"").append(topicName != null ? topicName : "").append("\"");
        json.append("}");
        return json.toString();
    }

    @Override
    public String toString() {
        if (type == MessageType.DIRECT) {
            return String.format("[%s] %s -> %s: %s", timestamp, from, to, content);
        } else {
            return String.format("[%s] %s -> TOPIC(%s): %s", timestamp, from, topicName, content);
        }
    }
}