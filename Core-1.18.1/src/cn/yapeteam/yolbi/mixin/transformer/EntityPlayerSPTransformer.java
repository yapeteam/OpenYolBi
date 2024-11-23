package cn.yapeteam.yolbi.mixin.transformer;

import cn.yapeteam.ymixin.ASMTransformer;
import cn.yapeteam.ymixin.utils.Mapper;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.event.impl.player.EventMovement;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import net.minecraft.client.player.LocalPlayer;
import org.objectweb.asm_9_2.Opcodes;
import org.objectweb.asm_9_2.Type;
import org.objectweb.asm_9_2.tree.*;

public class EntityPlayerSPTransformer extends ASMTransformer {
    public EntityPlayerSPTransformer() {
        super(LocalPlayer.class);
    }

    @SuppressWarnings("unused")
    public static EventMotion onMotion(double x, double y, double z, float yaw, float pitch) {
        return YolBi.instance.getEventManager().post(new EventMotion(x, y, z, yaw, pitch));
    }

    @SuppressWarnings("unused")
    public static void onUpdateEvent() {
        //Agent.System.out.println("update");
        //Agent.System.out.println(EventManager.REGISTRY_MAP);
        YolBi.instance.getEventManager().post(new EventUpdate());
    }
    //func_175161_p,onUpdateWalkingPlayer,0,called every tick when the player is on foot. Performs all the things that normally happen during movement.

    @SuppressWarnings("unused")
    public static void onLivingUpdateEvent() {
        //Agent.System.out.println("livingupdate");
        //Agent.System.out.println(EventManager.REGISTRY_MAP);
        YolBi.instance.getEventManager().post(new EventMovement());
    }

