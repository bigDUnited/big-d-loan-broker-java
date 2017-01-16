package entity;

import java.util.List;

public class MessageObject {

    private String cpr;
    private int loanAmount;
    private int loanDuration;
    private int creditScore;
    private List<String> bankNameList;
    private float interestRate;
    private String chosenBank;

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

    public MessageObject(String cpr, float interestRate) {
        this.cpr = cpr;
        this.interestRate = interestRate;
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

    public String getChosenBank() {
        return chosenBank;
    }

    public void setChosenBank(String chosenBank) {
        this.chosenBank = chosenBank;
    }

    public float getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(float interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public String toString() {
        return "MessageObject{" + "cpr=" + cpr + ", loanAmount=" + loanAmount + ", loanDuration=" + loanDuration + ", creditScore=" + creditScore + ", bankNameList=" + bankNameList + '}';
    }

    public String toStringTwo() {
        return "MessageObject{" + "cpr=" + cpr + ", loanAmount=" + loanAmount + ", loanDuration=" + loanDuration + ", creditScore=" + creditScore + ", bankNameList=" + bankNameList + ", chosenBank=" + chosenBank + ", interestRate=" + interestRate + '}';
    }

}
