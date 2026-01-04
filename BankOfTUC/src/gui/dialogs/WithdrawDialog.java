package gui.dialogs;

import accounts.BankAccount;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import users.Customer;

public class WithdrawDialog extends JDialog {

    public static class Result {
        public String fromIban;
        public double amount;
        public String reason;
    }

    private Result result = null;

    private final JComboBox<String> fromIbanCombo = new JComboBox<>();
    private final JSpinner amountSpinner = new JSpinner(new SpinnerNumberModel(20.00, 0.01, 1_000_000.00, 1.00));
    private final JTextField reasonField = new JTextField(22);

    private final JButton cancelBtn = new JButton("Cancel");
    private final JButton withdrawBtn = new JButton("Withdraw");

    public WithdrawDialog(Window owner, Customer customer, List<BankAccount> customerAccounts) {
        super(owner, "Withdraw", ModalityType.APPLICATION_MODAL);

        buildUI();

        // defaults
        reasonField.setText("Cash withdrawal");

        // populate accounts
        for (BankAccount a : customerAccounts) {
            fromIbanCombo.addItem(a.getIban());
        }

        cancelBtn.addActionListener(e -> {
            result = null;
            dispose();
        });
        withdrawBtn.addActionListener(e -> onWithdraw());

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
        form.add(new JLabel("From (IBAN):"), c);
        c.gridx = 1;
        form.add(fromIbanCombo, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Amount (€):"), c);
        c.gridx = 1;
        form.add(amountSpinner, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Reason:"), c);
        c.gridx = 1;
        form.add(reasonField, c);
        y++;

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelBtn);
        buttons.add(withdrawBtn);

        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        getRootPane().setDefaultButton(withdrawBtn);
    }

    private void onWithdraw() {
        try {
            String fromIban = (String) fromIbanCombo.getSelectedItem();
            if (fromIban == null || fromIban.isBlank())
                throw new IllegalArgumentException("Please select an account.");

            double amount = ((Number) amountSpinner.getValue()).doubleValue();
            if (amount <= 0)
                throw new IllegalArgumentException("Amount must be > 0.");

            String reason = reasonField.getText().trim();
            if (reason.isEmpty())
                reason = "Cash withdrawal";

            Result r = new Result();
            r.fromIban = fromIban;
            r.amount = amount;
            r.reason = reason;

            result = r;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }
}
