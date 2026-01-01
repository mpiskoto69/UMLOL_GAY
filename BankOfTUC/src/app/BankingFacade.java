package app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import accounts.BankAccount;
import bank.storage.StorageManager;
import managers.*;
import bank.storage.Bill;  
import users.*;

public class BankingFacade {

    private LocalDate currentDate = LocalDate.now();

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    // --- load/save ---
    public void loadAll() {
        
        UserManager.getInstance().clearAll();
        AccountManager.getInstance().clearAll();
        BillManager.getInstance().clearAll();
        StandingOrderManager.getInstance().clearAll();
        StorageManager.getInstance().loadAll();

    }
    public void saveAll() {
        StorageManager.getInstance().saveAll(currentDate);
    }

  public User login(String username, String password) {
    User u = UserManager.getInstance().findUserByUsername(username);

    System.out.println("LOGIN attempt username='" + username + "' found=" + (u != null));
    if (u != null) {
        System.out.println("stored username='" + u.getUsername() + "'");
        System.out.println("stored password='" + u.getPassword() + "'");
        System.out.println("pass ok? " + u.login(password));
    }

    if (u == null || !u.login(password)) {
        throw new IllegalArgumentException("Λάθος username ή password");
    }
    return u;
}


    // --- queries ---
    public List<BankAccount> accountsFor(Customer c) {
        return new ArrayList<>(c.getAccounts());
        // ή: return AccountManager.getInstance().getAllAccountsOfUser(c.getVatNumber());
    }

    public Bill findBillForPayment(String rfCode) {
        Bill b = BillManager.getInstance().getUnpaidBill(rfCode, currentDate);
        if (b == null) {
            throw new IllegalArgumentException("Δεν υπάρχει απλήρωτος λογαριασμός (due/overdue) με αυτό το RF");
        }
        return b;
    }

    // --- simulation ---
    public void nextDay() {
        currentDate = currentDate.plusDays(1);

        // 1) accrue daily interest
        AccountManager.getInstance().applyDailyInterestToAllAccounts();

        // 2) execute standing orders due today
        StandingOrderManager.getInstance().executeDueOrders(currentDate);

        // 3) end-of-month posting
        if (currentDate.getDayOfMonth() == currentDate.lengthOfMonth()) {
            for (BankAccount a : AccountManager.getInstance().getAllAccounts()) {
                a.endOfMonth();
            }
        }
    }
}
