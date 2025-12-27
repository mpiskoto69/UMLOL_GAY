package managers;

import accounts.BankAccount;
import accounts.MasterAccount;
import accounts.BusinessAccount;
import transactions.Transaction;
import transactions.Transfer;
import users.Customer;

public class TransactionManager {
    private static final TransactionManager instance = new TransactionManager();

    private TransactionManager() {
    }

    public static TransactionManager getInstance() {
        return instance;
    }

    public static void registerTransaction(Transaction transaction) {
        try {
            transaction.execute();
            StatementManager.getInstance().registerStatements(transaction);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void newTranfer(Customer transactor, BankAccount fromAccount, BankAccount toAccount, String reasonFrom,
            String reasonTo,
            double amount) {
        Transaction transfer = new Transfer(transactor, fromAccount, toAccount, amount, reasonTo);
        registerTransaction(transfer);
    }

    public void eofInterestPayment(BankAccount toAccount, double ammount) {
        MasterAccount bm = MasterAccount.getInstance();
        String reasonTo = "Interest payment from bank";
        String reasonFrom = "Interest payment to " + toAccount.getIban();
        Transaction transfer = new Transfer(bm.getPrimaryHolder(), bm, toAccount, reasonFrom, reasonTo, ammount);
        registerTransaction(transfer);
    }

    public void chargeMaintenanceFee(BusinessAccount fromAccount) {
        MasterAccount bm = MasterAccount.getInstance();
        String reasonFrom = "Maintenance fee";
        String reasonTo = "Maintenance fee from " + fromAccount.getIban();
        Transaction transfer = new Transfer(fromAccount.getPrimaryHolder(), fromAccount, bm, reasonFrom, reasonTo,
                BusinessAccount.getMaintenaceFee());
        registerTransaction(transfer);
    }

}
