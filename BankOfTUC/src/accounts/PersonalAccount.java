package accounts;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bank.storage.UnMarshalingException;
import managers.TransactionManager;
import managers.UserManager;
import users.Customer;
import users.Individual;

public class PersonalAccount extends BankAccount {
    private ArrayList<Individual> secondaryHolders;

    public PersonalAccount(Individual holder, double interestRate) {
        super(holder, interestRate, "100");
        this.secondaryHolders = new ArrayList<>();
    }

    public PersonalAccount() {
        super();
        this.secondaryHolders = new ArrayList<>();
    }

    public void addSecondaryHolder(Individual vatHolder) {
        secondaryHolders.add(vatHolder);
    }

    public ArrayList<Individual> getSecondaryHolders() {
        return secondaryHolders;
    }

    @Override
    public void endOfMonth() {
        TransactionManager tm = TransactionManager.getInstance();
        tm.eofInterestPayment(this, thisMonthsInterest);
        System.out.print("Interest paid ");
        System.out.printf("%.2f", thisMonthsInterest);
        System.out.println(" euros for " + iban);
        resetMonthsInterest();
    }

    @Override
    public String marshal() {
        StringBuilder sb = new StringBuilder();
        sb.append("type:PersonalAccount")
                .append(",iban:").append(iban)
                .append(",primaryOwner:").append(primaryHolder.getVatNumber())
                .append(",dateCreated:").append(dateCreated)
                .append(",rate:").append(interestRate)
                .append(",balance:").append(balance);
        for (Individual sec : secondaryHolders) {
            sb.append(",coOwner:").append(sec.getVatNumber());
        }
        return sb.toString();
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
                    if (!"PersonalAccount".equals(val))
                        throw new UnMarshalingException("Wrong type: " + val);
                    break;
                case "iban":
                    this.iban = val;
                    break;
                case "primaryOwner":
                    Customer cust = UserManager.getInstance().findCustomerByVat(val);
                    if (!(cust instanceof Individual))
                        throw new UnMarshalingException("No Individual for VAT: " + val);
                    this.primaryHolder = (Individual) cust;
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
                case "coOwner":
                    Customer sec = UserManager.getInstance().findCustomerByVat(val);
                    if (sec instanceof Individual) {
                        this.addSecondaryHolder((Individual) sec);
                        ((Individual) sec).addAccount(this);
                    }
                    break;
                default:
                    // ignore
                    break;
            }
        }
    }

}