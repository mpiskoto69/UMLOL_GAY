package gui;

import app.BankingFacade;
import gui.panels.AccountsPanel;
import gui.panels.CompanyBillsPanel;
import gui.panels.StandingOrdersPanel;
import users.Company;
import gui.panels.StatementsPanel;

import javax.swing.*;
import java.awt.*;

public class CompanyMainFrame extends JFrame {

    private final BankingFacade facade;
    private final Company user;

    private final JLabel welcomeLabel = new JLabel();
    private final JLabel dateLabel = new JLabel();

    private AccountsPanel accountsPanel;
    private StandingOrdersPanel soPanel;
    private CompanyBillsPanel billsPanel;

    public CompanyMainFrame(BankingFacade facade, Company user) {
        super("Company - " + user.getLegalName());
        this.facade = facade;
        this.user = user;

        buildUI();
        refreshHeader();
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);

        // ---------- Header ----------
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setBorder(BorderFactory.createEmptyBorder(12, 12, 6, 12));

        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 18f));
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {

    int r = JOptionPane.showConfirmDialog(
            CompanyMainFrame.this,
            "Save changes before logout?",
            "Logout",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
    );

    if (r == JOptionPane.CANCEL_OPTION) {
        return; // ❌ ακύρωση logout
    }

    if (r == JOptionPane.YES_OPTION) {
        try {
            facade.saveAll(); // ✅ σώζει ΟΛΑ + ημερομηνία
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    CompanyMainFrame.this,
                    "Failed to save data:\n" + ex.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return; // ❌ μην κάνεις logout αν απέτυχε το save
        }
    }

    // ✅ NO ή YES → logout κανονικά
    dispose();
    SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
});

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.add(dateLabel);
        right.add(logoutBtn);

        header.add(welcomeLabel, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        // ---------- Tabs ----------
        JTabbedPane tabs = new JTabbedPane();

        accountsPanel = new AccountsPanel(facade);
        accountsPanel.setCustomer(user);

        soPanel = new StandingOrdersPanel(facade);
        soPanel.setCustomer(user);

        billsPanel = new CompanyBillsPanel(facade);
        billsPanel.setCompany(user);
       
        StatementsPanel stPanel = new StatementsPanel(facade);
        stPanel.setCustomer(user);


        tabs.addTab("Accounts", accountsPanel);
        tabs.addTab("Standing Orders", soPanel);
        tabs.addTab("Bills", billsPanel);
        tabs.addTab("Statements", stPanel);

        // ---------- Root ----------
        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);

        setContentPane(root);
    }

    private void refreshHeader() {
        welcomeLabel.setText("Company – " + user.getLegalName());
        dateLabel.setText("Today: " + facade.getCurrentDate());
    }
}
