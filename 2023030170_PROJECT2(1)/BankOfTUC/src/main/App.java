package main;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import accounts.BankAccount;
import bank.storage.StorageManager;
import managers.AccountManager;
import managers.BillManager;
import managers.StandingOrderManager;
import managers.TransactionManager;
import managers.UserManager;
import standingOrders.Bill;
import standingOrders.StandingOrder;
import transactions.AccountStatement;
import transactions.Deposit;
import transactions.Payment;
import transactions.Transfer;
import transactions.Withdrawall;
import users.Admin;
import users.Company;
import users.Customer;
import users.Individual;
import users.User;

public class App {
	// Scanner for all console input
	private final Scanner in = new Scanner(System.in);
	// Tracks the current date for simulation purposes
	private LocalDate currentDate = LocalDate.now();

	/** Application entry point */
	public static void main(String[] args) {
		App app = new App();
		app.run();
	}

	/** Runs login + dispatches to the appropriate menu */
	public void run() {
		load();
		dailyTasks();

		while (true) {

			// 1) Log in
			User self = loginPrompt();

			// 2) Depending on role, show the correct menu
			switch (self.getRole()) {
				case "Individual":
					runIndividualMenu((Individual) self);
					break;
				case "Company":
					runCompanyMenu((Company) self);
					break;
				case "Admin":
					runAdminMenu((Admin) self);
					break;
				default:
					System.out.println("Unknown role, restarting.");
					run();
					return;
			}
		}
	}

	private User loginPrompt() {
		while (true) {
			System.out.print("Username: ");
			String username = in.nextLine().trim();
			System.out.print("Password: ");
			String password = in.nextLine().trim();

			User u = UserManager.getInstance().findUserByUsername(username);
			if (u != null && u.login(password)) {
				System.out.printf("Welcome, %s! (Role: %s)%n", username, u.getRole());
				return u;
			}
			System.out.println("Invalid credentials, please try again.");
		}
	}

	private void runIndividualMenu(Individual self) {
		List<Option> opts = List.of(new Option() {
			@Override
			public String getLabel() {
				return "Overview";
			}

			@Override
			public void execute() {
				System.out.println("Your accounts:");

				ArrayList<BankAccount> accs = self.getAccounts();

				for (BankAccount acc : accs) {

					System.out.printf("primary holder:%s, %s , %.2f€%n", acc.getPrimaryHolder().getVatNumber(),
							acc.getIban(), acc.getBalance());
				}
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Transactions";
			}

			@Override
			public void execute() {
				runTransactionsMenu(self);
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Logout";
			}

			@Override
			public void execute() {
				save();
				throw new Menu.ExitMenuException();
			}
		});

		new Menu("Individual Menu", opts).run();
	}

	private void runCompanyMenu(Company self) {
		List<Option> opts = List.of(new Option() {
			@Override
			public String getLabel() {
				return "Overview";
			}

			@Override
			public void execute() {
				System.out.println("Company accounts:");
				ArrayList<BankAccount> accs = self.getAccounts();

				for (BankAccount acc : accs) {
					System.out.printf("IBAN: %s -> Balance: %.2f€%n", acc.getIban(), acc.getBalance());
				}
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Bills";
			}

			@Override
			public void execute() {
				runBillsMenu(self);
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Logout";
			}

			@Override
			public void execute() {
				save();
				throw new Menu.ExitMenuException();
			}
		});

		new Menu("Company Menu", opts).run();
	}

	private void runAdminMenu(Admin self) {
		List<Option> opts = List.of(new Option() {
			@Override
			public String getLabel() {
				return "Customers";
			}

			@Override
			public void execute() {
				runCustomersMenu(self);
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Bank Accounts";
			}

			@Override
			public void execute() {
				runBankAccountsMenu(self);
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Company Bills";
			}

			@Override
			public void execute() {
				runCompanyBillsMenu(self);
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "List Standing Orders";
			}

			@Override
			public void execute() {
				System.out.println("Standing orders:");
				for (StandingOrder so : StandingOrderManager.getInstance().getAllOrders()) {
					System.out.println(so.getTitle() + " | " + so.getDescription() + " | Start: " + so.getStartDate()
							+ " | End: " + so.getEndDate());
				}
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Simulate Time Passing";
			}

			@Override
			public void execute() {
				System.out.print("End date (YYYY-MM-DD): ");
				LocalDate end = LocalDate.parse(in.nextLine().trim());
				simulateTimePassing(end);
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Pay Customer's Bill";
			}

			@Override
			public void execute() {
				System.out.print("Enter IBAN: ");
				String iban = in.nextLine().trim();
				BankAccount acct = AccountManager.getInstance().findByIban(iban);
				Customer cust = acct.getPrimaryHolder();
				if (cust instanceof Individual)
					doPayBill((Individual) cust);
				else
					System.out.println("Iban " + iban + " does not belong to individual");
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Logout";
			}

			@Override
			public void execute() {
				save();
				throw new Menu.ExitMenuException();
			}
		});

		new Menu("Admin Menu", opts).run();
	}

