package managers;

import accounts.BankAccount;
import accounts.MasterAccount;
import accounts.BusinessAccount;
import transactions.Transaction;
import transactions.Transfer;
import users.Customer;

public class TransactionManager {
    private static final TransactionManager instance = new TransactionManager();

    private TransactionManager() {}

    public static TransactionManager getInstance() {
        return instance;
    }

    // ✅ non-static: ο manager (ως Singleton) είναι ο μοναδικός executor
    public void registerTransaction(Transaction transaction) {
        try {
            transaction.execute();
            StatementManager.getInstance().registerStatements(transaction);
        } catch (Exception e) {
           e.printStackTrace(); // για να βλέπεις τι έγινε σε fail
        }
    }

public void newTransfer(Customer transactor,
                        BankAccount fromAccount,
                        BankAccount toAccount,
                        String reasonFrom,
                        String reasonTo,
                        double amount) {

    Transaction transfer = Transfer.builder()
        .transactor(transactor)
        .from(fromAccount)
        .to(toAccount)
        .reasonFrom(reasonFrom)
        .reasonTo(reasonTo)
        .amount(amount)
        .build();

    registerTransaction(transfer);
}

   public void eofInterestPayment(BankAccount toAccount, double amount) {
    if (toAccount == null) return;
    if (amount <= 0) return;

    Customer bank = (Customer) MasterAccount.getInstance().getPrimaryHolder();

    Transaction t = Transfer.builder()
            .transactor(bank)                     // η τράπεζα
            .from(MasterAccount.getInstance())    // πληρώνει
            .to(toAccount)                        // ο πελάτης λαμβάνει
            .reasonFrom("Monthly interest")       // statement στο Master
            .reasonTo("Monthly interest")         // statement στον πελάτη
            .amount(amount)
            .build();

    registerTransaction(t);
}


public void chargeMaintenanceFee(BusinessAccount fromAccount) {
    MasterAccount bm = MasterAccount.getInstance();

        Transaction transfer = Transfer.builder()
        .transactor(fromAccount.getPrimaryHolder())
        .from(fromAccount)
        .to(bm)
        .reasonFrom("Maintenance fee")
        .reasonTo("Maintenance fee from " + fromAccount.getIban())
        .amount(BusinessAccount.getMaintenaceFee())
        .build();

    registerTransaction(transfer);
}

   
}
