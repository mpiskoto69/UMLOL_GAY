package managers;

import java.time.LocalDate;
import java.util.ArrayList;

import accounts.BankAccount;
import accounts.MasterAccount;
import accounts.BusinessAccount;
import accounts.PersonalAccount;
import bank.storage.StorableList;
import users.Company;
import users.Customer;
import users.Individual;

public class AccountManager {
    private static final AccountManager instance = new AccountManager();
    private StorableList<BankAccount> accounts;
    private ArrayList<String> ibanList;

    private AccountManager() {
        accounts = new StorableList<>();
        ibanList = new ArrayList<>();
    }

    public static AccountManager getInstance() {
        return instance;
    }

    public void addAccount(BankAccount account) {
        accounts.add(account);
    }

    public StorableList<BankAccount> getAllAccounts() {
        return accounts;
    }

    public void addCoHolder(Individual co, PersonalAccount acc) {
        acc.addSecondaryHolder(co);
        co.addAccount(acc);
    }

    public BankAccount findByIban(String iban) {
    for (BankAccount acc : accounts) {
        if (acc != null && iban != null && iban.equals(acc.getIban())) {
            return acc;
        }
    }
    return null;
}

    public BankAccount getPrimaryAccountOfUser(String userId) {
        for (BankAccount acc : accounts) {
            if (acc.getPrimaryHolder().getVatNumber().equals(userId)) {
                return acc;
            }
        }
        return null;
    }


    public ArrayList<BankAccount> getAllAccountsOfUser(String userId) {
        ArrayList<BankAccount> accountsList = new ArrayList<>();
        for (BankAccount acc : accounts) {
            // 1) primary holder match?
            if (acc.getPrimaryHolder().getVatNumber().equals(userId)) {
                accountsList.add(acc);
                continue;
            }

            // 2) if it's a PersonalAccount, check secondary holders
            if (acc instanceof PersonalAccount) {
                PersonalAccount pAcc = (PersonalAccount) acc;
                for (Individual sec : pAcc.getSecondaryHolders()) {
                    if (sec.getVatNumber().equals(userId)) {
                        accountsList.add(acc);
                        break; // no need to check more secondaries for this account
                    }
                }
            }
        }
        return accountsList;
    }

    public void applyDailyInterestToAllAccounts() {
        for (BankAccount acct : accounts) {
            acct.applyDailyInterest();
        }
    }

    public boolean hasAccessToAccount(Customer customer, BankAccount account) {

        if (customer.equals(MasterAccount.getInstance().getPrimaryHolder()))
            return true;

        for (BankAccount a : customer.getAccounts()) {
            if (a.equals(account))
                return true;
        }

        return false;

    }

    public void addIban(String iban) throws IllegalArgumentException {
        if (iban.length() != 15)
            throw new IllegalArgumentException("Iban number part must have length of 15!");
        ibanList.add(iban);
    }

    public boolean existsIban(String iban) {
        for (String iban1 : ibanList) {
            if (iban.equals(iban1)) {
                return true;
            }
        }
        return false;
    }
public BankAccount requireByIban(String iban) {
    BankAccount a = findByIban(iban);
    if (a == null) throw new IllegalArgumentException("Δεν βρέθηκε λογαριασμός με IBAN: " + iban);
    return a;
}

    public void createPersonalAccount(String vat, double interestRate, double balance) {
        Customer ind = UserManager.getInstance().findCustomerByVat(vat);
        if (ind instanceof Individual) {
            Individual ind_i = (Individual) ind;
            PersonalAccount pacc = new PersonalAccount(ind_i, interestRate);
            pacc.changeBalance(balance);
            accounts.add(pacc);
        } else {
            System.out.println("VAT " + vat + " does not belong to an individual!");
        }

    }

    public void createBusinessAccount(String vat, double interestRate,
            LocalDate dateCreated, double balance) {
        Customer cust = UserManager.getInstance().findCustomerByVat(vat);
        if (cust instanceof Company) {
            Company comp = (Company) cust;

            if (comp.getAccounts().isEmpty()) {
                BusinessAccount b = new BusinessAccount(comp, interestRate, dateCreated);
                b.changeBalance(balance);
                accounts.add(b);
            } else {
                System.out.println("Compnay " + comp.getLegalName() + " already has a Buisiness Account");
            }
        } else {
            System.out.println("VAT " + vat + " does not belong to a company");
        }
    }

  public void addAccounts(StorableList<BankAccount> loaded) {
    if (loaded == null) return;

    for (BankAccount a : loaded) {
        if (a == null) continue;

        if (a instanceof MasterAccount) {
            boolean already = false;
            for (BankAccount existing : this.accounts) {
                if (existing instanceof MasterAccount) { already = true; break; }
            }
            if (already) continue; 
        }

        if (findByIban(a.getIban()) != null) {
            continue;
        }

        if (a instanceof BusinessAccount) {
            String vat = a.getPrimaryHolder() != null ? a.getPrimaryHolder().getVatNumber() : null;
            if (vat != null) {
                boolean companyAlreadyHas = false;
                for (BankAccount existing : this.accounts) {
                    if (existing instanceof BusinessAccount
                            && existing.getPrimaryHolder() != null
                            && vat.equals(existing.getPrimaryHolder().getVatNumber())) {
                        companyAlreadyHas = true;
                        break;
                    }
                }
                if (companyAlreadyHas) continue; 
            }
        }

        this.accounts.add(a);
        try {
            String iban = a.getIban();
            if (iban != null && iban.length() >= 15) {
                String uniquePart = iban.substring(iban.length() - 15);
                if (!existsIban(uniquePart)) addIban(uniquePart);
            }
        } catch (Exception ignored) {}
    }
}


    public BusinessAccount findBusinessAccountByVat(String vat) {
        for (BankAccount acc : accounts) {
            if (acc instanceof BusinessAccount) {
                BusinessAccount bacc = (BusinessAccount) acc;
                if (acc.getPrimaryHolder().getVatNumber().equals(vat))
                    return bacc;

            }
        }
        return null;
    }
public void clearAll() {
    accounts.clear();
    ibanList.clear();
}

}