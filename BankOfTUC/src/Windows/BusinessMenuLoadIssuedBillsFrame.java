package test101;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class BusinessMenuLoadIssuedBillsFrame extends JFrame implements ActionListener {

    private final JButton btnLogout = new JButton("log out");

    private final JLabel title = new JLabel("Business Menu 1.1");
    private final JLabel subtitle = new JLabel("Load Issued Bills");

    private final JLabel left1 = new JLabel("Txt.msg_1]");
    private final JLabel left2 = new JLabel("Txt.msg_2]");
    private final JLabel left3 = new JLabel("Txt.msg_3]");

    private final JLabel msg1 = new JLabel("\"Load Bills was successfull\"");
    private final JLabel msg2 = new JLabel("\"Load Bills failed\"");
    private final JLabel msg3 = new JLabel("\"There were no Bills to Load\"");

    public BusinessMenuLoadIssuedBillsFrame() {
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

        // Message rows (approx like sketch)
        Font font = new Font("SansSerif", Font.PLAIN, 16);

        int leftX = 340;
        int msgX = 520;
        int y0 = 250;
        int gapY = 60;

        styleMsgLabel(left1, font);
        styleMsgLabel(left2, font);
        styleMsgLabel(left3, font);

        styleMsgLabel(msg1, font);
        styleMsgLabel(msg2, font);
        styleMsgLabel(msg3, font);

        left1.setBounds(leftX, y0 + 0 * gapY, 140, 25);
        msg1.setBounds(msgX,  y0 + 0 * gapY, 450, 25);

        left2.setBounds(leftX, y0 + 1 * gapY, 140, 25);
        msg2.setBounds(msgX,  y0 + 1 * gapY, 450, 25);

        left3.setBounds(leftX, y0 + 2 * gapY, 140, 25);
        msg3.setBounds(msgX,  y0 + 2 * gapY, 450, 25);

        add(left1); add(msg1);
        add(left2); add(msg2);
        add(left3); add(msg3);

        setVisible(true);
    }

    private static void styleMsgLabel(JLabel l, Font f) {
        l.setFont(f);
        l.setHorizontalAlignment(SwingConstants.LEFT);
        l.setVerticalAlignment(SwingConstants.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogout) {
            System.out.println("Logout clicked");
            // dispose(); ή επιστροφή στο προηγούμενο frame
        }
    }

    // Αν θες να ενεργοποιείς 1 μήνυμα ανάλογα με αποτέλεσμα:
    public void showOnlyMessage(int which) {
        msg1.setVisible(which == 1);
        msg2.setVisible(which == 2);
        msg3.setVisible(which == 3);
        repaint();
    }
}
