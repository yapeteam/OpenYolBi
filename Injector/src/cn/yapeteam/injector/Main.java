package cn.yapeteam.injector;

import cn.yapeteam.loader.VersionInfo;
import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;

import javax.swing.*;
import java.io.File;

public class Main {
    public static final String version = VersionInfo.version;
    public static final File YolBi_Dir = new File(System.getProperty("user.home"), ".yolbi");
    public static final String dllName = "libinjection.dll";
    public static final String agentName = "agent.jar";
    public static final int port = 20181;
    // public static boolean devSwitch = true;

    public static String msg;

    // public static native boolean login(String username, String password);

    public static native void active(String username, String cdk);

    public static void main(String[] args) throws Exception {
        //if (devSwitch) {
        //    Utils.unzip(Main.class.getResourceAsStream("/injection.zip"), YolBi_Dir);
        //    if (OS.isFamilyWindows())
        //        System.load(new File(Main.YolBi_Dir, "libapi.dll").getAbsolutePath());
        //    UIManager.setLookAndFeel(new FlatXcodeDarkIJTheme());
        //    MainFrame frame = new MainFrame();
        //    new Thread(() -> frame.setVisible(true)).start();
        //    if (args.length == 2) {
        //        switch (args[0]) {
        //            case "dll":
        //                frame.inject_dll(Integer.parseInt(args[1]));
        //            case "agent":
        //                frame.inject_agent(args[1]);
        //        }
        //        frame.inject_ui();
        //    }
        //} else {
        SplashScreen splashScreen = new SplashScreen();
        splashScreen.display();
        Utils.unzip(Main.class.getResourceAsStream("/injection.zip"), YolBi_Dir);
        if (OS.isFamilyWindows()) {
            System.load(new File(Main.YolBi_Dir, "libapi.dll").getAbsolutePath());
            //System.loadLibrary("libauth");
        }
        UIManager.setLookAndFeel(new FlatOneDarkIJTheme());
        //LoginFrame frame = new LoginFrame((username, password) -> {
        //    if (login(username, password)) {
        MainFrame mainFrame = new MainFrame();
        new Thread(() -> mainFrame.setVisible(true)).start();
        if (args.length == 2) {
            switch (args[0]) {
                case "dll":
                    mainFrame.inject_dll(Integer.parseInt(args[1]));
                    mainFrame.inject_ui();
                    break;
                case "agent":
                    mainFrame.inject_agent(args[1]);
                    mainFrame.inject_ui();
            }
        }
        //        return true;
        //    }
        //    return false;
        //});
        splashScreen.close();
        // frame.setVisible(true);
        //}
    }
}
