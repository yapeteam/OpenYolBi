package cn.yapeteam.yolbi.event.impl.network;

import cn.yapeteam.yolbi.event.type.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Setter;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;

@Setter
@AllArgsConstructor
public class EventPacketSend extends CancellableEvent {

    private Packet packet;


    public Packet getPacket() {
        return  packet;
    }
}
