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

public class IndividualMenu_4_StandingOrdersFrame extends JFrame implements ActionListener {

    private final JButton btnLogout = new JButton("log out");

    private final JLabel title = new JLabel("Individual Menu 4");
    private final JLabel subtitle = new JLabel("Standing order");

    private final JLabel n1 = new JLabel("1]");
    private final JLabel n2 = new JLabel("2]");

    private final JButton btnCreate = new JButton("Create Standing Order");
    private final JButton btnManage = new JButton("Manage Standing Orders");

    public IndividualMenu_4_StandingOrdersFrame() {
        // Frame
        setTitle("Bank of TUC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setResizable(false);
        setLayout(null);

        // Optional icon
        // setIconImage(new ImageIcon("TUC.gif").getImage());

        // Logout (top-left)
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

        styleBtn(btnCreate, btnFont, thinBorder);
        styleBtn(btnManage, btnFont, thinBorder);

        // Layout (approx like sketch)
        int btnW = 240;
        int btnH = 55;
        int btnX = (1080 - btnW) / 2;

        int numX = btnX - 70;
        int numW = 40;
        int numH = btnH;

        int startY = 250;
        int gapY = 90;

        // Row 1
        n1.setBounds(numX, startY, numW, numH);
        btnCreate.setBounds(btnX, startY, btnW, btnH);

        // Row 2
        n2.setBounds(numX, startY + gapY, numW, numH);
        btnManage.setBounds(btnX, startY + gapY, btnW, btnH);

        add(n1);
        add(btnCreate);
        add(n2);
        add(btnManage);

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

        if (src == btnCreate) {
            System.out.println("Create Standing Order clicked");
        } else if (src == btnManage) {
            System.out.println("Manage Standing Orders clicked");
        }
    }
}
