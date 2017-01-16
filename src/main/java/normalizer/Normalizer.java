package normalizer;

import aggregator.Aggregator;
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
import entity.ReturnMessageObjectJson;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Normalizer {

    private Channel channel;
    private Connection connection;
    private ConnectionFactory connectionFactory;
    private Gson gson;
    private Publisher publisher;
    private Aggregator aggregator = null;

    public void init() {
        gson = new Gson();
        publisher = new Publisher();
        try {
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
                    System.out.println("[Normalizer *received*] : Content type : " + properties.getContentType());
                    System.out.println("[Normalizer *received*] : App id : " + properties.getAppId());
                    System.out.println("[Normalizer *received*] : " + message);
                    logic(message, properties.getContentType(), properties.getAppId());
                }
            };
            channel.basicConsume(StaticStrings.NORMALIZER_QUEUE, true, consumer);
        } catch (IOException ex) {
            Logger.getLogger(Normalizer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(Normalizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void logic(String queueMessage, String type, String appId) {
        if ("JSON".equals(type)) {
            ReturnMessageObjectJson rmo = gson.fromJson(queueMessage, ReturnMessageObjectJson.class);
            MessageObject mo = new MessageObject(rmo.getSsn(), rmo.getInterestRate());
            mo.setChosenBank(appId);

            String response = gson.toJson(mo);

            String hostAddress = StaticStrings.HOST_ADDRESS;
            String queueName = StaticStrings.AGGREGATOR_QUEUE;
            
            System.out.println("[Normalizer *send*] : " + queueMessage);
            publisher.publishMessage(hostAddress, queueName, response);

            if (aggregator == null) {
                aggregator = new Aggregator();
            }
            aggregator.init();
        }
    }

}
