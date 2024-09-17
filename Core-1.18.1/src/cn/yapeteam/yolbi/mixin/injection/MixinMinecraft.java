package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.Inject;
import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Overwrite;
import cn.yapeteam.ymixin.annotations.Target;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.managers.ReflectionManager;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Overwrite(method = "getInstance", desc = "()Lnet/minecraft/client/Minecraft;")
    public static Minecraft getInstance() {
        return ReflectionManager.Minecraft$getInstance();
    }

    @Inject(method = "close", desc = "()V", target = @Target("HEAD"))
    public void close() {
        YolBi.instance.shutdown();
    }
}
