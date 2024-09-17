package cn.yapeteam.yolbi.mixin.transformer;


import cn.yapeteam.ymixin.ASMTransformer;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.network.EventPacketReceive;
import cn.yapeteam.yolbi.event.type.CancellableEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.objectweb.asm_9_2.Opcodes;
import org.objectweb.asm_9_2.Type;
import org.objectweb.asm_9_2.tree.*;

import static org.objectweb.asm_9_2.Opcodes.ALOAD;
import static org.objectweb.asm_9_2.Opcodes.INVOKESTATIC;


public class NetworkManagerTransFormer extends ASMTransformer {
    public NetworkManagerTransFormer() {
        super(Connection.class);
    }

    // MD: ek/a (Lio/netty/channel/ChannelHandlerContext;Lff;)V net/minecraft/network/NetworkManager/channelRead0 (Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V
    @Inject(method = "genericsFtw", desc = "(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;)V")
    public void channelRead0(MethodNode mn) {
        InsnList list = new InsnList();
        LabelNode label = new LabelNode();
        list.add(new VarInsnNode(ALOAD, 0));

        list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(NetworkManagerTransFormer.class), "onPacket", "(Ljava/lang/Object;)Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, label));
        list.add(new InsnNode(Opcodes.RETURN));

        list.add(label);
        mn.instructions.insert(list);


    }
    /*@Mixin(method = Methods.channelRead0NoEvent_NetworkManager)
    public MethodNode channelRead0NoEvent(ClassNode n)
    {
        for(MethodNode mn : n.methods){
            if(mn.name.equals(Methods.channelRead0_NetworkManager.getName())&&
            mn.desc.equals(Methods.channelRead0_NetworkManager.getDescriptor())){
                MethodNode newNode=new MethodNode(mn.access,"channelRead0NoEvent",mn.desc,null,null);
                newNode.instructions.add(mn.instructions);
                return newNode;
            }
        }
        return null;
    }*/

    // func_179290_a,sendPacket,2,

    public static boolean onPacket(Object packet) {
        //Agent.logger.info(Mappings.getUnobfClass(packet.getClass().getName()));
        return ((CancellableEvent) YolBi.instance.getEventManager().post(new EventPacketReceive((Packet) packet))).isCancelled();
    }
}
