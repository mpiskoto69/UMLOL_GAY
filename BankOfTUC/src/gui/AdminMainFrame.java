package gui;

import app.BankingFacade;
import gui.panels.AccountsAdminPanel;
import gui.panels.CustomersPanel;
import gui.panels.SimulationPanel;
import java.awt.*;
import javax.swing.*;
import users.Admin;

public class AdminMainFrame extends JFrame {

    private final BankingFacade facade;
    private final Admin user;

    private final JLabel welcomeLabel = new JLabel();
    private final JLabel dateLabel = new JLabel();
    private final JLabel statusLabel = new JLabel(" ");

    private CustomersPanel customersPanel;
    private AccountsAdminPanel accountsPanel;
    private SimulationPanel simulationPanel;

    public AdminMainFrame(BankingFacade facade, Admin user) {
        super("Admin - " + user.getLegalName());
        this.facade = facade;
        this.user = user;

        buildUI();
        refreshHeader();
        refreshAll();
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);

        // Header
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setBorder(BorderFactory.createEmptyBorder(12, 12, 6, 12));

        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 18f));
        dateLabel.setFont(dateLabel.getFont().deriveFont(Font.PLAIN, 14f));
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {

            int r = JOptionPane.showConfirmDialog(
                    AdminMainFrame.this,
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
                            AdminMainFrame.this,
                            "Failed to save data:\n" + ex.getMessage(),
                            "Save Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.add(dateLabel);
        right.add(logoutBtn);

        header.add(welcomeLabel, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();

        customersPanel = new CustomersPanel(facade);
        accountsPanel = new AccountsAdminPanel(facade);

        simulationPanel = new SimulationPanel(facade, () -> {
            refreshHeader();
            refreshAll();
            setStatus("Advanced to: " + facade.getCurrentDate());
        });

        tabs.addTab("Customers", customersPanel);
        tabs.addTab("Accounts", accountsPanel);
        tabs.addTab("Simulation", simulationPanel);

        // Status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(6, 12, 10, 12));
        statusLabel.setForeground(new Color(70, 70, 70));
        statusBar.add(statusLabel, BorderLayout.WEST);

        // Root
        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        root.add(statusBar, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void refreshHeader() {
        welcomeLabel.setText("Admin panel – " + user.getLegalName());
        dateLabel.setText("Today: " + facade.getCurrentDate());
    }

    private void refreshAll() {
        if (customersPanel != null)
            customersPanel.refresh();
        if (accountsPanel != null)
            accountsPanel.refresh();
        if (simulationPanel != null)
            simulationPanel.refresh();
    }

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }
}
