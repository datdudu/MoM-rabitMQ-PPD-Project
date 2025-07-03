package com.MoMPPD.demo.test;
import com.MoMPPD.demo.utils.RabbitMQConnection;
import com.MoMPPD.demo.models.User;
import com.MoMPPD.demo.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
public class RabbitMQTest implements CommandLineRunner {

//    @Autowired
    private RabbitMQConnection rabbitMQConnection;

    @Override
    public void run(String... args) throws Exception {
        // 1. Criar fila para usuário
        User user = new User("joao");
        boolean filaCriada = rabbitMQConnection.createQueue(user.getPersonalQueueName());
        System.out.println("Fila criada: " + filaCriada);

        // 2. Enviar mensagem direta para o usuário
        Message msg = new Message("admin", user.getUsername(), "Bem-vindo ao sistema!");
        boolean enviada = rabbitMQConnection.sendMessageToQueue(user.getPersonalQueueName(), msg.toJson());
        System.out.println("Mensagem enviada: " + enviada);

        // 3. Receber mensagem da fila do usuário
        String recebida = rabbitMQConnection.receiveMessageFromQueue(user.getPersonalQueueName());
        System.out.println("Mensagem recebida: " + recebida);

        // 4. Contar mensagens na fila (deve ser 0 agora)
        long count = rabbitMQConnection.getMessageCount(user.getPersonalQueueName());
        System.out.println("Mensagens restantes na fila: " + count);
    }
}