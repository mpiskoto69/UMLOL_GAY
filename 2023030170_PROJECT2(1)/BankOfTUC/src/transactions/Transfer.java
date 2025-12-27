package transactions;

import javax.naming.InsufficientResourcesException;

import accounts.BankAccount;
import managers.AccountManager;
import transactions.AccountStatement.MovementType;
import users.Customer;

public class Transfer extends Transaction {
	private double amount;

	/** Full constructor (separate from/to reasons) */
	public Transfer(Customer transactor, BankAccount fromAccount, BankAccount toAccount, String reasonFrom,
			String reasonTo,
			double amount) {
		super(transactor, fromAccount, toAccount, reasonFrom, reasonTo);
		this.amount = amount;
	}

	/** Shortcut when same reason for both sides */
	public Transfer(Customer transactor, BankAccount fromAccount, BankAccount toAccount, double amount, String reason) {
		this(transactor, fromAccount, toAccount, reason, reason, amount);
	}

	@Override
	public void execute() throws IllegalArgumentException, IllegalAccessException, InsufficientResourcesException {

		if (!AccountManager.getInstance().hasAccessToAccount(transactor, account1))
			throw new IllegalAccessException("You don't have access to this account!");

		// 1) Move money
		getAccount1().debit(amount);
		getAccount2().credit(amount);

		// 2) Record DEBIT on sender
		AccountStatement debitStmt = new AccountStatement(getId(), getTransactor().getUsername(),
				getAccount1().getIban(), getAccount2().getIban(), getReason1(), amount, getAccount1().getBalance(),
				MovementType.DEBIT);
		getAccount1().addStatement(debitStmt);

		// 3) Record CREDIT on receiver
		AccountStatement creditStmt = new AccountStatement(getId(), getTransactor().getUsername(),
				getAccount2().getIban(), getAccount1().getIban(), getReason2(), amount, getAccount2().getBalance(),
				MovementType.CREDIT);
		getAccount2().addStatement(creditStmt);

		System.out.println(
				"Transfer of " + amount + "euros by " + transactor.getLegalName() + ", from " + getAccount1().getIban()
						+ ", to " + getAccount2().getIban() + " and reason " + getReason1() + " completed");

	}
}