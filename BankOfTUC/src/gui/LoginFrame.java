package gui;

import app.BankingFacade;
import users.Admin;
import users.Company;
import users.Individual;
import users.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {

    private final BankingFacade facade;

    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);

    private final JButton loginButton = new JButton("Login");
    private final JButton exitButton = new JButton("Exit");
    private final JButton forgotBtn = new JButton("Forgot password");

    private final JLabel statusLabel = new JLabel(" ");

    public LoginFrame() {
        super("Bank of TUC - Login");

        this.facade = new BankingFacade();

        buildUI();
        wireEvents();

        // load data once at startup
        try {
            facade.loadAll();
            statusLabel.setText("Data loaded.");
        } catch (Exception ex) {
            statusLabel.setText("Load failed.");
            JOptionPane.showMessageDialog(
                    this,
                    "Αποτυχία φόρτωσης δεδομένων:\n" + ex.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 230);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // status
        statusLabel.setForeground(new Color(70, 70, 70));
        root.add(statusLabel, BorderLayout.NORTH);

        // form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Username:"), c);

        c.gridx = 1;
        form.add(usernameField, c);

        c.gridx = 0; c.gridy = 1;
        form.add(new JLabel("Password:"), c);

        c.gridx = 1;
        form.add(passwordField, c);

        root.add(form, BorderLayout.CENTER);

        // buttons bottom: left = forgot, right = exit/login
        JPanel buttons = new JPanel(new BorderLayout());

        JPanel leftBtns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftBtns.add(forgotBtn);

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBtns.add(exitButton);
        rightBtns.add(loginButton);

        buttons.add(leftBtns, BorderLayout.WEST);
        buttons.add(rightBtns, BorderLayout.EAST);

        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);

        getRootPane().setDefaultButton(loginButton);
        usernameField.requestFocusInWindow();
    }

    private void wireEvents() {
        loginButton.addActionListener(this::onLogin);
        exitButton.addActionListener(e -> System.exit(0));
        forgotBtn.addActionListener(e -> onForgotPassword());

        // Enter on password triggers login
        passwordField.addActionListener(this::onLogin);
    }

    private void onLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Συμπλήρωσε username και password.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            User u = facade.login(username, password);

            if (u instanceof Admin) {
                SwingUtilities.invokeLater(() -> new AdminMainFrame(facade, (Admin) u).setVisible(true));
            } else if (u instanceof Company) {
                SwingUtilities.invokeLater(() -> new CompanyMainFrame(facade, (Company) u).setVisible(true));
            } else if (u instanceof Individual) {
                SwingUtilities.invokeLater(() -> new IndividualMainFrame(facade, (Individual) u).setVisible(true));
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Άγνωστος ρόλος χρήστη: " + u.getRole(),
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            passwordField.setText("");
            passwordField.requestFocusInWindow();
        }
    }

    private void onForgotPassword() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        JTextField uField = new JTextField(16);
        JPasswordField oldField = new JPasswordField(16);
        JPasswordField newField = new JPasswordField(16);
        JPasswordField confirmField = new JPasswordField(16);

        // prefill username from login field (nice UX)
        uField.setText(usernameField.getText().trim());

        int y = 0;

        c.gridx = 0; c.gridy = y; panel.add(new JLabel("Username:"), c);
        c.gridx = 1; panel.add(uField, c); y++;

        c.gridx = 0; c.gridy = y; panel.add(new JLabel("Old password:"), c);
        c.gridx = 1; panel.add(oldField, c); y++;

        c.gridx = 0; c.gridy = y; panel.add(new JLabel("New password:"), c);
        c.gridx = 1; panel.add(newField, c); y++;

        c.gridx = 0; c.gridy = y; panel.add(new JLabel("Confirm new:"), c);
        c.gridx = 1; panel.add(confirmField, c);

        int ok = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Reset password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (ok != JOptionPane.OK_OPTION) return;

        String username = uField.getText().trim();
        String oldPass = new String(oldField.getPassword());
        String newPass = new String(newField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (newPass.isBlank()) {
            JOptionPane.showMessageDialog(this, "New password is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            facade.resetPassword(username, oldPass, newPass);
            facade.saveAll(); // persist to CSV
            JOptionPane.showMessageDialog(this, "Password updated.", "OK", JOptionPane.INFORMATION_MESSAGE);

            // convenience: fill login fields
            usernameField.setText(username);
            passwordField.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Reset failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
