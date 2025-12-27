package transactions;

import accounts.BankAccount;
import managers.AccountManager;
import transactions.AccountStatement.MovementType;
import users.Customer;

public class Deposit extends Transaction {
    private double amount;

    public Deposit(Customer transactor, BankAccount account, String reason, double amount) {
        super(transactor, account, null, reason, null);
        this.amount = amount;
    }

    @Override
    public void execute() throws IllegalAccessException, IllegalArgumentException {

        if (!AccountManager.getInstance().hasAccessToAccount(transactor, account1))
            throw new IllegalAccessException("You don't have acces to this account!");

        getAccount1().credit(amount);

        AccountStatement custStmt = new AccountStatement(
                getId(),
                getTransactor().getUsername(),
                getAccount1().getIban(), // this account
                null, // no counterparty
                getReason1(),
                amount,
                getAccount1().getBalance(),
                MovementType.CREDIT);
        getAccount1().addStatement(custStmt);
    }

}