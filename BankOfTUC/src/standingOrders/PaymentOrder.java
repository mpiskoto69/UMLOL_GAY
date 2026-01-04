package standingOrders;

import accounts.BankAccount;
import bank.storage.Bill;
import bank.storage.UnMarshalingException;
import java.time.LocalDate;
import managers.AccountManager;
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

    public PaymentOrder() {}

    @Override
    public boolean isDue(LocalDate today) {
        return isActive(today) && BillManager.getInstance().isBillDueToday(rfCode, today);
    }

    @Override
    public void execute(LocalDate today) {
        try {
            Bill bill = BillManager.getInstance().getUnpaidBill(rfCode, today);

            if (bill == null || bill.getAmount() > maxAmount) {
                onAttemptFailure(today);
                return;
            }

            if (customer == null || fromAccount == null) {
                onAttemptFailure(today);
                return;
            }

            BankAccount payer = this.fromAccount;

            BankAccount payee = AccountManager.getInstance().findBusinessAccountByVat(bill.getIssuerVAT());
            if (payee == null) {
                onAttemptFailure(today);
                return;
            }

            if (!AccountManager.getInstance().hasAccessToAccount(customer, payer)) {
                onAttemptFailure(today);
                return;
            }

            TransactionManager.getInstance().registerTransaction(
                    new Payment(
                            customer,
                            payer,
                            payee,
                            "Πληρωμή λογαριασμού RF: " + rfCode,
                            "Είσπραξη λογαριασμού RF: " + rfCode,
                            bill.getAmount() + fee));

            bill.markAsPaid();

        } catch (Exception e) {
            onAttemptFailure(today);
        }
    }

    @Override
    public String marshal() {
        String customerVat = (customer != null) ? customer.getVatNumber() : "";
        String chargeIban = (fromAccount != null) ? fromAccount.getIban() : "";

        return String.join(",",
                "type:PaymentOrder",
                "orderId:" + id,
                "paymentCode:" + rfCode,
                "title:" + title,
                "description:" + description,
                "customer:" + customerVat,
                "maxAmount:" + maxAmount,
                "startDate:" + startDate,
                "endDate:" + endDate,
                "fee:" + fee,
                "failedAttempts:" + failedAttempts,
                "chargeAccount:" + chargeIban);
    }

    @Override
    public void unmarshal(String data) throws UnMarshalingException {
        String[] parts = data.split(",");
        if (parts.length == 0 || !parts[0].equals("type:PaymentOrder")) {
            throw new UnMarshalingException("Not a PaymentOrder: " + data);
        }

        for (String p : parts) {
            if (p.isBlank())
                continue;

            String[] kv = p.split(":", 2);
            if (kv.length != 2)
                throw new UnMarshalingException("Bad field: " + p);

            switch (kv[0]) {
                case "type":
                    break;
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
                case "failedAttempts":
                    failedAttempts = Integer.parseInt(kv[1]);
                    break;
                case "chargeAccount":
                    fromAccount = AccountManager.getInstance().findByIban(kv[1]);
                    if (fromAccount == null)
                        throw new UnMarshalingException("Unknown IBAN: " + kv[1]);
                    break;
                default:
                    // ignore
                    break;
            }
        }

        if (customer == null) {
            throw new UnMarshalingException("Unknown customer for PaymentOrder " + id);
        }
    }
}
