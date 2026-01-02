package accounts;

import java.time.LocalDate;
import bank.storage.UnMarshalingException;
import users.Company;

public class MasterAccount extends BusinessAccount {
    private static final MasterAccount instance = new MasterAccount();

    private MasterAccount() {
        super(); // empty, will be filled by init/unmarshal
    }

    public static MasterAccount getInstance() {
        return instance;
    }

    /** Call ONCE on startup (if not loaded from CSV) */
    public void initIfNeeded(Company bankCompany) {
        if (this.primaryHolder != null) return; // already initialized (e.g. from unmarshal)

        this.primaryHolder = bankCompany;
        this.interestRate = 0.0;
        this.dateCreated = LocalDate.now();
        // keep existing iban if already set, else generate one
        if (this.iban == null || this.iban.isBlank()) {
            this.iban = "GR200" + System.currentTimeMillis(); // ή δικό σου generator
        }
        this.balance = 10000.0;

        // σημαντικό: σύνδεση company <-> account
        bankCompany.addAccount(this);
    }

    @Override
    public void endOfMonth() { }

    @Override
    public String marshal() {
        return String.join(",",
                "type:MasterAccount",
                "iban:" + iban,
                "primaryOwner:" + primaryHolder.getVatNumber(),
                "dateCreated:" + dateCreated,
                "rate:" + interestRate,
                "balance:" + balance);
    }

    @Override
    public void unmarshal(String data) throws UnMarshalingException {
        // ίδιος κώδικας όπως έχεις, απλά ΔΕΝ κάνεις addAccount εδώ.
        String[] parts = data.split(",");
        for (String p : parts) {
            String[] kv = p.split(":", 2);
            if (kv.length != 2) throw new UnMarshalingException("Bad field: " + p);

            String key = kv[0], val = kv[1];
            switch (key) {
                case "type":
                    if (!"MasterAccount".equals(val))
                        throw new UnMarshalingException("Wrong type: " + val);
                    break;
                case "iban":
                    this.iban = val;
                    break;
                case "primaryOwner":
                    // το company πρέπει να υπάρχει ήδη στους users (άρα users load ΠΡΙΝ accounts load)
                    // εδώ απλά θα γίνει set από τον loader σου όπως κάνεις ήδη
                    // (θα το αφήσεις όπως είναι στο δικό σου unmarshal αν θες)
                    break;
                case "dateCreated":
                    this.dateCreated = LocalDate.parse(val);
                    break;
                case "rate":
                    this.interestRate = Double.parseDouble(val);
                    break;
                case "balance":
                    this.balance = Double.parseDouble(val);
                    break;
                default:
                    break;
            }
        }
    }
}
