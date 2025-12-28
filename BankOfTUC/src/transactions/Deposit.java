package transactions;
import javax.naming.InsufficientResourcesException;

import accounts.BankAccount;
import accounts.MasterAccount;
import managers.AccountManager;
import transactions.AccountStatement.MovementType;
import users.Customer;

public class Deposit extends Transaction {
    private final double amount;

    public Deposit(Customer transactor, BankAccount customerAccount, String reason, double amount) {
        // account1 = customer, account2 = bank (counterparty)
        super(transactor, customerAccount, MasterAccount.getInstance(), reason, "Cash deposit");
        this.amount = amount;
    }

    @Override
public void execute()
    throws IllegalAccessException,
           IllegalArgumentException,
           InsufficientResourcesException {

    if (!AccountManager.getInstance().hasAccessToAccount(transactor, account1))
        throw new IllegalAccessException("You don't have access to this account!");

    // 1) Move money: bank -> customer
    getAccount2().debit(amount);   // bank pays out
    getAccount1().credit(amount);  // customer receives

    // 2) CREDIT statement on customer
    AccountStatement custStmt = new AccountStatement(
        getId(),
        getTransactor().getUsername(),
        getAccount1().getIban(),
        getAccount2().getIban(),
        getReason1(),
        amount,
        getAccount1().getBalance(),
        AccountStatement.MovementType.CREDIT
    );
    getAccount1().addStatement(custStmt);

    // 3) DEBIT statement on bank
    AccountStatement bankStmt = new AccountStatement(
        getId(),
        getTransactor().getUsername(),
        getAccount2().getIban(),
        getAccount1().getIban(),
        getReason2(),
        amount,
        getAccount2().getBalance(),
        AccountStatement.MovementType.DEBIT
    );
    getAccount2().addStatement(bankStmt);
}
}