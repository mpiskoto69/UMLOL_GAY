package users;

import accounts.BankAccount;
import bank.storage.UnMarshalingException;

public class Company extends Customer {

	public Company(String username, String password, String vatNumber, String legalName) {
		super(username, password, vatNumber, legalName);
	}

	public Company() {
		super(null, null, null, null);
	}

	@Override
	public String getRole() {
		return "Company";
	}

	@Override
	public void addAccount(BankAccount acc) {
		if (accounts.isEmpty()) {
			accounts.add(acc);
		} else {
			System.out.println("the companies are allowed to have only one account");
		}
	}

	@Override
	public String marshal() {
		return String.join(",",
				"type:Company",
				"legalName:" + legalName,
				"userName:" + username,
				"password:" + password,
				"vatNumber:" + vatNumber);
	}

	@Override
	public void unmarshal(String data) throws UnMarshalingException {
		String[] parts = data.split(",");
		for (String p : parts) {
			String[] kv = p.split(":", 2);
			if (kv.length != 2)
				throw new UnMarshalingException("Bad field: " + p);
			switch (kv[0]) {
				case "type":
					if (!"Company".equals(kv[1]))
						throw new UnMarshalingException("Wrong type: " + kv[1]);
					break;
				case "legalName":
					this.legalName = kv[1];
					break;
				case "userName":
					this.username = kv[1];
					break;
				case "password":
					this.password = kv[1];
					break;
				case "vatNumber":
					this.vatNumber = kv[1];
					break;
				default:
					break;
			}
		}
	}

}
