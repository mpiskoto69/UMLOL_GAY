package app;

import accounts.BankAccount;
import accounts.MasterAccount;
import bank.storage.Bill;
import bank.storage.StorageManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import managers.*;
import standingOrders.PaymentOrder;
import standingOrders.TransferOrder;
import transactions.Deposit;
import transactions.Payment;
import transactions.Transfer;
import transactions.Withdrawal;
import transactions.protocol.TransferProtocol;
import users.*;

public class BankingFacade {

    private LocalDate currentDate = LocalDate.now();

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    // load/save
    public void loadAll() {

        UserManager.getInstance().clearAll();
        AccountManager.getInstance().clearAll();
        BillManager.getInstance().clearAll();
        StandingOrderManager.getInstance().clearAll();
        StorageManager.getInstance().loadAll();
        currentDate = StorageManager.getInstance().loadCurrentDateOrDefault();
        Customer bank = UserManager.getInstance().findCustomerByVat("bank");
        if (bank == null) {
            Company bankCo = new Company("bank", "bank", "bank", "Bank");
            UserManager.getInstance().addUser(bankCo);
            this.currentDate = StorageManager.getInstance().loadCurrentDateOrDefault();
        }

        if (MasterAccount.getInstance().getPrimaryHolder() == null) {
            Company bankCo = (Company) UserManager.getInstance().findCustomerByVat("bank");
            MasterAccount.getInstance().initIfNeeded(bankCo);
        }
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

    public Bill issueBill(Company company,
                          String customerVat,
                          double amount,
                          LocalDate dueDate) {
        if (company == null)
            throw new IllegalArgumentException("Company is required");
        if (customerVat == null || customerVat.isBlank())
            throw new IllegalArgumentException("Customer VAT is required");
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be > 0");
        if (dueDate == null)
            throw new IllegalArgumentException("Due date is required");
        Customer c = UserManager.getInstance().findCustomerByVat(customerVat);
        if (c == null)
            throw new IllegalArgumentException("Unknown customer VAT: " + customerVat);
        if (AccountManager.getInstance().findBusinessAccountByVat(company.getVatNumber()) == null) {
            throw new IllegalArgumentException("Company has no business account (cannot issue bills).");
        }

        Bill bill = new Bill(
                company.getVatNumber(),
                customerVat,
                amount,
                currentDate,
                dueDate);
        BillManager.getInstance().addBill(bill);
        return bill;
    }

    public void resetToToday() {
        try {
            StorageManager.getInstance().restoreCheckpointAndReload();

            this.currentDate = LocalDate.now();

            StorageManager.getInstance().saveCurrentDatePublic(this.currentDate);

        } catch (Exception e) {
            throw new RuntimeException("Reset failed: " + e.getMessage(), e);
        }
    }

    public void createPaymentOrder(Customer customer, String title, String description,
            String fromIban,
            String rfCode,
            double maxAmount,
            LocalDate startDate,
            LocalDate endDate,
            double fee) {

        if (customer == null)
            throw new IllegalArgumentException("Customer is required");

        if (fromIban == null || fromIban.isBlank())
            throw new IllegalArgumentException("Source account (IBAN) is required");

        if (rfCode == null || rfCode.isBlank())
            throw new IllegalArgumentException("RF code is required");

        if (!rfCode.startsWith("RF"))
            throw new IllegalArgumentException("RF must start with 'RF'");

        if (maxAmount <= 0)
            throw new IllegalArgumentException("Max amount must be > 0");

        if (fee < 0)
            throw new IllegalArgumentException("Fee must be >= 0");

        if (startDate == null || endDate == null || endDate.isBefore(startDate))
            throw new IllegalArgumentException("Invalid start/end date");

        BankAccount fromAccount = AccountManager.getInstance().findByIban(fromIban);
        if (fromAccount == null)
            throw new IllegalArgumentException("Source account not found");

        if (!AccountManager.getInstance().hasAccessToAccount(customer, fromAccount))
            throw new IllegalArgumentException("No access to source account");

        String id = UUID.randomUUID().toString();

        PaymentOrder order = new PaymentOrder(
                customer,
                id,
                (title != null && !title.isBlank()) ? title : "Standing Bill Payment",
                description,
                fromAccount,
                rfCode,
                maxAmount,
                startDate,
                endDate,
                fee);

        StandingOrderManager.getInstance().addOrder(order);
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

        // basic validation
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

        // find accounts
        BankAccount fromAccount = AccountManager.getInstance().findByIban(fromIban);
        if (fromAccount == null)
            throw new IllegalArgumentException("Source account not found");

        BankAccount toAccount = AccountManager.getInstance().findByIban(toIban);
        if (toAccount == null)
            throw new IllegalArgumentException("Target account not found");

        // access check
        if (!AccountManager.getInstance().hasAccessToAccount(customer, fromAccount))
            throw new IllegalArgumentException("No access to source account");

        // create order
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
                fee);

        // register
        StandingOrderManager.getInstance().addOrder(order);
    }

    // queries
    public List<BankAccount> accountsFor(Customer c) {
        return new ArrayList<>(c.getAccounts());
    }

    public Bill findBillForPayment(String rfCode) {
        Bill b = BillManager.getInstance().getUnpaidBill(rfCode, currentDate);
        if (b == null) {
            throw new IllegalArgumentException("Δεν υπάρχει απλήρωτος λογαριασμός (due/overdue) με αυτό το RF");
        }
        return b;
    }

