package cn.yapeteam.yolbi.mixin.injection;


import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Redirect;
import cn.yapeteam.ymixin.annotations.Shadow;
import net.minecraft.client.Camera;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import cn.yapeteam.ymixin.annotations.*;
import cn.yapeteam.yolbi.YolBi;


import static cn.yapeteam.yolbi.utils.IMinecraft.mc;


@Mixin(Camera.class)
public abstract class CameraMixin {
	@Shadow
	private Entity focusedEntity;

	@Shadow
	protected abstract void setRotation(float yaw, float pitch);

	@Redirect(method = "update",desc = "(Lnet/minecraft/client/render/Camera;FF)V", target = @Target(value = "INVOKEVIRTUAL", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 0))
	private void setRotation(Camera self, float yaw, float pitch) {
        mc.gui.getChat().addMessage(new TextComponent("A jOIN"));
			this.setRotation(1, -30);
            self.setAnglesInternal(1, -30);
            mc.gui.getChat().addMessage(new TextComponent("A END"));
            return;
	}
}
