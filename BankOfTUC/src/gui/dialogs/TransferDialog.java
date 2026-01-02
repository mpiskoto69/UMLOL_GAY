package gui.dialogs;

import accounts.BankAccount;
import users.Customer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TransferDialog extends JDialog {

    public enum Network {
        INTRA, SEPA, SWIFT
    }

    public static class Result {
        public final String fromIban;
        public final String toIban;
        public final double amount;
        public final String reason;
        public final Network network;

        public Result(String fromIban, String toIban, double amount, String reason, Network network) {
            this.fromIban = fromIban;
            this.toIban = toIban;
            this.amount = amount;
            this.reason = reason;
            this.network = network;
        }
    }

    private Result result;

    private final JComboBox<String> fromCombo = new JComboBox<>();
    private final JTextField toIbanField = new JTextField(20);
    private final JTextField amountField = new JTextField(10);
    private final JTextField reasonField = new JTextField(20);

    private final JComboBox<Network> networkCombo = new JComboBox<>(Network.values());

    public TransferDialog(Window owner, Customer customer, List<BankAccount> accounts, String preselectedIban) {
        super(owner, "Transfer", ModalityType.APPLICATION_MODAL);

        for (BankAccount a : accounts) {
            fromCombo.addItem(a.getIban());
        }
        if (preselectedIban != null) {
            fromCombo.setSelectedItem(preselectedIban);
        }

        buildUI();
        pack();
        setLocationRelativeTo(owner);
    }

    public Result showDialog() {
        setVisible(true);
        return result;
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;

        int y = 0;

        c.gridx=0; c.gridy=y; form.add(new JLabel("From account:"), c);
        c.gridx=1; form.add(fromCombo, c); y++;

        c.gridx=0; c.gridy=y; form.add(new JLabel("To IBAN:"), c);
        c.gridx=1; form.add(toIbanField, c); y++;

        c.gridx=0; c.gridy=y; form.add(new JLabel("Amount (€):"), c);
        c.gridx=1; form.add(amountField, c); y++;

        c.gridx=0; c.gridy=y; form.add(new JLabel("Reason:"), c);
        c.gridx=1; form.add(reasonField, c); y++;

        c.gridx=0; c.gridy=y; form.add(new JLabel("Network:"), c);
        c.gridx=1; form.add(networkCombo, c); y++;

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

        getRootPane().setDefaultButton(okBtn);
    }

    private void onOk() {
        try {
            String from = (String) fromCombo.getSelectedItem();
            String to = toIbanField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());
            String reason = reasonField.getText().trim();
            Network network = (Network) networkCombo.getSelectedItem();

            if (from == null || from.isBlank())
                throw new IllegalArgumentException("Select source account.");
            if (to.isEmpty())
                throw new IllegalArgumentException("Target IBAN is required.");
            if (amount <= 0)
                throw new IllegalArgumentException("Amount must be > 0.");
            if (network == null)
                network = Network.INTRA;

            if (reason.isBlank()) reason = "Transfer";

            result = new Result(from, to, amount, reason, network);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validation error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
