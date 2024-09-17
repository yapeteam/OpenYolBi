package cn.yapeteam.yolbi.mixin.transformer;

import cn.yapeteam.ymixin.ASMTransformer;
import cn.yapeteam.ymixin.utils.Mapper;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.game.EventKey;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm_9_2.Opcodes;
import org.objectweb.asm_9_2.Type;
import org.objectweb.asm_9_2.tree.InsnList;
import org.objectweb.asm_9_2.tree.MethodInsnNode;
import org.objectweb.asm_9_2.tree.MethodNode;
import org.objectweb.asm_9_2.tree.VarInsnNode;


public class KeyBindTransformer extends ASMTransformer {
    public KeyBindTransformer() {
        super(KeyMapping.class);
    }

    @Inject(method = "click", desc = "(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V")
    public void onKey(MethodNode methodNode) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));//MD: com/mojang/blaze3d/platform/InputConstants$Key/m_84873_ ()I com/mojang/blaze3d/platform/InputConstants$Key/getValue ()I
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Mapper.getObfClass("com/mojang/blaze3d/platform/InputConstants$Key"), Mapper.map("com/mojang/blaze3d/platform/InputConstants$Key", "getValue", "()I", Mapper.Type.Method), "()I"));
        //MD: com/mojang/blaze3d/platform/InputConstants$Key/m_84873_ ()I com/mojang/blaze3d/platform/InputConstants$Key/getValue ()I
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(KeyBindTransformer.class), "onKey", "(I)V"));
        methodNode.instructions.insert(list);
    }

    public static void onKey(int key) {
        if (key != 0 && key != 1 && GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), key) == GLFW.GLFW_PRESS)
            YolBi.instance.getEventManager().post(new EventKey(key));
    }
}
