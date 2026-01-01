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

public class IndividualMenu_4_1_CreateStandingOrderFrame extends JFrame implements ActionListener {

    private final JButton btnLogout = new JButton("log out");

    private final JLabel title = new JLabel("Individual Menu 4.1");
    private final JLabel subtitle = new JLabel("Create Standing Order");

    private final JLabel lblAmount = new JLabel("enter transfer amount");
    private final JTextField txtAmount = new JTextField();

    private final JLabel lblOtherAccount = new JLabel("enter other bank account");
    private final JTextField txtOtherAccount = new JTextField();

    private final JLabel lblYourAccount = new JLabel("enter your bank account");
    private final JTextField txtYourAccount = new JTextField();

    private final JLabel lblPaymentDate = new JLabel("enter payment date");
    private final JTextField txtPaymentDate = new JTextField();

    public IndividualMenu_4_1_CreateStandingOrderFrame() {
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

        // Center form layout (4 fields stacked)
        int fieldW = 200;
        int fieldH = 38;
        int fieldX = (1080 - fieldW) / 2;

        int lblW = 260;
        int lblH = 22;
        int lblX = (1080 - lblW) / 2;

        int startLabelY = 200;
        int gapBlock = 78; // distance between label+field blocks

        Font labelFont = new Font("SansSerif", Font.PLAIN, 14);
        Border thinBorder = BorderFactory.createLineBorder(Color.DARK_GRAY);

        // 1) Amount
        placeLabeledField(lblAmount, txtAmount, lblX, fieldX, startLabelY + 0 * gapBlock, labelFont, thinBorder, lblW, lblH, fieldW, fieldH);

        // 2) Other account
        placeLabeledField(lblOtherAccount, txtOtherAccount, lblX, fieldX, startLabelY + 1 * gapBlock, labelFont, thinBorder, lblW, lblH, fieldW, fieldH);

        // 3) Your account
        placeLabeledField(lblYourAccount, txtYourAccount, lblX, fieldX, startLabelY + 2 * gapBlock, labelFont, thinBorder, lblW, lblH, fieldW, fieldH);

        // 4) Payment date
        placeLabeledField(lblPaymentDate, txtPaymentDate, lblX, fieldX, startLabelY + 3 * gapBlock, labelFont, thinBorder, lblW, lblH, fieldW, fieldH);

        setVisible(true);
    }

    private void placeLabeledField(
            JLabel label, JTextField field,
            int lblX, int fieldX, int baseY,
            Font labelFont, Border border,
            int lblW, int lblH, int fieldW, int fieldH
    ) {
        // label
        label.setBounds(lblX, baseY, lblW, lblH);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(labelFont);
        add(label);

        // field
        field.setBounds(fieldX, baseY + 24, fieldW, fieldH);
        field.setBorder(border);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(field);
    }

    public String getAmountText() {
        return txtAmount.getText().trim();
    }

    public String getOtherAccountText() {
        return txtOtherAccount.getText().trim();
    }

    public String getYourAccountText() {
        return txtYourAccount.getText().trim();
    }

    public String getPaymentDateText() {
        return txtPaymentDate.getText().trim();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogout) {
            System.out.println("Logout clicked");
            // π.χ. dispose(); ή επιστροφή στο προηγούμενο frame
        }
    }
}
