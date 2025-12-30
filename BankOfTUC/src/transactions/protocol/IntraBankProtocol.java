//bridge:)
package transactions.protocol;

import accounts.BankAccount;

public class IntraBankProtocol implements TransferProtocol {

    @Override
    public void execute(BankAccount from, BankAccount to, double amount) throws Exception {
        from.debit(amount);
        to.credit(amount);
    }
}
