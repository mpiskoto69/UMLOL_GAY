package test101;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class BusinessMenuStatementsFrame extends JFrame implements ActionListener {

    private final JButton btnLogout = new JButton("log out");

    private final JLabel title = new JLabel("Business Menu 3");
    private final JLabel subtitle = new JLabel("Statements");

    private final JLabel sectionTitle = new JLabel("statements");

    // Column headers
    private final JLabel hParties = new JLabel("sender b.a. - recipient b.a.");
    private final JLabel hAmount  = new JLabel("amount €");
    private final JLabel hReason  = new JLabel("Reason");
    private final JLabel hDate    = new JLabel("date");

    // Example rows (3 όπως στο sketch)
    private final JLabel r1Parties = new JLabel("sender b.a. - recipient b.a.");
    private final JLabel r1Amount  = new JLabel("amount €");
    private final JLabel r1Reason  = new JLabel("Reason");
    private final JLabel r1Date    = new JLabel("date");

    private final JLabel r2Parties = new JLabel("sender b.a. - recipient b.a.");
    private final JLabel r2Amount  = new JLabel("amount €");
    private final JLabel r2Reason  = new JLabel("Reason");
    private final JLabel r2Date    = new JLabel("date");

    private final JLabel r3Parties = new JLabel("sender b.a. - recipient b.a.");
    private final JLabel r3Amount  = new JLabel("amount €");
    private final JLabel r3Reason  = new JLabel("Reason");
    private final JLabel r3Date    = new JLabel("date");

    public BusinessMenuStatementsFrame() {
        // Frame
        setTitle("Bank of TUC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setResizable(false);
        setLayout(null);

        // Optional icon
        // setIconImage(new ImageIcon("TUC.gif").getImage());

        // Logout
        btnLogout.setBounds(20, 20, 90, 35);
        btnLogout.setFocusable(false);
        btnLogout.setMargin(new Insets(2, 6, 2, 6));
        btnLogout.addActionListener(this);
        add(btnLogout);

        // Title
        title.setBounds(0, 55, 1080, 55);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.PLAIN, 32));
        add(title);

        // Subtitle
        subtitle.setBounds(0, 110, 1080, 35);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        add(subtitle);

        // Section title (left-ish)
        sectionTitle.setBounds(210, 190, 200, 25);
        sectionTitle.setHorizontalAlignment(SwingConstants.LEFT);
        sectionTitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        add(sectionTitle);

        // Table column positions (approx like sketch)
        int tableY = 235;
        int rowH = 35;

        int col1X = 150; // sender-recipient
        int col2X = 520; // amount
        int col3X = 690; // reason
        int col4X = 880; // date

        Font headerFont = new Font("SansSerif", Font.PLAIN, 14);
        Font rowFont = new Font("SansSerif", Font.PLAIN, 14);

        // Headers
        hParties.setFont(headerFont);
        hAmount.setFont(headerFont);
        hReason.setFont(headerFont);
        hDate.setFont(headerFont);

        hParties.setBounds(col1X, tableY, 320, 25);
        hAmount.setBounds(col2X, tableY, 120, 25);
        hReason.setBounds(col3X, tableY, 120, 25);
        hDate.setBounds(col4X, tableY, 120, 25);

        add(hParties);
        add(hAmount);
        add(hReason);
        add(hDate);

        // Rows (3)
        placeRow(r1Parties, r1Amount, r1Reason, r1Date, 1, tableY, rowH, col1X, col2X, col3X, col4X, rowFont);
        placeRow(r2Parties, r2Amount, r2Reason, r2Date, 2, tableY, rowH, col1X, col2X, col3X, col4X, rowFont);
        placeRow(r3Parties, r3Amount, r3Reason, r3Date, 3, tableY, rowH, col1X, col2X, col3X, col4X, rowFont);

        // Dots (continuation)
        int dotsY = tableY + 6 * rowH;
        addDots(col1X + 150, dotsY);
        addDots(col2X + 40,  dotsY);
        addDots(col3X + 35,  dotsY);
        addDots(col4X + 25,  dotsY);

        setVisible(true);
    }

    private void placeRow(
            JLabel parties, JLabel amount, JLabel reason, JLabel date,
            int rowIndex, int tableY, int rowH,
            int col1X, int col2X, int col3X, int col4X,
            Font rowFont
    ) {
        int y = tableY + rowIndex * rowH;

        parties.setFont(rowFont);
        amount.setFont(rowFont);
        reason.setFont(rowFont);
        date.setFont(rowFont);

        parties.setBounds(col1X, y, 320, 25);
        amount.setBounds(col2X, y, 120, 25);
        reason.setBounds(col3X, y, 120, 25);
        date.setBounds(col4X, y, 120, 25);

        add(parties);
        add(amount);
        add(reason);
        add(date);
    }

    private void addDots(int x, int y) {
        JLabel d1 = new JLabel(":");
        d1.setForeground(Color.DARK_GRAY);
        d1.setFont(new Font("SansSerif", Font.PLAIN, 22));
        d1.setBounds(x, y, 20, 25);
        add(d1);

        JLabel d2 = new JLabel(":");
        d2.setForeground(Color.DARK_GRAY);
        d2.setFont(new Font("SansSerif", Font.PLAIN, 22));
        d2.setBounds(x, y + 20, 20, 25);
        add(d2);

        JLabel d3 = new JLabel(":");
        d3.setForeground(Color.DARK_GRAY);
        d3.setFont(new Font("SansSerif", Font.PLAIN, 22));
        d3.setBounds(x, y + 40, 20, 25);
        add(d3);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogout) {
            System.out.println("Logout clicked");
            // dispose(); ή επιστροφή στο προηγούμενο frame
        }
    }
}
