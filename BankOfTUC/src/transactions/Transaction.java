package transactions;

import accounts.BankAccount;
import users.Customer;

import java.util.ArrayList;
import java.util.Random;

import javax.naming.InsufficientResourcesException;

public abstract class Transaction {
	protected String id;
	protected Customer transactor;
	protected BankAccount account1;
	protected BankAccount account2;
	protected String reason1;
	protected String reason2;
	private static ArrayList<String> usedIds = new ArrayList<>();
	private static Random random = new Random();

	// CONSTRUCTOR
	public Transaction(Customer transactor, BankAccount account1, BankAccount account2, String reason1,
			String reason2) {
		this.id = generateId(); // αν έχεις μέθοδο για ID
		this.transactor = transactor;
		this.account1 = account1;
		this.account2 = account2;
		this.reason1 = reason1;
		this.reason2 = reason2;
	}

	protected String getId() {
		return id;
	}

	public static String generateId() {
		for (int attempt = 0; attempt < 1000; attempt++) { // αποφυγή άπειρου loop
			int length = random.nextInt(20) + 1;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < length; i++) {
				sb.append(random.nextInt(10)); // 0-9
			}
			String id = sb.toString();
			if (!usedIds.contains(id)) {
				usedIds.add(id);
				return id;
			}
		}
		throw new RuntimeException("Failed to generate unique Transaction ID after many attempts");
	}

	protected Customer getTransactor() {
		return transactor;
	}

	public BankAccount getAccount1() {
		return account1;
	}

	public BankAccount getAccount2() {
		return account2;
	}

	protected String getReason1() {
		return reason1;
	}

	protected String getReason2() {
		return reason2;
	}

	public abstract void execute() throws IllegalAccessException, InsufficientResourcesException;

}
