package gui;

import app.BankingFacade;
import gui.panels.AccountsPanel;
import users.Individual;

import javax.swing.*;
import java.awt.*;

public class IndividualMainFrame extends JFrame {

    private final BankingFacade facade;
    private final Individual user;

    public IndividualMainFrame(BankingFacade facade, Individual user) {
        super("Individual - " + user.getLegalName());

        this.facade = facade;
        this.user = user;

        buildUI();
    }

    private void buildUI() {
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        AccountsPanel accountsPanel = new AccountsPanel(facade);
        accountsPanel.setCustomer(user);

        JLabel header = new JLabel("Welcome: " + user.getLegalName(), SwingConstants.LEFT);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));

        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH);
        root.add(accountsPanel, BorderLayout.CENTER);

        setContentPane(root);
    }
}
