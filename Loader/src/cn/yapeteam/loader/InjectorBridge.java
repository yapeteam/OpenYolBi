package cn.yapeteam.loader;

import cn.yapeteam.loader.logger.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class InjectorBridge {
    private static Socket socket = null;
    private static PrintWriter writer = null;

    public static void init() {
        try {
            socket = new Socket("127.0.0.1", Loader.port);
            writer = new PrintWriter(socket.getOutputStream());
        } catch (Throwable e) {
            Logger.warn("Failed to connect with injector");
        }
    }

    public static void send(String message) {
        if (writer == null) return;
        try {
            writer.println(message);
            writer.flush();
        } catch (Throwable e) {
            Logger.exception(e);
        }
    }

    public static void close() {
        if (socket == null) return;
        try {
            socket.close();
            socket = null;
            writer = null;
        } catch (IOException e) {
            Logger.exception(e);
        }
    }
}
