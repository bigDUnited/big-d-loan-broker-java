package entity;

public class ReturnMessageObjectJson {

    private String ssn;
    private float interestRate;

    public ReturnMessageObjectJson(String ssn, float interestRate) {
        this.ssn = ssn;
        this.interestRate = interestRate;
    }

    public float getInterestRate() {
        return interestRate;
    }

    public String getSsn() {
        return ssn;
    }

    @Override
    public String toString() {
        return "ReturnMessageObject{" + "ssn=" + ssn + ", interestRate=" + interestRate + '}';
    }
}
