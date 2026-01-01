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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class IndividualMenu_3_4_PaymentFrame extends JFrame implements ActionListener {

    private final JButton btnLogout = new JButton("log out");

    private final JLabel title = new JLabel("Individual Menu 3.4");
    private final JLabel subtitle = new JLabel("Payments");

    private final JLabel lblAmount = new JLabel("enter transfer amount");
    private final JTextField txtAmount = new JTextField();

    private final JLabel lblBusinessAccount = new JLabel("enter bussiness bank account");
    private final JTextField txtBusinessAccount = new JTextField();

    private final JLabel lblYourAccount = new JLabel("enter your bank account");
    private final JTextField txtYourAccount = new JTextField();

    public IndividualMenu_3_4_PaymentFrame() {
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

        // Center form layout
        int fieldW = 220;
        int fieldH = 40;
        int fieldX = (1080 - fieldW) / 2;

        int lblW = 300;
        int lblH = 25;
        int lblX = (1080 - lblW) / 2;

        int y1Label = 230;
        int y1Field = 260;

        int y2Label = 330;
        int y2Field = 360;

        int y3Label = 430;
        int y3Field = 460;

        Font labelFont = new Font("SansSerif", Font.PLAIN, 14);
        Border thinBorder = BorderFactory.createLineBorder(Color.DARK_GRAY);

        // Amount
        lblAmount.setBounds(lblX, y1Label, lblW, lblH);
        lblAmount.setHorizontalAlignment(SwingConstants.CENTER);
        lblAmount.setFont(labelFont);
        add(lblAmount);

        txtAmount.setBounds(fieldX, y1Field, fieldW, fieldH);
        txtAmount.setBorder(thinBorder);
        txtAmount.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(txtAmount);

        // Business account
        lblBusinessAccount.setBounds(lblX, y2Label, lblW, lblH);
        lblBusinessAccount.setHorizontalAlignment(SwingConstants.CENTER);
        lblBusinessAccount.setFont(labelFont);
        add(lblBusinessAccount);

        txtBusinessAccount.setBounds(fieldX, y2Field, fieldW, fieldH);
        txtBusinessAccount.setBorder(thinBorder);
        txtBusinessAccount.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(txtBusinessAccount);

        // Your account
        lblYourAccount.setBounds(lblX, y3Label, lblW, lblH);
        lblYourAccount.setHorizontalAlignment(SwingConstants.CENTER);
        lblYourAccount.setFont(labelFont);
        add(lblYourAccount);

        txtYourAccount.setBounds(fieldX, y3Field, fieldW, fieldH);
        txtYourAccount.setBorder(thinBorder);
        txtYourAccount.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(txtYourAccount);

        setVisible(true);
    }

    public String getAmountText() {
        return txtAmount.getText().trim();
    }

    public String getBusinessAccountText() {
        return txtBusinessAccount.getText().trim();
    }

    public String getYourAccountText() {
        return txtYourAccount.getText().trim();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogout) {
            System.out.println("Logout clicked");
            // π.χ. dispose(); ή επιστροφή στο προηγούμενο frame
        }
    }
}
