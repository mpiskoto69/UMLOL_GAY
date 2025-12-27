package users;

import java.util.ArrayList;
import accounts.BankAccount;

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
		accounts.add(acc);
	}

}
