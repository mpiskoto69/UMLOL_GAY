package standingOrders;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import accounts.BankAccount;
import bank.storage.UnMarshalingException;
import managers.AccountManager;
import managers.TransactionManager;
import managers.UserManager;
import transactions.Transfer;
import users.Customer;

public class TransferOrder extends StandingOrder {
    private BankAccount fromAccount;
    private BankAccount toAccount;
    private double amount;
    private int frequencyInMonths; // κάθε πόσους μήνες εκτελείται
    private int executionDay;      // ποια ημέρα κάθε μήνα εκτελείται (1..28/30/31)

    public TransferOrder(Customer customer, String id, String title, String description,
                         BankAccount fromAccount, BankAccount toAccount, double amount,
                         int frequencyInMonths, int executionDay,
                         LocalDate startDate, LocalDate endDate, double fee) {
        super(customer, id, title, description, startDate, endDate, fee);
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.frequencyInMonths = frequencyInMonths;
        this.executionDay = executionDay;
    }

    public TransferOrder() { }

    @Override
    public boolean isDue(LocalDate today) {
        if (!isActive(today)) return false;

        // basic guards
        if (frequencyInMonths <= 0) return false;
        if (executionDay <= 0 || executionDay > 31) return false;

        // Ημέρα του μήνα πρέπει να ταιριάζει
        if (today.getDayOfMonth() != executionDay) return false;

        // Υπολογίζουμε αν έχει περάσει ακέραιος αριθμός μηνών από την αρχή
        LocalDate anchorStart = LocalDate.of(getStartDate().getYear(), getStartDate().getMonth(), executionDay);
        LocalDate anchorToday = LocalDate.of(today.getYear(), today.getMonth(), executionDay);

        long monthsSinceStart = ChronoUnit.MONTHS.between(anchorStart, anchorToday);
        return monthsSinceStart % frequencyInMonths == 0;
    }

    @Override
    public void execute(LocalDate today) {
        // (Optional) If you want: do not attempt if already failed 3 times
        if (hasExceededMaxFailures()) return;

        if (customer == null || fromAccount == null || toAccount == null) {
           onAttemptFailure(today);

            return;
        }

        if (!AccountManager.getInstance().hasAccessToAccount(customer, fromAccount)) {
            System.out.println("You don't have access to this account!");
           onAttemptFailure(today);

            return;
        }

        try {
            // IMPORTANT: do NOT debit/credit here.
            // The TransactionManager/Transfer.execute() should be the only place that changes balances
            // and creates statements.

            // Business choice:
            // Here we charge the sender amount + fee in the same transfer.
            // If you want strict baseline alignment, do a 2nd transaction for the fee to MasterAccount.
       TransactionManager.getInstance().registerTransaction(
    new Transfer(
        customer,
        fromAccount,
        toAccount,
        "Πάγια Εντολή Μεταφοράς",        // reasonFrom
        "Πάγια Εντολή Μεταφοράς",        // reasonTo
        amount + fee
    )
);


            // If execution succeeds, you may optionally reset failures (depends on your policy)
            // failedAttempts = 0;

        } catch (Exception e) {
            onAttemptFailure(today);

        }
    }

    @Override
    public String marshal() {
        // Persist failedAttempts so it survives restarts
        String customerVat = (customer != null) ? customer.getVatNumber() : "";
        String chargeIban = (fromAccount != null) ? fromAccount.getIban() : "";
        String creditIban = (toAccount != null) ? toAccount.getIban() : "";

        return String.join(",",
            "type:TransferOrder",
            "orderId:" + id,
            "title:" + title,
            "description:" + description,
            "customer:" + customerVat,
            "amount:" + amount,
            "startDate:" + startDate,
            "endDate:" + endDate,
            "fee:" + fee,
            "failedAttempts:" + failedAttempts,
            "chargeAccount:" + chargeIban,
            "creditAccount:" + creditIban,
            "frequencyInMonths:" + frequencyInMonths,
            "dayOfMonth:" + executionDay
        );
    }

    @Override
    public void unmarshal(String data) throws UnMarshalingException {
        String[] parts = data.split(",");
        if (parts.length == 0 || !parts[0].equals("type:TransferOrder")) {
            throw new UnMarshalingException("Not a TransferOrder: " + data);
        }

        for (String p : parts) {
            if (p.isBlank()) continue;

            String[] kv = p.split(":", 2);
            if (kv.length != 2) throw new UnMarshalingException("Bad field: " + p);

            switch (kv[0]) {
                case "type":
                    // ignore (already checked)
                    break;
                case "orderId":
                    id = kv[1];
                    break;
                case "title":
                    title = kv[1];
                    break;
                case "description":
                    description = kv[1];
                    break;
                case "customer":
                    customer = UserManager.getInstance().findCustomerByVat(kv[1]);
                    break;
                case "amount":
                    amount = Double.parseDouble(kv[1]);
                    break;
                case "startDate":
                    startDate = LocalDate.parse(kv[1]);
                    break;
                case "endDate":
                    endDate = LocalDate.parse(kv[1]);
                    break;
                case "fee":
                    fee = Double.parseDouble(kv[1]);
                    break;
                case "failedAttempts":
                    failedAttempts = Integer.parseInt(kv[1]);
                    break;
                case "chargeAccount":
                    fromAccount = AccountManager.getInstance().findByIban(kv[1]);
                    if (fromAccount == null) throw new UnMarshalingException("Unknown IBAN: " + kv[1]);
                    break;
                case "creditAccount":
                    toAccount = AccountManager.getInstance().findByIban(kv[1]);
                    if (toAccount == null) throw new UnMarshalingException("Unknown IBAN: " + kv[1]);
                    break;
                case "frequencyInMonths":
                    frequencyInMonths = Integer.parseInt(kv[1]);
                    break;
                case "dayOfMonth":
                    executionDay = Integer.parseInt(kv[1]);
                    break;
                default:
                    // ignore unknown fields for forward compatibility
                    break;
            }
        }

        // final sanity
        if (customer == null) throw new UnMarshalingException("Unknown customer for order: " + id);
    }
}
