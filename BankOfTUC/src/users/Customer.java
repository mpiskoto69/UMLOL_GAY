package users;

import accounts.BankAccount;
import java.util.ArrayList;

public abstract class Customer extends User {

	protected String vatNumber;
	protected ArrayList<BankAccount> accounts;

	public Customer(String username, String password, String vatNumber, String legalName) {
		super(username, password, legalName);
		this.vatNumber = vatNumber;
		this.accounts = new ArrayList<>();
	}

	public String getVatNumber() {
		return vatNumber;
	}

	public ArrayList<BankAccount> getAccounts() {
		return accounts;
	}

	public void addAccount(BankAccount acc) {
		if (!accounts.contains(acc))
			accounts.add(acc);
	}

}
