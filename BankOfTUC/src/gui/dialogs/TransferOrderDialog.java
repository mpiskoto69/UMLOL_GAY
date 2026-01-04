package gui.dialogs;

import accounts.BankAccount;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import users.Customer;

public class TransferOrderDialog extends JDialog {

    public static class Result {
        public String title;
        public String description;
        public String fromIban;
        public String toIban;
        public double amount;
        public int frequencyInMonths;
        public int dayOfMonth;
        public LocalDate startDate;
        public LocalDate endDate;
        public double fee;
    }

    private Result result = null;

    private final JTextField titleField = new JTextField(22);
    private final JTextField descField = new JTextField(22);

    private final JComboBox<String> fromIbanCombo = new JComboBox<>();
    private final JTextField toIbanField = new JTextField(22);

    private final JSpinner amountSpinner = new JSpinner(new SpinnerNumberModel(10.00, 0.01, 1_000_000.00, 1.00));
    private final JSpinner freqSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));
    private final JSpinner daySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
    private final JSpinner feeSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 1_000_000.00, 0.50));

    private final JTextField startDateField = new JTextField(10);
    private final JTextField endDateField = new JTextField(10);

    private final JButton cancelBtn = new JButton("Cancel");
    private final JButton createBtn = new JButton("Create");

    public TransferOrderDialog(Window owner, Customer customer, List<BankAccount> customerAccounts, LocalDate today) {
        super(owner, "Create Transfer Standing Order", ModalityType.APPLICATION_MODAL);

        buildUI();

        titleField.setText("Standing Transfer");
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
        form.add(new JLabel("From (IBAN):"), c);
        c.gridx = 1;
        form.add(fromIbanCombo, c);
        y++;

        // to
        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("To (IBAN):"), c);
        c.gridx = 1;
        form.add(toIbanField, c);
        y++;

        // amount
        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Amount (€):"), c);
        c.gridx = 1;
        form.add(amountSpinner, c);
        y++;

        // frequency
        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Every N months:"), c);
        c.gridx = 1;
        form.add(freqSpinner, c);
        y++;

        // day of month
        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Day of month (1-31):"), c);
        c.gridx = 1;
        form.add(daySpinner, c);
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
                title = "Standing Transfer";

            String fromIban = (String) fromIbanCombo.getSelectedItem();
            if (fromIban == null || fromIban.isBlank()) {
                throw new IllegalArgumentException("Please select a source account.");
            }

            String toIban = toIbanField.getText().trim();
            if (toIban.isEmpty())
                throw new IllegalArgumentException("Target IBAN is required.");

            double amount = ((Number) amountSpinner.getValue()).doubleValue();
            if (amount <= 0)
                throw new IllegalArgumentException("Amount must be > 0.");

            int freq = ((Number) freqSpinner.getValue()).intValue();
            if (freq < 1)
                throw new IllegalArgumentException("Frequency must be >= 1.");

            int day = ((Number) daySpinner.getValue()).intValue();
            if (day < 1 || day > 31)
                throw new IllegalArgumentException("Day of month must be 1..31.");

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
            r.toIban = toIban;
            r.amount = amount;
            r.frequencyInMonths = freq;
            r.dayOfMonth = day;
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
