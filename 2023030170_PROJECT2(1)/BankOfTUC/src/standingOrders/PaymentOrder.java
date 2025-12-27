package standingOrders;

import managers.AccountManager;
import java.time.LocalDate;

import accounts.BankAccount;
import accounts.MasterAccount;
import bank.storage.UnMarshalingException;
import managers.BillManager;
import managers.TransactionManager;
import managers.UserManager;
import transactions.Payment;
import users.Customer;

public class PaymentOrder extends StandingOrder {
    private BankAccount fromAccount;
    private String rfCode;
    private double maxAmount;

    public PaymentOrder(Customer customer, String id, String title, String description,
            BankAccount fromAccount, String rfCode, double maxAmount,
            LocalDate startDate, LocalDate endDate, double fee) {
        super(customer, id, title, description, startDate, endDate, fee);
        this.fromAccount = fromAccount;
        this.rfCode = rfCode;
        this.maxAmount = maxAmount;
    }

    public PaymentOrder() {

    }

    @Override
    public boolean isDue(LocalDate today) {
        try {
            return isActive(today) && BillManager.getInstance().isBillDueToday(rfCode, today);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // <- πρόσθεσέ το για να μην έχεις compile error
    }

    @Override
    public void execute(LocalDate today) {
        Bill bill = null;
        try {
            bill = BillManager.getInstance().getUnpaidBill(rfCode, today);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bill == null || bill.getAmount() > maxAmount) {
            registerFailure();
            return;
        }

        BankAccount fromAccount = MasterAccount.getInstance();
        // BankAccount toAccount = AccountManager.getInstance().getPrimaryAccountOfUser(bill.getId());
        BankAccount toAccount = AccountManager.getInstance().findBusinessAccountByVat(bill.getIssuerVAT());
        Customer bank = MasterAccount.getInstance().getPrimaryHolder();

        TransactionManager.registerTransaction(new Payment(
                bank,
                fromAccount,
                toAccount,
                "Πληρωμή λογαριασμού RF: " + rfCode,
                "Είσπραξη λογαριασμού RF: " + rfCode,
                bill.getAmount() + fee));

        bill.markAsPaid();

    }

    @Override
    public String marshal() {
        // type, orderId, paymentCode, title, description, customer, maxAmount,
        // startDate, endDate, fee, chargeAccount
        return String.join(",",
                "type:PaymentOrder",
                "orderId:" + id,
                "paymentCode:" + rfCode,
                "title:" + title,
                "description:" + description,
                "customer:" + fromAccount.getPrimaryHolder().getVatNumber(),
                "maxAmount:" + maxAmount,
                "startDate:" + startDate,
                "endDate:" + endDate,
                "fee:" + fee,
                "chargeAccount:" + fromAccount.getIban());
    }

    @Override
    public void unmarshal(String data) throws UnMarshalingException {
        String[] parts = data.split(",");
        if (!parts[0].equals("type:PaymentOrder")) {
            throw new UnMarshalingException("Not a PaymentOrder: " + data);
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
                case "paymentCode":
                    rfCode = kv[1];
                    break;
                case "title":
                    title = kv[1];
                    break;
                case "description":
                    description = kv[1];
                    break;
                case "customer":
                    // ensure customer exists
                    customer = UserManager.getInstance().findCustomerByVat(kv[1]);
                    break;
                case "maxAmount":
                    maxAmount = Double.parseDouble(kv[1]);
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
                    fromAccount = AccountManager.getInstance().findByIban(kv[1]);
                    if (fromAccount == null)
                        throw new UnMarshalingException("Unknown IBAN: " + kv[1]);
                    break;
                default:
                    // ignore type or any extra fields
                    break;
            }
        }
    }
}
