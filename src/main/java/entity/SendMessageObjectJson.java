package entity;

public class SendMessageObjectJson {

    private int ssn;
    private int creditScore;
    private float loanAmount;
    private int loanDuration;

    public SendMessageObjectJson(int ssn, int creditScore, float loanAmount, int loanDuration) {
        this.ssn = ssn;
        this.creditScore = creditScore;
        this.loanAmount = loanAmount;
        this.loanDuration = loanDuration;
    }

    @Override
    public String toString() {
        return "MessageObjectJsonBank{" + "ssn=" + ssn + ", creditScore=" + creditScore + ", loanAmount=" + loanAmount + ", loanDuration=" + loanDuration + '}';
    }

}
