package cn.yapeteam.yolbi.mixin.transformer;

import cn.yapeteam.ymixin.ASMTransformer;
import cn.yapeteam.ymixin.utils.Mapper;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.player.EventStrafe;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm_9_2.Opcodes;
import org.objectweb.asm_9_2.Type;
import org.objectweb.asm_9_2.tree.*;


public class EntityTransformer extends ASMTransformer {
    public EntityTransformer() {
        super(Entity.class);
    }

    @Inject(method = "moveRelative", desc = "(FLnet/minecraft/world/phys/Vec3;)V")
    public void onMoveFly(MethodNode methodNode) {
        InsnList list = new InsnList();//m_19920_
        AbstractInsnNode point = null;
        for (int i = 0; i < methodNode.instructions.size(); ++i) {
            AbstractInsnNode aisn = methodNode.instructions.get(i);
            if (aisn instanceof MethodInsnNode) {
                MethodInsnNode meth = (MethodInsnNode) aisn;
                //MD: net/minecraft/world/entity/Entity/m_20015_ (Lnet/minecraft/world/phys/Vec3;FF)Lnet/minecraft/world/phys/Vec3; net/minecraft/world/entity/Entity/getInputVector (Lnet/minecraft/world/phys/Vec3;FF)Lnet/minecraft/world/phys/Vec3;
                if (meth.name.equals(Mapper.map("net/minecraft/world/entity/Entity", "getInputVector", "(Lnet/minecraft/world/phys/Vec3;FF)Lnet/minecraft/world/phys/Vec3;", Mapper.Type.Method))) {
                    point = aisn;
                }
            }
        }
        methodNode.instructions.insert(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EntityTransformer.class), "onStrafe", "(Ljava/lang/Object;Ljava/lang/Object;FF)Lcn/yapeteam/yolbi/mixin/transformer/EntityTransformer$EventStructure;", false));
        int event = methodNode.maxLocals;
        list.add(new VarInsnNode(Opcodes.ASTORE, event));

        list.add(new VarInsnNode(Opcodes.ALOAD, event));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, Type.getInternalName(EventStructure.class), "vec", "L" + Mapper.getObfClass("net/minecraft/world/phys/Vec3") + ";"));

        list.add(new VarInsnNode(Opcodes.ALOAD, event));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, Type.getInternalName(EventStructure.class), "friction", "F"));

        list.add(new VarInsnNode(Opcodes.ALOAD, event));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, Type.getInternalName(EventStructure.class), "yaw", "F"));
        methodNode.instructions.insertBefore(point, list);

    }

    @SuppressWarnings("unused")
    public static EventStructure onStrafe(Object entity, Object moveVec, float friction, float yaw) {
        //System.out.println("onStrafe1");
        //System.out.println("onStrafe");
        if (!(moveVec instanceof Vec3)) throw new RuntimeException("invalid vec");

        EventStrafe eventStrafe = new EventStrafe((float) ((Vec3) moveVec).z, (float) ((Vec3) moveVec).x, yaw, friction);
        if (entity instanceof LocalPlayer) YolBi.instance.getEventManager().post(eventStrafe);
        return new EventStructure(new Vec3(eventStrafe.strafe, ((Vec3) moveVec).y, eventStrafe.forward),
                eventStrafe.friction, eventStrafe.yaw);
    }

    public static class EventStructure {
        public Vec3 vec;
        public float friction, yaw;

        public EventStructure(Vec3 v, float friction, float yaw) {
            super();
            this.vec = v;
            this.yaw = yaw;
            this.friction = friction;
        }
    }

    @Inject(method = "getViewVector", desc = "(F)Lnet/minecraft/world/phys/Vec3;")
    public void getLookAngle(MethodNode methodNode) {
        for (int i = 0; i < methodNode.instructions.size(); ++i) {
            AbstractInsnNode node = methodNode.instructions.get(i);
            if (node instanceof MethodInsnNode) {
                if (((MethodInsnNode) node).name.equals(Mapper.map("net/minecraft/world/entity/Entity", "getViewYRot", "(F)F", Mapper.Type.Method))
                        && ((MethodInsnNode) node).desc.equals("(F)F")) {

                    methodNode.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EntityTransformer.class), "yaw", "(Ljava/lang/Object;F)F"));

                    methodNode.instructions.remove(node);
                }
                if (((MethodInsnNode) node).name.equals(Mapper.map("net/minecraft/world/entity/Entity", "getViewXRot", "(F)F", Mapper.Type.Method))
                        && ((MethodInsnNode) node).desc.equals("(F)F")) {

                    methodNode.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EntityTransformer.class), "pitch", "(Ljava/lang/Object;F)F"));
                    methodNode.instructions.remove(node);
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static float yaw(Object entity, float f) {
        if (entity instanceof LocalPlayer)
            return YolBi.instance.getRotationManager().getRation().y;
        else if (entity instanceof Entity) return ((Entity) entity).getViewYRot(f);
        throw new RuntimeException("arg0 isn't Entity");
    }

    @SuppressWarnings("unused")
    public static float pitch(Object entity, float f) {//getViewVector
        if (entity instanceof LocalPlayer)
            return YolBi.instance.getRotationManager().getRation().x;
        else if (entity instanceof Entity) return ((Entity) entity).getViewXRot(f);
        throw new RuntimeException("arg0 isn't Entity");
    }
}
