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

public class IndividualMenu_2_CreateAccountFrame extends JFrame implements ActionListener {

    private final JButton btnLogout = new JButton("log out");

    private final JLabel title = new JLabel("Individual Menu 2");
    private final JLabel subtitle = new JLabel("Create new account");
    private final JLabel info = new JLabel("Choose what type you want your back account to be:");

    private final JButton btnSavings = new JButton("savings account");
    private final JButton btnCurrent = new JButton("current account");

    public IndividualMenu_2_CreateAccountFrame() {
        // Frame
        setTitle("Bank of TUC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setResizable(false);
        setLayout(null);

        // Optional icon
        // setIconImage(new ImageIcon("TUC.gif").getImage());

        // Logout button (top-left)
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

        // Info line
        info.setBounds(0, 185, 1080, 30);
        info.setHorizontalAlignment(SwingConstants.CENTER);
        info.setFont(new Font("SansSerif", Font.PLAIN, 16));
        add(info);

        // Buttons style
        Border thinBorder = BorderFactory.createLineBorder(Color.DARK_GRAY);
        Font btnFont = new Font("SansSerif", Font.PLAIN, 16);

        styleChoiceButton(btnSavings, btnFont, thinBorder);
        styleChoiceButton(btnCurrent, btnFont, thinBorder);

        // Buttons positions (like the sketch)
        btnSavings.setBounds(260, 290, 180, 55);
        btnCurrent.setBounds(620, 290, 180, 55);

        add(btnSavings);
        add(btnCurrent);

        setVisible(true);
    }

    private static void styleChoiceButton(JButton b, Font f, Border border) {
        b.setFont(f);
        b.setBorder(border);
        b.setFocusable(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnLogout) {
            System.out.println("Logout clicked");
            // dispose(); ή επιστροφή στο προηγούμενο frame
            return;
        }

        if (src == btnSavings) {
            System.out.println("Savings account selected");
            // εδώ μπορείς να ανοίξεις επόμενο frame ή να καλέσεις service
        }

        if (src == btnCurrent) {
            System.out.println("Current account selected");
            // εδώ μπορείς να ανοίξεις επόμενο frame ή να καλέσεις service
        }
    }
}
