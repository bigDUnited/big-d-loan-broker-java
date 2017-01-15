package entity;

import java.util.List;

public class MessageObject {

    private String cpr;
    private int loanAmount;
    private int loanDuration;
    private int creditScore;
    private List<String> bankNameList;

    public MessageObject(String cpr, int loanAmount, int loanDuration) {
        this.cpr = cpr;
        this.loanAmount = loanAmount;
        this.loanDuration = loanDuration;
    }

    public MessageObject(String cpr, int loanAmount, int loanDuration, int creditScore) {
        this.cpr = cpr;
        this.loanAmount = loanAmount;
        this.loanDuration = loanDuration;
        this.creditScore = creditScore;
    }

    public MessageObject(String cpr, int loanAmount, int loanDuration, int creditScore, List<String> bankNameList) {
        this.cpr = cpr;
        this.loanAmount = loanAmount;
        this.loanDuration = loanDuration;
        this.creditScore = creditScore;
        this.bankNameList = bankNameList;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public String getCpr() {
        return cpr;
    }

    public int getLoanAmount() {
        return loanAmount;
    }

    public int getLoanDuration() {
        return loanDuration;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public List<String> getBankNameList() {
        return bankNameList;
    }

    @Override
    public String toString() {
        return "MessageObject{" + "cpr=" + cpr + ", loanAmount=" + loanAmount + ", loanDuration=" + loanDuration + ", creditScore=" + creditScore + '}';
    }

}
