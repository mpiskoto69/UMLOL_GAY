package transactions;

import bank.storage.Storable;
import bank.storage.UnMarshalingException;
import java.time.LocalDateTime;

public class AccountStatement implements Storable {

    public enum MovementType {
        CREDIT, DEBIT
    }

    private LocalDateTime timestamp;
    private String transactionId;
    private String transactorName;
    private String accountIban;
    private String counterpartyIban;
    private String reason;
    private double amount;
    private double balanceAfter;
    private MovementType movementType;

    public AccountStatement(String transactionId,
            String transactorName,
            String accountIban,
            String counterpartyIban,
            String reason,
            double amount,
            double balanceAfter,
            MovementType movementType) {
        this.timestamp = LocalDateTime.now();
        this.transactionId = transactionId;
        this.transactorName = transactorName;
        this.accountIban = accountIban;
        this.counterpartyIban = counterpartyIban;
        this.reason = reason;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.movementType = movementType;
    }

    public AccountStatement() {
    }

    // ---------- Builder pattern ----------
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocalDateTime timestamp = LocalDateTime.now();
        private String transactionId;
        private String transactorName;
        private String accountIban;
        private String counterpartyIban;
        private String reason;
        private Double amount; // wrapper to detect missing
        private Double balanceAfter; // wrapper to detect missing
        private MovementType movementType;

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder transactor(String transactorName) {
            this.transactorName = transactorName;
            return this;
        }

        public Builder account(String accountIban) {
            this.accountIban = accountIban;
            return this;
        }

        public Builder counterparty(String counterpartyIban) {
            this.counterpartyIban = counterpartyIban;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public Builder balanceAfter(double balanceAfter) {
            this.balanceAfter = balanceAfter;
            return this;
        }

        public Builder type(MovementType movementType) {
            this.movementType = movementType;
            return this;
        }

        public AccountStatement build() {
            // required fields
            if (transactionId == null || transactionId.isBlank())
                throw new IllegalArgumentException("transactionId is required");
            if (transactorName == null || transactorName.isBlank())
                throw new IllegalArgumentException("transactorName is required");
            if (accountIban == null || accountIban.isBlank())
                throw new IllegalArgumentException("accountIban is required");
            if (reason == null)
                throw new IllegalArgumentException("reason is required");
            if (movementType == null)
                throw new IllegalArgumentException("movementType is required");
            if (amount == null)
                throw new IllegalArgumentException("amount is required");
            if (balanceAfter == null)
                throw new IllegalArgumentException("balanceAfter is required");

            AccountStatement s = new AccountStatement();
            s.timestamp = this.timestamp;
            s.transactionId = this.transactionId;
            s.transactorName = this.transactorName;
            s.accountIban = this.accountIban;
            s.counterpartyIban = this.counterpartyIban;
            s.reason = this.reason;
            s.amount = this.amount;
            s.balanceAfter = this.balanceAfter;
            s.movementType = this.movementType;
            return s;
        }
    }

    // ---------- Getters ----------
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getTransactorName() {
        return transactorName;
    }

    public String getAccountIban() {
        return accountIban;
    }

    public String getCounterpartyIban() {
        return counterpartyIban;
    }

    public String getReason() {
        return reason;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    // ---------- Storage ----------
    @Override
    public String marshal() {
        return String.join(",",
                "type:Statement",
                "timestamp:" + timestamp,
                "transactionId:" + transactionId,
                "transactor:" + transactorName,
                "accountIban:" + accountIban,
                "counterpartyIban:" + (counterpartyIban != null ? counterpartyIban : ""),
                "reason:" + reason,
                "amount:" + amount,
                "balanceAfter:" + balanceAfter,
                "movementType:" + movementType);
    }

    @Override
    public void unmarshal(String data) throws UnMarshalingException {
        String[] parts = data.split(",");
        if (parts.length == 0 || !parts[0].equals("type:Statement")) {
            throw new UnMarshalingException("Not a statement: " + data);
        }

        for (String p : parts) {
            String[] kv = p.split(":", 2);
            if (kv.length != 2)
                throw new UnMarshalingException("Bad field: " + p);

            switch (kv[0]) {
                case "type":
                    break;
                case "timestamp":
                    timestamp = LocalDateTime.parse(kv[1]);
                    break;
                case "transactionId":
                    transactionId = kv[1];
                    break;
                case "transactor":
                    transactorName = kv[1];
                    break;
                case "accountIban":
                    accountIban = kv[1];
                    break;
                case "counterpartyIban":
                    counterpartyIban = kv[1].isEmpty() ? null : kv[1];
                    break;
                case "reason":
                    reason = kv[1];
                    break;
                case "amount":
                    amount = Double.parseDouble(kv[1]);
                    break;
                case "balanceAfter":
                    balanceAfter = Double.parseDouble(kv[1]);
                    break;
                case "movementType":
                    movementType = MovementType.valueOf(kv[1]);
                    break;
                default:
                    // ignore unknown fields
                    break;
            }
        }
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + movementType
                + " | " + amount + "€"
                + " | REASON: " + reason
                + " | BALANCEAFTER: " + balanceAfter + "€"
                + " | Transactor: " + transactorName;
    }
}
