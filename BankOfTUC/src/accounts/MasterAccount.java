package accounts;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import bank.storage.UnMarshalingException;
import managers.AccountManager;
import managers.UserManager;
import users.Company;
import users.Customer;

public class MasterAccount extends BusinessAccount {
    private static MasterAccount instance = new MasterAccount(new Company("bank", "bank", "bank", "bank"));

    private MasterAccount(Company bank) {
        super(bank, 0, LocalDate.now());
        UserManager.getInstance().addUser(bank);
        AccountManager.getInstance().addAccount(this);
        changeBalance(10000);
    }

    public static MasterAccount getInstance() {
        return instance;
    }

    @Override
    public void endOfMonth() {
        // Bank has no fees and no interest
    }

    @Override
    public String marshal() {
        return String.join(",",
                "type:MasterAccount",
                "iban:" + iban,
                "primaryOwner:" + primaryHolder.getVatNumber(),
                "dateCreated:" + dateCreated,
                "rate:" + interestRate,
                "balance:" + balance);
    }

    // @Override
    // public void unmarshal(String data) throws UnMarshalingException {
    // String[] parts = data.split(",");
    // if (parts.length != 6 || !parts[0].equals("type:MasterAccount"))
    // throw new UnMarshalingException("Bad BusinessAccount line: " + data);

    // Map<String, String> map = new LinkedHashMap<>();
    // for (String p : parts) {
    // String[] kv = p.split(":", 2);
    // if (kv.length != 2)
    // throw new UnMarshalingException("Bad field: " + p);
    // map.put(kv[0], kv[1]);
    // }

    // // lookup primary holder
    // String vat = map.get("primaryOwner");
    // Customer cust = UserManager.getInstance().findCustomerByVat(vat);
    // if (!(cust instanceof Company))
    // throw new UnMarshalingException("No Company for VAT " + vat);
    // this.primaryHolder = (Company) cust;
    // this.primaryHolder.addAccount(this);

    // // set rest
    // this.iban = map.get("iban");
    // this.dateCreated = LocalDate.parse(map.get("dateCreated"));
    // this.interestRate = Double.parseDouble(map.get("rate"));
    // this.balance = Double.parseDouble(map.get("balance"));
    // }

    @Override
    public void unmarshal(String data) throws UnMarshalingException {
        String[] parts = data.split(",");
        for (String p : parts) {
            String[] kv = p.split(":", 2);
            if (kv.length != 2)
                throw new UnMarshalingException("Bad field: " + p);
            String key = kv[0], val = kv[1];
            switch (key) {
                case "type":
                    if (!"MasterAccount".equals(val))
                        throw new UnMarshalingException("Wrong type: " + val);
                    break;
                case "iban":
                    this.iban = val;
                    break;
                case "primaryOwner":
                    Customer cust = UserManager.getInstance().findCustomerByVat(val);
                    if (!(cust instanceof Company))
                        throw new UnMarshalingException("No Company for VAT: " + val);
                    this.primaryHolder = (Company) cust;
                    this.primaryHolder.addAccount(this);
                    break;
                case "dateCreated":
                    this.dateCreated = LocalDate.parse(val);
                    break;
                case "rate":
                    this.interestRate = Double.parseDouble(val);
                    break;
                case "balance":
                    this.balance = Double.parseDouble(val);
                    break;
                default:
                    // ignore any unexpected key
                    break;
            }
        }
    }

}
