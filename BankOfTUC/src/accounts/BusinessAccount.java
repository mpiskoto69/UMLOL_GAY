package accounts;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import bank.storage.UnMarshalingException;
import managers.TransactionManager;
import managers.UserManager;
import users.Company;
import users.Customer;

public class BusinessAccount extends BankAccount {
    protected static final double MAINTENANCE_FEE = 10.00;
    protected LocalDate dateCreated;

    public BusinessAccount(Company holder,
            double interestRate,
            LocalDate dateCreated) {
        super(holder, interestRate, "200");
        this.dateCreated = dateCreated;
    }

    public BusinessAccount() {
        super();
    }

    @Override
    public void endOfMonth() {
        // Interest
        TransactionManager tm = TransactionManager.getInstance();
        tm.eofInterestPayment(this, thisMonthsInterest);
        System.out.print("Interest paid ");
        System.out.printf("%.2f", thisMonthsInterest);
        System.out.println(" euros for " + iban);
        resetMonthsInterest();

        // Maintenace Fee
        tm.chargeMaintenanceFee(this);
        System.out.print("Maintenance fee paid ");
        System.out.printf("%.2f", MAINTENANCE_FEE);
        System.out.println(" euros from " + iban);
    }

    public static double getMaintenaceFee() {
        return MAINTENANCE_FEE;
    }

    @Override
    public String marshal() {
        return String.join(",",
                "type:BusinessAccount",
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
                    if (!"BusinessAccount".equals(val))
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