	private void runTransactionsMenu(Individual self) {
		List<Option> opts = List.of(new Option() {
			@Override
			public String getLabel() {
				return "Withdraw";
			}

			@Override
			public void execute() {
				doWithdraw(self);
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Deposit";
			}

			@Override
			public void execute() {
				doDeposit(self);
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Transfer";
			}

			@Override
			public void execute() {
				doTransfer(self);
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Pay Bill";
			}

			@Override
			public void execute() {
				doPayBill(self);
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Go back";
			}

			@Override
			public void execute() {
				throw new Menu.ExitMenuException();
			}
		});

		new Menu("Transactions Menu", opts).run();
	}

	private void runBillsMenu(Company self) {
		List<Option> opts = List.of(new Option() {
			@Override
			public String getLabel() {
				return "Load Issued Bills";
			}

			@Override
			public void execute() {
				System.out.print("Enter CSV path: ");
				String path = in.nextLine().trim();
				StorageManager.getInstance().loadIssuedBills(path);

			}
		},
				new Option() {
					@Override
					public String getLabel() {
						return "Show Issued Bills";
					}

					@Override
					public void execute() {
						System.out.println("Issued bills:");
						for (Bill bill : BillManager.getInstance().getAllBills()) {
							if (bill.getIssuerVAT().equals(self.getVatNumber())) {
								System.out.printf(
										"  Bill #%s – Date: %s – Amount: %.2f - Issuer Name: %s - Recipient Customer ID: %s - Is Paid: %s%n",
										bill.getId(),
										bill.getDueDate(),
										bill.getAmount(),
										bill.getIssuerVAT(),
										bill.getRecipientCustomerId(),
										bill.isPaid() ? "Paid" : "Unpaid");
							}
						}

					}
				},

				new Option() {
					@Override
					public String getLabel() {
						return "Show Paid Bills";
					}

					@Override
					public void execute() {
						System.out.println("Paid bills:");
						for (Bill bill : BillManager.getInstance().getAllBills()) {
							if (bill.isPaid() && bill.getIssuerVAT().equals(self.getVatNumber())) {
								System.out.printf(
										"  Bill #%s – Date: %s – Amount: %.2f - Issuer Name: %s - Recipient Customer ID: %s - Is Paid: %s%n",
										bill.getId(),
										bill.getDueDate(),
										bill.getAmount(),
										bill.getIssuerVAT(),
										bill.getRecipientCustomerId(),
										bill.isPaid() ? "Paid" : "Unpaid");
							}
						}

					}
				}, new Option() {
					@Override
					public String getLabel() {
						return "Go back";
					}

					@Override
					public void execute() {
						throw new Menu.ExitMenuException();
					}
				});

		new Menu("Bills Menu", opts).run();
	}

	private void runCustomersMenu(Admin self) {
		List<Option> opts = List.of(new Option() {
			@Override
			public String getLabel() {
				return "Show Customers";
			}

			@Override
			public void execute() {
				System.out.println("All customers:");
				for (User c : UserManager.getInstance().getAllUsers()) {
					if (c instanceof Customer) {
						Customer cc = (Customer) c;
						System.out.println(c.getLegalName() + " | " + c.getRole() + " | " + cc.getVatNumber());
					}
				}
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Show Customer Details";
			}

			@Override
			public void execute() {
				System.out.println("Enter VAT:");
				String vat = in.nextLine().trim();
				Customer c = UserManager.getInstance().findCustomerByVat(vat);
				System.out.println(c.getLegalName() + " | " + c.getRole() + " | " + c.getVatNumber() + " | username: "
						+ c.getUsername() + " | password: " + c.getPassword());
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Go back";
			}

			@Override
			public void execute() {
				throw new Menu.ExitMenuException();
			}
		});

		new Menu("Customers Menu", opts).run();
	}

