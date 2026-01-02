package gui.dialogs;

import accounts.BankAccount;
import users.Customer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DepositDialog extends JDialog {

    public static class Result {
        public final String toIban;
        public final double amount;
        public final String reason;

        public Result(String toIban, double amount, String reason) {
            this.toIban = toIban;
            this.amount = amount;
            this.reason = reason;
        }
    }

    private Result result;

    private final JComboBox<String> toCombo = new JComboBox<>();
    private final JTextField amountField = new JTextField(10);
    private final JTextField reasonField = new JTextField(20);

    public DepositDialog(Window owner, Customer customer, List<BankAccount> accounts) {
        super(owner, "Deposit", ModalityType.APPLICATION_MODAL);

        for (BankAccount a : accounts) {
            toCombo.addItem(a.getIban());
        }

        buildUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;

        int y = 0;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("To account:"), c);
        c.gridx = 1;
        form.add(toCombo, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Amount (€):"), c);
        c.gridx = 1;
        form.add(amountField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Reason:"), c);
        c.gridx = 1;
        form.add(reasonField, c);
        y++;

        JButton cancelBtn = new JButton("Cancel");
        JButton okBtn = new JButton("Deposit");

        cancelBtn.addActionListener(e -> dispose());
        okBtn.addActionListener(e -> onOk());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelBtn);
        buttons.add(okBtn);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okBtn);
    }

    private void onOk() {
        try {
            String to = (String) toCombo.getSelectedItem();
            if (to == null || to.isBlank()) throw new IllegalArgumentException("Select an account.");

            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0.");

            String reason = reasonField.getText().trim();
            if (reason.isBlank()) reason = "Cash deposit";

            result = new Result(to, amount, reason);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    public Result showDialog() {
        setVisible(true);
        return result;
    }
}

