package cn.yapeteam.yolbi.mixin.transformer;

import cn.yapeteam.ymixin.ASMTransformer;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.network.EventPacketSend;
import cn.yapeteam.yolbi.event.type.CancellableEvent;
import cn.yapeteam.yolbi.utils.network.PacketUtils;
import lombok.val;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import org.objectweb.asm_9_2.Type;
import org.objectweb.asm_9_2.tree.*;

import static org.objectweb.asm_9_2.Opcodes.*;


public class NetworkHandlerTransformer extends ASMTransformer {
    public NetworkHandlerTransformer() {
        super(ClientPacketListener.class);
    }


    @Inject(method = "send", desc = "(Lnet/minecraft/network/protocol/Packet;)V")
    public void sendPacket(MethodNode mn) {
        val list = new InsnList();
        LabelNode label = new LabelNode();

        list.add(new VarInsnNode(ALOAD, 1));
        //list.add(new InsnNode(ACONST));
        list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(NetworkHandlerTransformer.class), "onPacket", "(Ljava/lang/Object;)Z"));
        list.add(new JumpInsnNode(IFEQ, label));
        list.add(new InsnNode(RETURN));
        list.add(label);
        mn.instructions.insert(list);
    }

    public static boolean onPacket(Object packet) {
        if (PacketUtils.shouldSkip((Packet) packet)) {
            PacketUtils.remove((Packet) packet);
            return false;
        }
        //Agent.logger.info(Mappings.getUnobfClass(packet.getClass().getName()));
        return ((CancellableEvent) YolBi.instance.getEventManager().post(new EventPacketSend((Packet) packet))).isCancelled();
    }
}
