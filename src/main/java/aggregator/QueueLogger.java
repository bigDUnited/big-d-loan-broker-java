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
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueueLogger {

    private MessageObject messageObject;
    private Gson gson;
    private String hostAddress = StaticStrings.HOST_ADDRESS;
    private Publisher publisher;
    private Channel channel;

    public void init(MessageObject mo) {
        messageObject = mo;
        gson = new Gson();
        publisher = new Publisher();

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(StaticStrings.HOST_ADDRESS);
            Connection connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(StaticStrings.LOGGER_QUEUE, false, false, false, null);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                        AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("[Logger - *received*] : " + message);
                    if (messageObject != null) {;
                        logic(message);
                    }
                }
            };
            channel.basicConsume(StaticStrings.LOGGER_QUEUE, true, consumer);

        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(GetBanksEnricher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            java.util.logging.Logger.getLogger(GetBanksEnricher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void logic(String queueMessage) {
        System.out.println("--------------------------");
        MessageObject mo = gson.fromJson(queueMessage, MessageObject.class);

        int ssn = Integer.parseInt(mo.getCpr().replaceAll("[^\\d.]", ""));

        System.out.println("Local object : " + mo.toStringTwo());
        System.out.println("Local object SSN : " + ssn);
        System.out.println("Outer object : " + messageObject.toStringTwo());
        if (ssn == Integer.parseInt(messageObject.getCpr())) {
            System.out.println("CPR ARE EQUAL : " + ssn);

            List<String> localBanks = mo.getBankNameList();
            System.out.println("SIZE ?? " + localBanks.size());
            for (int i = 0; i < localBanks.size(); i++) {
                System.out.println("Local bank : " + localBanks.get(i) + " vs chosen bank : " + messageObject.getChosenBank());
                if (localBanks.get(i).equals(messageObject.getChosenBank())) {
                    System.out.println("BANKS ARE EQUAL! " + messageObject.getChosenBank());
                    System.out.println("Local : " + mo.getInterestRate() + " VS OUTER: " + messageObject.getInterestRate());
                    System.out.println("RESULT ? : " + Math.abs(mo.getInterestRate() - messageObject.getInterestRate()));
                    int retval = Float.compare(mo.getInterestRate(), messageObject.getInterestRate());

                    if (retval < 0) {
                        System.out.println("f1 is less than f2 YEEEEEEEE");
                        mo.setInterestRate(messageObject.getInterestRate());
                        mo.setChosenBank(messageObject.getChosenBank());
                    }
                    mo.getBankNameList().remove(localBanks.get(i));
                    messageObject = null;
                    try {
                        channel.abort();
                    } catch (IOException ex) {
                        Logger.getLogger(QueueLogger.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;

                }
            }

            if (mo.getBankNameList().isEmpty()) {
                System.out.println("DONE!!!!" + mo.toStringTwo());
                System.exit(0);
            } else {
                String queueMessageToLogger = gson.toJson(mo);
                System.out.println("[QueueLogger *send* - Logger] : " + queueMessageToLogger);
                publisher.publishMessage(hostAddress, StaticStrings.LOGGER_QUEUE, queueMessageToLogger);
            }

        } else {
            System.out.println("[QueueLogger *send* - NEGATIVE - BACK TO Logger] : " + queueMessage);
            publisher.publishMessage(hostAddress, StaticStrings.LOGGER_QUEUE, queueMessage);
        }
        System.out.println("-------------------***");
    }

//        float a = 2.5f;
//        float b = 3.5f;
//        if(Math.abs(a - b) < 0.1) {
//            //is a bigger than b ? - no
//            System.out.println("Yes?");
//        }else {
//            System.out.println("No!");
//        }
//        System.exit(0);
}
