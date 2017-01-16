package normalizer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import config.StaticStrings;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import structure.ComponentInterface;

public class Normalizer implements ComponentInterface {

    private Channel channel;
    private Connection connection;
    private ConnectionFactory connectionFactory;

    @Override
    public void init() {
        try {
            connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("localhost");
            connectionFactory.setUsername("guest");
            connectionFactory.setPassword("guest");
            connection = connectionFactory.newConnection();

            channel = connection.createChannel();

            channel.queueDeclare(StaticStrings.NORMALIZER_QUEUE, false, false, false, null);
            channel.exchangeDeclare(StaticStrings.NORMALIZER_EXCHANGE, "direct");
            channel.queueBind(StaticStrings.NORMALIZER_QUEUE, StaticStrings.NORMALIZER_EXCHANGE, "");

            //
            connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("datdb.cphbusiness.dk");
            connectionFactory.setUsername("what");
            connectionFactory.setPassword("what");
            connection = connectionFactory.newConnection();

            channel = connection.createChannel();

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                        AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("[Normalizer *received*] : " + message);
                    logic(message);
                }
            };
            channel.basicConsume(StaticStrings.NORMALIZER_QUEUE, true, consumer);
        } catch (IOException ex) {
            Logger.getLogger(Normalizer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(Normalizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void logic(String queueMessage) {
        System.out.println("EXIT : " + queueMessage);
        System.exit(0);
    }

}
