package creditScore;

import org.bank.credit.web.service.CreditScoreService;
import org.bank.credit.web.service.CreditScoreService_Service;

public class GetCreditScoreChannelAdapter {

    public int getCreditScore(String cpr) {
        CreditScoreService_Service service = new CreditScoreService_Service();
        CreditScoreService port = service.getCreditScoreServicePort();
        return port.creditScore(cpr);
    }

}
