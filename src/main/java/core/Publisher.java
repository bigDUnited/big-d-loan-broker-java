package core;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Publisher {

    public boolean publishMessage(String hostAddress, String queueName, String queueMessage) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(hostAddress);
            Connection connection;

            connection = factory.newConnection();

            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);
            String message = queueMessage;
            channel.basicPublish("", queueName, null, message.getBytes());
            System.out.println(" [Publisher - publishMessage] Sent '" + message + "'");

            channel.close();
            connection.close();
            return true;
        } catch (TimeoutException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
