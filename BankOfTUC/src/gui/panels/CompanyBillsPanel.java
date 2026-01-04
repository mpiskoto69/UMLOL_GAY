package gui.panels;

import app.BankingFacade;
import bank.storage.Bill;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import users.Company;

public class CompanyBillsPanel extends JPanel {

    private final BankingFacade facade;
    private Company company;

    private final JTabbedPane tabs = new JTabbedPane();

    // Issued
    private final DefaultTableModel issuedModel = new DefaultTableModel(
            new Object[] { "RF", "Amount", "Issue Date", "Due Date", "Status", "Customer(VAT)" }, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable issuedTable = new JTable(issuedModel);
    private final JButton issuedRefreshBtn = new JButton("Refresh");
    private final JComboBox<String> issuedFilter = new JComboBox<>(new String[] { "All", "Unpaid", "Paid" });

    // To Pay
    private final DefaultTableModel toPayModel = new DefaultTableModel(
            new Object[] { "RF", "Amount", "Issue Date", "Due Date", "Status", "Issuer(VAT)" }, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable toPayTable = new JTable(toPayModel);
    private final JButton toPayRefreshBtn = new JButton("Refresh");
    private final JComboBox<String> toPayFilter = new JComboBox<>(new String[] { "All", "Unpaid", "Paid" });

    public CompanyBillsPanel(BankingFacade facade) {
        this.facade = facade;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Bills");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        issuedTable.setAutoCreateRowSorter(true);
        toPayTable.setAutoCreateRowSorter(true);

        tabs.addTab("Issued Bills", buildIssuedPanel());
        tabs.addTab("Bills To Pay", buildToPayPanel());

        add(tabs, BorderLayout.CENTER);
    }

    public void setCompany(Company company) {
        this.company = company;
        refreshAll();
    }

    public void refreshAll() {
        refreshIssued();
        refreshToPay();
    }

    private JPanel buildIssuedPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Filter:"));
        top.add(issuedFilter);
        top.add(issuedRefreshBtn);

        issuedRefreshBtn.addActionListener(e -> refreshIssued());
        issuedFilter.addActionListener(e -> refreshIssued());

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(issuedTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildToPayPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Filter:"));
        top.add(toPayFilter);
        top.add(toPayRefreshBtn);

        toPayRefreshBtn.addActionListener(e -> refreshToPay());
        toPayFilter.addActionListener(e -> refreshToPay());

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(toPayTable), BorderLayout.CENTER);
        return p;
    }

    private void refreshIssued() {
        issuedModel.setRowCount(0);
        if (company == null)
            return;

        List<Bill> bills = facade.issuedBillsFor(company);
        String filter = (String) issuedFilter.getSelectedItem();

        for (Bill b : bills) {
            if (!passesFilter(b, filter))
                continue;

            issuedModel.addRow(new Object[] {
                    b.getRfCode(),
                    String.format("%.2f", b.getAmount()),
                    b.getIssueDate(),
                    b.getDueDate(),
                    b.isPaid() ? "PAID" : "UNPAID",
                    b.getRecipientCustomerId()
            });
        }
    }

    private void refreshToPay() {
        toPayModel.setRowCount(0);
        if (company == null)
            return;

        List<Bill> bills = facade.billsToPayFor(company);
        String filter = (String) toPayFilter.getSelectedItem();

        for (Bill b : bills) {
            if (!passesFilter(b, filter))
                continue;

            toPayModel.addRow(new Object[] {
                    b.getRfCode(),
                    String.format("%.2f", b.getAmount()),
                    b.getIssueDate(),
                    b.getDueDate(),
                    b.isPaid() ? "PAID" : "UNPAID",
                    b.getIssuerVAT()
            });
        }
    }

    private boolean passesFilter(Bill b, String filter) {
        if (filter == null || filter.equals("All"))
            return true;
        return filter.equals("Paid") ? b.isPaid() : !b.isPaid();
    }
}
