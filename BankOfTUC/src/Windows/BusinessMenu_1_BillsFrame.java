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

public class BusinessMenu_1_BillsFrame extends JFrame implements ActionListener {

    private final JButton btnLogout = new JButton("log out");

    private final JLabel title = new JLabel("Business Menu 1");
    private final JLabel subtitle = new JLabel("Bills");

    private final JLabel n1 = new JLabel("1]");
    private final JLabel n2 = new JLabel("2]");
    private final JLabel n3 = new JLabel("3]");

    private final JButton btnLoadIssued = new JButton("Load Issued Bills");
    private final JButton btnShowIssued = new JButton("Show Issued Bills");
    private final JButton btnShowPaid = new JButton("Show Paid Bills");

    public BusinessMenu_1_BillsFrame() {
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
        Font btnFont = new Font("SansSerif", Font.PLAIN, 16);
        Font numFont = new Font("SansSerif", Font.PLAIN, 16);

        styleNum(n1, numFont);
        styleNum(n2, numFont);
        styleNum(n3, numFont);

        styleBtn(btnLoadIssued, btnFont, thinBorder);
        styleBtn(btnShowIssued, btnFont, thinBorder);
        styleBtn(btnShowPaid, btnFont, thinBorder);

        btnLoadIssued.addActionListener(this);
        btnShowIssued.addActionListener(this);
        btnShowPaid.addActionListener(this);

        // Layout (like sketch)
        int btnW = 220;
        int btnH = 55;
        int btnX = (1080 - btnW) / 2;

        int numX = btnX - 70;
        int numW = 40;
        int numH = btnH;

        int startY = 210;
        int gapY = 95;

        // Row 1
        n1.setBounds(numX, startY + 0 * gapY, numW, numH);
        btnLoadIssued.setBounds(btnX, startY + 0 * gapY, btnW, btnH);

        // Row 2
        n2.setBounds(numX, startY + 1 * gapY, numW, numH);
        btnShowIssued.setBounds(btnX, startY + 1 * gapY, btnW, btnH);

        // Row 3
        n3.setBounds(numX, startY + 2 * gapY, numW, numH);
        btnShowPaid.setBounds(btnX, startY + 2 * gapY, btnW, btnH);

        add(n1); add(btnLoadIssued);
        add(n2); add(btnShowIssued);
        add(n3); add(btnShowPaid);

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

        if (src == btnLoadIssued) {
            System.out.println("Load Issued Bills clicked");
        } else if (src == btnShowIssued) {
            System.out.println("Show Issued Bills clicked");
        } else if (src == btnShowPaid) {
            System.out.println("Show Paid Bills clicked");
        }
    }
}
