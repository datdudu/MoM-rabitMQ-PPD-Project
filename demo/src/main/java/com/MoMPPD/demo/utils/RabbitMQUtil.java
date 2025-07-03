package com.MoMPPD.demo.utils;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQUtil {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String queueName, String message) {
        rabbitTemplate.convertAndSend(queueName, message);
    }

    public Object receiveMessage(String queueName) {
        return rabbitTemplate.receiveAndConvert(queueName);
    }
}