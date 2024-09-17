package cn.yapeteam.ymixin;

import cn.yapeteam.ymixin.operation.Operation;
import cn.yapeteam.ymixin.operation.impl.InjectOperation;
import cn.yapeteam.ymixin.operation.impl.OverwriteOperation;
import cn.yapeteam.ymixin.utils.ASMUtils;
import cn.yapeteam.ymixin.utils.Mapper;
import lombok.Getter;
import org.objectweb.asm_9_2.tree.ClassNode;
import org.objectweb.asm_9_2.tree.MethodNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static cn.yapeteam.ymixin.YMixin.Logger;

@Getter
public class MixinTransformer {
    private final ClassBytesProvider provider;
    private final ArrayList<cn.yapeteam.ymixin.Mixin> mixins;
    private final ArrayList<ASMTransformer> transformers;
    private final ArrayList<Operation> operations;
    private final Map<String, byte[]> oldBytes = new HashMap<>();

    public MixinTransformer(ClassBytesProvider classBytesProvider) {
        this.provider = classBytesProvider;
        this.mixins = new ArrayList<>();
        this.transformers = new ArrayList<>();
        this.operations = new ArrayList<>();
        operations.add(new InjectOperation());
        operations.add(new OverwriteOperation());
    }

    public void addMixin(ClassNode node) throws Throwable {
        mixins.add(new Mixin(node, provider));
    }

    public void addMixin(byte[] bytes) throws Throwable {
        addMixin(ASMUtils.node(bytes));
    }

    public void addTransformer(ASMTransformer transformer) {
        transformers.add(transformer);
    }

    public Map<String, byte[]> transform() {
        Map<String, byte[]> classMap = new HashMap<>();
        oldBytes.clear();
        for (Mixin mixin : mixins) {
            if (mixin.getTarget() == null) {
                Logger.warn("Mixin {} has no target class, skipping.", mixin.getSource().name);
                continue;
            }
            String name = mixin.getTarget().name.replace('/', '.');
            if (!oldBytes.containsKey(name))
                oldBytes.put(name, mixin.getTargetOldBytes());
            if (classMap.containsKey(name))
                mixin.setTarget(ASMUtils.node(classMap.get(name)));
            for (Operation operation : operations)
                operation.dispose(mixin);
            try {
                byte[] class_bytes = ASMUtils.rewriteClass(mixin.getTarget());
                classMap.put(name, class_bytes);
            } catch (Throwable e) {
                Logger.error("Failed to transform class " + name, e);
                Logger.exception(e);
            }
        }
        for (ASMTransformer transformer : transformers) {
            if (transformer.getTarget() == null) {
                Logger.warn("Transformer {} has no target class, skipping.", transformer.getClass().getName());
                continue;
            }
            String name = transformer.getTarget().getName().replace('/', '.');
            byte[] bytes = classMap.get(name);
            ClassNode targetNode;
            if (bytes == null) {
                ClassNode node = null;
                while (node == null) {
                    try {
                        bytes = provider.getClassBytes(transformer.getTarget());
                        node = ASMUtils.node(bytes);
                        Thread.sleep(500);
                    } catch (Throwable ignored) {
                    }
                }
                targetNode = node;
                oldBytes.put(name, bytes);
            } else targetNode = ASMUtils.node(bytes);
            for (Method method : transformer.getClass().getDeclaredMethods()) {
                method.setAccessible(true);
                if (method.getParameterCount() != 1) continue;
                ASMTransformer.Inject annotation = method.getAnnotation(ASMTransformer.Inject.class);
                if (annotation == null) continue;
                MethodNode node = Operation.findTargetMethod(targetNode.methods, Mapper.getFriendlyClass(targetNode.name), annotation.method(), annotation.desc());
                try {
                    method.invoke(transformer, node);
                } catch (IllegalAccessException e) {
                    Logger.exception(e);
                } catch (InvocationTargetException e) {
                    Logger.exception(e);
                    Logger.exception(e.getCause());
                    Logger.exception(e.getTargetException());
                }
            }
            byte[] class_bytes = ASMUtils.rewriteClass(targetNode);
            classMap.put(name, class_bytes);
        }
        return classMap;
    }
}
