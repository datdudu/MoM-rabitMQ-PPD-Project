package com.MoMPPD.demo;

import com.MoMPPD.demo.broker.BrokerGUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.MoMPPD.demo")
public class BrokerApplication implements CommandLineRunner {

    @Autowired
    private com.MoMPPD.demo.broker.BrokerGUI brokerGUI;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        System.setProperty("app.type", "broker");
        SpringApplication.run(BrokerApplication.class, args);
        System.out.println("Broker Manager iniciado!");
    }

    @Override
    public void run(String... args) {
        String appType = System.getProperty("app.type", "broker");
        if ("broker".equals(appType)) {
            brokerGUI.init();
        }
    }
}