
package bank.storage;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

import accounts.BankAccount;
import users.User;

import bank.storage.dao.BillDao;
import bank.storage.dao.StandingOrderDao;

import managers.AccountManager;
import managers.BillManager;
import managers.StandingOrderManager;
import managers.UserManager;

import standingOrders.Bill;
import standingOrders.StandingOrder;
import transactions.AccountStatement;

public class StorageManager {
    private static final StorageManager instance = new StorageManager();
    private String storagePath = "./data/";

    private StorageManager() {}

    public static StorageManager getInstance() {
        return instance;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath.endsWith("/") ? storagePath : storagePath + "/";
    }

    private Path p(String... parts) {
        return Paths.get(storagePath, parts);
    }

    // --- Load ---
    public void loadAll() {
        loadUsers("users/users.csv");
        loadAccounts("accounts/accounts.csv");

        loadBills();      // bills/issued.csv + bills/paid.csv
        loadOrders();     // orders/active.csv + orders/expired.csv + orders/failed.csv

        loadAllStatements("statements"); // your existing method is OK
    }

    public void loadUsers(String fileName) {
		StorableList<User> users = new StorableList<>();
		try {
			loadObject(users, fileName);
			UserManager.getInstance().addUsers(users);
		} catch (IOException e) {
			System.out.println("Could not open file: " + e.getMessage());
		} catch (UnMarshalingException e) {
			System.out.println("Could not load data from file: " + e.getMessage());
		}
	}

	public void loadAccounts(String fileName) {
		StorableList<BankAccount> accounts = new StorableList<>();
		try {
			loadObject(accounts, fileName);
			AccountManager.getInstance().addAccounts(accounts);
		} catch (IOException e) {
			System.out.println("Could not open file: " + e.getMessage());
		} catch (UnMarshalingException e) {
			System.out.println("Could not load data from file: " + e.getMessage());
		}
	}

    public void loadBills() {
        try {
            BillDao dao = new BillDao(p("bills"));
            List<Bill> all = dao.loadAll();
            BillManager.getInstance().addBills(toStorableList(all));
        } catch (IOException e) {
            System.err.println("Cannot load bills: " + e.getMessage());
        }
    }

    public void loadOrders() {
        try {
            StandingOrderDao dao = new StandingOrderDao(p("orders"));
            StorableList<StandingOrder> a = dao.loadFile("active.csv");
            StorableList<StandingOrder> e = dao.loadFile("expired.csv");
            StorableList<StandingOrder> f = dao.loadFile("failed.csv");

            StandingOrderManager.getInstance().addOrders(a);
            StandingOrderManager.getInstance().addOrders(e);
            StandingOrderManager.getInstance().addOrders(f);
        } catch (Exception ex) {
            System.err.println("Cannot load orders: " + ex.getMessage());
        }
    }

