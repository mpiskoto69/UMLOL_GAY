package transactions;

import accounts.BankAccount;
import bank.storage.Bill;
import javax.naming.InsufficientResourcesException;
import managers.AccountManager;
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

		// 3) CREDIT statement on the business
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

		System.out.println(
				"Payment of " + amount + "euros by " + transactor.getLegalName() + ", from " + getAccount1().getIban()
						+ ", to " + getAccount2().getIban() + " and reason " + getReason1() + " completed");
	}

}