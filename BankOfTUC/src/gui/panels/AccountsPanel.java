package gui.panels;

import app.BankingFacade;
import gui.dialogs.WithdrawDialog;
import accounts.BankAccount;
import users.Customer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AccountsPanel extends JPanel {

    private final BankingFacade facade;
    private Customer customer;

    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);

    private final JLabel titleLabel = new JLabel("Accounts");
    private final JLabel dateLabel = new JLabel(" ");

    private final JButton refreshBtn = new JButton("Refresh");
    private final JButton withdrawBtn = new JButton("Withdraw");

    public AccountsPanel(BankingFacade facade) {
        this.facade = facade;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));

        JPanel top = new JPanel(new BorderLayout());
        top.add(titleLabel, BorderLayout.WEST);
        top.add(dateLabel, BorderLayout.EAST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(refreshBtn);
        actions.add(withdrawBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> refresh());
        withdrawBtn.addActionListener(e -> onWithdraw());

        updateDate();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        refresh();
    }

    private void onWithdraw() {
        if (customer == null) return;

        WithdrawDialog dlg = new WithdrawDialog(
                SwingUtilities.getWindowAncestor(this),
                customer,
                facade.accountsFor(customer)
        );

        WithdrawDialog.Result res = dlg.showDialog();
        if (res == null) return;

        try {
            facade.withdraw(customer, res.fromIban, res.amount, res.reason);
            refresh();
            JOptionPane.showMessageDialog(this, "Withdrawal completed.", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Withdraw failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refresh() {
        if (customer == null) return;

        model.clear();
        List<BankAccount> accs = facade.accountsFor(customer);

        for (BankAccount a : accs) {
            model.addElement(String.format(
                    "%s | Balance: %.2f€ | Owner VAT: %s",
                    a.getIban(),
                    a.getBalance(),
                    a.getPrimaryHolder().getVatNumber()
            ));
        }

        if (accs.isEmpty()) {
            model.addElement("(No accounts)");
        }

        updateDate();
    }

    private void updateDate() {
        dateLabel.setText("Today: " + facade.getCurrentDate());
    }
}
