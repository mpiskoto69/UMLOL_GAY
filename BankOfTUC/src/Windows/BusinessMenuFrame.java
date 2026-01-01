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

public class BusinessMenuFrame extends JFrame implements ActionListener {

    private final JButton btnLogout = new JButton("log out");

    private final JLabel title = new JLabel("Business Menu");

    private final JLabel lblBankAccount = new JLabel("Bank account");
    private final JLabel lblTotalAmount = new JLabel("Total amount €");

    private final JLabel n1 = new JLabel("1]");
    private final JLabel n2 = new JLabel("2]");
    private final JLabel n3 = new JLabel("3]");

    private final JButton btnBills = new JButton("Bills");
    private final JButton btnTransactions = new JButton("Transactions");
    private final JButton btnStatements = new JButton("statements");

    public BusinessMenuFrame() {
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
        title.setBounds(0, 65, 1080, 60);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.PLAIN, 36));
        add(title);

        // Top labels ("Bank account" and "Total amount €")
        lblBankAccount.setBounds(180, 170, 200, 30);
        lblBankAccount.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblBankAccount.setHorizontalAlignment(SwingConstants.LEFT);
        add(lblBankAccount);

        lblTotalAmount.setBounds(700, 170, 200, 30);
        lblTotalAmount.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblTotalAmount.setHorizontalAlignment(SwingConstants.LEFT);
        add(lblTotalAmount);

        // Styles
        Border thinBorder = BorderFactory.createLineBorder(Color.DARK_GRAY);
        Font btnFont = new Font("SansSerif", Font.PLAIN, 18);
        Font numFont = new Font("SansSerif", Font.PLAIN, 16);

        styleNum(n1, numFont);
        styleNum(n2, numFont);
        styleNum(n3, numFont);

        styleBtn(btnBills, btnFont, thinBorder);
        styleBtn(btnTransactions, btnFont, thinBorder);
        styleBtn(btnStatements, btnFont, thinBorder);

        btnBills.addActionListener(this);
        btnTransactions.addActionListener(this);
        btnStatements.addActionListener(this);

        // Buttons layout (center-ish)
        int btnW = 220;
        int btnH = 55;
        int btnX = (1080 - btnW) / 2;

        int numX = btnX - 70;
        int numW = 40;
        int numH = btnH;

        int startY = 260;
        int gapY = 85;

        // Row 1
        n1.setBounds(numX, startY + 0 * gapY, numW, numH);
        btnBills.setBounds(btnX, startY + 0 * gapY, btnW, btnH);

        // Row 2
        n2.setBounds(numX, startY + 1 * gapY, numW, numH);
        btnTransactions.setBounds(btnX, startY + 1 * gapY, btnW, btnH);

        // Row 3
        n3.setBounds(numX, startY + 2 * gapY, numW, numH);
        btnStatements.setBounds(btnX, startY + 2 * gapY, btnW, btnH);

        add(n1); add(btnBills);
        add(n2); add(btnTransactions);
        add(n3); add(btnStatements);

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

        if (src == btnBills) {
            System.out.println("Bills clicked");
        } else if (src == btnTransactions) {
            System.out.println("Transactions clicked");
        } else if (src == btnStatements) {
            System.out.println("Statements clicked");
        }
    }

    // Αν θες να γεμίζεις δυναμικά πληροφορίες:
    public void setBankAccountLabel(String text) {
        lblBankAccount.setText(text);
    }

    public void setTotalAmountLabel(String text) {
        lblTotalAmount.setText(text);
    }
}
