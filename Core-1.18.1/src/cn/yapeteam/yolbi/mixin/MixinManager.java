package cn.yapeteam.yolbi.mixin;

import cn.yapeteam.loader.InjectorBridge;
import cn.yapeteam.loader.JVMTIWrapper;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.utils.ClassUtils;
import cn.yapeteam.ymixin.ASMTransformer;
import cn.yapeteam.ymixin.MixinTransformer;
import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.utils.ASMUtils;
import cn.yapeteam.yolbi.mixin.transformer.*;
import org.objectweb.asm_9_2.tree.ClassNode;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class MixinManager {
    public static final ArrayList<ClassNode> mixins = new ArrayList<>();
    public static final ArrayList<ASMTransformer> transformers = new ArrayList<>();
    public static MixinTransformer mixinTransformer;
    public static final String MIXIN_PACKAGE = "cn.yapeteam.yolbi.mixin.injection";

    public static void init() throws Throwable {
        mixinTransformer = new MixinTransformer(JVMTIWrapper.instance::getClassBytes);
        addMixin("MixinMinecraft");
        addMixin("MixinGaeRenderer");
        addMixin("CameraMixin");
        addTransformer(new EntityPlayerSPTransformer());
        addTransformer(new EntityTransformer());
        addTransformer(new GuiScreenTransformer());
        addTransformer(new KeyBindTransformer());
        addTransformer(new MinecraftTransformer());
        addTransformer(new NetworkHandlerTransformer());
        addTransformer(new NetworkManagerTransFormer());
    }

    public static void destroyClient() {
        Map<String, byte[]> map = mixinTransformer.getOldBytes();
        for (ClassNode mixin : mixins) {
            Class<?> targetClass = Objects.requireNonNull(Mixin.Helper.getAnnotation(mixin)).value();
            if (targetClass != null) {
                byte[] bytes = map.get(targetClass.getName());
                int code = JVMTIWrapper.instance.redefineClass(targetClass, bytes);
                Logger.success("Redefined {}, Return Code {}.", targetClass, code);
            }
        }
        for (ASMTransformer transformer : transformers) {
            Class<?> targetClass = transformer.getTarget();
            if (targetClass != null) {
                byte[] bytes = map.get(targetClass.getName());
                int code = JVMTIWrapper.instance.redefineClass(targetClass, bytes);
                Logger.success("Redefined {}, Return Code {}.", targetClass, code);
            }
        }
    }

    //for debug
    private static final File dir = new File("generated");

    public static void transform() throws Throwable {
        boolean ignored = dir.mkdirs();
        Map<String, byte[]> map = mixinTransformer.transform();
        InjectorBridge.send("S2");
        int total = mixins.size() + transformers.size();
        ArrayList<String> failed = new ArrayList<>();
        ArrayList<Class<?>> redefined = new ArrayList<>();
        for (int i = 0; i < mixins.size(); i++) {
            ClassNode mixin = mixins.get(i);
            Class<?> targetClass = Objects.requireNonNull(Mixin.Helper.getAnnotation(mixin)).value();
            if (targetClass != null && !redefined.contains(targetClass)) {
                byte[] bytes = map.get(targetClass.getName());
                if (bytes == null) {
                    failed.add(mixin.name.replace('/', '.'));
                    continue;
                }
                Files.write(new File(dir, targetClass.getName()).toPath(), bytes);
                int code = JVMTIWrapper.instance.redefineClass(targetClass, bytes);
                InjectorBridge.send("P2" + "=>" + (float) (i + 1) / total * 100f);
                if (code != 0)
                    failed.add(mixin.name.replace('/', '.'));
                Logger.success("Redefined {}, Return Code {}.", targetClass, code);
                redefined.add(targetClass);
                Thread.sleep(100);
            }
        }
        for (int i = 0; i < transformers.size(); i++) {
            ASMTransformer asmTransformer = transformers.get(i);
            Class<?> targetClass = asmTransformer.getTarget();
            byte[] bytes = map.get(targetClass.getName());
            if (bytes == null) {
                failed.add(asmTransformer.getClass().getName());
                continue;
            }
            Files.write(new File(dir, targetClass.getName()).toPath(), bytes);
            if (redefined.contains(targetClass)) continue;
            int code = JVMTIWrapper.instance.redefineClass(targetClass, bytes);
            InjectorBridge.send("P2" + "=>" + (float) (i + mixins.size() + 1) / total * 100f);
            if (code != 0)
                failed.add(asmTransformer.getClass().getName());
            Logger.success("Redefined {}, Return Code {}.", targetClass, code);
            redefined.add(targetClass);
            Thread.sleep(100);
        }
        for (String s : failed)
            Logger.error("Failed to apply patch: {}", s);
        InjectorBridge.send("E2");
    }

    private static void addMixin(String name) throws Throwable {
        ClassNode node = ASMUtils.node(ClassUtils.getClassBytes(MIXIN_PACKAGE + "." + name));
        mixins.add(node);
        mixinTransformer.addMixin(node);
    }

    private static void addTransformer(ASMTransformer transformer) {
        transformers.add(transformer);
        mixinTransformer.addTransformer(transformer);
    }
}
