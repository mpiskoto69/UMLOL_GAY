package users;

import bank.storage.UnMarshalingException;

public class Admin extends User {

    public Admin(String username, String password, String legalName) {
        super(username, password, legalName);
    }

    public Admin() {
        super(null, null, null);
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    @Override
    public String marshal() {
        return String.join(",",
                "type:Admin",
                "legalName:" + legalName,
                "userName:" + username,
                "password:" + password);
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
                    if (!"Admin".equals(kv[1]))
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
                default:
                    break;
            }
        }
    }

}
