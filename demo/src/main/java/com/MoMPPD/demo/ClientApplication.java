package com.MoMPPD.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.MoMPPD.demo")
public class ClientApplication implements CommandLineRunner{

    @Autowired
    private com.MoMPPD.demo.user.UserGUI userGUI;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        System.setProperty("app.type", "client");
        SpringApplication.run(ClientApplication.class, args);
        System.out.println("Cliente iniciado!");
    }

    @Override
    public void run(String... args) {
        String appType = System.getProperty("app.type", "client");
        if ("client".equals(appType)) {
            userGUI.init();
        }
    }
}