    // --- Save ---
    public void saveAll(LocalDate simulatedToday) {
        storeUsers("users/users.csv");
        storeAccounts("accounts/accounts.csv");

        storeBills();
        storeOrders(simulatedToday);

        storeAllStatements("statements");
    }

    
 public void storeUsers(String fileName) {
		try {
			storeObject(UserManager.getInstance().getAllUsers(), fileName);
		} catch (IOException e) {
			System.out.println("Could not create file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void storeAccounts(String fileName) {
		try {
			storeObject(AccountManager.getInstance().getAllAccounts(), fileName);
		} catch (IOException e) {
			System.out.println("Could not create file: " + e.getMessage());
			e.printStackTrace();
		}
	}


    public void storeBills() {
        try {
            BillDao dao = new BillDao(p("bills"));
            dao.saveAll(BillManager.getInstance().getAllBills());
        } catch (IOException e) {
            System.err.println("Cannot store bills: " + e.getMessage());
        }
    }

    public void storeOrders(LocalDate simulatedToday) {
        try {
            StandingOrderDao dao = new StandingOrderDao(p("orders"));
            dao.saveByStatus(StandingOrderManager.getInstance().getAllOrders(), simulatedToday);
        } catch (IOException e) {
            System.err.println("Cannot store orders: " + e.getMessage());
        }
    }

    // helper
    private <T extends Storable> StorableList<T> toStorableList(List<T> list) {
        StorableList<T> sl = new StorableList<>();
        sl.addAll(list);
        return sl;
    }

    
   
	public void storeAllStatements(String folderName) {
		// ensure directory exists
		Path dir = Paths.get(storagePath, folderName);
		try {
			Files.createDirectories(dir);
		} catch (IOException e) {
			System.err.println("Could not create statements directory: " + e.getMessage());
			return;
		}

		// for each account, dump its statements to <iban>.csv
		for (BankAccount acct : AccountManager.getInstance().getAllAccounts()) {
			String iban = acct.getIban();
			storeStatements(folderName, iban);
		}
	}
   public void loadAllStatements(String folderName) {
		for (File f : listCsvFiles(folderName)) {
			String name = f.getName();
			String iban = name.substring(0, name.length() - 4); // drop “.csv”
			loadStatements(folderName, iban);
		}
	}
public <T extends Storable> void storeObject(T storable, String fileName) throws IOException {
    File file = new File(storagePath + fileName);

    File parent = file.getParentFile();
    if (parent != null && !parent.exists()) {
        if (!parent.mkdirs()) {
            throw new IOException("Could not create directory: " + parent.getAbsolutePath());
        }
    }

    try (FileWriter writer = new FileWriter(file)) {
        writer.write(storable.marshal());
    }
}

public <T extends Storable> void loadObject(T storable, String fileName) throws IOException, UnMarshalingException {
    File f = new File(storagePath + fileName);
    if (!f.exists() || !f.canRead()) throw new FileNotFoundException(f.getPath());

    StringBuilder sb = new StringBuilder();
    try (BufferedReader r = new BufferedReader(new FileReader(f))) {
        String line;
        while ((line = r.readLine()) != null) sb.append(line).append("\n");
    }
    storable.unmarshal(sb.toString());
}
// --- Statements helpers (required by storeAllStatements/loadAllStatements) ---

public void storeStatements(String folderName, String iban) {
    Path dir = Paths.get(storagePath, folderName);
    try {
        Files.createDirectories(dir);
    } catch (IOException e) {
        System.err.println("Cannot create statements directory: " + e);
        return;
    }

    Path file = dir.resolve(iban + ".csv");
    try (BufferedWriter w = Files.newBufferedWriter(
            file,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING)) {

        BankAccount acct = AccountManager.getInstance().findByIban(iban);
        List<AccountStatement> stmts = acct.getStatements();
        for (AccountStatement s : stmts) {
            w.write(s.marshal());
            w.newLine();
        }
    } catch (IOException ioe) {
        System.err.println("Error writing " + file + ": " + ioe.getMessage());
    }
}

public void loadStatements(String folderName, String iban) {
    Path file = Paths.get(storagePath, folderName, iban + ".csv");
    if (!Files.exists(file)) return;

    try (BufferedReader r = Files.newBufferedReader(file)) {
        String line;
        BankAccount acct = AccountManager.getInstance().findByIban(iban);
        while ((line = r.readLine()) != null) {
            if (line.isBlank()) continue;
            AccountStatement s = new AccountStatement();
            s.unmarshal(line);
            acct.addStatement(s);
        }
    } catch (IOException e) {
        System.err.println("I/O error reading " + file + ": " + e.getMessage());
    } catch (UnMarshalingException e) {
        System.err.println("Malformed statement in " + file + ": " + e.getMessage());
    }
}

private File[] listCsvFiles(String folderName) {
    File dir = new File(storagePath + folderName);
    if (!dir.isDirectory()) return new File[0];

    return dir.listFiles((d, name) -> name.endsWith(".csv"));
}

}
