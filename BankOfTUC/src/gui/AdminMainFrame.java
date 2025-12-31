package gui;

import app.BankingFacade;
import users.Admin;

import javax.swing.*;
import java.awt.*;

public class AdminMainFrame extends JFrame {

    private final BankingFacade facade;
    private final Admin user;

    public AdminMainFrame(BankingFacade facade, Admin user) {
        super("Admin - " + user.getLegalName());

        this.facade = facade;
        this.user = user;

        buildUI();
    }

    private void buildUI() {
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel(
            "Admin panel – " + user.getLegalName(),
            SwingConstants.CENTER
        );
        label.setFont(new Font("Arial", Font.BOLD, 18));

        add(label, BorderLayout.CENTER);
    }
}
