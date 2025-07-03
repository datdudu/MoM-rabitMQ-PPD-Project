package com.MoMPPD.demo.test;

import com.MoMPPD.demo.broker.BrokerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
public class BrokerManagerTest implements CommandLineRunner {

//    @Autowired
    private BrokerManager brokerManager;

    @Override
    public void run(String... args) throws Exception {
        // Adiciona usuário
        System.out.println("Usuário joao criado? " + brokerManager.addUser("joao"));
        System.out.println("Usuário maria criado? " + brokerManager.addUser("maria"));
        System.out.println("Usuário joao criado de novo? " + brokerManager.addUser("joao")); // deve ser false

        // Lista usuários
        System.out.println("Usuários: " + brokerManager.listUsers());

        // Adiciona tópico
        System.out.println("Tópico 'noticias' criado? " + brokerManager.addTopic("noticias"));
        System.out.println("Tópico 'noticias' criado de novo? " + brokerManager.addTopic("noticias")); // deve ser false

        // Lista tópicos
        System.out.println("Tópicos: " + brokerManager.listTopics());

        // Remove usuário
        System.out.println("Usuário joao removido? " + brokerManager.removeUser("joao"));
        System.out.println("Usuários: " + brokerManager.listUsers());
    }
}
