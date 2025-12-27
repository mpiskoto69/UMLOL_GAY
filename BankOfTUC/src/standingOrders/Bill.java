package standingOrders;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import bank.storage.Storable;
import bank.storage.UnMarshalingException;
import managers.BillManager;

public class Bill implements Storable {
    private String id;
    private String rfCode;
    private String issuerVAT; // επιχείρηση
    private String recipientCustomerId;
    private double amount;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private boolean isPaid;

    public Bill(String issuerVAT, String recipientCustomerId, double amount,
            LocalDate issueDate, LocalDate dueDate) {
        this.id = generateID();
        this.rfCode = generateRFcode();
        this.issuerVAT = issuerVAT;
        this.recipientCustomerId = recipientCustomerId;
        this.amount = amount;
        this.dueDate = dueDate;
        this.isPaid = false;
        this.issueDate = issueDate;
    }

    public Bill() {

    }

    private String generateRFcode() {
        Random random = new Random();
        StringBuilder unique = new StringBuilder();
        boolean exists = true;
        while (exists) {
            unique = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                int digit = random.nextInt(10);
                unique.append(digit);
            }
            exists = BillManager.getInstance().existsRF(unique.toString());
        }
        BillManager.getInstance().addRFcode(unique.toString());
        return "RF" + unique.toString();
    }

    private String generateID() {
        Random random = new Random();
        StringBuilder unique = new StringBuilder();
        boolean exists = true;
        while (exists) {
            unique = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                int digit = random.nextInt(10);
                unique.append(digit);
            }
            exists = BillManager.getInstance().existsBillID(unique.toString());
        }
        BillManager.getInstance().addBillID(unique.toString());
        return "BI" + unique.toString();
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void markAsPaid() {
        this.isPaid = true;
    }

    public String getId() {
        return id;
    }

    public String getRfCode() {
        return rfCode;
    }

    public String getIssuerVAT() {
        return issuerVAT;
    }

    public String getRecipientCustomerId() {
        return recipientCustomerId;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isDue(LocalDate date) {
        return !isPaid && dueDate.equals(date);
    }

    @Override
    public String marshal() {
        // type, paymentCode, billNumber, issuer, customer, amount, issueDate, dueDate,
        // isPaid
        return String.join(",",
                "type:Bill",
                "paymentCode:" + rfCode,
                "billNumber:" + id,
                "issuer:" + issuerVAT,
                "customer:" + recipientCustomerId,
                "amount:" + amount,
                "issueDate:" + issueDate,
                "dueDate:" + dueDate);
    }

    @Override
    public void unmarshal(String data) throws UnMarshalingException {
        String[] parts = data.split(",");
        // we expect at least 8 fields, but we'll just trust keys
        for (String p : parts) {
            String[] kv = p.split(":", 2);
            if (kv.length != 2)
                throw new UnMarshalingException("Bad field: " + p);
            String key = kv[0], val = kv[1];
            switch (key) {
                case "type":
                    if (!"Bill".equals(val))
                        throw new UnMarshalingException("Wrong type: " + val);
                    break;
                case "paymentCode":
                    this.rfCode = val;
                    break;
                case "billNumber":
                    this.id = val;
                    break;
                case "issuer":
                    this.issuerVAT = val;
                    break;
                case "customer":
                    this.recipientCustomerId = val;
                    break;
                case "amount":
                    this.amount = Double.parseDouble(val);
                    break;
                case "issueDate":
                    this.issueDate = LocalDate.parse(val);
                    break;
                case "dueDate":
                    this.dueDate = LocalDate.parse(val);
                    break;
                // paid=false by default
                default:
                    break;
            }
        }
    }

}