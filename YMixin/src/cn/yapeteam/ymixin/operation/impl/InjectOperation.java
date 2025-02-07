package cn.yapeteam.ymixin.operation.impl;

import cn.yapeteam.ymixin.Mixin;
import cn.yapeteam.ymixin.annotations.Inject;
import cn.yapeteam.ymixin.annotations.Local;
import cn.yapeteam.ymixin.annotations.Target;
import cn.yapeteam.ymixin.operation.Operation;
import cn.yapeteam.ymixin.utils.ASMUtils;
import cn.yapeteam.ymixin.utils.DescParser;
import cn.yapeteam.ymixin.utils.Mapper;
import cn.yapeteam.ymixin.utils.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm_9_2.Label;
import org.objectweb.asm_9_2.MethodVisitor;
import org.objectweb.asm_9_2.Opcodes;
import org.objectweb.asm_9_2.tree.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.yapeteam.ymixin.YMixin.Logger;

public class InjectOperation implements Operation {
    private static LabelNode[] getBlock(AbstractInsnNode node, InsnList list) {
        LabelNode first = null, last = null;
        for (int i = 0; i < list.size(); i++) {
            AbstractInsnNode abstractInsnNode = list.get(i);
            if (abstractInsnNode instanceof LabelNode)
                first = (LabelNode) abstractInsnNode;
            if (abstractInsnNode == node)
                break;
        }
        boolean passed = false;
        for (AbstractInsnNode abstractInsnNode : list) {
            if (abstractInsnNode == node)
                passed = true;
            if (passed) {
                if (abstractInsnNode instanceof LabelNode) {
                    last = (LabelNode) abstractInsnNode;
                    break;
                }
            }
        }
        return new LabelNode[]{first, last};
    }

    private static void processReturnLabel(MethodNode source) {
        if (source.desc.endsWith("V") && source.instructions.size() > 0) {
            if (source.instructions.get(source.instructions.size() - 1) instanceof LabelNode)
                source.instructions.remove(source.instructions.get(source.instructions.size() - 1));
            while (source.instructions.size() > 0 &&
                    !(source.instructions.get(source.instructions.size() - 1) instanceof LabelNode))
                source.instructions.remove(source.instructions.get(source.instructions.size() - 1));
        }
    }

    private static int getLocalVarIndex(MethodNode node, String name) {
        try {
            return Integer.parseInt(name);
        } catch (Exception ignored) {
        }
        final int[] varIndex = {-1};
        node.accept(new MethodVisitor(ASM_API) {
            @Override
            public void visitLocalVariable(String varName, String descriptor, String signature, Label start, Label end, int index) {
                if (name.equals(varName))
                    varIndex[0] = index;
                super.visitLocalVariable(varName, descriptor, signature, start, end, index);
            }
        });
        return varIndex[0];
    }

    private static ArrayList<String[]> getLocalParameters(MethodNode node) {
        ArrayList<String[]> parameters = new ArrayList<>();
        if (node.visibleParameterAnnotations == null) return parameters;
        for (List<AnnotationNode> visibleParameterAnnotation : node.visibleParameterAnnotations) {
            for (AnnotationNode annotationNode : visibleParameterAnnotation) {
                if (annotationNode.desc.contains(ASMUtils.slash(Local.class.getName()))) {
                    Local local = Local.Builder.fromAnnotation(annotationNode);
                    parameters.add(new String[]{local.source(), local.index() != -1 ? String.valueOf(local.index()) : local.target()});
                }
            }
        }
        return parameters;
    }

