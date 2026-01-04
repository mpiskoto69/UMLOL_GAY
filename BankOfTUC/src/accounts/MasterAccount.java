package accounts;

import bank.storage.UnMarshalingException;
import java.time.LocalDate;
import managers.UserManager;
import users.Company;
import users.Customer;

public class MasterAccount extends BusinessAccount {
    private static final MasterAccount instance = new MasterAccount();

    private MasterAccount() {
        super();
    }

    public static MasterAccount getInstance() {
        return instance;
    }

    public void initIfNeeded(Company bankCompany) {
        if (this.primaryHolder != null)
            return;

        this.primaryHolder = bankCompany;
        this.interestRate = 0.0;
        this.dateCreated = LocalDate.now();
        if (this.iban == null || this.iban.isBlank()) {
            this.iban = "GR200" + System.currentTimeMillis();
        }
        this.balance = 10000.0;

        bankCompany.addAccount(this);
    }

    @Override
    public void endOfMonth() {
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
                    if (cust == null) {
                        throw new UnMarshalingException("No customer found for MasterAccount primaryOwner: " + val);
                    }
                    if (!(cust instanceof Company)) {
                        throw new UnMarshalingException(
                                "MasterAccount primaryOwner must be Company, got: " + cust.getClass().getSimpleName());
                    }
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
                    break;
            }
        }
    }
}
