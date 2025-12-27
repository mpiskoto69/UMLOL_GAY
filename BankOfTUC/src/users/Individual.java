package users;

import bank.storage.UnMarshalingException;

public class Individual extends Customer {

	public Individual(String vat, String legaName, String username, String password) {
		super(username, password, vat, legaName);
	}

	public Individual() {
		super(null, null, null, null);
	}

	@Override
	public String getRole() {
		return "Individual";
	}

	@Override
	public String marshal() {
		// type,legalName,userName,password,vatNumber
		return String.join(",",
				"type:Individual",
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
					if (!"Individual".equals(kv[1]))
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
