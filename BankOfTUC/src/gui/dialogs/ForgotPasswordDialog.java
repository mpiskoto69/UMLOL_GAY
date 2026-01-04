package gui.dialogs;

import java.awt.*;
import javax.swing.*;

public class ForgotPasswordDialog extends JDialog {

    public static class Result {
        public String username;
        public String newPassword;
    }

    private Result result;

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField newPassField = new JPasswordField(20);
    private final JPasswordField confirmField = new JPasswordField(20);

    private final JButton cancelBtn = new JButton("Cancel");
    private final JButton resetBtn = new JButton("Reset");

    public ForgotPasswordDialog(Window owner) {
        super(owner, "Forgot password", ModalityType.APPLICATION_MODAL);
        buildUI();

        cancelBtn.addActionListener(e -> {
            result = null;
            dispose();
        });
        resetBtn.addActionListener(e -> onReset());

        pack();
        setLocationRelativeTo(owner);
    }

    public Result showDialog() {
        setVisible(true);
        return result;
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        int y = 0;

        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Username:"), c);
        c.gridx = 1;
        form.add(usernameField, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("New password:"), c);
        c.gridx = 1;
        form.add(newPassField, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Confirm new password:"), c);
        c.gridx = 1;
        form.add(confirmField, c);
        y++;

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelBtn);
        buttons.add(resetBtn);

        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        getRootPane().setDefaultButton(resetBtn);
    }

    private void onReset() {
        try {
            String username = usernameField.getText().trim();
            String newPass = new String(newPassField.getPassword());
            String conf = new String(confirmField.getPassword());

            if (username.isEmpty())
                throw new IllegalArgumentException("Username is required");

            if (newPass.isBlank())
                throw new IllegalArgumentException("New password is required");

            if (!newPass.equals(conf))
                throw new IllegalArgumentException("Passwords do not match");

            Result r = new Result();
            r.username = username;
            r.newPassword = newPass;

            result = r;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }
}