	private void runBankAccountsMenu(Admin self) {
		List<Option> opts = List.of(new Option() {
			@Override
			public String getLabel() {
				return "Show Bank Accounts";
			}

			@Override
			public void execute() {
				System.out.println("All bank accounts:");
				List<BankAccount> all = AccountManager.getInstance().getAllAccounts();
				for (BankAccount a : all) {
					System.out.printf("  IBAN: %s, Balance: %.2f€, Owner VAT: %s%n", a.getIban(), a.getBalance(),
							a.getPrimaryHolder().getVatNumber());
				}
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Show Bank Account Info";
			}

			@Override
			public void execute() {
				System.out.print("Enter IBAN: ");
				String iban = in.nextLine().trim();
				BankAccount acct = AccountManager.getInstance().findByIban(iban);
				if (acct == null) {
					System.out.println("No account found for IBAN " + iban);
				} else {
					System.out.println("Account Info:");
					System.out.printf("  IBAN: %s%n", acct.getIban());
					System.out.printf("  Owner VAT: %s%n", acct.getPrimaryHolder().getVatNumber());
					System.out.printf("  Balance: %.2f€%n", acct.getBalance());
					System.out.printf("  Interest rate: %.2f%%%n", acct.getInterestRate());
				}
			}
		},

				new Option() {
					@Override
					public String getLabel() {
						return "Show Bank Account Statements";
					}

					@Override
					public void execute() {
						System.out.print("Enter IBAN: ");
						String iban = in.nextLine().trim();
						BankAccount acct = AccountManager.getInstance().findByIban(iban);
						if (acct == null) {
							System.out.println("No account found for IBAN " + iban);
						} else {
							System.out.println("Statements for " + iban + ":");
							for (AccountStatement stmt : acct.getStatements()) {
								System.out.println("  " + stmt);
							}
						}
					}
				},

				new Option() {
					@Override
					public String getLabel() {
						return "Go back";
					}

					@Override
					public void execute() {
						throw new Menu.ExitMenuException();
					}
				});
		new Menu("Bank Accounts", opts).run();
	};

	private void runCompanyBillsMenu(Admin self) {
		List<Option> opts = List.of(new Option() {
			@Override
			public String getLabel() {
				return "Show Issued Bills";
			}

			@Override
			public void execute() {
				BillManager bm = BillManager.getInstance();

				for (Bill bill : bm.getAllBills()) {
					System.out.printf(
							"  Bill #%s - RF: %s – Date: %s – Amount: %.2f - Issuer Vat: %s - Recipient Customer ID: %s - Is Paid: %s%n",
							bill.getId(),
							bill.getRfCode(),
							bill.getDueDate(),
							bill.getAmount(),
							bill.getIssuerVAT(),
							bill.getRecipientCustomerId(),
							bill.isPaid() ? "Paid" : "Unpaid");
				}

			}

		}, new Option() {
			@Override
			public String getLabel() {
				return "Show Paid Bills";
			}

			@Override
			public void execute() {
				BillManager bm = BillManager.getInstance();

				for (Bill bill : bm.getPaidBills()) {
					if (bill.isPaid()) {
						System.out.printf(
								"  Bill #%s – Date: %s – Amount: %.2f - Issuer Name: %s - Recipient Customer ID: %s - Is Paid: %s%n",
								bill.getId(),
								bill.getDueDate(),
								bill.getAmount(),
								bill.getIssuerVAT(),
								bill.getRecipientCustomerId(),
								bill.isPaid() ? "Paid" : "Unpaid");
					}

				}
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Load Company Bills";
			}

			@Override
			public void execute() {
				System.out.println("Enter the file name of the bills you want to load");
				String fileName = in.nextLine().trim();
				StorageManager.getInstance().loadIssuedBills(fileName);
			}
		}, new Option() {
			@Override
			public String getLabel() {
				return "Go back";
			}

			@Override
			public void execute() {
				throw new Menu.ExitMenuException();
			}
		});

		new Menu("Company Bills", opts).run();
	}

