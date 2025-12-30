package bank.storage;

import java.time.LocalDate;
import java.util.Random;
import managers.BillManager;

public class Bill implements Storable {

    private static final Random RANDOM = new Random();

    private String id;                 // e.g. BIxxxxxxxxxxxxxxxx
    private String rfCode;             // e.g. RFxxxxxxxx
    private String issuerVAT;          // επιχείρηση (VAT)
    private String recipientCustomerId; // πελάτης (VAT ή id όπως το ορίζετε)
    private double amount;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private boolean isPaid;

    /** Business constructor: creates a NEW bill */
    public Bill(String issuerVAT, String recipientCustomerId, double amount,
                LocalDate issueDate, LocalDate dueDate) {
        this.id = generateID();
        this.rfCode = generateRFcode();
        this.issuerVAT = issuerVAT;
        this.recipientCustomerId = recipientCustomerId;
        this.amount = amount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.isPaid = false;
    }

    /** Empty constructor: for unmarshal ONLY (do not generate IDs here) */
    public Bill() {
        // intentionally empty
    }

    private String generateRFcode() {
        String uniquePart;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8; i++) sb.append(RANDOM.nextInt(10));
            uniquePart = sb.toString();
        } while (BillManager.getInstance().existsRF(uniquePart));

        BillManager.getInstance().addRFcode(uniquePart);
        return "RF" + uniquePart;
    }

    private String generateID() {
        String uniquePart;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) sb.append(RANDOM.nextInt(10));
            uniquePart = sb.toString();
        } while (BillManager.getInstance().existsBillID(uniquePart));

        BillManager.getInstance().addBillID(uniquePart);
        return "BI" + uniquePart;
    }

    // --- domain methods ---
    public boolean isPaid() { return isPaid; }

    public void markAsPaid() { this.isPaid = true; }

    public String getId() { return id; }

    public String getRfCode() { return rfCode; }

    public String getIssuerVAT() { return issuerVAT; }

    public String getRecipientCustomerId() { return recipientCustomerId; }

    public double getAmount() { return amount; }

    public LocalDate getIssueDate() { return issueDate; }

    public LocalDate getDueDate() { return dueDate; }

public boolean isDue(LocalDate today) {
    return !isPaid && !today.isBefore(dueDate);
}



    // --- storage ---
    @Override
    public String marshal() {
        return String.join(",",
            "type:Bill",
            "paymentCode:" + rfCode,
            "billNumber:" + id,
            "issuer:" + issuerVAT,
            "customer:" + recipientCustomerId,
            "amount:" + amount,
            "issueDate:" + issueDate,
            "dueDate:" + dueDate,
            "isPaid:" + isPaid
        );
    }

    @Override
    public void unmarshal(String data) throws UnMarshalingException {
        String[] parts = data.split(",");
        if (parts.length == 0) throw new UnMarshalingException("Empty bill line");

        for (String p : parts) {
            if (p.isBlank()) continue;

            String[] kv = p.split(":", 2);
            if (kv.length != 2) throw new UnMarshalingException("Bad field: " + p);

            String key = kv[0];
            String val = kv[1];

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
                case "isPaid":
                    this.isPaid = Boolean.parseBoolean(val);
                    break;
                default:
                    // ignore unknown keys
                    break;
            }
        }

        // register identifiers so future generated ones won't collide
        if (rfCode != null && rfCode.startsWith("RF") && rfCode.length() > 2) {
            BillManager.getInstance().addRFcode(rfCode.substring(2));
        }
        if (id != null && id.startsWith("BI") && id.length() > 2) {
            BillManager.getInstance().addBillID(id.substring(2));
        }
    }
}
