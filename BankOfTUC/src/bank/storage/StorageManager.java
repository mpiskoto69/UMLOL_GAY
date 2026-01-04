
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

import standingOrders.StandingOrder;
import transactions.AccountStatement;

public class StorageManager {
    private static final StorageManager instance = new StorageManager();
 private String storagePath = resolveDataPath();
private Path metaFile() {
    return Paths.get(storagePath, "meta", "date.txt");
}


private String resolveDataPath() {
    File d1 = new File("./data");
    if (d1.exists() && d1.isDirectory()) return "./data/";

    File d2 = new File("../data");
    if (d2.exists() && d2.isDirectory()) return "../data/";

    File d3 = new File("./BankOfTUC/data");
    if (d3.exists() && d3.isDirectory()) return "./BankOfTUC/data/";

    return "./data/";
}


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

    // Load 
    public void loadAll() {
    System.out.println("Storage path=" + storagePath);
     createCheckpointIfMissing();
    UserManager.getInstance().clearAll();
    AccountManager.getInstance().clearAll();
    BillManager.getInstance().clearAll();
    StandingOrderManager.getInstance().clearAll();

    loadUsers("users/users.csv");
    loadAccounts("accounts/accounts.csv");
    loadBills();
    loadOrders();
    loadAllStatements("statements");
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

    //  Save 
   public void saveAll(LocalDate simulatedToday) {
    storeUsers("users/users.csv");
    storeAccounts("accounts/accounts.csv");
    storeBills();
    storeOrders(simulatedToday);
    storeAllStatements("statements");

    saveCurrentDate(simulatedToday);
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

    private <T extends Storable> StorableList<T> toStorableList(List<T> list) {
        StorableList<T> sl = new StorableList<>();
        sl.addAll(list);
        return sl;
    }

    
   
	public void storeAllStatements(String folderName) {
		Path dir = Paths.get(storagePath, folderName);
		try {
			Files.createDirectories(dir);
		} catch (IOException e) {
			System.err.println("Could not create statements directory: " + e.getMessage());
			return;
		}

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

  private void saveCurrentDate(LocalDate d) {
    try {
        Path f = metaFile();
        Files.createDirectories(f.getParent());
        Files.writeString(f, d.toString(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    } catch (Exception ignored) {}
}
public void saveCurrentDatePublic(LocalDate d) {
    saveCurrentDate(d);
}

public LocalDate loadCurrentDateOrDefault() {
    try {
        Path f = metaFile();
        if (!Files.exists(f)) return LocalDate.now();
        return LocalDate.parse(Files.readString(f).trim());
    } catch (Exception e) {
        return LocalDate.now();
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
        BankAccount acct = AccountManager.getInstance().findByIban(iban);

        acct.getStatements().clear();

        String line;
        while ((line = r.readLine()) != null) {
            if (line.isBlank()) continue;
            AccountStatement s = new AccountStatement();
            s.unmarshal(line);
            acct.addStatement(s);
        }
    } catch (Exception e) {
        System.err.println("loadStatements error: " + e.getMessage());
    }
}


private File[] listCsvFiles(String folderName) {
    File dir = new File(storagePath + folderName);
    if (!dir.isDirectory()) return new File[0];

    return dir.listFiles((d, name) -> name.endsWith(".csv"));
}

private Path checkpointDir() {
    return Paths.get(storagePath, "_checkpoint");
}

public void createCheckpointIfMissing() {
    try {
        Path cp = checkpointDir();
        if (Files.exists(cp)) return;

        Files.createDirectories(cp);

        // copy users, accounts, bills, orders, statements, meta
        copyIfExists(p("users"), cp.resolve("users"));
        copyIfExists(p("accounts"), cp.resolve("accounts"));
        copyIfExists(p("bills"), cp.resolve("bills"));
        copyIfExists(p("orders"), cp.resolve("orders"));
        copyIfExists(p("statements"), cp.resolve("statements"));
        copyIfExists(p("meta"), cp.resolve("meta"));

        System.out.println("Checkpoint created at: " + cp.toAbsolutePath());
    } catch (Exception e) {
        System.err.println("Checkpoint creation failed: " + e.getMessage());
    }
}

public void restoreCheckpoint() throws IOException {
    Path cp = checkpointDir();
    if (!Files.exists(cp)) throw new FileNotFoundException("Checkpoint not found: " + cp);

    // delete current data folders 
    deleteIfExists(p("users"));
    deleteIfExists(p("accounts"));
    deleteIfExists(p("bills"));
    deleteIfExists(p("orders"));
    deleteIfExists(p("statements"));
    deleteIfExists(p("meta"));

    // restore from checkpoint
    copyIfExists(cp.resolve("users"), p("users"));
    copyIfExists(cp.resolve("accounts"), p("accounts"));
    copyIfExists(cp.resolve("bills"), p("bills"));
    copyIfExists(cp.resolve("orders"), p("orders"));
    copyIfExists(cp.resolve("statements"), p("statements"));
    copyIfExists(cp.resolve("meta"), p("meta"));

    System.out.println("Checkpoint restored.");
}

public void restoreCheckpointAndReload() throws IOException {
    restoreCheckpoint();

    // ⚠️ καθάρισε τα πάντα από τη μνήμη
    UserManager.getInstance().clearAll();
    AccountManager.getInstance().clearAll();
    BillManager.getInstance().clearAll();
    StandingOrderManager.getInstance().clearAll();

    // 🔄 φόρτωσε ΞΑΝΑ από τα restored αρχεία
    loadAll();
}
public void resetToRealToday() {
    try {
        restoreCheckpoint();                // 1. restore snapshot
        LocalDate realToday = LocalDate.now();
        saveCurrentDate(realToday);         // 2. γράψε ΠΡΑΓΜΑΤΙΚΗ ημερομηνία
        System.out.println("RESET done. Today is: " + realToday);
    } catch (Exception e) {
        throw new RuntimeException("Reset failed", e);
    }
}

private void copyIfExists(Path src, Path dst) throws IOException {
    if (!Files.exists(src)) return;
    Files.walk(src).forEach(from -> {
        try {
            Path to = dst.resolve(src.relativize(from));
            if (Files.isDirectory(from)) {
                Files.createDirectories(to);
            } else {
                Files.createDirectories(to.getParent());
                Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    });
}

private void deleteIfExists(Path path) throws IOException {
    if (!Files.exists(path)) return;

    Files.walk(path)
        .sorted((a, b) -> b.compareTo(a))
        .forEach(p -> {
            try {
                Files.deleteIfExists(p);
            } catch (IOException ex) {
                System.err.println("DELETE FAILED: " + p + " -> " + ex.getMessage());
            }
        });
}



}
