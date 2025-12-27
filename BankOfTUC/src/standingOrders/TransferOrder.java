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
    private int executionDay; // ποια ημέρα κάθε μήνα εκτελείται (π.χ. 5 = 5 του μηνός)

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

    public TransferOrder() {

    }

    @Override
    public boolean isDue(LocalDate today) {
        if (!isActive(today))
            return false;

        // Ημέρα του μήνα πρέπει να ταιριάζει
        if (today.getDayOfMonth() != executionDay)
            return false;

        // Υπολογίζουμε αν έχει περάσει ακέραιος αριθμός μηνών από την αρχή
        long monthsSinceStart = ChronoUnit.MONTHS.between(
                LocalDate.of(getStartDate().getYear(), getStartDate().getMonth(), executionDay),
                LocalDate.of(today.getYear(), today.getMonth(), executionDay));

        return monthsSinceStart % frequencyInMonths == 0;
    }

    @Override
    public void execute(LocalDate today) {

        if (!AccountManager.getInstance().hasAccessToAccount(customer, fromAccount)) {
            System.out.println("You don't have access to this account!");
            return;
        }

        try {
            fromAccount.debit(amount);
            toAccount.credit(amount);
            TransactionManager.registerTransaction(
                    new Transfer(customer, fromAccount, toAccount, amount + fee, "Πάγια Εντολή Μεταφοράς"));
        } catch (Exception e) {
            registerFailure();
        }
    }

    @Override
    public String marshal() {
        return String.join(",",
                "type:TransferOrder",
                "orderId:" + id,
                "title:" + title,
                "description:" + description,
                "customer:" + fromAccount.getPrimaryHolder().getVatNumber(),
                "amount:" + amount,
                "startDate:" + startDate,
                "endDate:" + endDate,
                "fee:" + fee,
                "chargeAccount:" + fromAccount.getIban(),
                "creditAccount:" + toAccount.getIban(),
                "frequencyInMonths:" + frequencyInMonths,
                "dayOfMonth:" + executionDay);
    }

    @Override
    public void unmarshal(String data) throws UnMarshalingException {
        String[] parts = data.split(",");
        if (!parts[0].equals("type:TransferOrder")) {
            throw new UnMarshalingException("Not a TransferOrder: " + data);
        }
        for (String p : parts) {
            String[] kv = p.split(":", 2);
            if (kv.length != 2) {
                throw new UnMarshalingException("Bad field: " + p);
            }
            switch (kv[0]) {
                case "orderId":
                    id = kv[1];
                    break;
                case "title":
                    title = kv[1];
                    break;
                case "description":
                    description = kv[1];
                    break;
                case "customer": // ignore, used for lookup only
                    customer = UserManager.getInstance()
                            .findCustomerByVat(kv[1]);
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
                case "chargeAccount":
                    fromAccount = AccountManager.getInstance()
                            .findByIban(kv[1]);
                    if (fromAccount == null)
                        throw new UnMarshalingException("Unknown IBAN: " + kv[1]);
                    break;
                case "creditAccount":
                    toAccount = AccountManager.getInstance()
                            .findByIban(kv[1]);
                    if (toAccount == null)
                        throw new UnMarshalingException("Unknown IBAN: " + kv[1]);
                    break;
                case "frequencyInMonths":
                    frequencyInMonths = Integer.parseInt(kv[1]);
                    break;
                case "dayOfMonth":
                    executionDay = Integer.parseInt(kv[1]);
                    break;
                default:
                    // ignore
                    break;
            }
        }
    }

}
