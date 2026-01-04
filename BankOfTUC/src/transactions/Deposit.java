package transactions;

import accounts.BankAccount;
import accounts.MasterAccount;
import javax.naming.InsufficientResourcesException;
import managers.AccountManager;
import users.Customer;

public class Deposit extends Transaction {
    private final double amount;

    public Deposit(Customer transactor, BankAccount customerAccount, String reason, double amount) {
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
        getAccount2().debit(amount); // bank pays out
        getAccount1().credit(amount); // customer receives
        // 2) CREDIT statement on customer
        AccountStatement custStmt = AccountStatement.builder()
                .transactionId(getId())
                .transactor(getTransactor().getUsername())
                .account(getAccount1().getIban())
                .counterparty(getAccount2().getIban())
                .reason(getReason1())
                .amount(amount)
                .balanceAfter(getAccount1().getBalance())
                .type(AccountStatement.MovementType.CREDIT)
                .build();
        getAccount1().addStatement(custStmt);

        // 3) DEBIT statement on bank
        AccountStatement bankStmt = AccountStatement.builder()
                .transactionId(getId())
                .transactor(getTransactor().getUsername())
                .account(getAccount2().getIban())
                .counterparty(getAccount1().getIban())
                .reason(getReason2())
                .amount(amount)
                .balanceAfter(getAccount2().getBalance())
                .type(AccountStatement.MovementType.DEBIT)
                .build();
        getAccount2().addStatement(bankStmt);
    }
}