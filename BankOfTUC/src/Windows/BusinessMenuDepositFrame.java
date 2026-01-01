package test101;

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

public class BusinessMenuDepositFrame extends JFrame implements ActionListener {

    private final JButton btnLogout = new JButton("log out");

    private final JLabel title = new JLabel("Business Menu 2.1");
    private final JLabel subtitle = new JLabel("Deposite"); // όπως στο sketch

    private final JLabel lblAmount = new JLabel("enter deposite amount");
    private final JTextField txtAmount = new JTextField();

    public BusinessMenuDepositFrame() {
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

        // Center form positions
        int fieldW = 220;
        int fieldH = 40;
        int fieldX = (1080 - fieldW) / 2;

        int lblW = 260;
        int lblH = 25;
        int lblX = (1080 - lblW) / 2;

        int yLabel = 260;
        int yField = 290;

        Font labelFont = new Font("SansSerif", Font.PLAIN, 14);
        Border thinBorder = BorderFactory.createLineBorder(Color.DARK_GRAY);

        // Label
        lblAmount.setBounds(lblX, yLabel, lblW, lblH);
        lblAmount.setHorizontalAlignment(SwingConstants.CENTER);
        lblAmount.setFont(labelFont);
        add(lblAmount);

        // Text field
        txtAmount.setBounds(fieldX, yField, fieldW, fieldH);
        txtAmount.setBorder(thinBorder);
        txtAmount.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(txtAmount);

        setVisible(true);
    }

    public String getAmountText() {
        return txtAmount.getText().trim();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogout) {
            System.out.println("Logout clicked");
            // dispose(); ή επιστροφή στο προηγούμενο frame
        }
    }
}
