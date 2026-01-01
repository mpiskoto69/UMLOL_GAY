package gui;

import app.BankingFacade;
import gui.panels.AccountsPanel;
import gui.panels.StandingOrdersPanel;
import users.Individual;

import javax.swing.*;
import java.awt.*;

public class IndividualMainFrame extends JFrame {

    private final BankingFacade facade;
    private final Individual user;

    private final JLabel welcomeLabel = new JLabel();
    private final JLabel dateLabel = new JLabel();

    private AccountsPanel accountsPanel;

    public IndividualMainFrame(BankingFacade facade, Individual user) {
        super("Individual - " + user.getLegalName());
        this.facade = facade;
        this.user = user;

        buildUI();
        refreshHeader();
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 520);
        setLocationRelativeTo(null);

        // Header
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setBorder(BorderFactory.createEmptyBorder(12, 12, 6, 12));

        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 18f));
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(welcomeLabel, BorderLayout.WEST);
        header.add(dateLabel, BorderLayout.EAST);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();

        accountsPanel = new AccountsPanel(facade);
        accountsPanel.setCustomer(user);

        tabs.addTab("Accounts", accountsPanel);

        // Root
        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        
        StandingOrdersPanel soPanel = new StandingOrdersPanel(facade);
        soPanel.setCustomer(user);
        tabs.addTab("Standing Orders", soPanel);

        setContentPane(root);
    }

    private void refreshHeader() {
        welcomeLabel.setText("Welcome – " + user.getLegalName());
        dateLabel.setText("Today: " + facade.getCurrentDate());
    }
}
