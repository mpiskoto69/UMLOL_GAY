package gui.dialogs;

import javax.swing.*;
import java.awt.*;

public class ForgotPasswordDialog extends JDialog {

    public static class Result {
        public String username;
        public String oldPassword;
        public String newPassword;
    }

    private Result result;

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField oldPassField = new JPasswordField(20);
    private final JPasswordField newPassField = new JPasswordField(20);
    private final JPasswordField confirmField = new JPasswordField(20);

    private final JButton cancelBtn = new JButton("Cancel");
    private final JButton changeBtn = new JButton("Change");

    public ForgotPasswordDialog(Window owner) {
        super(owner, "Forgot password", ModalityType.APPLICATION_MODAL);
        buildUI();

        cancelBtn.addActionListener(e -> { result = null; dispose(); });
        changeBtn.addActionListener(e -> onChange());

        pack();
        setLocationRelativeTo(owner);
    }

    public Result showDialog() {
        setVisible(true);
        return result;
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;

        int y = 0;

        c.gridx = 0; c.gridy = y; form.add(new JLabel("Username:"), c);
        c.gridx = 1; form.add(usernameField, c); y++;

        c.gridx = 0; c.gridy = y; form.add(new JLabel("Old password:"), c);
        c.gridx = 1; form.add(oldPassField, c); y++;

        c.gridx = 0; c.gridy = y; form.add(new JLabel("New password:"), c);
        c.gridx = 1; form.add(newPassField, c); y++;

        c.gridx = 0; c.gridy = y; form.add(new JLabel("Confirm new password:"), c);
        c.gridx = 1; form.add(confirmField, c); y++;

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelBtn);
        buttons.add(changeBtn);

        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        getRootPane().setDefaultButton(changeBtn);
    }

    private void onChange() {
        try {
            String username = usernameField.getText().trim();
            String oldPass = new String(oldPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String conf = new String(confirmField.getPassword());

            if (username.isEmpty()) throw new IllegalArgumentException("Username is required");
            if (newPass.isBlank()) throw new IllegalArgumentException("New password is required");
            if (!newPass.equals(conf)) throw new IllegalArgumentException("Passwords do not match");

            Result r = new Result();
            r.username = username;
            r.oldPassword = oldPass;
            r.newPassword = newPass;

            result = r;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }
}
