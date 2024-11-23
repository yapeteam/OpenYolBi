package cn.yapeteam.yolbi.utils.network;

import cn.yapeteam.ymixin.utils.Mapper;
import cn.yapeteam.yolbi.utils.IMinecraft;
import net.minecraft.network.protocol.Packet;

import java.util.Objects;

public class PacketUtils implements IMinecraft {
    public static boolean skip = false;

    public static void sendPacketNoEvent(Packet packet) {
        skip = true;
        if (isCPacket(packet)) {
            mc.getConnection().send(packet);
        }
        if (isSPacket(packet)) {
            packet.handle(Objects.requireNonNull(mc.getConnection().getConnection().getPacketListener()));
        }
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
