package gui.panels;

import app.BankingFacade;
import accounts.BankAccount;
import transactions.AccountStatement;
import users.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StatementsPanel extends JPanel {

    private final BankingFacade facade;
    private Customer customer;

    private final JComboBox<String> ibanCombo = new JComboBox<>();
    private final JButton refreshBtn = new JButton("Refresh");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Time", "Type", "Amount", "Balance After", "Reason", "Counterparty", "TxId"},
            0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public StatementsPanel(BankingFacade facade) {
        this.facade = facade;

        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel title = new JLabel("Statements");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JPanel topLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topLeft.add(new JLabel("Account (IBAN):"));
        topLeft.add(ibanCombo);
        topLeft.add(refreshBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.WEST);
        top.add(topLeft, BorderLayout.EAST);

        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> refresh());
        ibanCombo.addActionListener(e -> refresh());
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        reloadAccounts();
        refresh();
    }

    private void reloadAccounts() {
        ibanCombo.removeAllItems();
        if (customer == null) return;

        List<BankAccount> accs = facade.accountsFor(customer);
        for (BankAccount a : accs) {
            ibanCombo.addItem(a.getIban());
        }
    }

    public void refresh() {
        model.setRowCount(0);
        if (customer == null) return;

        String iban = (String) ibanCombo.getSelectedItem();
        if (iban == null || iban.isBlank()) return;

        BankAccount acct = null;
        for (BankAccount a : facade.accountsFor(customer)) {
            if (iban.equals(a.getIban())) { acct = a; break; }
        }
        if (acct == null) return;

        for (AccountStatement s : acct.getStatements()) {
            model.addRow(new Object[]{
                    s.getTimestamp(),
                    s.getMovementType(),
                    String.format("%.2f", s.getAmount()),
                    String.format("%.2f", s.getBalanceAfter()),
                    s.getReason(),
                    s.getCounterpartyIban() == null ? "" : s.getCounterpartyIban(),
                    s.getTransactionId()
            });
        }
    }
}
