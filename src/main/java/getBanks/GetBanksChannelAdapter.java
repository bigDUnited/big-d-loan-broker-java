package getBanks;

import entity.Bank;
import entity.RuleBaseResponse;
import getBanks.soap.RuleBaseImplementationService;
import getBanks.soap.RuleBaseInterface;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class GetBanksChannelAdapter {

    public List<Bank> getBanks(int creditScore) {
        try {
            RuleBaseImplementationService helloService = new RuleBaseImplementationService();
            RuleBaseInterface rbi = (RuleBaseInterface) helloService.getRuleBaseImplementationPort();
            String xmlString = rbi.getBanksByCreditScoreJson(creditScore);

            JAXBContext jaxbContext = JAXBContext.newInstance(RuleBaseResponse.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            StringReader reader = new StringReader(xmlString);
            RuleBaseResponse rbs = (RuleBaseResponse) unmarshaller.unmarshal(reader);
            return rbs.getBankElem();
        } catch (JAXBException ex) {
            Logger.getLogger(GetBanksChannelAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
