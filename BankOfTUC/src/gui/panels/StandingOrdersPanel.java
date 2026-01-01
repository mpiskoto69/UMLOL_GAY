package gui.panels;

import app.BankingFacade;
import gui.dialogs.TransferOrderDialog;
import managers.StandingOrderManager;
import standingOrders.StandingOrder;
import users.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StandingOrdersPanel extends JPanel {

    private final BankingFacade facade;
    private Customer customer;

    private final DefaultTableModel model;
    private final JTable table;

    private final JButton refreshBtn = new JButton("Refresh");
    private final JButton createTransferBtn = new JButton("Create Transfer Order");
    private final JButton createPaymentBtn = new JButton("Create Payment Order");

    public StandingOrdersPanel(BankingFacade facade) {
        this.facade = facade;

        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel title = new JLabel("My Standing Orders");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(refreshBtn);
        actions.add(createTransferBtn);
        actions.add(createPaymentBtn);
        top.add(actions, BorderLayout.EAST);

        model = new DefaultTableModel(
                new Object[]{"Type","OrderId","Title","Start","End","Fee","Failed"},
                0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> refresh());

        // TODO: wire dialogs when you create them:
        
        createTransferBtn.addActionListener(e -> onCreateTransfer());
        createPaymentBtn.addActionListener(e -> onCreatePayment());
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        refresh();
    }

    public void refresh() {
        model.setRowCount(0);
        if (customer == null) return;

        List<StandingOrder> mine = getOrdersFor(customer);

        for (StandingOrder so : mine) {
            model.addRow(new Object[]{
                    so.getClass().getSimpleName(),
                    so.getId(),
                    so.getTitle(),
                    so.getStartDate(),
                    so.getEndDate(),
                    String.format("%.2f", so.getFee()),
                    so.getFailedAttempts()
            });
        }

        if (mine.isEmpty()) {
            // optional: show empty state row
            // model.addRow(new Object[]{"(none)", "", "", "", "", "", ""});
        }
    }

    private List<StandingOrder> getOrdersFor(Customer c) {
        List<StandingOrder> out = new ArrayList<>();
        String vat = c.getVatNumber();

        for (StandingOrder so : StandingOrderManager.getInstance().getAllOrders()) {
            if (so == null || so.getCustomer() == null) continue;
            if (vat != null && vat.equals(so.getCustomer().getVatNumber())) {
                out.add(so);
            }
        }
        return out;
    }

  private void onCreateTransfer() {
    if (customer == null) return;

    TransferOrderDialog dlg = new TransferOrderDialog(
            SwingUtilities.getWindowAncestor(this),
            customer,
            facade.accountsFor(customer),
            facade.getCurrentDate()
    );

    TransferOrderDialog.Result res = dlg.showDialog();
    if (res == null) return;

    try {
        facade.createTransferOrder(
                customer,
                res.title,
                res.description,
                res.fromIban,
                res.toIban,
                res.amount,
                res.frequencyInMonths,
                res.dayOfMonth,
                res.startDate,
                res.endDate,
                res.fee
        );
        refresh();
        JOptionPane.showMessageDialog(this, "Standing transfer order created.", "OK", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Create failed", JOptionPane.ERROR_MESSAGE);
    }
}

    private void onCreatePayment() {
        if (customer == null) return;
        JOptionPane.showMessageDialog(this,
                "TODO: PaymentOrder dialog εδώ.\n" +
                "Μετά το OK: facade.createPaymentOrder(...)\n" +
                "και refresh().",
                "Create Payment Order",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
