package managers;

import bank.storage.Bill;
import bank.storage.StorableList;
import java.time.LocalDate;
import java.util.*;

public class BillManager {

    private static final BillManager instance = new BillManager();

    private final StorableList<Bill> bills = new StorableList<>();

    private final ArrayList<String> RFcodes = new ArrayList<>();
    private final ArrayList<String> billIDs = new ArrayList<>();

    private final Set<String> seenKeys = new HashSet<>();

    private BillManager() {}

    public static BillManager getInstance() {
        return instance;
    }

    private String keyOf(Bill b) {
        if (b == null)
            return null;

        String id = b.getId();
        if (id != null && !id.isBlank())
            return "ID:" + id;

        String rf = b.getRfCode();
        if (rf != null && !rf.isBlank())
            return "RF:" + rf;

        return null;
    }

    public void createBill(String issuerVat, String recipientCustomerId, double amount,
                           LocalDate issueDate, LocalDate dueDate) {
        Bill bill = new Bill(issuerVat, recipientCustomerId, amount, issueDate, dueDate);
        addBill(bill);
    }

    public void addBill(Bill bill) {
        if (bill == null)
            return;

        String key = keyOf(bill);
        if (key == null)
            return;

        if (seenKeys.add(key)) {
            bills.add(bill);

            if (bill.getRfCode() != null && bill.getRfCode().startsWith("RF") && bill.getRfCode().length() > 2) {
                addRFcode(bill.getRfCode().substring(2));
            }
            if (bill.getId() != null) {
                addBillID(bill.getId());
            }
        }
    }

    public void addBills(StorableList<Bill> incoming) {
        if (incoming == null)
            return;
        for (Bill b : incoming)
            addBill(b);
    }

    public StorableList<Bill> getAllBills() {
        return bills;
    }

    public Bill getUnpaidBill(String rfCode, LocalDate today) {
        if (rfCode == null)
            return null;

        for (Bill b : bills) {
            if (b == null)
                continue;
            if (rfCode.equals(b.getRfCode()) && b.isDue(today)) {
                return b;
            }
        }
        return null;
    }

    public ArrayList<Bill> getPaidBills() {
        ArrayList<Bill> paidBills = new ArrayList<>();
        for (Bill b : bills) {
            if (b != null && b.isPaid())
                paidBills.add(b);
        }
        return paidBills;
    }

    public boolean isBillDueToday(String rfCode, LocalDate today) {
        return getUnpaidBill(rfCode, today) != null;
    }

    public void addRFcode(String rf) {
        if (rf == null)
            return;
        if (!RFcodes.contains(rf))
            RFcodes.add(rf);
    }

    public boolean existsRF(String rf) {
        return rf != null && RFcodes.contains(rf);
    }

    public void addBillID(String id) {
        if (id == null)
            return;
        if (!billIDs.contains(id))
            billIDs.add(id);
    }

    public boolean existsBillID(String id) {
        return id != null && billIDs.contains(id);
    }

    public void clearAll() {
        bills.clear();
        RFcodes.clear();
        billIDs.clear();
        seenKeys.clear();
    }

    public List<Bill> getBillsIssuedBy(String issuerVat) {
        List<Bill> out = new ArrayList<>();
        if (issuerVat == null)
            return out;

        for (Bill b : bills) {
            if (b != null && issuerVat.equals(b.getIssuerVAT()))
                out.add(b);
        }
        return out;
    }

    public List<Bill> getBillsIssuedByStatus(String issuerVat, Boolean paid) {
        List<Bill> out = new ArrayList<>();
        if (issuerVat == null)
            return out;

        for (Bill b : bills) {
            if (b == null)
                continue;
            if (!issuerVat.equals(b.getIssuerVAT()))
                continue;

            if (paid == null)
                out.add(b);
            else if (paid && b.isPaid())
                out.add(b);
            else if (!paid && !b.isPaid())
                out.add(b);
        }
        return out;
    }

    public List<Bill> getBillsToPayBy(String recipientVat) {
        List<Bill> out = new ArrayList<>();
        if (recipientVat == null)
            return out;

        for (Bill b : bills) {
            if (b != null && recipientVat.equals(b.getRecipientCustomerId()))
                out.add(b);
        }
        return out;
    }
}
