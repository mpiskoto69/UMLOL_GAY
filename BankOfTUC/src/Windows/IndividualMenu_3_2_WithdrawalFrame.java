package Windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.Border;

public class IndividualMenu_3_2_WithdrawalFrame extends JFrame implements ActionListener {

    private final JButton btnLogout = new JButton("log out");

    private final JLabel title = new JLabel("Individual Menu 3.2");
    private final JLabel subtitle = new JLabel("Withdrawal");

    private final JLabel lblAmount = new JLabel("enter withdrawal amount");
    private final JTextField txtAmount = new JTextField();

    private final JLabel lblAccount = new JLabel("enter your bank account");
    private final JTextField txtAccount = new JTextField();

    public IndividualMenu_3_2_WithdrawalFrame() {
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

        // Center form positions
        int fieldW = 220;
        int fieldH = 40;
        int fieldX = (1080 - fieldW) / 2;

        int lblW = 260;
        int lblH = 25;
        int lblX = (1080 - lblW) / 2;

        int y1Label = 250;
        int y1Field = 280;

        int y2Label = 350;
        int y2Field = 380;

        Font labelFont = new Font("SansSerif", Font.PLAIN, 14);
        Border thinBorder = BorderFactory.createLineBorder(Color.DARK_GRAY);

        // Amount label + field
        lblAmount.setBounds(lblX, y1Label, lblW, lblH);
        lblAmount.setHorizontalAlignment(SwingConstants.CENTER);
        lblAmount.setFont(labelFont);
        add(lblAmount);

        txtAmount.setBounds(fieldX, y1Field, fieldW, fieldH);
        txtAmount.setBorder(thinBorder);
        txtAmount.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(txtAmount);

        // Account label + field
        lblAccount.setBounds(lblX, y2Label, lblW, lblH);
        lblAccount.setHorizontalAlignment(SwingConstants.CENTER);
        lblAccount.setFont(labelFont);
        add(lblAccount);

        txtAccount.setBounds(fieldX, y2Field, fieldW, fieldH);
        txtAccount.setBorder(thinBorder);
        txtAccount.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(txtAccount);

        setVisible(true);
    }

    public String getAmountText() {
        return txtAmount.getText().trim();
    }

    public String getAccountText() {
        return txtAccount.getText().trim();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogout) {
            System.out.println("Logout clicked");
            // π.χ. dispose(); ή επιστροφή στο προηγούμενο frame
        }
    }
}
