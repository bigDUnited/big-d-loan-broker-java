package translators;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.sun.xml.internal.ws.util.StringUtils;
import config.StaticStrings;
import entity.MessageObject;
import entity.SendMessageObjectXML;
import getBanks.GetBanksEnricher;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import normalizer.Normalizer;
import structure.ComponentInterface;

public class NordeaBankTranslator implements ComponentInterface {

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

            channel.queueDeclare(StaticStrings.NORDEA_TRANSLATOR_QUEUE, false, false, false, null);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                        AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("[NordeaBankTranslator *received*] : " + message);
                    logic(message);
                }
            };
            channel.basicConsume(StaticStrings.NORDEA_TRANSLATOR_QUEUE, true, consumer);

        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(GetBanksEnricher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            java.util.logging.Logger.getLogger(GetBanksEnricher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void logic(String queueMessage) {
        System.out.println("Nordea accepted : " + queueMessage);

        MessageObject mo = gson.fromJson(queueMessage, MessageObject.class);
        String preCpr = mo.getCpr().substring(0, 9);
        int ssn = Integer.parseInt(preCpr.replaceAll("[^\\d.]", ""));

        SendMessageObjectXML smoXML = new SendMessageObjectXML();
        smoXML.setSsn(ssn);
        smoXML.setCreditScore(mo.getCreditScore());
        smoXML.setLoanAmount(mo.getLoanAmount());

        int loanDuration = mo.getLoanDuration();
        String xmlLoanDuration = "";
        if (loanDuration < 12) {
            xmlLoanDuration = "1971-01-01 01:00:00.0 CET";
        } else if (loanDuration < 24) {
            xmlLoanDuration = "1972-01-01 01:00:00.0 CET";
        } else {
            xmlLoanDuration = "1973-01-01 01:00:00.0 CET";
        }

        smoXML.setLoanDuration(xmlLoanDuration);

        String xmlString = "";

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(SendMessageObjectXML.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(smoXML, System.out);

//            JAXBContext context = JAXBContext.newInstance(SendMessageObjectXML.class);
//            Marshaller m = context.createMarshaller();
//            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//
//            StringWriter sw = new StringWriter();
//            m.marshal(jaxbMarshaller, sw);
//
//            String result = sw.toString();
//            System.out.println("result is : " + result);

        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }

    private void sendToQueue(MessageObject mo, String xmlString) {
        System.out.println("Hello worldasdhjasdaskdhasjkasaaskj");
        try {
            System.out.println("Hello??");
            String EXCHANGE_NAME = "cphbusiness.bankXML";

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("datdb.cphbusiness.dk");

            Connection connection = factory.newConnection();

            Channel channel = connection.createChannel();

            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
            builder.replyTo(StaticStrings.NORMALIZER_QUEUE);
            builder.contentType("XML");
            builder.appId(mo.getChosenBank());

            channel.basicPublish(EXCHANGE_NAME, "", builder.build(), xmlString.getBytes());
            System.out.println("[NordeaBankTranslator *sent*] : " + xmlString);
            normalizer.init();

            channel.close();
            connection.close();

        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(NordeaBankTranslator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            java.util.logging.Logger.getLogger(NordeaBankTranslator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
