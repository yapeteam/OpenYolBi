package cn.yapeteam.injector;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
}