    private static void processLocalValues(MethodNode source, MethodNode target) {
        int max_index = 0;
        for (AbstractInsnNode instruction : target.instructions) {
            if (instruction instanceof VarInsnNode && (Operation.isLoadOpe(instruction.getOpcode()) || Operation.isStoreOpe(instruction.getOpcode()))) {
                VarInsnNode varInsnNode = (VarInsnNode) instruction;
                max_index = Math.max(max_index, varInsnNode.var);
            }
        }

        Map<Integer, Integer> varMap = new HashMap<>();
        ArrayList<String[]> sourceParameters = getLocalParameters(source);
        //Process local var store & load
        for (int i = 0; i < source.instructions.size(); i++) {
            AbstractInsnNode instruction = source.instructions.get(i);
            if (instruction instanceof VarInsnNode && Operation.isStoreOpe(instruction.getOpcode())) {
                VarInsnNode varInsnNode = (VarInsnNode) instruction;
                boolean canChange = true;
                for (String[] sourceParameter : sourceParameters)
                    if (getLocalVarIndex(source, sourceParameter[0]) == varInsnNode.var)
                        canChange = false;
                if (canChange)
                    varMap.put(varInsnNode.var, varInsnNode.var + max_index);
            }
        }
        //Access context local var
        for (String[] sourceParameter : sourceParameters) {
            varMap.put(
                    getLocalVarIndex(source, sourceParameter[0]),
                    getLocalVarIndex(target, sourceParameter[1])
            );
        }
        for (int i = 0; i < source.instructions.size(); i++) {
            AbstractInsnNode instruction = source.instructions.get(i);
            if (instruction instanceof VarInsnNode && (Operation.isLoadOpe(instruction.getOpcode()) || Operation.isStoreOpe(instruction.getOpcode()))) {
                VarInsnNode varInsnNode = (VarInsnNode) instruction;
                Integer index = varMap.get(varInsnNode.var);
                if (index != null)
                    varInsnNode.var = index;
            } else if (instruction instanceof IincInsnNode) {
                IincInsnNode iincInsnNode = (IincInsnNode) instruction;
                Integer index = varMap.get(iincInsnNode.var);
                if (index != null)
                    iincInsnNode.var = index;
            }
        }
    }

    private static void insert(MethodNode source, MethodNode target, Inject info) {
        AbstractInsnNode targetNode = findTargetInsnNode(target, info);
        if (targetNode == null) {
            Logger.error("No target found: {} in {}", info.target().value() + " " + info.target().target(), target.name);
            return;
        }
        LabelNode[] block = getBlock(targetNode, target.instructions);
        Target.Shift shift = info.target().shift();
        InsnList list = new InsnList();
        if (info.target().value().equals("HEAD")) {
            boolean added = false;
            for (int i = 0; i < target.instructions.size(); i++) {
                AbstractInsnNode instruction = target.instructions.get(i);
                if (instruction instanceof LineNumberNode && !added) {
                    added = true;
                    list.add(source.instructions);
                }
                list.add(instruction);
            }
            target.instructions = list;
            return;
        }
        source.instructions.remove(source.instructions.get(0));//刪除開頭的label node
        //  L0
        //    LINENUMBER 5 L0
        //    LDC 10.0
        //    FSTORE 0
        //   L1 ; block[0]
        //    ++GOTO XX ; Before
        // ++LX
        //    LINENUMBER 6 L1
        //    LDC 11.0 ; target
        //    FSTORE 1
        //    ++GOTO XX ; After
        //   L2 ; block[1]
        //   RETURN
        //   XX
        //   INVOKEXXX
        //   GOTO LX ; 跳回
        // block[0]為當前區塊的label node
        // block[1]為下個區塊的label node
        LabelNode back = new LabelNode();
        source.instructions.add(new JumpInsnNode(Opcodes.GOTO, shift == Target.Shift.BEFORE ? back : block[1]));//跳回下一個區塊
        for (int i = 0; i < target.instructions.size(); i++) {
            AbstractInsnNode instruction = target.instructions.get(i);
            if (shift == Target.Shift.BEFORE && instruction == block[0]) {
                list.add(instruction);
                list.add(new JumpInsnNode(Opcodes.GOTO, (LabelNode) target.instructions.get(target.instructions.size() - 1)));//寫入跳轉碼
                list.add(back);//將下面的指令歸入back
            } else if (shift == Target.Shift.AFTER && instruction.getNext() == block[1]) {
                list.add(instruction);
                list.add(new JumpInsnNode(Opcodes.GOTO, (LabelNode) target.instructions.get(target.instructions.size() - 1)));
            } else list.add(instruction);
        }
        list.add(source.instructions);//將需要插入的字節碼追加到結尾
        list.add(new LabelNode());
        target.instructions = list;
    }

