package entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LoanRequest")
public class SendMessageObjectXML {

    private int ssn;
    private int creditScore;
    private float loanAmount;
    private String loanDuration;

    public SendMessageObjectXML() {
    }

    public int getSsn() {
        return ssn;
    }

    @XmlElement
    public void setSsn(int ssn) {
        this.ssn = ssn;
    }

    public int getCreditScore() {
        return creditScore;
    }

    @XmlElement
    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public float getLoanAmount() {
        return loanAmount;
    }

    @XmlElement
    public void setLoanAmount(float loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getLoanDuration() {
        return loanDuration;
    }

    @XmlElement
    public void setLoanDuration(String loanDuration) {
        this.loanDuration = loanDuration;
    }

}
