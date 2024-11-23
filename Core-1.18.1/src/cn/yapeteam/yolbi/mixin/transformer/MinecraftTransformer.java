package cn.yapeteam.yolbi.mixin.transformer;

import cn.yapeteam.ymixin.ASMTransformer;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.render.EventView;
import net.minecraft.client.Minecraft;
import org.objectweb.asm_9_2.Opcodes;
import org.objectweb.asm_9_2.Type;
import org.objectweb.asm_9_2.tree.MethodInsnNode;
import org.objectweb.asm_9_2.tree.MethodNode;


public class MinecraftTransformer extends ASMTransformer {
    public MinecraftTransformer() {
        super(Minecraft.class);
    }

    @Inject(method = "runTick", desc = "(Z)V")//Minecraft/runTick (Z)V
    public void runTick(MethodNode methodNode) {
        methodNode.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(MinecraftTransformer.class), "onRunTick", "()V"));
    }

    @Inject(method = "getCameraEntity", desc = "()Lnet/minecraft/world/entity/Entity;")
    public void getRenderViewEntity(MethodNode methodNode) {
        methodNode.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(MinecraftTransformer.class), "onGetRenderViewEntity", "()V"));
    }

    @SuppressWarnings("unused")
    public static void onRunTick() {
        YolBi.instance.getEventManager().post(new EventTick());
    }

    @SuppressWarnings("unused")
    public static void onGetRenderViewEntity() {
        YolBi.instance.getEventManager().post(new EventView());
    }
}
