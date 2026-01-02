package gui.dialogs;

import accounts.BankAccount;
import users.Customer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PayBillDialog extends JDialog {

    public static class Result {
        public final String fromIban;
        public final String rfCode;

        public Result(String fromIban, String rfCode) {
            this.fromIban = fromIban;
            this.rfCode = rfCode;
        }
    }

    private Result result;

    private final JComboBox<String> fromCombo = new JComboBox<>();
    private final JTextField rfField = new JTextField(18);

    public PayBillDialog(Window owner, Customer customer, List<BankAccount> accounts) {
        super(owner, "Pay Bill (RF)", ModalityType.APPLICATION_MODAL);

        for (BankAccount a : accounts) {
            fromCombo.addItem(a.getIban());
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
        form.add(new JLabel("Pay from account:"), c);
        c.gridx = 1;
        form.add(fromCombo, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("RF code:"), c);
        c.gridx = 1;
        form.add(rfField, c);
        y++;

        JButton cancelBtn = new JButton("Cancel");
        JButton payBtn = new JButton("Pay");

        cancelBtn.addActionListener(e -> dispose());
        payBtn.addActionListener(e -> onPay());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelBtn);
        buttons.add(payBtn);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(payBtn);
    }

    private void onPay() {
        try {
            String from = (String) fromCombo.getSelectedItem();
            String rf = rfField.getText().trim();

            if (from == null || from.isBlank())
                throw new IllegalArgumentException("Select an account.");

            if (rf.isEmpty() || !rf.startsWith("RF"))
                throw new IllegalArgumentException("RF must start with 'RF'.");

            result = new Result(from, rf);
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

