package gui.panels;

import app.BankingFacade;
import gui.dialogs.DepositDialog;
import gui.dialogs.PayBillDialog;
import gui.dialogs.TransferDialog;
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
    private final JButton transferBtn = new JButton("Transfer");
    private final JButton payBillBtn = new JButton("Pay Bill");
    private final JButton depositBtn = new JButton("Deposit");




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
        actions.add(transferBtn);
        actions.add(payBillBtn);
        actions.add(depositBtn);



        add(top, BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> refresh());
        withdrawBtn.addActionListener(e -> onWithdraw());
        transferBtn.addActionListener(e -> onTransfer());
        payBillBtn.addActionListener(e -> onPayBill());
        depositBtn.addActionListener(e -> onDeposit());
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
    private void onDeposit() {
    if (customer == null) return;

    DepositDialog dlg = new DepositDialog(
        SwingUtilities.getWindowAncestor(this),
        customer,
        facade.accountsFor(customer)
    );

    DepositDialog.Result res = dlg.showDialog();
    if (res == null) return;

    try {
        facade.deposit(customer, res.toIban, res.amount, res.reason);
        refresh();
        JOptionPane.showMessageDialog(this, "Deposit completed.", "OK", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Deposit failed", JOptionPane.ERROR_MESSAGE);
    }
}
    private void onPayBill() {
    if (customer == null) return;

    PayBillDialog dlg = new PayBillDialog(
        SwingUtilities.getWindowAncestor(this),
        customer,
        facade.accountsFor(customer)
    );

    PayBillDialog.Result res = dlg.showDialog();
    if (res == null) return;

    try {
        facade.payBill(customer, res.fromIban, res.rfCode);
        refresh();
        JOptionPane.showMessageDialog(this, "Bill paid successfully.", "OK", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Payment failed", JOptionPane.ERROR_MESSAGE);
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

    private void onTransfer() {
    if (customer == null) return;

    TransferDialog dlg = new TransferDialog(
        SwingUtilities.getWindowAncestor(this),
        customer,
        facade.accountsFor(customer)
    );

    TransferDialog.Result res = dlg.showDialog();
    if (res == null) return;

    try {
        facade.transfer(customer, res.fromIban, res.toIban, res.amount, res.reason);
        refresh();
        JOptionPane.showMessageDialog(this, "Transfer completed.", "OK",
                JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Transfer failed", JOptionPane.ERROR_MESSAGE);
    }
}


    private void updateDate() {
        dateLabel.setText("Today: " + facade.getCurrentDate());
    }
}
