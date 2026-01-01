package Windows;

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

public class IndividualMenuFrame extends JFrame implements ActionListener {

    private final JButton btnLogout = new JButton("log out");
    private final JButton btn1 = new JButton("View accounts");
    private final JButton btn2 = new JButton("Create new bank account");
    private final JButton btn3 = new JButton("Transactions");
    private final JButton btn4 = new JButton("Standing orders");

    private final JLabel title = new JLabel("Individual Menu");

    private final JLabel n1 = new JLabel("1]");
    private final JLabel n2 = new JLabel("2]");
    private final JLabel n3 = new JLabel("3]");
    private final JLabel n4 = new JLabel("4]");

    public IndividualMenuFrame() {
        // Frame basics
        setTitle("Bank of TUC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setResizable(false);
        setLayout(null);

        // Optional icon (βάλε το αρχείο σου αν θες)
        // setIconImage(new ImageIcon("TUC.gif").getImage());

        // Borders like the sketch
        Border thinBorder = BorderFactory.createLineBorder(java.awt.Color.DARK_GRAY);

        // Title label (center top)
        title.setBounds(0, 70, 1080, 60);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.PLAIN, 36));
        add(title);

        // Logout button (top-left)
        btnLogout.setBounds(20, 20, 90, 35);
        btnLogout.setFocusable(false);
        btnLogout.setMargin(new Insets(2, 6, 2, 6));
        btnLogout.addActionListener(this);
        add(btnLogout);

        // Common button style
        Font btnFont = new Font("SansSerif", Font.PLAIN, 18);

        styleMenuButton(btn1, btnFont, thinBorder);
        styleMenuButton(btn2, btnFont, thinBorder);
        styleMenuButton(btn3, btnFont, thinBorder);
        styleMenuButton(btn4, btnFont, thinBorder);

        // Positions (approximately like the image)
        int btnX = 420, btnW = 330, btnH = 55;
        int startY = 180, gapY = 75;

        btn1.setBounds(btnX, startY + 0 * gapY, btnW, btnH);
        btn2.setBounds(btnX, startY + 1 * gapY, btnW, btnH);
        btn3.setBounds(btnX, startY + 2 * gapY, btnW, btnH);
        btn4.setBounds(btnX, startY + 3 * gapY, btnW, btnH);

        add(btn1);
        add(btn2);
        add(btn3);
        add(btn4);

        // Number labels (left of each button)
        Font nFont = new Font("SansSerif", Font.PLAIN, 18);
        styleNumberLabel(n1, nFont);
        styleNumberLabel(n2, nFont);
        styleNumberLabel(n3, nFont);
        styleNumberLabel(n4, nFont);

        int nX = btnX - 70, nW = 50, nH = btnH;
        n1.setBounds(nX, startY + 0 * gapY, nW, nH);
        n2.setBounds(nX, startY + 1 * gapY, nW, nH);
        n3.setBounds(nX, startY + 2 * gapY, nW, nH);
        n4.setBounds(nX, startY + 3 * gapY, nW, nH);

        add(n1);
        add(n2);
        add(n3);
        add(n4);

        setVisible(true);
    }

    private static void styleMenuButton(JButton b, Font f, Border border) {
        b.setFont(f);
        b.setFocusable(false);
        b.setBorder(border);
    }

    private static void styleNumberLabel(JLabel l, Font f) {
        l.setFont(f);
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        l.setVerticalAlignment(SwingConstants.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnLogout) {
            System.out.println("Logout clicked");
            // π.χ. dispose(); ή γύρνα στο login frame
        } else if (src == btn1) {
            System.out.println("View accounts");
        } else if (src == btn2) {
            System.out.println("Create new bank account");
        } else if (src == btn3) {
            System.out.println("Transactions");
        } else if (src == btn4) {
            System.out.println("Standing orders");
        }
    }
}
