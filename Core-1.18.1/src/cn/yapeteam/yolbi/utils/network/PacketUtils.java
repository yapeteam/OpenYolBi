package cn.yapeteam.yolbi.utils.network;

import cn.yapeteam.yolbi.utils.IMinecraft;
import net.minecraft.network.protocol.Packet;

import java.util.ArrayList;

public class PacketUtils implements IMinecraft {
    public static ArrayList<Packet> skip_list = new ArrayList<>();

    public static void skip(Packet packet) {
        if (!skip_list.contains(packet)) {
            skip_list.add(packet);
        }
    }

    public static void remove(Packet packet) {
        skip_list.remove(packet);
    }

    public static boolean shouldSkip(Packet packet) {
        return skip_list.contains(packet);
    }

    public static void sendPacketNoEvent(Packet packet) {
        skip(packet);
        mc.getConnection().send(packet);
    }
}
