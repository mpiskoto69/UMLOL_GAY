package Windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class BusinessMenuShowPaidBillsFrame extends JFrame implements ActionListener {

    private final JButton btnLogout = new JButton("log out");

    private final JLabel title = new JLabel("Business Menu 1.3");
    private final JLabel subtitle = new JLabel("Show paid Bills");

    // Headers
    private final JLabel hCustomer = new JLabel("Customer's account");
    private final JLabel hExecDate = new JLabel("execution date");
    private final JLabel hAmount = new JLabel("amount €");
    private final JLabel hRf = new JLabel("RF code");

    public BusinessMenuShowPaidBillsFrame() {
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

        // Column positions (approx like sketch)
        int headerY = 210;

        int col1X = 120; // Customer's account
        int col2X = 410; // execution date
        int col3X = 640; // amount
        int col4X = 820; // RF code

        Font headerFont = new Font("SansSerif", Font.PLAIN, 14);

        hCustomer.setFont(headerFont);
        hExecDate.setFont(headerFont);
        hAmount.setFont(headerFont);
        hRf.setFont(headerFont);

        hCustomer.setBounds(col1X, headerY, 220, 25);
        hExecDate.setBounds(col2X, headerY, 200, 25);
        hAmount.setBounds(col3X, headerY, 120, 25);
        hRf.setBounds(col4X, headerY, 120, 25);

        add(hCustomer);
        add(hExecDate);
        add(hAmount);
        add(hRf);

        // Dots κάτω για "continuation"
        int dotsY = 290;
        addDots(col1X + 80, dotsY);
        addDots(col2X + 70, dotsY);
        addDots(col3X + 30, dotsY);
        addDots(col4X + 25, dotsY);

        setVisible(true);
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

        JLabel d3 = new JLabel(":");
        d3.setFont(new Font("SansSerif", Font.PLAIN, 22));
        d3.setForeground(Color.DARK_GRAY);
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
