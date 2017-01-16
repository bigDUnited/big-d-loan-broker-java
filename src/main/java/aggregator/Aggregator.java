package aggregator;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import config.StaticStrings;
import core.Publisher;
import entity.MessageObject;
import getBanks.GetBanksEnricher;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import structure.ComponentInterface;

public class Aggregator implements ComponentInterface {

    private Gson gson;
    private Publisher publisher;
    private QueueLogger queueLogger;

    @Override
    public void init() {
        gson = new Gson();
        publisher = new Publisher();
        queueLogger = new QueueLogger();

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(StaticStrings.HOST_ADDRESS);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(StaticStrings.AGGREGATOR_QUEUE, false, false, false, null);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                        AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("[Aggregator - *received*] : " + message);
                    logic(message);
                }
            };
            channel.basicConsume(StaticStrings.AGGREGATOR_QUEUE, true, consumer);

        } catch (IOException ex) {
            Logger.getLogger(GetBanksEnricher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(GetBanksEnricher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void logic(String queueMessage) {
        MessageObject mo = gson.fromJson(queueMessage, MessageObject.class);
        queueLogger.init(mo);

    }

}
