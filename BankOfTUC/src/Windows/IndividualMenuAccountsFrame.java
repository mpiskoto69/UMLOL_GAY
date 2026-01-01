package Windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class IndividualMenuAccountsFrame extends JFrame implements ActionListener {

    private final JButton btnLogout = new JButton("log out");

    private final JLabel title = new JLabel("Individual Menu 1");
    private final JLabel subtitleAccounts = new JLabel("Accounts");
    private final JLabel subtitleTotal = new JLabel("Total amount €");

    // Header row
    private final JLabel hName = new JLabel("");          // (κενό για τη στήλη "Bank account X")
    private final JLabel hType = new JLabel("Type");
    private final JLabel hAmount = new JLabel("amount €");

    // Example rows (3 accounts όπως στο sketch)
    private final JLabel a1 = new JLabel("Bank account 1");
    private final JLabel a2 = new JLabel("Bank account 2");
    private final JLabel a3 = new JLabel("Bank account 3");

    private final JLabel t1 = new JLabel("Type");
    private final JLabel t2 = new JLabel("Type");
    private final JLabel t3 = new JLabel("Type");

    private final JLabel m1 = new JLabel("amount €");
    private final JLabel m2 = new JLabel("amount €");
    private final JLabel m3 = new JLabel("amount €");

    private final JButton s1 = new JButton("view statements");
    private final JButton s2 = new JButton("view statements");
    private final JButton s3 = new JButton("view statements");

    // Right-side index
    private final JLabel idx1 = new JLabel("1]");

    public IndividualMenuAccountsFrame() {
        // Frame
        setTitle("Bank of TUC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setResizable(false);
        setLayout(null);

        // Optional icon
        // setIconImage(new ImageIcon("TUC.gif").getImage());

        Border thinBorder = BorderFactory.createLineBorder(Color.DARK_GRAY);

        // Logout button (top-left)
        btnLogout.setBounds(20, 20, 90, 35);
        btnLogout.setFocusable(false);
        btnLogout.setMargin(new Insets(2, 6, 2, 6));
        btnLogout.addActionListener(this);
        add(btnLogout);

        // Titles
        title.setBounds(0, 55, 1080, 50);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.PLAIN, 30));
        add(title);

        subtitleAccounts.setBounds(0, 110, 1080, 30);
        subtitleAccounts.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleAccounts.setFont(new Font("SansSerif", Font.PLAIN, 18));
        add(subtitleAccounts);

        subtitleTotal.setBounds(0, 145, 1080, 30);
        subtitleTotal.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleTotal.setFont(new Font("SansSerif", Font.PLAIN, 18));
        add(subtitleTotal);

        // Table-ish layout positions (approx like the sketch)
        int baseY = 240;
        int rowH = 35;

        int colNameX = 120, colNameW = 220;
        int colTypeX = 360, colTypeW = 120;
        int colAmtX  = 500, colAmtW  = 150;

        int colBtnX  = 690, colBtnW  = 160, colBtnH = 30;

        Font headerFont = new Font("SansSerif", Font.PLAIN, 14);
        Font rowFont = new Font("SansSerif", Font.PLAIN, 14);
        Font btnFont = new Font("SansSerif", Font.PLAIN, 14);

        // Header row labels (πάνω από τις γραμμές λογαριασμών)
        hType.setFont(headerFont);
        hAmount.setFont(headerFont);

        hType.setBounds(colTypeX, baseY - 35, colTypeW, 25);
        hAmount.setBounds(colAmtX, baseY - 35, colAmtW, 25);

        add(hType);
        add(hAmount);

        // Row 1
        placeRow(a1, t1, m1, s1, 0, baseY, rowH, colNameX, colNameW, colTypeX, colTypeW, colAmtX, colAmtW, colBtnX, colBtnW, colBtnH, rowFont, btnFont, thinBorder);
        // Row 2
        placeRow(a2, t2, m2, s2, 1, baseY, rowH, colNameX, colNameW, colTypeX, colTypeW, colAmtX, colAmtW, colBtnX, colBtnW, colBtnH, rowFont, btnFont, thinBorder);
        // Row 3
        placeRow(a3, t3, m3, s3, 2, baseY, rowH, colNameX, colNameW, colTypeX, colTypeW, colAmtX, colAmtW, colBtnX, colBtnW, colBtnH, rowFont, btnFont, thinBorder);

        // Right-side "1]" (όπως στο sketch, δεξιά από τα κουμπιά)
        idx1.setFont(new Font("SansSerif", Font.PLAIN, 14));
        idx1.setHorizontalAlignment(SwingConstants.LEFT);
        idx1.setBounds(colBtnX + colBtnW + 20, baseY - 2, 40, 30);
        add(idx1);

        // Dots to show "more rows" (όπως στην εικόνα)
        addDots(colNameX + 140, baseY + 4 * rowH + 40);
        addDots(colTypeX + 40,  baseY + 4 * rowH + 40);
        addDots(colAmtX + 60,   baseY + 4 * rowH + 40);
        addDots(colBtnX + 80,   baseY + 4 * rowH + 40);

        setVisible(true);
    }

    private void placeRow(
            JLabel name, JLabel type, JLabel amount, JButton btn,
            int rowIndex, int baseY, int rowH,
            int colNameX, int colNameW,
            int colTypeX, int colTypeW,
            int colAmtX,  int colAmtW,
            int colBtnX,  int colBtnW, int colBtnH,
            Font rowFont, Font btnFont, Border thinBorder
    ) {
        int y = baseY + rowIndex * rowH;

        name.setFont(rowFont);
        type.setFont(rowFont);
        amount.setFont(rowFont);

        name.setBounds(colNameX, y, colNameW, 25);
        type.setBounds(colTypeX, y, colTypeW, 25);
        amount.setBounds(colAmtX, y, colAmtW, 25);

        btn.setFont(btnFont);
        btn.setFocusable(false);
        btn.setBorder(thinBorder);
        btn.setBounds(colBtnX, y - 5, colBtnW, colBtnH);
        btn.addActionListener(this);

        add(name);
        add(type);
        add(amount);
        add(btn);
    }

    private void addDots(int x, int y) {
        JLabel dots = new JLabel(":");
        dots.setFont(new Font("SansSerif", Font.PLAIN, 22));
        dots.setBounds(x, y, 20, 25);
        add(dots);

        JLabel dots2 = new JLabel(":");
        dots2.setFont(new Font("SansSerif", Font.PLAIN, 22));
        dots2.setBounds(x, y + 20, 20, 25);
        add(dots2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnLogout) {
            System.out.println("Logout clicked");
            // dispose(); ή επιστροφή στο προηγούμενο frame
            return;
        }

        if (src == s1) System.out.println("View statements for account 1");
        if (src == s2) System.out.println("View statements for account 2");
        if (src == s3) System.out.println("View statements for account 3");
    }
}