    public Bill createBill(Company issuer, String customerVat, double amount, LocalDate dueDate) {
        if (issuer == null)
            throw new IllegalArgumentException("Issuer is required");
        if (customerVat == null || customerVat.isBlank())
            throw new IllegalArgumentException("Customer VAT required");
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be > 0");
        if (dueDate == null)
            throw new IllegalArgumentException("Due date required");

        LocalDate issueDate = getCurrentDate();

        Customer c = UserManager.getInstance().findCustomerByVat(customerVat);
        if (c == null)
            throw new IllegalArgumentException("Unknown customer VAT: " + customerVat);

        Bill b = new Bill(issuer.getVatNumber(), customerVat, amount, issueDate, dueDate);
        BillManager.getInstance().addBill(b);
        return b;
    }

    public void withdraw(Customer customer, String fromIban, double amount, String reason) {
        if (customer == null)
            throw new IllegalArgumentException("Customer is required");
        if (fromIban == null || fromIban.isBlank())
            throw new IllegalArgumentException("From IBAN is required");
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be > 0");
        if (reason == null || reason.isBlank())
            reason = "Cash withdrawal";

        BankAccount from = AccountManager.getInstance().findByIban(fromIban);
        if (from == null)
            throw new IllegalArgumentException("Account not found: " + fromIban);

        if (!AccountManager.getInstance().hasAccessToAccount(customer, from))
            throw new IllegalArgumentException("No access to this account");

        TransactionManager.getInstance().registerTransaction(
                new Withdrawal(customer, from, reason, amount));
    }

    public void transfer(Customer customer,
            String fromIban,
            String toIban,
            double amount,
            String reason,
            TransferProtocol protocol) {

        if (customer == null)
            throw new IllegalArgumentException("Customer is required");
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be > 0");
        if (reason == null || reason.isBlank())
            reason = "Transfer";

        BankAccount from;
        BankAccount to;

        try {
            from = AccountManager.getInstance().findByIban(fromIban);
            to = AccountManager.getInstance().findByIban(toIban);
        } catch (IllegalArgumentException ex) {
            throw ex;
        }

        if (!AccountManager.getInstance().hasAccessToAccount(customer, from))
            throw new IllegalArgumentException("No access to source account");

        TransactionManager.getInstance().registerTransaction(
                new Transfer(customer, from, to, reason, reason, amount, protocol));
    }

    public void payBill(Customer customer, String fromIban, String rfCode) {
        if (customer == null)
            throw new IllegalArgumentException("Customer is required");
        if (fromIban == null || fromIban.isBlank())
            throw new IllegalArgumentException("From IBAN is required");
        if (rfCode == null || rfCode.isBlank())
            throw new IllegalArgumentException("RF is required");

        BankAccount payer = AccountManager.getInstance().findByIban(fromIban);
        if (payer == null)
            throw new IllegalArgumentException("Account not found: " + fromIban);

        if (!AccountManager.getInstance().hasAccessToAccount(customer, payer))
            throw new IllegalArgumentException("No access to this account");

        Bill bill = findBillForPayment(rfCode);
        BankAccount payee = AccountManager.getInstance().findBusinessAccountByVat(bill.getIssuerVAT());
        if (payee == null)
            throw new IllegalArgumentException("Issuer business account not found");

        TransactionManager.getInstance().registerTransaction(
                new Payment(
                        customer,
                        payer,
                        payee,
                        "Πληρωμή λογαριασμού RF: " + rfCode,
                        "Είσπραξη λογαριασμού RF: " + rfCode,
                        bill.getAmount()));

        bill.markAsPaid();
    }

    public void deposit(Customer customer, String toIban, double amount, String reason) {
        if (customer == null)
            throw new IllegalArgumentException("Customer is required");
        if (toIban == null || toIban.isBlank())
            throw new IllegalArgumentException("IBAN is required");
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be > 0");
        if (reason == null || reason.isBlank())
            reason = "Cash deposit";

        BankAccount to = AccountManager.getInstance().findByIban(toIban);
        if (to == null)
            throw new IllegalArgumentException("Account not found: " + toIban);

        if (!AccountManager.getInstance().hasAccessToAccount(customer, to))
            throw new IllegalArgumentException("No access to this account");

        TransactionManager.getInstance().registerTransaction(
                new Deposit(customer, to, reason, amount));
    }

    public List<Bill> issuedBillsFor(Company c) {
        return BillManager.getInstance().getBillsIssuedBy(c.getVatNumber());
    }

    public List<Bill> billsToPayFor(Company c) {
        return BillManager.getInstance().getBillsToPayBy(c.getVatNumber());
    }

    // simulation
    public StandingOrderManager.ExecutionReport nextDayWithReport() {
        currentDate = currentDate.plusDays(1);
        StorageManager.getInstance().loadIssuedBills("bills/" + currentDate + ".csv");

        AccountManager.getInstance().applyDailyInterestToAllAccounts();

        StandingOrderManager.ExecutionReport rep = StandingOrderManager.getInstance()
                .executeDueOrdersWithReport(currentDate);

        if (currentDate.getDayOfMonth() == currentDate.lengthOfMonth()) {
            for (BankAccount a : AccountManager.getInstance().getAllAccounts()) {
                a.endOfMonth();
            }
        }
        return rep;
    }

    public void nextDay() {
        currentDate = currentDate.plusDays(1);
        StorageManager.getInstance().loadIssuedBills("bills/" + currentDate + ".csv");

        AccountManager.getInstance().applyDailyInterestToAllAccounts();

        StandingOrderManager.getInstance().executeDueOrders(currentDate);

        if (currentDate.getDayOfMonth() == currentDate.lengthOfMonth()) {
            for (BankAccount a : AccountManager.getInstance().getAllAccounts()) {
                a.endOfMonth();
            }
        }
    }
}