    @Override
    public void dispose(Mixin mixin) {
        ClassNode source = mixin.getSource();
        ClassNode target = mixin.getTarget();
        List<MethodNode> injections = source.methods.stream()
                .filter(Inject.Helper::hasAnnotation)
                .collect(Collectors.toList());
        for (MethodNode injection : injections) {
            Inject info = Inject.Helper.getAnnotation(injection);
            if (info == null) continue;
            MethodNode targetMethod = Operation.findTargetMethod(target.methods, mixin.getTargetName(), info.method(), info.desc());
            if (targetMethod == null) {
                Logger.error("No method found: {} in {}", Mapper.mapWithSuper(mixin.getTargetName(), info.method(), info.desc(), Mapper.Type.Method) + DescParser.mapDesc(info.desc()), target.name);
                return;
            }
            processReturnLabel(injection);
            processLocalValues(injection, targetMethod);
            try {
                insert(injection, targetMethod, info);
            } catch (Throwable e) {
                Logger.exception(e);
            }
        }
    }

    //"RETURN" -> 177
    private static int getOperationCode(String ope) {
        int opcode = -1;
        try {
            opcode = (int) Opcodes.class.getField(ope).get(null);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
        return opcode;
    }

    //"INVOKEVIRTUAL net/minecraft/client/Minecraft.runTick()V"
    private static String getMethodInsnNodeOperation(@NotNull AbstractInsnNode node) {
        final String[] target = {null};
        node.accept(new MethodVisitor(ASM_API) {
            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                target[0] = owner + "." + name + descriptor;
            }
        });
        return target[0];
    }

    //"GETSTATIC net/minecraft/client/Minecraft.res Lnet/minecraft/utils/ResourceLocation;"
    private static String getFieldInsnNodeOperation(@NotNull AbstractInsnNode node) {
        final String[] target = {null};
        node.accept(new MethodVisitor(ASM_API) {
            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                target[0] = owner + "." + name + " " + descriptor;
            }
        });
        return target[0];
    }

    private static String[] parseOpe(String ope) {
        String[] owner_name$desc = StringUtil.split(ope, ".");
        String owner = owner_name$desc[0];
        String name = ope.contains(" ") ? StringUtil.split(owner_name$desc[1], " ")[0] : StringUtil.split(owner_name$desc[1], "(")[0];
        String desc = owner_name$desc[1].replace(name, "").replace(" ", "");
        return new String[]{owner, name, desc};
    }

    //Examples:
    //"net/minecraft/client/Minecraft.pickBlockWithNBT(Lnet/minecraft/item/Item;ILnet/minecraft/tileentity/TileEntity;)Lnet/minecraft/item/ItemStack;" -> "ave.a(Lzw;ILakw;)Lzx;"
    //"net/minecraft/init/Items.skull Lnet/minecraft/item/Item;" -> "zy.bX Lzw;"
    private static String mapOperation(String ope) {
        boolean isMethod = !ope.contains(" ");
        String[] values = parseOpe(ope);
        String[] res = new String[3];
        res[0] = Mapper.map(null, values[0], null, Mapper.Type.Class);
        res[1] = isMethod ? Mapper.mapMethodWithSuper(values[0], values[1], values[2]) : Mapper.mapFieldWithSuper(values[0], values[1], values[2]);
        //res[1] = Mapper.map(values[1].startsWith("*") ? null : values[0], values[1].startsWith("*") ? values[1].substring(1) : values[1], values[2], isMethod ? Mapper.Type.Method : Mapper.Type.Field);
        res[2] = DescParser.mapDesc(values[2]);
        return res[0] + "." + res[1] + (isMethod ? "" : " ") + res[2];
    }

    private static AbstractInsnNode findTargetInsnNode(MethodNode target, Inject info) {
        Target targetInfo = info.target();
        String targetOpe = targetInfo.target().isEmpty() ? "" : mapOperation(targetInfo.target());
        int opcode = getOperationCode(targetInfo.value());
        int index = 0;
        for (AbstractInsnNode instruction : target.instructions) {
            if (
                    instruction.getOpcode() == opcode &&
                            (
                                    targetOpe.isEmpty() ||
                                            (targetOpe.contains(" ") ? getFieldInsnNodeOperation(instruction) : getMethodInsnNodeOperation(instruction)).equals(targetOpe)
                            )
            ) {
                if (index == targetInfo.ordinal())
                    return instruction;
                else index++;
            }
        }
        return null;
    }
}
