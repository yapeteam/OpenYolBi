package cn.yapeteam.yolbi.event.impl.network;

import cn.yapeteam.yolbi.event.type.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.Packet;

@Getter
@Setter
@AllArgsConstructor
public class EventPacketSend extends CancellableEvent {
    private Packet packet;
}
