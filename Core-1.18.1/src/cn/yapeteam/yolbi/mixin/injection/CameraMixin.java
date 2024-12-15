package cn.yapeteam.yolbi.mixin.injection;


import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Redirect;
import cn.yapeteam.ymixin.annotations.Shadow;
import net.minecraft.client.Camera;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import cn.yapeteam.ymixin.annotations.*;


@Mixin(LocalPlayer.class)
public abstract class CameraMixin {
	@Shadow
	private Entity focusedEntity;

	@Shadow
	protected abstract void setRotation(float yaw, float pitch);

	@Redirect(method = "update",desc = "(Lnet/minecraft/client/render/Camera;FF)V", target = @Target(value = "INVOKEVIRTUAL", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 0))
	private void setRotation(Camera self, float yaw, float pitch) {
			this.setRotation(yaw,pitch);
            return;
	}
}
