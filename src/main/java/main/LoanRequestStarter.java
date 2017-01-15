package main;

import com.google.gson.Gson;
import creditScore.GetCreditScoreEnricher;
import entity.MessageObject;
import java.util.Scanner;

public class LoanRequestStarter {

    private Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        new LoanRequestStarter().execute();
    }

    //ssn, loan amount, loan duration
    private void execute() {
        System.out.println("Welcome to the Loan Broker"
                + "\nPlease provide your CPR");
        String cpr = "123456-1234";//scan.nextLine();

        System.out.println("\nPlease provide loan amount (in numbers).");
        int loanAmount = 20000;//scan.nextInt();

        System.out.println("\nPlease provide loan duration (in numbers).");
        int loanDuration = 12;//scan.nextInt();

        MessageObject mo = new MessageObject(cpr, loanAmount, loanDuration);

        Gson gson = new Gson();
        String jsonString = gson.toJson(mo);

        GetCreditScoreEnricher gcse = new GetCreditScoreEnricher();
        gcse.init();
        gcse.logic(jsonString);
    }

}
