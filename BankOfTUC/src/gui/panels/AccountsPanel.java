package gui.panels;

import app.BankingFacade;
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
    private final JButton nextDayBtn = new JButton("Next day");

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
        actions.add(nextDayBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        // events
        refreshBtn.addActionListener(e -> refresh());
        nextDayBtn.addActionListener(e -> {
            facade.nextDay();
            refresh();
        });

        updateDate();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        refresh();
    }

    public void refresh() {
        if (customer == null) return;

        model.clear();
        List<BankAccount> accs = facade.accountsFor(customer);

        for (BankAccount a : accs) {
            String line = String.format(
                "%s | Balance: %.2f€ | Owner VAT: %s",
                a.getIban(),
                a.getBalance(),
                a.getPrimaryHolder().getVatNumber()
            );
            model.addElement(line);
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
