package creditScore;

import com.google.gson.Gson;
import config.StaticStrings;
import core.Publisher;
import entity.MessageObject;
import getBanks.GetBanksEnricher;
import structure.ComponentInterface;

public class GetCreditScoreEnricher implements ComponentInterface {

    private Publisher publisher;
    private GetCreditScoreChannelAdapter gcsca;
    private GetBanksEnricher gbe;
    private Boolean isInit = false;
    private Gson gson;

    @Override
    public void init() {
        publisher = new Publisher();
        gcsca = new GetCreditScoreChannelAdapter();
        gbe = new GetBanksEnricher();
        gson = new Gson();
        isInit = true;
    }

    @Override
    public void logic(String queueMessage) {
        if (isInit) {
            System.out.println(" [GetCreditScoreEnricher *received*] : " + queueMessage);
            
            MessageObject mo = gson.fromJson(queueMessage, MessageObject.class);

            mo.setCreditScore(gcsca.getCreditScore(mo.getCpr()));
            mo = new MessageObject(mo.getCpr(), mo.getLoanAmount(), mo.getLoanDuration(), gcsca.getCreditScore(mo.getCpr()));

            queueMessage = gson.toJson(mo);
            String hostAddress = StaticStrings.HOST_ADDRESS;
            String queueName = StaticStrings.GET_BANKS_QUEUE_NAME;
            
            System.out.println("[GetCreditScoreEnricher *send*] : " + queueMessage);
            publisher.publishMessage(hostAddress, queueName, queueMessage);
            
            gbe.init();
        }
    }
}
