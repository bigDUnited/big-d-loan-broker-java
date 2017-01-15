package entity;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RuleBaseList")
public class RuleBaseResponse {

    private ArrayList<Bank> bankElem;

    public RuleBaseResponse() {
    }

    public RuleBaseResponse(ArrayList<Bank> bankElem) {
        this.bankElem = bankElem;
    }

    public ArrayList<Bank> getBankElem() {
        return bankElem;
    }

    public void setBankElem(ArrayList<Bank> bankElem) {
        this.bankElem = bankElem;
    }
}
