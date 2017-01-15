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
import core.Publisher;
import entity.Bank;
import entity.MessageObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import recipientList.RecipientList;
import structure.ComponentInterface;

public class GetBanksEnricher implements ComponentInterface {

    private GetBanksChannelAdapter gbca;
    private Gson gson;
    private Publisher publisher;
    private RecipientList recipientList;

    @Override
    public void init() {
        gbca = new GetBanksChannelAdapter();
        gson = new Gson();
        publisher = new Publisher();
        recipientList = new RecipientList();

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

        } catch (IOException ex) {
            Logger.getLogger(GetBanksEnricher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(GetBanksEnricher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void logic(String queueMessage) {

        MessageObject mo = gson.fromJson(queueMessage, MessageObject.class);
        List<Bank> banks = gbca.getBanks(mo.getCreditScore());

        List<String> bankNames = new ArrayList();
        for (int i = 0; i < banks.size(); i++) {
            bankNames.add(banks.get(i).getName());
        }

        mo = new MessageObject(mo.getCpr(), mo.getLoanAmount(), mo.getLoanDuration(), mo.getCreditScore(), bankNames);

        queueMessage = gson.toJson(mo);
        String hostAddress = StaticStrings.HOST_ADDRESS;
        String queueName = StaticStrings.RECIPIENT_LIST_QUEUE_NAME;

        publisher.publishMessage(hostAddress, queueName, queueMessage);
        
        recipientList.init();

    }

}
