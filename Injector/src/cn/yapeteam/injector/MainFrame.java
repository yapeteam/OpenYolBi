package cn.yapeteam.injector;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

public class MainFrame extends JFrame {
    private JPanel panel;
    private JProgressBar progressBar1;
    private JProgressBar progressBar2;
    private JPanel buttons;

    private volatile float value1 = 0, value2 = 0;
    private final Thread progressThread1;
    private final Thread progressThread2;
    private Thread serverThread;
    private final Thread updateThread;

    private ArrayList<Pair<String, Integer>> lastList = null;

    public MainFrame() {
        super("YolBi Lite v" + Main.version + " - Development Build");
        float width = 500, height = width * 0.618f;
        $$$setupUI$$$();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        int[] size = {(int) (width / 1920 * screenWidth + 225), (int) (height / 1080 * screenHeight + 100)};
        setSize(size[0], size[1]);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Panel p = new Panel();
        // p.add(new JButton());
        // p.add(new JButton());
        // p.add(new JButton());
        // p.add(new JButton());

        progressBar1.setVisible(false);
        progressBar2.setVisible(false);

        //getRootPane().setDefaultButton(inject);
        //inject.addActionListener(e -> {
        //    if (!targets.isEmpty() && process.getSelectedIndex() != -1)
        //        inject(targets.get(process.getSelectedIndex()).b);
        //});
        float speed = 0.1f;
        int fps = 60;
        progressThread1 = new Thread(() -> {
            float cache = 0;
            while (true) {
                cache += (value1 - cache) * speed;
                progressBar1.setValue((int) cache);
                try {
                    Thread.sleep(1000 / fps);
                } catch (InterruptedException ignored) {
                    break;
                }
            }
        });
        progressThread2 = new Thread(() -> {
            float cache = 0;
            while (true) {
                cache += (value2 - cache) * speed;
                progressBar2.setValue((int) cache);
                try {
                    Thread.sleep(1000 / fps);
                } catch (InterruptedException ignored) {
                    break;
                }
            }
        });
        serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(Main.port)) {
                // while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> {
                    try {
                        InputStream stream = socket.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                        while (true) {
                            String message = reader.readLine();
                            String[] values = message.split(" ");
                            if (values.length == 2) {
                                float value = Float.parseFloat(values[1]);
                                switch (values[0]) {
                                    case "P1":
                                        value1 = value;
                                        break;
                                    case "P2":
                                        value2 = value;
                                        break;
                                }
                            } else switch (message) {
                                case "S1":
                                    progressThread1.start();
                                    progressBar1.setVisible(true);
                                    break;
                                case "S2":
                                    progressThread2.start();
                                    progressBar2.setVisible(true);
                                    break;
                                case "E1":
                                    progressThread1.interrupt();
                                    progressBar1.setVisible(false);
                                    break;
                                case "E2":
                                    progressThread2.interrupt();
                                    progressBar2.setVisible(false);
                                    break;
                                case "CLOSE":
                                    progressThread1.interrupt();
                                    progressThread2.interrupt();
                                    serverThread.interrupt();
                                    System.exit(0);
                            }
                        }
                    } catch (IOException ignored) {
                    }
                }).start();
                //break;
                //}
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        updateThread = new Thread(() -> {
            if (OS.isFamilyWindows())
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                    ArrayList<Pair<String, Integer>> minecraftProcesses = Utils.getMinecraftProcesses();
                    if (minecraftProcesses.equals(lastList)) continue;
                    lastList = minecraftProcesses;
                    // int selected = process.getSelectedIndex();
                    // process.removeAllItems();
                    // if (minecraftProcesses.isEmpty()) {
                    //     process.addItem("No Minecraft instance found");
                    //     inject.setEnabled(false);
                    //     continue;
                    // }
                    // inject.setEnabled(true);
                    buttons.removeAll();
                    for (Pair<String, Integer> minecraftProcess : minecraftProcesses) {
                        JButton button = new JButton();
                        button.setPreferredSize(new Dimension(200, 45));
                        button.setAction(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                inject(minecraftProcess.b);
                            }
                        });
                        button.setText(minecraftProcess.a + ": " + minecraftProcess.b);
                        buttons.add(button);
                    }
                    buttons.updateUI();
                    // process.addItem(minecraftProcess.a);
                    //if (selected != -1)
                    //    process.setSelectedIndex(selected);
                    //targets = minecraftProcesses;
                }
        });
        add(panel);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b && OS.isFamilyWindows()) updateThread.start();
    }

    public void inject(int pid) {
        inject_dll(pid);
        inject_ui();
    }

    public void inject_dll(int pid) {
        new Thread(() -> Utils.injectDLL(pid, new File(Main.YolBi_Dir, Main.dllName).getAbsolutePath())).start();
    }

    public void inject_agent(String pid) {
        new Thread(() -> {
            try {
                VirtualMachine virtualMachine = VirtualMachine.attach(pid);
                virtualMachine.loadAgent(new File(Main.YolBi_Dir, Main.agentName).getAbsolutePath());
            } catch (AttachNotSupportedException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Couldn't attach target VM", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException | AgentLoadException | AgentInitializationException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    public void inject_ui() {
        if (updateThread.isAlive())
            updateThread.interrupt();
        serverThread.start();
        buttons.removeAll();
    }

    private void $$$setupUI$$$() {
        JProgressBar jProgressBar;
        JProgressBar jProgressBar2;
        JPanel jPanel;
        JPanel jPanel2;
        this.panel = jPanel2 = new JPanel();
        jPanel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel2.setDoubleBuffered(true);
        ((Component) jPanel2).setEnabled(true);
        Font font = this.$$$getFont$$$(null, -1, -1, jPanel2.getFont());
        if (font != null) {
            jPanel2.setFont(font);
        }
        ((Component) jPanel2).setVisible(true);
        jPanel2.setBorder(BorderFactory.createTitledBorder(null, "", 0, 0, null, null));
        JPanel jPanel3 = new JPanel();
        jPanel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel2.add(jPanel3, new GridConstraints(0, 0, 1, 1, 2, 1, 3, 3, null, null, null));
        JLabel jLabel = new JLabel();
        Font font2 = this.$$$getFont$$$(null, -1, 36, jLabel.getFont());
        if (font2 != null) {
            jLabel.setFont(font2);
        }
        jLabel.setText("YolBi Lite");
        jPanel3.add(jLabel, new GridConstraints(0, 0, 1, 1, 0, 0, 0, 0, null, null, null));
        this.buttons = jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout(1, 5, 5));
        jPanel3.add(jPanel, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        JPanel jPanel4 = new JPanel();
        jPanel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel2.add(jPanel4, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        this.progressBar1 = jProgressBar2 = new JProgressBar();
        jPanel4.add(jProgressBar2, new GridConstraints(0, 0, 1, 1, 0, 0, 6, 0, new Dimension(200, -1), null, null));
        this.progressBar2 = jProgressBar = new JProgressBar();
        jPanel4.add(jProgressBar, new GridConstraints(1, 0, 1, 1, 1, 0, 6, 0, new Dimension(200, -1), null, null));
    }

    public JComponent $$$getRootComponent$$$() {
        return this.panel;
    }

    private Font $$$getFont$$$(String string, int n, int n2, Font font) {
        Font font2;
        if (font == null) {
            return null;
        }
        String string2 = string == null ? font.getName() : ((font2 = new Font(string, 0, 10)).canDisplay('a') && font2.canDisplay('1') ? string : font.getName());
        Font font3 = new Font(string2, n >= 0 ? n : font.getStyle(), n2 >= 0 ? n2 : font.getSize());
        boolean bl = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font font4 = bl ? new Font(font3.getFamily(), font3.getStyle(), font3.getSize()) : new StyleContext().getFont(font3.getFamily(), font3.getStyle(), font3.getSize());
        return font4 instanceof FontUIResource ? font4 : new FontUIResource(font4);
    }
}
