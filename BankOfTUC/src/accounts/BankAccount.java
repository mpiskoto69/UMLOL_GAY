package accounts;

import bank.storage.Storable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;
import javax.naming.InsufficientResourcesException;
import managers.AccountManager;
import transactions.AccountStatement;
import users.Customer;

public abstract class BankAccount implements Storable {
    protected String iban;
    protected double balance;
    protected double interestRate;
    protected Customer primaryHolder;
    protected double thisMonthsInterest;
    private ArrayList<AccountStatement> statements = new ArrayList<>();
    protected LocalDate dateCreated;

    public BankAccount(Customer holder, double interestRate, String accountTypeCode) {
        this.primaryHolder = holder;
        this.thisMonthsInterest = 0;
        this.interestRate = interestRate;
        this.balance = 0;
        this.iban = generateIban(accountTypeCode);
        this.dateCreated = LocalDate.now();

        holder.addAccount(this);
    }

    public BankAccount() {

    }

    private static final Random RANDOM = new Random();

    private String generateIban(String typeCode) {
        String countryCode = "GR";
        String uniquePart;

        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 15; i++) {
                sb.append(RANDOM.nextInt(10));
            }
            uniquePart = sb.toString();
        } while (AccountManager.getInstance().existsIban(uniquePart));

        AccountManager.getInstance().addIban(uniquePart);

        return countryCode + typeCode + uniquePart;
    }

    public String getIban() {
        return iban;
    }

    public double getBalance() {
        return balance;
    }

    public Customer getPrimaryHolder() {
        return primaryHolder;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void applyDailyInterest() {
        double dailyRate = interestRate / 365.0;
        thisMonthsInterest += balance * dailyRate;
    }

    public void resetMonthsInterest() {
        thisMonthsInterest = 0;
    }

    public double getThisMonthsInterest() {
        return thisMonthsInterest;
    }

    public void changeBalance(double amount) {
        this.balance += amount;
    }

    public void credit(double amount) throws IllegalArgumentException {
        if (amount < 0)
            throw new IllegalArgumentException("Cannot credit negative amount");
        balance += amount;
    }

    public void debit(double amount) throws IllegalArgumentException, InsufficientResourcesException {
        if (amount < 0)
            throw new IllegalArgumentException("Cannot debit negative amount");
        if (balance < amount)
            throw new InsufficientResourcesException("Insufficient funds");
        balance -= amount;
    }

    public void addStatement(AccountStatement statement) {
        statements.add(0, statement);
    }

    public ArrayList<AccountStatement> getStatements() {
        return statements;
    }

    public abstract void endOfMonth();
}
