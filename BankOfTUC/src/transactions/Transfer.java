package transactions;
import accounts.BankAccount;
import managers.AccountManager;
import transactions.AccountStatement.MovementType;
import transactions.protocol.IntraBankProtocol;
import transactions.protocol.TransferProtocol;
import users.Customer;

public class Transfer extends Transaction {
    private final double amount;

    // Bridge: implementation (protocol)
    private final TransferProtocol protocol;

    /** Full constructor */
    public Transfer(Customer transactor, BankAccount fromAccount, BankAccount toAccount,
                    String reasonFrom, String reasonTo, double amount,
                    TransferProtocol protocol) {
        super(transactor, fromAccount, toAccount, reasonFrom, reasonTo);
        this.amount = amount;
        this.protocol = (protocol != null) ? protocol : new IntraBankProtocol();
    }

    /** Backward compatible constructor (default intra-bank protocol) */
    public Transfer(Customer transactor, BankAccount fromAccount, BankAccount toAccount,
                    String reasonFrom, String reasonTo, double amount) {
        this(transactor, fromAccount, toAccount, reasonFrom, reasonTo, amount, new IntraBankProtocol());
    }

    /** Shortcut when same reason for both sides */
    public Transfer(Customer transactor, BankAccount fromAccount, BankAccount toAccount,
                    double amount, String reason) {
        this(transactor, fromAccount, toAccount, reason, reason, amount, new IntraBankProtocol());
    }

    /** Builder entry point */
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Customer transactor;
        private BankAccount fromAccount;
        private BankAccount toAccount;
        private String reasonFrom;
        private String reasonTo;
        private Double amount;

        // Bridge choice (optional)
        private TransferProtocol protocol;

        public Builder transactor(Customer transactor) { this.transactor = transactor; return this; }
        public Builder from(BankAccount fromAccount) { this.fromAccount = fromAccount; return this; }
        public Builder to(BankAccount toAccount) { this.toAccount = toAccount; return this; }

        public Builder reason(String reason) {
            this.reasonFrom = reason;
            this.reasonTo = reason;
            return this;
        }

        public Builder reasonFrom(String reasonFrom) { this.reasonFrom = reasonFrom; return this; }
        public Builder reasonTo(String reasonTo) { this.reasonTo = reasonTo; return this; }

        public Builder amount(double amount) { this.amount = amount; return this; }

        // Bridge setter
        public Builder protocol(TransferProtocol protocol) { this.protocol = protocol; return this; }

        public Transfer build() {
            if (transactor == null) throw new IllegalArgumentException("transactor is required");
            if (fromAccount == null) throw new IllegalArgumentException("fromAccount is required");
            if (toAccount == null) throw new IllegalArgumentException("toAccount is required");
            if (amount == null) throw new IllegalArgumentException("amount is required");
            if (amount <= 0) throw new IllegalArgumentException("amount must be > 0");

            String rf = (reasonFrom != null && !reasonFrom.isBlank()) ? reasonFrom : "Transfer";
            String rt = (reasonTo != null && !reasonTo.isBlank()) ? reasonTo : rf;

            return new Transfer(transactor, fromAccount, toAccount, rf, rt, amount, protocol);
        }
    }

    @Override
    public void execute() throws IllegalArgumentException, IllegalAccessException {
        if (!AccountManager.getInstance().hasAccessToAccount(transactor, account1))
            throw new IllegalAccessException("You don't have access to this account!");

        // 1) Move money via Bridge
        try {
			protocol.execute(getAccount1(), getAccount2(), amount);
		} catch (Exception e) {
			e.printStackTrace();
		}

        // 2) DEBIT on sender
        AccountStatement debitStmt = AccountStatement.builder()
            .transactionId(getId())
            .transactor(getTransactor().getUsername())
            .account(getAccount1().getIban())
            .counterparty(getAccount2().getIban())
            .reason(getReason1())
            .amount(amount)
            .balanceAfter(getAccount1().getBalance())
            .type(MovementType.DEBIT)
            .build();
        getAccount1().addStatement(debitStmt);

        // 3) CREDIT on receiver
        AccountStatement creditStmt = AccountStatement.builder()
            .transactionId(getId())
            .transactor(getTransactor().getUsername())
            .account(getAccount2().getIban())
            .counterparty(getAccount1().getIban())
            .reason(getReason2())
            .amount(amount)
            .balanceAfter(getAccount2().getBalance())
            .type(MovementType.CREDIT)
            .build();
        getAccount2().addStatement(creditStmt);
    }
}
