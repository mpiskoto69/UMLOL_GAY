package transactions;

import javax.naming.InsufficientResourcesException;

import accounts.BankAccount;
import managers.AccountManager;
import transactions.AccountStatement.MovementType;
import users.Customer;

public class Transfer extends Transaction {
    private final double amount;

    /** Keep existing constructors for backward compatibility (optional) */
    public Transfer(Customer transactor, BankAccount fromAccount, BankAccount toAccount,
                    String reasonFrom, String reasonTo, double amount) {
        super(transactor, fromAccount, toAccount, reasonFrom, reasonTo);
        this.amount = amount;
    }

    public Transfer(Customer transactor, BankAccount fromAccount, BankAccount toAccount,
                    double amount, String reason) {
        this(transactor, fromAccount, toAccount, reason, reason, amount);
    }

    /** ✅ Builder entry point */
    public static Builder builder() {
        return new Builder();
    }

    /** ✅ Builder implementation */
    public static class Builder {
        private Customer transactor;
        private BankAccount fromAccount;
        private BankAccount toAccount;
        private String reasonFrom;
        private String reasonTo;
        private Double amount; // use wrapper to detect "not set"

        public Builder transactor(Customer transactor) {
            this.transactor = transactor;
            return this;
        }

        public Builder from(BankAccount fromAccount) {
            this.fromAccount = fromAccount;
            return this;
        }

        public Builder to(BankAccount toAccount) {
            this.toAccount = toAccount;
            return this;
        }

        /** same reason for both sides */
        public Builder reason(String reason) {
            this.reasonFrom = reason;
            this.reasonTo = reason;
            return this;
        }

        public Builder reasonFrom(String reasonFrom) {
            this.reasonFrom = reasonFrom;
            return this;
        }

        public Builder reasonTo(String reasonTo) {
            this.reasonTo = reasonTo;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public Transfer build() {
            // minimal validation (business-safe)
            if (transactor == null) throw new IllegalArgumentException("transactor is required");
            if (fromAccount == null) throw new IllegalArgumentException("fromAccount is required");
            if (toAccount == null) throw new IllegalArgumentException("toAccount is required");
            if (amount == null) throw new IllegalArgumentException("amount is required");
            if (amount <= 0) throw new IllegalArgumentException("amount must be > 0");

            // default reasons if missing
            String rf = (reasonFrom != null && !reasonFrom.isBlank()) ? reasonFrom : "Transfer";
            String rt = (reasonTo != null && !reasonTo.isBlank()) ? reasonTo : rf;

            return new Transfer(transactor, fromAccount, toAccount, rf, rt, amount);
        }
    }

    @Override
    public void execute() throws IllegalArgumentException, IllegalAccessException, InsufficientResourcesException {

        if (!AccountManager.getInstance().hasAccessToAccount(transactor, account1))
            throw new IllegalAccessException("You don't have access to this account!");

        // 1) Move money
        getAccount1().debit(amount);
        getAccount2().credit(amount);

        // 2) Record DEBIT on sender
        AccountStatement debitStmt = new AccountStatement(
            getId(),
            getTransactor().getUsername(),
            getAccount1().getIban(),
            getAccount2().getIban(),
            getReason1(),
            amount,
            getAccount1().getBalance(),
            MovementType.DEBIT
        );
        getAccount1().addStatement(debitStmt);

        // 3) Record CREDIT on receiver
        AccountStatement creditStmt = new AccountStatement(
            getId(),
            getTransactor().getUsername(),
            getAccount2().getIban(),
            getAccount1().getIban(),
            getReason2(),
            amount,
            getAccount2().getBalance(),
            MovementType.CREDIT
        );
        getAccount2().addStatement(creditStmt);

        System.out.println(
            "Transfer of " + amount + " euros by " + transactor.getLegalName()
            + ", from " + getAccount1().getIban()
            + ", to " + getAccount2().getIban()
            + " and reason " + getReason1() + " completed"
        );
    }
}
