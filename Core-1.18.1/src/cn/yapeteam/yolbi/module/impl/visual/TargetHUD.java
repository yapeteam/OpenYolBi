package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.render.ColorUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import static cn.yapeteam.yolbi.utils.Cam.RenderSkin.getPlayerSkin;
import static cn.yapeteam.yolbi.utils.Cam.RenderSkin.renderAvatar;

public class TargetHUD extends Module {
    public TargetHUD(){
        super("TargetHUD", ModuleCategory.VISUAL, InputConstants.KEY_H);
        addValues(sz);
    }
    public AbstractFontRenderer font = YolBi.instance.getFontManager().getMINE14();
    private NumberValue<Integer> sz = new NumberValue<Integer>("SkinSize",35,35,200,5);
    @Listener
    public void onRenderOverlay(EventRender2D e) {
        LivingEntity target;
        target = YolBi.instance.getModuleManager().FindTarget();
        if(target==null){
            return;
        }
        if (mc.player == null) return;
        if(target instanceof Player){
            Player player = (Player) target;
            GameProfile profile = player.getGameProfile();
            if (profile != null) {
                ResourceLocation skinTexture = getPlayerSkin(profile);
                if (skinTexture != null) {
                    int a = sz.getValue().intValue();
                    renderAvatar(e.getPoseStack(), skinTexture, mc.screen.width/2, mc.screen.height/2,a,a);
                    font.drawStringWithShadow(e.getPoseStack(),String.valueOf(target.getHealth())+"\n"+target.getName().getString(),mc.screen.width/2 + mc.screen.width/15 + 3, mc.screen.height/2 + 3, ColorUtils.color(0,0,0,0));
                }
            }
        }else{
            return;
        }

    }
}
