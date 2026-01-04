package gui;

import app.BankingFacade;
import gui.dialogs.ForgotPasswordDialog;
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
    private final JLabel statusLabel = new JLabel(" ");
    private final JButton forgotBtn = new JButton("Forgot password");


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
        setSize(420, 220);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // status
        statusLabel.setForeground(new Color(70, 70, 70));
        root.add(statusLabel, BorderLayout.NORTH);

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

        // buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(exitButton);
        buttons.add(forgotBtn);
        buttons.add(loginButton);

        // status
        statusLabel.setForeground(new Color(70, 70, 70));

        root.add(form, BorderLayout.CENTER);

      // JPanel buttons = new JPanel(new BorderLayout());

        JPanel leftBtns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftBtns.add(forgotBtn);

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBtns.add(exitButton);
        rightBtns.add(loginButton);

        buttons.add(leftBtns, BorderLayout.WEST);
        buttons.add(rightBtns, BorderLayout.EAST);

        root.add(buttons, BorderLayout.SOUTH);
        root.add(statusLabel, BorderLayout.NORTH);

        setContentPane(root);

        // nicer default
        getRootPane().setDefaultButton(loginButton);
        usernameField.requestFocusInWindow();
    }

    private void wireEvents() {
        loginButton.addActionListener(this::onLogin);
        exitButton.addActionListener(e -> System.exit(0));
        forgotBtn.addActionListener(e -> onForgotPassword());

        // Enter on password triggers login (optional but nice)
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

            // Open correct main frame
            if (u instanceof Admin) {
                SwingUtilities.invokeLater(() -> {
                    new AdminMainFrame(facade, (Admin) u).setVisible(true);
                });
            } else if (u instanceof Company) {
                SwingUtilities.invokeLater(() -> {
                    new CompanyMainFrame(facade, (Company) u).setVisible(true);
                });
            } else if (u instanceof Individual) {
                SwingUtilities.invokeLater(() -> {
                    new IndividualMainFrame(facade, (Individual) u).setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Άγνωστος ρόλος χρήστη: " + u.getRole(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // close login
            this.dispose();

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
    ForgotPasswordDialog dlg = new ForgotPasswordDialog(this);
    ForgotPasswordDialog.Result res = dlg.showDialog();
    if (res == null) return;

    try {
        User u = managers.UserManager.getInstance().findUserByUsername(res.username);
        if (u == null) throw new IllegalArgumentException("User not found");

        u.setPassword(res.newPassword);

       
        facade.saveAll();

        JOptionPane.showMessageDialog(this, "Password reset successful.", "OK", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Reset failed", JOptionPane.ERROR_MESSAGE);
    }
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

}