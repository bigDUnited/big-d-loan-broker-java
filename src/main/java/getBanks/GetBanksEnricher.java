package getBanks;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import config.StaticStrings;
import entity.Bank;
import entity.MessageObject;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import structure.ComponentInterface;

public class GetBanksEnricher implements ComponentInterface {

    private GetBanksChannelAdapter gbca;
    private Gson gson;

    @Override
    public void init() {
        gbca = new GetBanksChannelAdapter();
        gson = new Gson();

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(StaticStrings.HOST_ADDRESS);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(StaticStrings.GET_BANKS_QUEUE_NAME, false, false, false, null);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                        AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(" [GetBanksEnricher - init] Received '" + message + "'");
                    logic(message);
                }
            };
            channel.basicConsume(StaticStrings.GET_BANKS_QUEUE_NAME, true, consumer);

        } catch (TimeoutException ex) {
        } catch (IOException ex) {
        }
    }

    @Override
    public void logic(String queueMessage) {

        MessageObject mo = gson.fromJson(queueMessage, MessageObject.class);
        List<Bank> banks = gbca.getBanks(mo.getCreditScore());

        for (int i = 0; i < banks.size(); i++) {
            System.out.println("i : " + i + " : " + banks.toString());
        }
    }

}
