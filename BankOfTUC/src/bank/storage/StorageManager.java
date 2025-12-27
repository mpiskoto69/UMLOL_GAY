package bank.storage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;

import accounts.BankAccount;
import managers.AccountManager;
import managers.BillManager;
import managers.StandingOrderManager;
import managers.UserManager;
import standingOrders.Bill;
import standingOrders.PaymentOrder;
import standingOrders.StandingOrder;
import standingOrders.TransferOrder;
import transactions.AccountStatement;
import users.User;

public class StorageManager {
	private static StorageManager instance;
	private String storagePath;

	private StorageManager() {
		this.storagePath = "./data/";
	}

	public static StorageManager getInstance() {
		if (instance == null) {
			instance = new StorageManager();
		}
		return instance;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath.endsWith(File.separator)
				? storagePath
				: storagePath + File.separator;
	}

	public <T extends Storable> void storeObject(T storable, String fileName)
			throws IOException {
		File file = new File(storagePath + fileName);

		// ← new: create parent directories if needed
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			if (!parent.mkdirs()) {
				throw new IOException("Could not create directory: "
						+ parent.getAbsolutePath());
			}
		}

		try (FileWriter writer = new FileWriter(file)) {
			writer.write(storable.marshal());
		}
	}

	public <T extends Storable> void loadObject(T storable, String fileName)
			throws IOException, UnMarshalingException {
		File f = new File(storagePath + fileName);
		if (!f.exists() || !f.canRead())
			throw new FileNotFoundException(f.getPath());
		StringBuilder sb = new StringBuilder();
		try (BufferedReader r = new BufferedReader(new FileReader(f))) {
			String line;
			while ((line = r.readLine()) != null) {
				sb.append(line).append("\n");
			}
		}
		storable.unmarshal(sb.toString());
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

	public void loadIssuedBills(String fileName) {
		StorableList<Bill> issued = new StorableList<>();
		try {
			loadObject(issued, fileName);
			// leave isPaid==false (default from unmarshal ctor)
			BillManager.getInstance().addBills(issued);
		} catch (IOException e) {
			System.out.println("Could not open issued file: " + e.getMessage());
		} catch (UnMarshalingException e) {
			System.out.println("Could not parse issued data: " + e.getMessage());
		}
	}

	public void loadPaidBills(String fileName) {
		StorableList<Bill> paid = new StorableList<>();
		try {
			loadObject(paid, fileName);
			// explicitly mark each one paid
			for (Bill b : paid) {
				b.markAsPaid();
			}
			BillManager.getInstance().addBills(paid);
		} catch (IOException e) {
			System.out.println("Could not open paid file: " + e.getMessage());
		} catch (UnMarshalingException e) {
			System.out.println("Could not parse paid data: " + e.getMessage());
		}
	}

	public void storeBills(String folderName) {
		Path dir = Paths.get(storagePath, folderName);
		try {
			Files.createDirectories(dir);
		} catch (IOException e) {
			System.err.println("Could not create bills directory: " + e.getMessage());
			return;
		}

		// prepare writers for the two files (overwrite each time)
		Path issuedFile = dir.resolve("issued.csv");
		Path paidFile = dir.resolve("paid.csv");
		try (
				BufferedWriter issuedW = Files.newBufferedWriter(
						issuedFile,
						StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING);
				BufferedWriter paidW = Files.newBufferedWriter(
						paidFile,
						StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING)) {
			List<Bill> all = BillManager.getInstance().getAllBills();
			for (Bill b : all) {
				if (b.isPaid()) {
					paidW.write(b.marshal());
					paidW.newLine();
				} else {
					issuedW.write(b.marshal());
					issuedW.newLine();
				}
			}
		} catch (IOException ioe) {
			System.err.println("Error writing bills files: " + ioe.getMessage());
		}
	}

	private void loadOrdersFile(String folderName, String fileName/* , StandingOrder.Status fileStatus */) {
		File file = new File(storagePath + folderName + "/" + fileName);
		if (!file.exists())
			return;

		StorableList<StandingOrder> list = new StorableList<>() {
			@Override
			public void unmarshal(String data) throws UnMarshalingException {
				String[] lines = data.split("\\R");
				for (String line : lines) {
					if (line.isBlank())
						continue;
					String type = line.split(",", 2)[0].substring("type:".length());
					StandingOrder so;
					switch (type) {
						case "PaymentOrder":
							so = new PaymentOrder();
							break;
						case "TransferOrder":
							so = new TransferOrder();
							break;
						default:
							throw new UnMarshalingException("Unknown order type: " + type);
					}
					so.unmarshal(line);
					// ← **set the status based on which file you’re in**:
					// so.setStatus(fileStatus);
					this.add((StandingOrder) so);
				}
			}
		};

		try {
			loadObject(list, folderName + "/" + fileName);
			StandingOrderManager.getInstance().addOrders(list);
		} catch (IOException | UnMarshalingException e) {
			System.err.println("Cannot load " + fileName + ": " + e.getMessage());
		}
	}

	public void loadActiveOrders(String folder) {
		loadOrdersFile(folder, "active.csv"/* , StandingOrder.Status.ACTIVE */);
	}

	public void loadExpiredOrders(String folder) {
		loadOrdersFile(folder, "expired.csv"/* , StandingOrder.Status.EXPIRED */);
	}

	public void loadFailedOrders(String folder) {
		loadOrdersFile(folder, "failed.csv"/* , StandingOrder.Status.FAILED */);
	}

	public void storeOrders(String folderName) {
		File dir = new File(storagePath + folderName);
		dir.mkdirs(); // create if missing

		// clear out old files
		new File(dir, "active.csv").delete();
		new File(dir, "expired.csv").delete();
		new File(dir, "failed.csv").delete();

		List<StandingOrder> all = StandingOrderManager.getInstance().getAllOrders();
		LocalDate today = LocalDate.now();

		for (StandingOrder so : all) {
			String fn;
			if (so.hasExceededMaxFailures()) {
				fn = "failed.csv";
			} else if (!so.isActive(today)) {
				fn = "expired.csv";
			} else {
				fn = "active.csv";
			}

			File out = new File(dir, fn);
			try (BufferedWriter w = new BufferedWriter(new FileWriter(out, true))) {
				w.write(((bank.storage.Storable) so).marshal());
				w.newLine();
			} catch (IOException ioe) {
				System.err.println("Error writing " + fn + ": " + ioe.getMessage());
			}
		}
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
		if (!Files.exists(file))
			return;

		try (BufferedReader r = Files.newBufferedReader(file)) {
			String line;
			BankAccount acct = AccountManager.getInstance().findByIban(iban);
			while ((line = r.readLine()) != null) {
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

	public void loadAllStatements(String folderName) {
		for (File f : listCsvFiles(folderName)) {
			String name = f.getName();
			String iban = name.substring(0, name.length() - 4); // drop “.csv”
			loadStatements(folderName, iban);
		}
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

	private File[] listCsvFiles(String folderName) {
		File dir = new File(storagePath + folderName);
		if (!dir.isDirectory())
			return new File[0];

		return dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File d, String name) {
				return name.endsWith(".csv");
			}
		});
	}

}
