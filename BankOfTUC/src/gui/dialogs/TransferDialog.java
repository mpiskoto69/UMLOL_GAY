package gui.dialogs;

import accounts.BankAccount;
import users.Customer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TransferDialog extends JDialog {

    public static class Result {
        public final String fromIban;
        public final String toIban;
        public final double amount;
        public final String reason;

        public Result(String fromIban, String toIban, double amount, String reason) {
            this.fromIban = fromIban;
            this.toIban = toIban;
            this.amount = amount;
            this.reason = reason;
        }
    }

    private Result result;

    private final JComboBox<String> fromCombo = new JComboBox<>();
    private final JTextField toIbanField = new JTextField(20);
    private final JTextField amountField = new JTextField(10);
    private final JTextField reasonField = new JTextField(20);

    public TransferDialog(Window owner, Customer customer, List<BankAccount> accounts) {
        super(owner, "Transfer", ModalityType.APPLICATION_MODAL);
        buildUI(accounts);
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUI(List<BankAccount> accounts) {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;

        for (BankAccount a : accounts) {
            fromCombo.addItem(a.getIban());
        }

        c.gridx=0; c.gridy=0;
        form.add(new JLabel("From account:"), c);
        c.gridx=1;
        form.add(fromCombo, c);

        c.gridx=0; c.gridy++;
        form.add(new JLabel("To IBAN:"), c);
        c.gridx=1;
        form.add(toIbanField, c);

        c.gridx=0; c.gridy++;
        form.add(new JLabel("Amount (€):"), c);
        c.gridx=1;
        form.add(amountField, c);

        c.gridx=0; c.gridy++;
        form.add(new JLabel("Reason:"), c);
        c.gridx=1;
        form.add(reasonField, c);

        JButton cancelBtn = new JButton("Cancel");
        JButton okBtn = new JButton("Transfer");

        cancelBtn.addActionListener(e -> dispose());
        okBtn.addActionListener(e -> onOk());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelBtn);
        buttons.add(okBtn);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void onOk() {
        try {
            String from = (String) fromCombo.getSelectedItem();
            String to = toIbanField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());
            String reason = reasonField.getText().trim();

            if (from == null || to.isEmpty() || amount <= 0) {
                throw new IllegalArgumentException("Invalid input");
            }

            result = new Result(from, to, amount, reason.isBlank() ? "Transfer" : reason);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Invalid data. Check IBAN / amount.",
                "Validation error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public Result showDialog() {
        setVisible(true);
        return result;
    }
}