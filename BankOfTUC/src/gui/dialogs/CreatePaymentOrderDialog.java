package gui.dialogs;

import accounts.BankAccount;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import users.Customer;

public class CreatePaymentOrderDialog extends JDialog {

    public static class Result {
        public String title;
        public String description;
        public String fromIban;
        public String rfCode;
        public double maxAmount;
        public LocalDate startDate;
        public LocalDate endDate;
        public double fee;
    }

    private Result result = null;

    // Fields
    private final JTextField titleField = new JTextField(22);
    private final JTextField descField = new JTextField(22);

    private final JComboBox<String> fromIbanCombo = new JComboBox<>();
    private final JTextField rfField = new JTextField(18);

    private final JSpinner maxAmountSpinner = new JSpinner(new SpinnerNumberModel(100.00, 0.01, 1_000_000.00, 1.00));
    private final JSpinner feeSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 1_000_000.00, 0.50));

    private final JTextField startDateField = new JTextField(10); // yyyy-mm-dd
    private final JTextField endDateField = new JTextField(10); // yyyy-mm-dd

    private final JButton cancelBtn = new JButton("Cancel");
    private final JButton createBtn = new JButton("Create");

    public CreatePaymentOrderDialog(Window owner,
            Customer customer,
            List<BankAccount> customerAccounts,
            LocalDate today) {
        super(owner, "Create Payment Standing Order", ModalityType.APPLICATION_MODAL);

        buildUI();

        // defaults
        titleField.setText("Standing Bill Payment");
        descField.setText("");
        startDateField.setText(today.toString());
        endDateField.setText(today.plusMonths(12).toString());

        // populate from accounts
        for (BankAccount a : customerAccounts) {
            fromIbanCombo.addItem(a.getIban());
        }

        // wire
        cancelBtn.addActionListener(e -> {
            result = null;
            dispose();
        });
        createBtn.addActionListener(e -> onCreate());

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

        // title
        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Title:"), c);
        c.gridx = 1;
        form.add(titleField, c);
        y++;

        // description
        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Description:"), c);
        c.gridx = 1;
        form.add(descField, c);
        y++;

        // from
        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Pay from (IBAN):"), c);
        c.gridx = 1;
        form.add(fromIbanCombo, c);
        y++;

        // RF
        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("RF code:"), c);
        c.gridx = 1;
        form.add(rfField, c);
        y++;

        // max amount
        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Max amount (€):"), c);
        c.gridx = 1;
        form.add(maxAmountSpinner, c);
        y++;

        // start date
        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Start date (yyyy-mm-dd):"), c);
        c.gridx = 1;
        form.add(startDateField, c);
        y++;

        // end date
        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("End date (yyyy-mm-dd):"), c);
        c.gridx = 1;
        form.add(endDateField, c);
        y++;

        // fee
        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Fee (€):"), c);
        c.gridx = 1;
        form.add(feeSpinner, c);
        y++;

        // buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelBtn);
        buttons.add(createBtn);

        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        getRootPane().setDefaultButton(createBtn);
    }

    private void onCreate() {
        try {
            String title = titleField.getText().trim();
            if (title.isEmpty())
                title = "Standing Bill Payment";

            String fromIban = (String) fromIbanCombo.getSelectedItem();
            if (fromIban == null || fromIban.isBlank())
                throw new IllegalArgumentException("Please select a source account.");

            String rf = rfField.getText().trim();
            if (rf.isEmpty())
                throw new IllegalArgumentException("RF code is required.");
            if (!rf.startsWith("RF"))
                throw new IllegalArgumentException("RF must start with 'RF'.");

            double maxAmount = ((Number) maxAmountSpinner.getValue()).doubleValue();
            if (maxAmount <= 0)
                throw new IllegalArgumentException("Max amount must be > 0.");

            LocalDate start = LocalDate.parse(startDateField.getText().trim());
            LocalDate end = LocalDate.parse(endDateField.getText().trim());
            if (end.isBefore(start))
                throw new IllegalArgumentException("End date must be after start date.");

            double fee = ((Number) feeSpinner.getValue()).doubleValue();
            if (fee < 0)
                throw new IllegalArgumentException("Fee must be >= 0.");

            Result r = new Result();
            r.title = title;
            r.description = descField.getText().trim();
            r.fromIban = fromIban;
            r.rfCode = rf;
            r.maxAmount = maxAmount;
            r.startDate = start;
            r.endDate = end;
            r.fee = fee;

            result = r;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
