package transactions;

import javax.naming.InsufficientResourcesException;

import accounts.BankAccount;
import accounts.MasterAccount;
import managers.AccountManager;
import users.Customer;

public class Withdrawall extends Transaction {
    private final double amount;

 public Withdrawall(Customer transactor, BankAccount account, String reason, double amount) {
    super(transactor, account, MasterAccount.getInstance(), reason, "Cash withdrawal");
    this.amount = amount;
}


  @Override
public void execute() throws IllegalAccessException, InsufficientResourcesException, IllegalArgumentException {

    if (!AccountManager.getInstance().hasAccessToAccount(transactor, account1))
        throw new IllegalAccessException("You don't have access to this account!");

    // 1) Move money: customer -> bank
    getAccount1().debit(amount);
    getAccount2().credit(amount);

  // 2) DEBIT statement on customer
AccountStatement debitStmt = AccountStatement.builder()
    .transactionId(getId())
    .transactor(getTransactor().getUsername())
    .account(getAccount1().getIban())
    .counterparty(getAccount2().getIban())
    .reason(getReason1())
    .amount(amount)
    .balanceAfter(getAccount1().getBalance())
    .type(AccountStatement.MovementType.DEBIT)
    .build();
getAccount1().addStatement(debitStmt);

// 3) CREDIT statement on bank
AccountStatement creditStmt = AccountStatement.builder()
    .transactionId(getId())
    .transactor(getTransactor().getUsername())
    .account(getAccount2().getIban())
    .counterparty(getAccount1().getIban())
    .reason(getReason2())
    .amount(amount)
    .balanceAfter(getAccount2().getBalance())
    .type(AccountStatement.MovementType.CREDIT)
    .build();
getAccount2().addStatement(creditStmt);
}
}
