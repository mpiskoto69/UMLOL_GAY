
package gui.panels;

import app.BankingFacade;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class SimulationPanel extends JPanel {

    private final BankingFacade facade;
    private final Runnable afterAdvance;

    private final JLabel dateLabel = new JLabel();
    private final JButton nextDayBtn = new JButton("Next day");
    private final JButton resetBtn = new JButton("Reset to Today");

   
    private final JTextArea logArea = new JTextArea(10, 60);
    private final JTextField targetDateField = new JTextField(10); // yyyy-mm-dd
    private final JButton goBtn = new JButton("Go to date");

    public SimulationPanel(BankingFacade facade, Runnable afterAdvance) {
        this.facade = facade;
        this.afterAdvance = afterAdvance;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Time Simulation");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.add(title, BorderLayout.WEST);

        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        top.add(dateLabel, BorderLayout.EAST);

        // Buttons row
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.add(nextDayBtn);
        
        targetDateField.setText(facade.getCurrentDate().toString());
actions.add(new JLabel("Target (yyyy-mm-dd):"));
actions.add(targetDateField);
actions.add(goBtn);
actions.add(resetBtn);


        // Log
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(logArea);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(actions, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        // Wire events
        nextDayBtn.addActionListener(e -> advanceDays(1));
        goBtn.addActionListener(e -> goToDate());
        resetBtn.addActionListener(e -> onReset());

        refresh();
    }

    public void refresh() {
        LocalDate d = facade.getCurrentDate();
        dateLabel.setText("Today: " + d);
    }

    private void advanceDays(int n) {
        LocalDate before = facade.getCurrentDate();
       for (int i = 0; i < n; i++) {
    var rep = facade.nextDayWithReport();
    logReport(rep);
}

        LocalDate after = facade.getCurrentDate();

        log("Advanced: " + before + " -> " + after + " (+" + n + " day(s))");

        // Notify parent frame to refresh header/tables
        if (afterAdvance != null) afterAdvance.run();

        refresh();
    }
    private void goToDate() {
    try {
        LocalDate target = LocalDate.parse(targetDateField.getText().trim());
        if (target.isBefore(facade.getCurrentDate())) {
            log("Target must be >= today");
            return;
        }

        LocalDate before = facade.getCurrentDate();
        while (facade.getCurrentDate().isBefore(target)) {
            var rep = facade.nextDayWithReport();
            logReport(rep);
        }
        log("Advanced: " + before + " -> " + facade.getCurrentDate());

        if (afterAdvance != null) afterAdvance.run();
        refresh();

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid date", JOptionPane.ERROR_MESSAGE);
    }
}

private void logReport(managers.StandingOrderManager.ExecutionReport rep) {
    if (rep == null) return;
    if (rep.success.isEmpty() && rep.failed.isEmpty()) return;

    log("== Standing Orders " + rep.date + " ==");
    for (String s : rep.success) log(" OK   " + s);
    for (String f : rep.failed)  log(" FAIL " + f);
}

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
 private void onReset() {
    int r = JOptionPane.showConfirmDialog(
            this,
            "Reset to real today and discard ALL simulated changes?",
            "Reset",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
    );
    if (r != JOptionPane.YES_OPTION) return;

    try {
        facade.resetToRealTodayAndDiscardSimulated();
        log("RESET done. Today is: " + facade.getCurrentDate());

        if (afterAdvance != null) afterAdvance.run();
        refresh();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Reset failed", JOptionPane.ERROR_MESSAGE);
    }
}

}
