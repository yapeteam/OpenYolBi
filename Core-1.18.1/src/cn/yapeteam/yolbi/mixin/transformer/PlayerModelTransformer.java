package cn.yapeteam.yolbi.mixin.transformer;

import cn.yapeteam.ymixin.ASMTransformer;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.impl.render.Rotations;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import org.objectweb.asm_9_2.Opcodes;
import org.objectweb.asm_9_2.Type;
import org.objectweb.asm_9_2.tree.*;


public class PlayerModelTransformer extends ASMTransformer {
    public PlayerModelTransformer() {
        super(PlayerModel.class);
    }
    //MD: net/minecraft/client/model/PlayerModel/m_6973_ (Lnet/minecraft/world/entity/Entity;FFFFF)V net/minecraft/client/model/PlayerModel/setupAnim (Lnet/minecraft/world/entity/Entity;FFFFF)V
    //MD: net/minecraft/client/model/PlayerModel/m_6973_ (Lnet/minecraft/world/entity/LivingEntity;FFFFF)V net/minecraft/client/model/PlayerModel/setupAnim (Lnet/minecraft/world/entity/LivingEntity;FFFFF)V


    @Inject(method = "setupAnim", desc = "(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V")
    public void setupAnim(MethodNode methodNode) {
        AbstractInsnNode node = methodNode.instructions.get(methodNode.instructions.size() - 1);
        for (int i = 0; i < methodNode.instructions.size(); ++i) {
            AbstractInsnNode n = methodNode.instructions.get(i);
            if (n.getOpcode() == Opcodes.RETURN) node = n;
        }
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(PlayerModelTransformer.class), "onSetupAnim", "(Ljava/lang/Object;Ljava/lang/Object;)V"));
        methodNode.instructions.insertBefore(node, list);


    }

    public static void onSetupAnim(Object model, Object entity) {
        Rotations rotations = YolBi.instance.getModuleManager().getModule(Rotations.class);
        if (YolBi.instance.getRotationManager().isActive()
                && rotations.isEnabled()
                && entity instanceof LocalPlayer && model instanceof PlayerModel) {
            ((HumanoidModel<?>) model).head.xRot = (float) (YolBi.instance.getRotationManager().getRation().x / (180.0 / Math.PI));
        }
    }
}
