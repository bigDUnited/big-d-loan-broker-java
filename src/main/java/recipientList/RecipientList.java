package recipientList;

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
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import structure.ComponentInterface;
import translators.DanskeBankTranslator;
import translators.NordeaBankTranslator;

public class RecipientList implements ComponentInterface {

    private Gson gson;
    private Publisher publisher;
    private String hostAddress = StaticStrings.HOST_ADDRESS;
    private Random random;

    @Override
    public void init() {
        gson = new Gson();
        publisher = new Publisher();
        random = new Random();

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(StaticStrings.HOST_ADDRESS);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(StaticStrings.RECIPIENT_LIST_QUEUE_NAME, false, false, false, null);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                        AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("[RecipientList - *received*] : " + message);
                    logic(message);
                }
            };
            channel.basicConsume(StaticStrings.RECIPIENT_LIST_QUEUE_NAME, true, consumer);

        } catch (IOException ex) {
            Logger.getLogger(GetBanksEnricher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(GetBanksEnricher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void logic(String queueMessage) {

        MessageObject mo = gson.fromJson(queueMessage, MessageObject.class);
        List<String> banks = mo.getBankNameList();

        for (int i = 0; i < banks.size(); i++) {
            String queueName = "";
            switch (banks.get(i)) {
                case "danskebank_translator_queue":
                    queueName = StaticStrings.DANSKEBANK_TRANSLATOR_QUEUE;
                    break;
                case "nordea_translator_queue":
                    queueName = StaticStrings.NORDEA_TRANSLATOR_QUEUE;
                    break;
                case "nytkredit_translator_queue":
                    queueName = StaticStrings.NYTKREDIT_TRANSLATOR_QUEUE;
                    break;
                case "bdo_translator_queue":
                    queueName = StaticStrings.BDO_TRANSLATOR_QUEUE;
                    break;
                default:
                    queueName = StaticStrings.DANSKEBANK_TRANSLATOR_QUEUE;
                    break;

            }
            //Simplify solution
            if (!queueName.equals(StaticStrings.DANSKEBANK_TRANSLATOR_QUEUE)) {
                mo.setLoanAmount(random.nextInt(20000 - 1000 + 1) + 1000);
                mo.setLoanDuration(random.nextInt(24 - 6 + 1) + 6);
            }
            queueName = StaticStrings.DANSKEBANK_TRANSLATOR_QUEUE;
            //queueName = StaticStrings.NORDEA_TRANSLATOR_QUEUE;

            MessageObject moToSend = new MessageObject(mo.getCpr(), mo.getLoanAmount(), mo.getLoanDuration(), mo.getCreditScore());
            moToSend.setChosenBank(banks.get(i));
            String queueMessageToBank = gson.toJson(moToSend);

            System.out.println("[RecipientList *send* - Bank] : " + queueMessage);
            publisher.publishMessage(hostAddress, queueName, queueMessageToBank);

            DanskeBankTranslator dbt = null;
            NordeaBankTranslator nbt = null;
            switch (queueName) {
                case StaticStrings.DANSKEBANK_TRANSLATOR_QUEUE:
                    if (dbt == null) {
                        dbt = new DanskeBankTranslator();
                    }
                    dbt.init();
                    break;
                case StaticStrings.NORDEA_TRANSLATOR_QUEUE:
                    if (nbt == null) {
                        nbt = new NordeaBankTranslator();
                    }
                    nbt.init();
                    break;
                case StaticStrings.NYTKREDIT_TRANSLATOR_QUEUE:
                    //other
                    break;
                case StaticStrings.BDO_TRANSLATOR_QUEUE:
                    //other
                    break;
                default:
                    queueName = StaticStrings.DANSKEBANK_TRANSLATOR_QUEUE;
                    break;

            }
        }

        if (!banks.isEmpty()) {
            String queueMessageToLogger = gson.toJson(mo);
            System.out.println("[RecipientList *send* - Logger] : " + queueMessageToLogger);
            publisher.publishMessage(hostAddress, StaticStrings.LOGGER_QUEUE, queueMessageToLogger);
        }

    }

}
