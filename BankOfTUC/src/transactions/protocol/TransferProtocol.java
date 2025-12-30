package transactions.protocol;

import accounts.BankAccount;

public interface TransferProtocol {
    void execute(BankAccount from, BankAccount to, double amount) throws Exception;
}
