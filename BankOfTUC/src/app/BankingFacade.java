package app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import accounts.BankAccount;
import bank.storage.StorageManager;
import managers.*;
import standingOrders.TransferOrder;
import transactions.Withdrawall;
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
public void createTransferOrder(Customer customer,
                                String title,
                                String description,
                                String fromIban,
                                String toIban,
                                double amount,
                                int frequencyInMonths,
                                int dayOfMonth,
                                LocalDate startDate,
                                LocalDate endDate,
                                double fee) {

    // ---------- basic validation ----------
    if (customer == null)
        throw new IllegalArgumentException("Customer is required");

    if (amount <= 0)
        throw new IllegalArgumentException("Amount must be > 0");

    if (fee < 0)
        throw new IllegalArgumentException("Fee must be >= 0");

    if (frequencyInMonths < 1)
        throw new IllegalArgumentException("Frequency must be >= 1");

    if (dayOfMonth < 1 || dayOfMonth > 31)
        throw new IllegalArgumentException("Day of month must be 1..31");

    if (startDate == null || endDate == null || endDate.isBefore(startDate))
        throw new IllegalArgumentException("Invalid start/end date");

    // ---------- find accounts ----------
    BankAccount fromAccount = AccountManager.getInstance().findByIban(fromIban);
    if (fromAccount == null)
        throw new IllegalArgumentException("Source account not found");

    BankAccount toAccount = AccountManager.getInstance().findByIban(toIban);
    if (toAccount == null)
        throw new IllegalArgumentException("Target account not found");

    // ---------- access check ----------
    if (!AccountManager.getInstance().hasAccessToAccount(customer, fromAccount))
        throw new IllegalArgumentException("No access to source account");

    // ---------- create order ----------
    String id = UUID.randomUUID().toString();

    TransferOrder order = new TransferOrder(
            customer,
            id,
            title != null && !title.isBlank() ? title : "Standing Transfer",
            description,
            fromAccount,
            toAccount,
            amount,
            frequencyInMonths,
            dayOfMonth,
            startDate,
            endDate,
            fee
    );

    // ---------- register ----------
    StandingOrderManager.getInstance().addOrder(order);
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
  
public void withdraw(Customer customer, String fromIban, double amount, String reason) {
    if (customer == null) throw new IllegalArgumentException("Customer is required");
    if (fromIban == null || fromIban.isBlank()) throw new IllegalArgumentException("From IBAN is required");
    if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");
    if (reason == null || reason.isBlank()) reason = "Cash withdrawal";

    BankAccount from = AccountManager.getInstance().findByIban(fromIban);
    if (from == null) throw new IllegalArgumentException("Account not found: " + fromIban);

    // (optional, but bank-grade) access check here too
    if (!AccountManager.getInstance().hasAccessToAccount(customer, from))
        throw new IllegalArgumentException("No access to this account");

    TransactionManager.getInstance().registerTransaction(
        new Withdrawall(customer, from, reason, amount)
    );
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
