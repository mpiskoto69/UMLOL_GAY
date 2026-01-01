package test101;

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

public class BusinessMenuTransactionsFrame extends JFrame implements ActionListener {

    private final JButton btnLogout = new JButton("log out");

    private final JLabel title = new JLabel("Business Menu 2");
    private final JLabel subtitle = new JLabel("Transactions");

    private final JLabel n1 = new JLabel("1]");
    private final JLabel n2 = new JLabel("2]");
    private final JLabel n3 = new JLabel("3]");
    private final JLabel n4 = new JLabel("4]");

    private final JButton btnDeposit = new JButton("deposit");
    private final JButton btnWithdrawal = new JButton("withdrawal");
    private final JButton btnTransfer = new JButton("transfer");
    private final JButton btnPayment = new JButton("payment");

    public BusinessMenuTransactionsFrame() {
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

        // Styles
        Border thinBorder = BorderFactory.createLineBorder(Color.DARK_GRAY);
        Font btnFont = new Font("SansSerif", Font.PLAIN, 18);
        Font numFont = new Font("SansSerif", Font.PLAIN, 16);

        styleNum(n1, numFont);
        styleNum(n2, numFont);
        styleNum(n3, numFont);
        styleNum(n4, numFont);

        styleBtn(btnDeposit, btnFont, thinBorder);
        styleBtn(btnWithdrawal, btnFont, thinBorder);
        styleBtn(btnTransfer, btnFont, thinBorder);
        styleBtn(btnPayment, btnFont, thinBorder);

        btnDeposit.addActionListener(this);
        btnWithdrawal.addActionListener(this);
        btnTransfer.addActionListener(this);
        btnPayment.addActionListener(this);

        // Layout like the sketch
        int btnX = 410, btnW = 220, btnH = 55;
        int numX = btnX - 70, numW = 40, numH = btnH;

        int startY = 200;
        int gapY = 85;

        // Row 1
        n1.setBounds(numX, startY + 0 * gapY, numW, numH);
        btnDeposit.setBounds(btnX, startY + 0 * gapY, btnW, btnH);

        // Row 2
        n2.setBounds(numX, startY + 1 * gapY, numW, numH);
        btnWithdrawal.setBounds(btnX, startY + 1 * gapY, btnW, btnH);

        // Row 3
        n3.setBounds(numX, startY + 2 * gapY, numW, numH);
        btnTransfer.setBounds(btnX, startY + 2 * gapY, btnW, btnH);

        // Row 4
        n4.setBounds(numX, startY + 3 * gapY, numW, numH);
        btnPayment.setBounds(btnX, startY + 3 * gapY, btnW, btnH);

        add(n1); add(btnDeposit);
        add(n2); add(btnWithdrawal);
        add(n3); add(btnTransfer);
        add(n4); add(btnPayment);

        setVisible(true);
    }

    private static void styleBtn(JButton b, Font f, Border border) {
        b.setFont(f);
        b.setBorder(border);
        b.setFocusable(false);
    }

    private static void styleNum(JLabel l, Font f) {
        l.setFont(f);
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        l.setVerticalAlignment(SwingConstants.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnLogout) {
            System.out.println("Logout clicked");
            return;
        }

        if (src == btnDeposit) System.out.println("Deposit clicked");
        if (src == btnWithdrawal) System.out.println("Withdrawal clicked");
        if (src == btnTransfer) System.out.println("Transfer clicked");
        if (src == btnPayment) System.out.println("Payment clicked");
    }
}
