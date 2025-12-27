package transactions;

import java.time.LocalDateTime;

import bank.storage.Storable;
import bank.storage.UnMarshalingException;

public class AccountStatement implements Storable {

    public enum MovementType {
        CREDIT, DEBIT
    }

    private LocalDateTime timestamp;
    private String transactionId;
    private String transactorName;
    private String accountIban;
    private String counterpartyIban; // ο άλλος λογαριασμός, μπορεί να είναι null
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

    // --- Getters ---
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

    @Override
    public String marshal() {
        // emit a single CSV line with key:value pairs
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
        if (!parts[0].equals("type:Statement")) {
            throw new UnMarshalingException("Not a statement: " + data);
        }
        for (String p : parts) {
            String[] kv = p.split(":", 2);
            if (kv.length != 2) {
                throw new UnMarshalingException("Bad field: " + p);
            }
            switch (kv[0]) {
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
                // ignore the type:Statement field
                default:
                    break;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[")
                .append(timestamp)
                .append("] ")
                .append(movementType)
                .append(" | ")
                .append(amount)
                .append("€ | ")
                .append("REASON: ")
                .append(reason)
                .append(" | ")
                .append("BALANCEAFTER: ")
                .append(balanceAfter)
                .append("€ | ")
                .append("Transactor: ")
                .append(transactorName);
        return sb.toString();
    }
}