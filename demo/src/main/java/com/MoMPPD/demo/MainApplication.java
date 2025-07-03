package com.MoMPPD.demo;

import com.MoMPPD.demo.broker.BrokerGUI;
import com.MoMPPD.demo.user.UserGUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

@SpringBootApplication(scanBasePackages = "com.MoMPPD.demo")
public class MainApplication implements CommandLineRunner {

    @Autowired
    private BrokerGUI brokerGUI;

    @Autowired
    private UserGUI userGUI;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(MainApplication.class, args);
    }

    @Override
    public void run(String... args) {
        SwingUtilities.invokeLater(() -> {
            String[] options = {"Broker Manager", "Cliente"};
            int escolha = JOptionPane.showOptionDialog(
                    null,
                    "Selecione o modo de execução:",
                    "Modo de Execução",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (escolha == 0) {
                System.setProperty("app.type", "broker");
                brokerGUI.init();
                System.out.println("Broker Manager iniciado!");
            } else if (escolha == 1) {
                System.setProperty("app.type", "client");
                userGUI.init();
                System.out.println("Cliente iniciado!");
            } else {
                System.out.println("Nenhuma opção selecionada. Encerrando aplicação.");
                System.exit(0);
            }
        });
    }
}