    //MD: bew/m ()V net/minecraft/client/entity/EntityPlayerSP/func_70636_d ()V onLivingUpdate
    @Inject(method = "aiStep", desc = "()V")//@Mixin(method= Methods.onLivingUpdate_SP)
    public void onLivingUpdate(MethodNode methodNode) {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EntityPlayerSPTransformer.class), "onLivingUpdateEvent", "()V"));
        MethodInsnNode target = null;
        for (int i = 0; i < methodNode.instructions.size(); ++i) {
            AbstractInsnNode node = methodNode.instructions.get(i);
            if (node instanceof MethodInsnNode && ((MethodInsnNode) node).name.equals(
                    Mapper.map("net/minecraft/client/tutorial/Tutorial", "onInput", "(Lnet/minecraft/client/player/Input;)V", Mapper.Type.Method))//Mappings.getObfMethod("m_120586_")
                    && ((MethodInsnNode) node).owner.equals(
                    Mapper.getObfClass("net/minecraft/client/tutorial/Tutorial"))) {
                //MD: net/minecraft/client/tutorial/Tutorial/m_120586_ (Lnet/minecraft/client/player/Input;)V net/minecraft/client/tutorial/Tutorial/onInput (Lnet/minecraft/client/player/Input;)V

                target = (MethodInsnNode) node;
            }

        }

        methodNode.instructions.insert(target, list);
    }

    @Inject(method = "tick", desc = "()V")//@Mixin(method=Methods.onUpdate_Entity)
    public void onUpdate(MethodNode methodNode) {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EntityPlayerSPTransformer.class), "onUpdateEvent", "()V"));
        methodNode.instructions.insert(list);
        //BuiltinClassLoader

    }

    @Inject(method = "sendPosition", desc = "()V")//@Mixin(method = Methods.onUpdateWalking_SP)
    public void onUpdateWalking(MethodNode methodNode) {
        InsnList list = new InsnList();
        int j = 0;
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));//net.minecraft.world.entity.Entity
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                Mapper.getObfClass("net/minecraft/world/entity/Entity"), Mapper.map("net/minecraft/world/entity/Entity", "getX", "()D", Mapper.Type.Method), "()D"));//"field_70165_t" Mapper.getObfClass("net/minecraft/world/entity/Entity")
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Mapper.getObfClass("net/minecraft/world/entity/Entity"), Mapper.map("net/minecraft/world/entity/Entity", "getY", "()D", Mapper.Type.Method), "()D"));
        //field_70163_u,posY,2,Entity position Y
        //FD: pk/t net/minecraft/world/entity/Entity/field_70163_u
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Mapper.getObfClass("net/minecraft/world/entity/Entity"), Mapper.map("net/minecraft/world/entity/Entity", "getZ", "()D", Mapper.Type.Method), "()D"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Mapper.getObfClass("net/minecraft/world/entity/Entity"), Mapper.map("net/minecraft/world/entity/Entity", "getYRot", "()F", Mapper.Type.Method), "()F"));//yaw
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Mapper.getObfClass("net/minecraft/world/entity/Entity"), Mapper.map("net/minecraft/world/entity/Entity", "getXRot", "()F", Mapper.Type.Method), "()F"));//pitch
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EntityPlayerSPTransformer.class), "onMotion", "(DDDFF)L" + EventMotion.class.getName().replace(".", "/") + ";"));
        list.add(new VarInsnNode(Opcodes.ASTORE, 1));
        j++;
        //ArrayList<AbstractInsnNode> rl=new ArrayList<>();
        for (int i = 0; i < methodNode.instructions.size(); ++i) {
            AbstractInsnNode node = methodNode.instructions.get(i);
            if (node instanceof VarInsnNode && ((VarInsnNode) node).var >= j) {
                ((VarInsnNode) node).var += j;//插入偏移值;
            }
            //FD: pk/s net/minecraft/world/entity/Entity/field_70165_t
            //FD: pk/t net/minecraft/world/entity/Entity/field_70163_u
            //FD: pk/u net/minecraft/world/entity/Entity/field_70161_v
            //FD: pk/y net/minecraft/world/entity/Entity/field_70177_z
            //FD: pk/z net/minecraft/world/entity/Entity/field_70125_A
            if (node instanceof MethodInsnNode && ((MethodInsnNode) node).name.equals(Mapper.map("getYRot", Mapper.getObfClass("net/minecraft/world/entity/Entity"), "()F", Mapper.Type.Method))) {
                //标记替换yaw轴
                AbstractInsnNode aload_0 = methodNode.instructions.get(i - 1);
                if (aload_0 instanceof VarInsnNode) {
                    ((VarInsnNode) aload_0).var = 1;
                    methodNode.instructions.insert(node, new FieldInsnNode(Opcodes.GETFIELD, EventMotion.class.getName().replace(".", "/"), "yaw", "F"));
                    methodNode.instructions.remove(node);
                    //rl.add(node);
                }
            }
            if (node instanceof MethodInsnNode && ((MethodInsnNode) node).name.equals(Mapper.map("getXRot", Mapper.getObfClass("net/minecraft/world/entity/Entity"), "()F", Mapper.Type.Method))) {
                //标记替换yaw轴
                AbstractInsnNode aload_0 = methodNode.instructions.get(i - 1);
                if (aload_0 instanceof VarInsnNode) {
                    ((VarInsnNode) aload_0).var = 1;
                    methodNode.instructions.insert(node, new FieldInsnNode(Opcodes.GETFIELD, EventMotion.class.getName().replace(".", "/"), "pitch", "F"));
                    methodNode.instructions.remove(node);
                    //rl.add(node);
                }
            }
            if (node instanceof MethodInsnNode && ((MethodInsnNode) node).name.equals(Mapper.map("getX", Mapper.getObfClass("net/minecraft/world/entity/Entity"), "()D", Mapper.Type.Method))) {
                //标记替换yaw轴
                AbstractInsnNode aload_0 = methodNode.instructions.get(i - 1);
                if (aload_0 instanceof VarInsnNode) {
                    ((VarInsnNode) aload_0).var = 1;
                    methodNode.instructions.insert(node, new FieldInsnNode(Opcodes.GETFIELD, EventMotion.class.getName().replace(".", "/"), "x", "D"));
                    methodNode.instructions.remove(node);
                    //rl.add(node);
                }
            }
            if (node instanceof MethodInsnNode && ((MethodInsnNode) node).name.equals(Mapper.map("getY", Mapper.getObfClass("net/minecraft/world/entity/Entity"), "()D", Mapper.Type.Method))) {
                //标记替换yaw轴
                AbstractInsnNode aload_0 = methodNode.instructions.get(i - 1);
                if (aload_0 instanceof VarInsnNode) {
                    ((VarInsnNode) aload_0).var = 1;
                    methodNode.instructions.insert(node, new FieldInsnNode(Opcodes.GETFIELD, EventMotion.class.getName().replace(".", "/"), "y", "D"));
                    methodNode.instructions.remove(node);
                    //rl.add(node);
                }
            }
            if (node instanceof MethodInsnNode && ((MethodInsnNode) node).name.equals(Mapper.map("getZ", Mapper.getObfClass("net/minecraft/world/entity/Entity"), "()D", Mapper.Type.Method))) {
                //标记替换yaw轴
                AbstractInsnNode aload_0 = methodNode.instructions.get(i - 1);
                if (aload_0 instanceof VarInsnNode) {
                    ((VarInsnNode) aload_0).var = 1;
                    methodNode.instructions.insert(node, new FieldInsnNode(Opcodes.GETFIELD, EventMotion.class.getName().replace(".", "/"), "z", "D"));
                    methodNode.instructions.remove(node);
                    //rl.add(node);
                }
            }


        }
        methodNode.instructions.insert(list);
    }
}
