package managers;

import transactions.Transaction;

public class StatementManager {
	private static final StatementManager instance = new StatementManager();

	private StatementManager() {}

	public static StatementManager getInstance() {
		return instance;
	}

	public void registerStatements(Transaction transaction) {}
}
