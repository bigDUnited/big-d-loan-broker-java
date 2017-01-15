package entity;

public class MessageObject {

    private String cpr;
    private int loanAmount;
    private int loanDuration;
    private int creditScore;

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

    @Override
    public String toString() {
        return "MessageObject{" + "cpr=" + cpr + ", loanAmount=" + loanAmount + ", loanDuration=" + loanDuration + ", creditScore=" + creditScore + '}';
    }

}
