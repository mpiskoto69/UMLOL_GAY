
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
    private final JButton advance7Btn = new JButton("Advance 7 days");
    private final JTextArea logArea = new JTextArea(10, 60);

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
        actions.add(advance7Btn);

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
        advance7Btn.addActionListener(e -> advanceDays(7));

        refresh();
    }

    public void refresh() {
        LocalDate d = facade.getCurrentDate();
        dateLabel.setText("Today: " + d);
    }

    private void advanceDays(int n) {
        LocalDate before = facade.getCurrentDate();
        for (int i = 0; i < n; i++) {
            facade.nextDay();
        }
        LocalDate after = facade.getCurrentDate();

        log("Advanced: " + before + " -> " + after + " (+" + n + " day(s))");

        // Notify parent frame to refresh header/tables
        if (afterAdvance != null) afterAdvance.run();

        refresh();
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
}