	// ────────────────────────────────────────────────────
	// Transaction helper methods
	// ────────────────────────────────────────────────────

	private void doWithdraw(Individual u) {
		try {
			System.out.print("IBAN: ");
			String iban = in.nextLine().trim();
			System.out.print("Amount: ");
			double amt = Double.parseDouble(in.nextLine());
			BankAccount acct = AccountManager.getInstance().findByIban(iban);
			TransactionManager.registerTransaction(new Withdrawall(u, acct, "ATM withdrawal", amt));
			System.out.println("New balance: " + acct.getBalance());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void doDeposit(Individual u) {
		System.out.print("IBAN: ");
		String iban = in.nextLine().trim();
		System.out.print("Amount: ");
		double amt = Double.parseDouble(in.nextLine());
		BankAccount acct = AccountManager.getInstance().findByIban(iban);
		TransactionManager.registerTransaction(new Deposit(u, acct, "Customer deposit", amt));
		System.out.println("New balance: " + acct.getBalance());
	}

	private void doTransfer(Individual u) {
		System.out.print("From IBAN: ");
		String from = in.nextLine().trim();
		System.out.print("To IBAN:   ");
		String to = in.nextLine().trim();
		System.out.print("Amount:    ");
		double amt = Double.parseDouble(in.nextLine());
		BankAccount a1 = AccountManager.getInstance().findByIban(from);
		BankAccount a2 = AccountManager.getInstance().findByIban(to);
		TransactionManager.registerTransaction(new Transfer(u, a1, a2, amt, "Customer transfer"));
		System.out.println("Transfer complete.");
	}

	private void doPayBill(Individual u) {
		System.out.print("RF code: ");
		String rf = in.nextLine().trim();
		Bill bill = BillManager.getInstance().getUnpaidBill(rf, currentDate);
		if (bill == null) {
			System.out.println("No bill due today or already paid.");
			return;
		}
		BankAccount acct = AccountManager.getInstance().getPrimaryAccountOfUser(u.getVatNumber());
		TransactionManager
				.registerTransaction(
						new Payment(u, acct, AccountManager.getInstance().findBusinessAccountByVat(bill.getIssuerVAT()),
								"Pay Bill " + rf, "Receive Bill " + rf, bill.getAmount()));
		bill.markAsPaid();
		System.out.println("Bill paid: " + bill.getAmount());
	}

	private void save() {
		StorageManager sm = StorageManager.getInstance();
		sm.storeUsers("users/users.csv");
		sm.storeAccounts("accounts/accounts.csv");
		sm.storeBills("bills");
		sm.storeOrders("orders");
		sm.storeAllStatements("statements");
	}

	private void load() {
		StorageManager sm = StorageManager.getInstance();
		sm.loadUsers("users/users.csv");
		sm.loadAccounts("accounts/accounts.csv");
		sm.loadActiveOrders("orders");
		sm.loadExpiredOrders("orders");
		sm.loadFailedOrders("orders");
		sm.loadIssuedBills("bills/issued.csv");
		sm.loadPaidBills("bills/paid.csv");
		sm.loadAllStatements("statements");
	}

	private void simulateTimePassing(LocalDate endDate) {
		if (endDate.isBefore(currentDate)) {
			System.out.println("End date must be on or after " + currentDate);
			return;
		}

		while (!currentDate.isAfter(endDate)) {
			currentDate = currentDate.plusDays(1);
			System.out.println("=== Simulating " + currentDate + " ===");
			dailyTasks();
		}

		System.out.println("Simulation complete. Today is " + currentDate);
	}

	private void dailyTasks() {
		// 1) read today's bills
		StorageManager.getInstance().loadIssuedBills("bills/" + currentDate + ".csv");
		// 2) accrue daily interest
		AccountManager.getInstance().applyDailyInterestToAllAccounts();
		// 3) execute all standing orders due today
		StandingOrderManager.getInstance().executeDueOrders(currentDate);
		// 4) end-of-month posting if today is month’s last day
		if (currentDate.getDayOfMonth() == currentDate.lengthOfMonth()) {
			AccountManager.getInstance().getAllAccounts().forEach(BankAccount::endOfMonth);
		}
	}
}