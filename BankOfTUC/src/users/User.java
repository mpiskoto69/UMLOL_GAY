package users;

import bank.storage.Storable;

public abstract class User implements Storable {
	protected String username;
	protected String password;
	protected String legalName;

	public User(String username, String password, String legalName) {
		this.username = username;
		this.password = password;
		this.legalName = legalName;
	}

	public String getLegalName() {
		return legalName;
	}

	public String getUsername() {
		return username;
	}

	public boolean login(String inputPassword) {
    return password != null && password.equals(inputPassword);
}


	public String getPassword() {
		return password;
	}
public void setPassword(String password) {
    this.password = password;
}


	public abstract String getRole();
}
