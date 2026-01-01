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

public class IndividualMenu_4_2_ManageStandingOrdersFrame extends JFrame implements ActionListener {

    private final JButton btnLogout = new JButton("log out");

    private final JLabel title = new JLabel("Individual Menu 4.2");
    private final JLabel subtitle = new JLabel("Manage Standing order");

    // Headers
    private final JLabel hFromTo = new JLabel("From (your b.a.) to (other b.a.)");
    private final JLabel hAmount = new JLabel("amount");
    private final JLabel hDate   = new JLabel("date");

    // Example rows (3)
    private final JLabel r1FromTo = new JLabel("From (your b.a.) to (other b.a.)");
    private final JLabel r1Amount = new JLabel("amount");
    private final JLabel r1Date   = new JLabel("date");
    private final JButton r1Delete = new JButton("delete");

    private final JLabel r2FromTo = new JLabel("From (your b.a.) to (other b.a.)");
    private final JLabel r2Amount = new JLabel("amount");
    private final JLabel r2Date   = new JLabel("date");
    private final JButton r2Delete = new JButton("delete");

    private final JLabel r3FromTo = new JLabel("From (your b.a.) to (other b.a.)");
    private final JLabel r3Amount = new JLabel("amount");
    private final JLabel r3Date   = new JLabel("date");
    private final JButton r3Delete = new JButton("delete");

    public IndividualMenu_4_2_ManageStandingOrdersFrame() {
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

        // Columns positions (approx like sketch)
        int tableY = 215;
        int rowH = 45;

        int colFromX = 120;
        int colAmountX = 540;
        int colDateX = 660;
        int colDelX = 820;

        Font headerFont = new Font("SansSerif", Font.PLAIN, 14);
        Font rowFont = new Font("SansSerif", Font.PLAIN, 14);

        Border thinBorder = BorderFactory.createLineBorder(Color.DARK_GRAY);

        // Headers
        hFromTo.setFont(headerFont);
        hAmount.setFont(headerFont);
        hDate.setFont(headerFont);

        hFromTo.setBounds(colFromX, tableY, 380, 25);
        hAmount.setBounds(colAmountX, tableY, 100, 25);
        hDate.setBounds(colDateX, tableY, 100, 25);

        add(hFromTo);
        add(hAmount);
        add(hDate);

        // Rows (3)
        placeRow(r1FromTo, r1Amount, r1Date, r1Delete, 1, tableY, rowH, colFromX, colAmountX, colDateX, colDelX, rowFont, thinBorder);
        placeRow(r2FromTo, r2Amount, r2Date, r2Delete, 2, tableY, rowH, colFromX, colAmountX, colDateX, colDelX, rowFont, thinBorder);
        placeRow(r3FromTo, r3Amount, r3Date, r3Delete, 3, tableY, rowH, colFromX, colAmountX, colDateX, colDelX, rowFont, thinBorder);

        // Dots for continuation (όπως στο sketch)
        int dotsY = tableY + 5 * rowH + 20;
        addDots(colFromX + 200, dotsY);
        addDots(colAmountX + 30, dotsY);
        addDots(colDateX + 20, dotsY);
        addDots(colDelX + 35, dotsY);

        setVisible(true);
    }

    private void placeRow(
            JLabel fromTo, JLabel amount, JLabel date, JButton deleteBtn,
            int rowIndex, int tableY, int rowH,
            int colFromX, int colAmountX, int colDateX, int colDelX,
            Font rowFont, Border border
    ) {
        int y = tableY + rowIndex * rowH;

        fromTo.setFont(rowFont);
        amount.setFont(rowFont);
        date.setFont(rowFont);

        fromTo.setBounds(colFromX, y, 380, 25);
        amount.setBounds(colAmountX, y, 80, 25);
        date.setBounds(colDateX, y, 80, 25);

        deleteBtn.setBounds(colDelX, y - 5, 90, 32);
        deleteBtn.setFocusable(false);
        deleteBtn.setBorder(border);
        deleteBtn.addActionListener(this);

        add(fromTo);
        add(amount);
        add(date);
        add(deleteBtn);
    }

    private void addDots(int x, int y) {
        JLabel d1 = new JLabel(":");
        d1.setFont(new Font("SansSerif", Font.PLAIN, 22));
        d1.setForeground(Color.DARK_GRAY);
        d1.setBounds(x, y, 20, 25);
        add(d1);

        JLabel d2 = new JLabel(":");
        d2.setFont(new Font("SansSerif", Font.PLAIN, 22));
        d2.setForeground(Color.DARK_GRAY);
        d2.setBounds(x, y + 20, 20, 25);
        add(d2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnLogout) {
            System.out.println("Logout clicked");
            return;
        }

        if (src == r1Delete) System.out.println("Delete standing order row 1");
        if (src == r2Delete) System.out.println("Delete standing order row 2");
        if (src == r3Delete) System.out.println("Delete standing order row 3");
    }
}
