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
import entity.MessageObjectJsonBank;
import getBanks.GetBanksEnricher;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import structure.ComponentInterface;

public class DanskeBankTranslator implements ComponentInterface {

    private Gson gson;
    private Publisher publisher;

    @Override
    public void init() {
        gson = new Gson();
        publisher = new Publisher();

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
                    System.out.println(" [DanskeBankTranslator - init] Received '" + message + "'");
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

        System.out.println("Hello from danske bank - resceived : " + queueMessage);

        MessageObject mo = gson.fromJson(queueMessage, MessageObject.class);

        int ssn = Integer.parseInt(mo.getCpr().replaceAll("[^\\d.]", ""));
        MessageObjectJsonBank mojb = new MessageObjectJsonBank(ssn, mo.getCreditScore(), mo.getLoanAmount(), mo.getLoanDuration());

        String message = gson.toJson(mojb);
        System.out.println("Final version : " + message);
//        
//        try {
//
//            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
//            builder.replyTo(StaticStrings.NORMALIZER_QUEUE);
//            builder.contentType("JSON");
//
//            String EXCHANGE_NAME = "cphbusiness.bankJSON";
//
//            ConnectionFactory factory = new ConnectionFactory();
//            
//            factory.setHost("datdb.cphbusiness.dk");
//            
//            Connection connection = factory.newConnection();
//            Channel channel = connection.createChannel();
//            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
//            channel.basicPublish(EXCHANGE_NAME, "", builder.build(), message.getBytes());
//            System.out.println(" [x] Sent [XXX] '" + message + "'");
//
//            channel.close();
//            connection.close();
//        } catch (IOException ex) {
//            Logger.getLogger(DanskeBankTranslator.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (TimeoutException ex) {
//            Logger.getLogger(DanskeBankTranslator.class.getName()).log(Level.SEVERE, null, ex);
//        }

//            String QUEUE_FROM_RECIPLIST = "nmc_reciplist_to_translatorbankjson_queue";
//            String QUEUE_FROM_BANKJSON_TO_NORMALIZER = "nmc_banks_to_normalizer_queue";
//
//            ConnectionFactory factory = new ConnectionFactory();
//            factory.setHost("datdb.cphbusiness.dk");
//            factory.setPort(5672);
//            factory.setUsername("student");
//            factory.setPassword("cph");
//            Connection connection = factory.newConnection();
//            Channel channel = connection.createChannel();
//            channel.queueDeclare(QUEUE_FROM_RECIPLIST, false, false, false, null);
//            System.out.println("Hello1");
//
//            QueueingConsumer consumer = new QueueingConsumer(channel);
//            channel.basicConsume(QUEUE_FROM_RECIPLIST, true, consumer);
//            while (true) {
//                System.out.println("Hello2");
//                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
//                System.out.println(" [x] Received from the web app: " + new String(delivery.getBody()));
//
//                AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
//                builder.replyTo(QUEUE_FROM_BANKJSON_TO_NORMALIZER);
//                builder.correlationId(delivery.getProperties().getCorrelationId());
//                builder.contentType("JSON");
//                builder.appId(delivery.getProperties().getAppId());
//
//                channel.basicPublish(delivery.getProperties().getReplyTo(), "", builder.build(), message.getBytes());
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(DanskeBankTranslator.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (TimeoutException ex) {
//            Logger.getLogger(DanskeBankTranslator.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(DanskeBankTranslator.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ShutdownSignalException ex) {
//            Logger.getLogger(DanskeBankTranslator.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ConsumerCancelledException ex) {
//            Logger.getLogger(DanskeBankTranslator.class.getName()).log(Level.SEVERE, null, ex);
//        }
        try {
            String EXCHANGE_NAME = "cphbusiness.bankJSON";

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("datdb.cphbusiness.dk");
            factory.setPort(5672);
            Connection connection = factory.newConnection();

            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

            channel.close();
            connection.close();
        } catch (IOException ex) {
            Logger.getLogger(DanskeBankTranslator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(DanskeBankTranslator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
