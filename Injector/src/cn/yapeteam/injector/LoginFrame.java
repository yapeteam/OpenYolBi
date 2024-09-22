package cn.yapeteam.injector;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginFrame extends JFrame {
    private JPanel panel;
    private JButton loginButton;
    private JTextField UsernameField;
    private JPasswordField PasswordField;
    private JButton activateButton;

    public LoginFrame(LoginCallBack callBack) {
        super("LoginYourAccount");
        add(panel);
        float width = 500;
        float height = width * 0.618f;
        $$$setupUI$$$();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        int[] size = {(int) (width / 1920 * screenWidth), (int) (height / 1080 * screenHeight)};
        setSize(size[0], size[1]);
        setResizable(false);
        getRootPane().setDefaultButton(loginButton);
        setLocationRelativeTo(null);
        loginButton.addActionListener(e -> {
            loginButton.setEnabled(false);
            if (callBack.run(UsernameField.getText(), String.valueOf(PasswordField.getPassword()))) {
                loginButton.setEnabled(true);
                setVisible(false);
            } else {
                loginButton.setEnabled(true);
                JOptionPane.showMessageDialog(this, Main.msg);
            }
        });
        activateButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog("Username:");
            if (username == null) return;
            String cdk = JOptionPane.showInputDialog("CDK:");
            if (cdk == null) return;
            Main.active(username, cdk);
            JOptionPane.showMessageDialog(this, Main.msg);
        });
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                System.exit(0);
            }
        });
    }

    @Override
    public void setVisible(boolean b) {
        this.UsernameField.setText("");
        this.PasswordField.setText("");
        super.setVisible(b);
    }

    private void $$$setupUI$$$() {
        JButton jButton;
        JButton jButton2;
        JTextField jTextField;
        JPasswordField jPasswordField;
        JPanel jPanel;
        this.panel = jPanel = new JPanel();
        jPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel.add(jPanel2, new GridConstraints(1, 0, 1, 1, 0, 0, 3, 3, null, null, null));
        JLabel jLabel = new JLabel();
        jLabel.setAlignmentX(0.0f);
        jLabel.setHorizontalAlignment(4);
        jLabel.setHorizontalTextPosition(0);
        jLabel.setText("Password:");
        jPanel2.add(jLabel, new GridConstraints(0, 0, 1, 1, 0, 1, 0, 0, null, null, null));
        this.PasswordField = jPasswordField = new JPasswordField();
        jPanel2.add(jPasswordField, new GridConstraints(0, 1, 1, 1, 0, 1, 6, 0, null, new Dimension(300, -1), null));
        JPanel jPanel3 = new JPanel();
        jPanel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel.add(jPanel3, new GridConstraints(0, 0, 1, 1, 2, 0, 3, 3, null, null, null));
        JLabel jLabel2 = new JLabel();
        jLabel2.setAlignmentX(0.0f);
        jLabel2.setHorizontalAlignment(4);
        jLabel2.setHorizontalTextPosition(4);
        jLabel2.setText("Username:");
        jPanel3.add(jLabel2, new GridConstraints(0, 0, 1, 1, 0, 1, 0, 0, null, null, null));
        this.UsernameField = jTextField = new JTextField();
        jPanel3.add(jTextField, new GridConstraints(0, 1, 1, 1, 0, 1, 6, 0, null, new Dimension(300, -1), null));
        JPanel jPanel4 = new JPanel();
        jPanel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel.add(jPanel4, new GridConstraints(2, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        this.loginButton = jButton2 = new JButton();
        jButton2.setHorizontalTextPosition(0);
        jButton2.setText("Login/Register");
        jPanel4.add(jButton2, new GridConstraints(0, 1, 1, 1, 8, 0, 3, 0, null, null, null));
        this.activateButton = jButton = new JButton();
        jButton.setText("Activate");
        jPanel4.add(jButton, new GridConstraints(0, 0, 1, 1, 4, 0, 3, 0, null, null, null));
    }

    public JComponent $$$getRootComponent$$$() {
        return this.panel;
    }
}
