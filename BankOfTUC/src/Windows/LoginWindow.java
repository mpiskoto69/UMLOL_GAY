package Windows;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class LoginWindow extends JFrame implements ActionListener{
    
    JButton submitBtn;

    JLabel title = new JLabel();
    JLabel usernameLbl = new JLabel();
    JLabel passwordLbl = new JLabel();

    JTextField usernameTf;
    JTextField passwordTf;
    

    public LoginWindow() {

        //Things for layout
        this.setTitle("Bank of TUC");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(1080,720);
        this.setResizable(false);

        //Things for title
        title.setText("Log in");
        title.setBounds(460, 50, 160, 50);
        title.setFont(new Font("Comic Sans",Font.BOLD,40));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setVerticalAlignment(JLabel.CENTER);
        title.setVisible(true);
        this.add(title);

        //things for submitBtn 
        submitBtn = new JButton();
        submitBtn.setBounds(465,475 , 150, 50);
        submitBtn.setFont(new Font("Comic Sans",Font.BOLD,25));
        submitBtn.addActionListener(this);
        submitBtn.setText("submit");
        submitBtn.setFocusable(false);
        this.add(submitBtn);

        //Things for usernameLbl
        usernameLbl.setText("username");
        usernameLbl.setBounds(465, 270, 150, 25);
        usernameLbl.setFont(new Font("Comic Sans",Font.BOLD,25));
        usernameLbl.setHorizontalAlignment(JLabel.CENTER);
        usernameLbl.setVerticalAlignment(JLabel.CENTER);
        usernameLbl.setVisible(true);
        this.add(usernameLbl);

        //Things for passwordLbl
        passwordLbl.setText("password");
        passwordLbl.setBounds(465, 370, 150, 25);
        passwordLbl.setFont(new Font("Comic Sans",Font.BOLD,25));
        passwordLbl.setHorizontalAlignment(JLabel.CENTER);
        passwordLbl.setVerticalAlignment(JLabel.CENTER);
        passwordLbl.setVisible(true);
        this.add(passwordLbl);
        
        //Things for usernameTf
        usernameTf = new JTextField();
        usernameTf.setBounds(390, 300, 300, 50);
        usernameTf.setFont(new Font("Comic Sans",Font.BOLD,25));
        //usernameTf.setText("text");
        this.add(usernameTf);

        //Things for passwordTf
        passwordTf = new JTextField();
        passwordTf.setBounds(390, 400, 300, 50);
        passwordTf.setFont(new Font("Comic Sans",Font.BOLD,25));
        //passwordTf.setText("");
        this.add(passwordTf);

        // Image
        ImageIcon image = new ImageIcon("TUC.gif");
        this.setIconImage(image.getImage());

        // Make the window visible
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == submitBtn){
            System.out.println("poo");
        }

    }


}
