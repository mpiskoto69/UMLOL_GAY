package gui;

import app.BankingFacade;
import users.Company;

import javax.swing.*;
import java.awt.*;

public class CompanyMainFrame extends JFrame {

    private final BankingFacade facade;
    private final Company user;

    public CompanyMainFrame(BankingFacade facade, Company user) {
        super("Company - " + user.getLegalName());

        this.facade = facade;
        this.user = user;

        buildUI();
    }

    private void buildUI() {
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel(
            "Welcome company " + user.getLegalName(),
            SwingConstants.CENTER
        );
        label.setFont(new Font("Arial", Font.BOLD, 18));

        add(label, BorderLayout.CENTER);
    }
}
