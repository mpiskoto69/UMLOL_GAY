package managers;

import java.time.LocalDate;
import java.util.ArrayList;

import bank.storage.StorableList;
import bank.storage.Bill;

public class BillManager {
    private static final BillManager instance = new BillManager();
    private StorableList<Bill> bills;
    private ArrayList<String> RFcodes;
    private ArrayList<String> billIDs;
    private BillManager() {
        bills = new StorableList<>();
        RFcodes = new ArrayList<>();
        billIDs = new ArrayList<>();
    }

    public static BillManager getInstance() {
        return instance;
    }

    public void createBill(String issuerName, String recipientCustomerId, double amount,
        LocalDate issuDate,  LocalDate dueDate) {
        Bill bill = new Bill(issuerName, recipientCustomerId, amount, issuDate, dueDate);
        bills.add(bill);
    }

    public void addBill(Bill bill) {
        bills.add(bill);
    }

    public StorableList<Bill> getAllBills() {
        return bills;
    }

    public Bill getUnpaidBill(String rfCode, LocalDate today) {
    for (Bill b : bills) {
        if (b.getRfCode().equals(rfCode) && b.isDue(today)) {
            return b;
        }
    }
    return null;
}

    public ArrayList<Bill> getPaidBills() {
        ArrayList<Bill> paidBills = new ArrayList<>();
        for (Bill b : this.bills) {
            if (b.isPaid())
                paidBills.add(b);
        }

        return paidBills;
    }

   public boolean isBillDueToday(String rfCode, LocalDate today) {
    return getUnpaidBill(rfCode, today) != null;
}


    public void addRFcode(String rf) {
        RFcodes.add(rf);
    }

    public boolean existsRF(String rf) {
        for (String code : RFcodes) {
            if (code.equals(rf))
                return true;
        }
        return false;
    }

    public void addBillID(String id) {
        billIDs.add(id);
    }

    public boolean existsBillID(String id) {
        for (String bid : billIDs) {
            if (id.equals(bid))
                return true;
        }
        return false;
    }

    public void addBills(StorableList<Bill> bills) {
        this.bills.addAll(bills);
    }
public void clearAll() {
    bills.clear();
    RFcodes.clear();
    billIDs.clear();
}

}
