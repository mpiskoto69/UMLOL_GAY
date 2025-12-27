package transactions;

import javax.naming.InsufficientResourcesException;

import accounts.BankAccount;
import managers.AccountManager;
import standingOrders.Bill;
import users.Customer;

public class Payment extends Transaction {
	private double amount;

	public Payment(Customer transactor, BankAccount fromAccount, BankAccount businessAccount, String reasonFrom,
			String reasonTo, double amount) {
		super(transactor, fromAccount, businessAccount, reasonFrom, reasonTo);
		this.amount = amount;
	}

	public Payment(Customer transactor, BankAccount fromAccount, Bill bill, String reasonFrom, String reasonTo,
			double ammount) {
		super(transactor, fromAccount, AccountManager.getInstance().findByIban(bill.getRecipientCustomerId()),
				reasonFrom, reasonTo);
		this.amount = ammount;
	}

	@Override
	public void execute() throws IllegalArgumentException, IllegalAccessException, InsufficientResourcesException {
		if (!AccountManager.getInstance().hasAccessToAccount(transactor, account1))
			throw new IllegalAccessException("You don't have access to this account:!");

		// 1) Move the money
		getAccount1().debit(amount);
		getAccount2().credit(amount);

		// 2) DEBIT statement on the payer
		AccountStatement debitStmt = new AccountStatement(getId(), getTransactor().getUsername(),
				getAccount1().getIban(), // payer’s IBAN
				getAccount2().getIban(), // business’s IBAN
				getReason1(), amount, getAccount1().getBalance(), AccountStatement.MovementType.DEBIT);
		getAccount1().addStatement(debitStmt);

		// 3) CREDIT statement on the business
		AccountStatement creditStmt = new AccountStatement(getId(), getTransactor().getUsername(),
				getAccount2().getIban(), // business’s IBAN
				getAccount1().getIban(), // payer’s IBAN
				getReason2(), amount, getAccount2().getBalance(), AccountStatement.MovementType.CREDIT);
		getAccount2().addStatement(creditStmt);

		System.out.println(
				"Payment of " + amount + "euros by " + transactor.getLegalName() + ", from " + getAccount1().getIban()
						+ ", to " + getAccount2().getIban() + " and reason " + getReason1() + " completed");
	}

}