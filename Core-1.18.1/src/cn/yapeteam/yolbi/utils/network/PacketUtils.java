package cn.yapeteam.yolbi.utils.network;

import cn.yapeteam.ymixin.utils.Mapper;
import cn.yapeteam.yolbi.utils.IMinecraft;
import net.minecraft.network.protocol.Packet;

public class PacketUtils implements IMinecraft {
    public static boolean skip = false;

    public static void sendPacketNoEvent(Packet packet) {
        skip = true;
        mc.getConnection().send(packet);
        skip = false;
    }

    public static boolean isCPacket(Packet packet) {
        return Mapper.getFriendlyClass(packet.getClass().getName()).startsWith("Clientbound");
    }

    public static boolean isSPacket(Packet packet) {
        return Mapper.getFriendlyClass(packet.getClass().getName()).startsWith("Serverbound");
    }

    public static boolean isUseFulPacket(Packet packet) {
        return isCPacket(packet) || isSPacket(packet);
    }
}
