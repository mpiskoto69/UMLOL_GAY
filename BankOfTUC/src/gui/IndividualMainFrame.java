package gui;

import app.BankingFacade;
import gui.panels.AccountsPanel;
import gui.panels.StandingOrdersPanel;
import gui.panels.StatementsPanel;
import java.awt.*;
import javax.swing.*;
import users.Individual;

public class IndividualMainFrame extends JFrame {

    private final BankingFacade facade;
    private final Individual user;

    private final JLabel welcomeLabel = new JLabel();
    private final JLabel dateLabel = new JLabel();

    private AccountsPanel accountsPanel;
    private StandingOrdersPanel soPanel;

    public IndividualMainFrame(BankingFacade facade, Individual user) {
        super("Individual - " + user.getLegalName());
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
                    IndividualMainFrame.this,
                    "Save changes before logout?",
                    "Logout",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (r == JOptionPane.CANCEL_OPTION) {
                return;
            }

            if (r == JOptionPane.YES_OPTION) {
                try {
                    facade.saveAll();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            IndividualMainFrame.this,
                            "Failed to save data:\n" + ex.getMessage(),
                            "Save Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

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
        StatementsPanel stPanel = new StatementsPanel(facade);
        stPanel.setCustomer(user);

        tabs.addTab("Accounts", accountsPanel);
        tabs.addTab("Standing Orders", soPanel);
        tabs.addTab("Statements", stPanel);

        // ---------- Root ----------
        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);

        setContentPane(root);
    }

    private void refreshHeader() {
        welcomeLabel.setText("Welcome – " + user.getLegalName());
        dateLabel.setText("Today: " + facade.getCurrentDate());
    }
}
