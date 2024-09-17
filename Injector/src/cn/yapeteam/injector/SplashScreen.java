package cn.yapeteam.injector;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class SplashScreen extends JFrame {
    public SplashScreen() {
        super("YolBi Shield");
        float width = 300;
        float height = width * 0.618f;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        int[] size = {(int) (width / 1920 * screenWidth), (int) (height / 1080 * screenHeight)};
        setSize(size[0], size[1]);
        setResizable(false);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        try {
            ImageIcon shieldIcon = new ImageIcon(ImageIO.read(Objects.requireNonNull(SplashScreen.class.getResourceAsStream("/deobf.png"))));
            JLabel shieldLabel = new JLabel(shieldIcon);
            panel.add(shieldLabel, BorderLayout.CENTER);
            add(panel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void display() {
        new Thread(() -> setVisible(true)).start();
    }

    public void close() {
        new Thread(() -> setVisible(false)).start();
    }
}
