package transactions;

import javax.naming.InsufficientResourcesException;
import java.util.UUID;

import accounts.BankAccount;
import users.Customer;

/**
 * Abstract base class for all banking transactions (Transfer, Payment, Deposit, Withdrawal).
 * Acts as a Command in the Command pattern.
 */
public abstract class Transaction {

    protected final String id;
    protected final Customer transactor;
    protected final BankAccount account1; // source / primary
    protected final BankAccount account2; // target / counterparty (can be MasterAccount)
    protected final String reason1;        // reason for account1
    protected final String reason2;        // reason for account2

    /**
     * Constructor used when loading from persistent storage (stable ID).
     */
    protected Transaction(String id,
                          Customer transactor,
                          BankAccount account1,
                          BankAccount account2,
                          String reason1,
                          String reason2) {

        this.id = id;
        this.transactor = transactor;
        this.account1 = account1;
        this.account2 = account2;
        this.reason1 = reason1;
        this.reason2 = reason2;
    }

    /**
     * Constructor used for runtime creation of new transactions.
     */
    protected Transaction(Customer transactor,
                          BankAccount account1,
                          BankAccount account2,
                          String reason1,
                          String reason2) {

        this(generateId(), transactor, account1, account2, reason1, reason2);
    }

    // ---------------- getters ----------------

    public String getId() {
        return id;
    }

    public Customer getTransactor() {
        return transactor;
    }

    public BankAccount getAccount1() {
        return account1;
    }

    public BankAccount getAccount2() {
        return account2;
    }

    public String getReason1() {
        return reason1;
    }

    public String getReason2() {
        return reason2;
    }

    // ---------------- helpers ----------------

    protected static String generateId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Executes the transaction.
     * All balance changes and statement creation MUST happen here.
     */
    public abstract void execute()
            throws IllegalAccessException, InsufficientResourcesException;
}
