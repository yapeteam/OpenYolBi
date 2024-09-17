package cn.yapeteam.yolbi.event.impl.network;

import cn.yapeteam.yolbi.event.type.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Setter;
import net.minecraft.network.protocol.Packet;
@Setter
@AllArgsConstructor
public class EventPacketReceive extends CancellableEvent {
    private Packet packet;


    public Packet getPacket() {
        return  packet;
    }
}
