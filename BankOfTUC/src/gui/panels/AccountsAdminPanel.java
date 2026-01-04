package gui.panels;

import app.BankingFacade;
import accounts.BankAccount;
import managers.AccountManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AccountsAdminPanel extends JPanel {

    private final BankingFacade facade;

    private final DefaultTableModel model;
    private final JTable table;

    private final JButton refreshBtn = new JButton("Refresh");
    private final JLabel countLabel = new JLabel(" ");

    public AccountsAdminPanel(BankingFacade facade) {
        this.facade = facade;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Accounts (All)");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.add(countLabel);
        actions.add(refreshBtn);
        top.add(actions, BorderLayout.EAST);

        model = new DefaultTableModel(new Object[]{"IBAN", "Type", "Owner VAT", "Balance (€)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> refresh());
    }

    public void refresh() {
        model.setRowCount(0);

        List<BankAccount> all = AccountManager.getInstance().getAllAccounts();

        for (BankAccount a : all) {
            String iban = safe(a.getIban());
        String type = typeFromIban(iban);

            String ownerVat = "";
            try {
                if (a.getPrimaryHolder() != null) ownerVat = safe(a.getPrimaryHolder().getVatNumber());
            } catch (Exception ignored) {}

            double bal = 0.0;
            try { bal = a.getBalance(); } catch (Exception ignored) {}

            model.addRow(new Object[]{iban, type, ownerVat, String.format("%.2f", bal)});
        }

        countLabel.setText("Count: " + all.size());
    }

    public String getSelectedIban() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = table.convertRowIndexToModel(viewRow);
        return (String) model.getValueAt(modelRow, 0);
    }
    private String typeFromIban(String iban) {
    if (iban == null) return "";
    iban = iban.trim();

    if (!iban.startsWith("GR") || iban.length() < 5) return "Unknown";

    String code = iban.substring(2, 5);

    return switch (code) {
        case "100" -> "Personal";
        case "200" -> "Business";
        default -> "Other(" + code + ")";
    };
}

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
