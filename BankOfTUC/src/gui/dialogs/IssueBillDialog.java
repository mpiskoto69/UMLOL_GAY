package gui.dialogs;

import users.Company;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class IssueBillDialog extends JDialog {

    public static class Result {
        public String customerVat;
        public double amount;
        public LocalDate dueDate;
    }

    private Result result;

    private final JTextField customerVatField = new JTextField(18);
    private final JSpinner amountSpinner =
            new JSpinner(new SpinnerNumberModel(10.00, 0.01, 1_000_000.00, 1.00));
    private final JTextField dueDateField = new JTextField(10); // yyyy-mm-dd

    private final JButton cancelBtn = new JButton("Cancel");
    private final JButton issueBtn = new JButton("Issue");

    public IssueBillDialog(Window owner, Company company, LocalDate today) {
        super(owner, "Issue Bill", ModalityType.APPLICATION_MODAL);

        buildUI();

        dueDateField.setText(today.plusDays(15).toString());

        cancelBtn.addActionListener(e -> { result = null; dispose(); });
        issueBtn.addActionListener(e -> onIssue());

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

        c.gridx=0; c.gridy=y; form.add(new JLabel("Customer VAT:"), c);
        c.gridx=1; form.add(customerVatField, c); y++;

        c.gridx=0; c.gridy=y; form.add(new JLabel("Amount (€):"), c);
        c.gridx=1; form.add(amountSpinner, c); y++;

        c.gridx=0; c.gridy=y; form.add(new JLabel("Due date (yyyy-mm-dd):"), c);
        c.gridx=1; form.add(dueDateField, c); y++;

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelBtn);
        buttons.add(issueBtn);

        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        getRootPane().setDefaultButton(issueBtn);
    }

    private void onIssue() {
        try {
            String vat = customerVatField.getText().trim();
            if (vat.isBlank()) throw new IllegalArgumentException("Customer VAT is required");

            double amount = ((Number) amountSpinner.getValue()).doubleValue();
            if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");

            LocalDate due = LocalDate.parse(dueDateField.getText().trim());

            Result r = new Result();
            r.customerVat = vat;
            r.amount = amount;
            r.dueDate = due;

            result = r;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }
}
