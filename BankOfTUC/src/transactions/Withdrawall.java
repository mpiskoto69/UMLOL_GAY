package transactions;

import javax.naming.InsufficientResourcesException;

import accounts.BankAccount;
import accounts.MasterAccount;
import managers.AccountManager;
import users.Customer;

public class Withdrawall extends Transaction {
	private double amount;

	public Withdrawall(Customer transactor, BankAccount account, String reason, double amount) {
		super(transactor, account, null, reason, null);
		this.amount = amount;
	}

	@Override
	public void execute() throws IllegalAccessException, InsufficientResourcesException, IllegalArgumentException {

		if (!AccountManager.getInstance().hasAccessToAccount(transactor, account1))
			throw new IllegalAccessException("You don't have access to this account!");

		getAccount1().debit(amount);
		MasterAccount.getInstance().credit(amount);
		AccountStatement statement = new AccountStatement(getId(), getTransactor().getUsername(), // όνομα εκτελεστή
				getAccount1().getIban(), // IBAN λογαριασμού
				null, // κανένας άλλος λογαριασμός
				getReason1(), // αιτιολογία
				amount, getAccount1().getBalance(), // νέο υπόλοιπο
				AccountStatement.MovementType.DEBIT // τύπος κίνησης
		);

		getAccount1().addStatement(statement);
	}

}
