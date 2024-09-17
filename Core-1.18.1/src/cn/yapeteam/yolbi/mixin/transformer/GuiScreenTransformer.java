package cn.yapeteam.yolbi.mixin.transformer;

import cn.yapeteam.ymixin.ASMTransformer;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.player.EventChat;
import net.minecraft.client.gui.screens.Screen;

import org.objectweb.asm_9_2.Opcodes;
import org.objectweb.asm_9_2.Type;
import org.objectweb.asm_9_2.tree.*;

public class GuiScreenTransformer extends ASMTransformer {
    public GuiScreenTransformer() {
        super(Screen.class);
    }

    @Inject(method = "sendMessage", desc = "(Ljava/lang/String;Z)V")
    public void onMessage(MethodNode methodNode) {
        InsnList list = new InsnList();
        LabelNode label = new LabelNode();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        //list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,Type.getInternalName(String.class),"toString","()Ljava/lang/String;"));
        //list.add(new LdcInsnNode(false));//
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(GuiScreenTransformer.class), "handler", "(Ljava/lang/String;)Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, label));
        //Opcodes.INVOKE
        //methodNode.visitMaxs(0,0);
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(label);

        methodNode.instructions.insert(list);
    }

    public static boolean handler(String msg) {
        EventChat eventChat = new EventChat(msg);
        YolBi.instance.getEventManager().post(eventChat);
        return eventChat.isCancelled();
    }
}
