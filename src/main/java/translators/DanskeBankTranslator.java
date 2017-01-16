package translators;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import config.StaticStrings;
import core.Publisher;
import entity.MessageObject;
import entity.SendMessageObjectJson;
import getBanks.GetBanksEnricher;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import normalizer.Normalizer;
import structure.ComponentInterface;

public class DanskeBankTranslator implements ComponentInterface {

    private Gson gson;
    private Normalizer normalizer;

    @Override
    public void init() {
        gson = new Gson();
        normalizer = new Normalizer();

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(StaticStrings.HOST_ADDRESS);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(StaticStrings.DANSKEBANK_TRANSLATOR_QUEUE, false, false, false, null);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                        AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("[DanskeBankTranslator *received*] : " + message);
                    logic(message);
                }
            };
            channel.basicConsume(StaticStrings.DANSKEBANK_TRANSLATOR_QUEUE, true, consumer);

        } catch (IOException ex) {
            Logger.getLogger(GetBanksEnricher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(GetBanksEnricher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void logic(String queueMessage) {
        MessageObject mo = gson.fromJson(queueMessage, MessageObject.class);

        int ssn = Integer.parseInt(mo.getCpr().replaceAll("[^\\d.]", ""));
        SendMessageObjectJson mojb = new SendMessageObjectJson(ssn, mo.getCreditScore(), mo.getLoanAmount(), mo.getLoanDuration());

        String message = gson.toJson(mojb);

        try {
            String EXCHANGE_NAME = "cphbusiness.bankJSON";

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("datdb.cphbusiness.dk");

            Connection connection = factory.newConnection();

            Channel channel = connection.createChannel();

            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
            builder.replyTo(StaticStrings.NORMALIZER_QUEUE);
            builder.contentType("JSON");
            builder.appId(mo.getChosenBank());

            channel.basicPublish(EXCHANGE_NAME, "", builder.build(), message.getBytes());
            System.out.println("[DanskeBankTranslator *sent*] : " + message);
            normalizer.init();

            channel.close();
            connection.close();

        } catch (IOException ex) {
            Logger.getLogger(DanskeBankTranslator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(DanskeBankTranslator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
