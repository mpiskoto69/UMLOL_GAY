package gui.panels;

import app.BankingFacade;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import managers.UserManager;
import users.Customer;
import users.User;

public class CustomersPanel extends JPanel {

    private final BankingFacade facade;

    private final DefaultTableModel model;
    private final JTable table;

    private final JButton refreshBtn = new JButton("Refresh");
    private final JLabel countLabel = new JLabel(" ");

    public CustomersPanel(BankingFacade facade) {
        this.facade = facade;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Customers (Individuals + Companies)");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.add(countLabel);
        actions.add(refreshBtn);
        top.add(actions, BorderLayout.EAST);

        // Table model
        model = new DefaultTableModel(new Object[] { "Role", "Legal Name", "VAT", "Username" }, 0) {
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

        List<Customer> customers = getAllCustomers();
        for (Customer c : customers) {
            model.addRow(new Object[] {
                    c.getRole(),
                    c.getLegalName(),
                    c.getVatNumber(),
                    c.getUsername()
            });
        }

        countLabel.setText("Count: " + customers.size());
    }

    private List<Customer> getAllCustomers() {
        List<Customer> out = new ArrayList<>();
        for (User u : UserManager.getInstance().getAllUsers()) {
            if (u instanceof Customer) {
                out.add((Customer) u);
            }
        }
        return out;
    }
}
