package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.render.ColorUtils;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.system.CallbackI;

import java.util.concurrent.TimeUnit;

import static cn.yapeteam.yolbi.module.impl.combat.AutoClicker.generate;

public class YolBiTelly extends Module {
    public YolBiTelly() {
        super("Scaffold",ModuleCategory.MOVEMENT,InputConstants.KEY_R);
        addValues(cps,pitch);
    }
  //  public AbstractFontRenderer font = YolBi.instance.getFontManager().getMINE14();
    private NumberValue<Integer> cps = new NumberValue<Integer>("Cps",27,1,50,1);
    private NumberValue<Float> pitch = new NumberValue<Float>("Pitch",85.0f,-180.1f,180.1f,0.01f);
    private float y;
    @Override
    protected void onEnable() {
        y = MathYaw();
        selectBlock();
        dealya = 1000 / generate(13, 5);
    }

    public static int findBlock() {
        if (mc.player != null) {
            for (int i = 0; i < mc.player.getInventory().items.size(); i++) {
                ItemStack stack = mc.player.getInventory().items.get(i);
                if (stack.getItem() instanceof BlockItem blockItem) {
                    return i;
                }
            }
        }
        return -1;
    }
    public static boolean selectBlock() {
        int slot = findBlock();
        if (slot != -1) {
            mc.player.getInventory().selected = slot;
            return true;
        }

        return false;
    }
    public float MathYaw(){
        if (mc.player == null) {
            return -1111111;
        }
        //This is from 1.8.9 TIMER_err
        float yaw = mc.player.getYRot() + 180;
        float delta = yaw % 45;
        if (delta > 22.5 && delta <= 45)
            yaw += 45 - delta;
        else if (delta < -22.5 && delta >= -45)
            yaw -= 45 + delta;
        else if (delta <= 22.5 && delta > 0)
            yaw -= delta;
        else if (delta >= -22.5 && delta < 0)
            yaw -= delta;
        return yaw;
    }
    double dealya = -1;
    public void startaucr(boolean b){
        if(b){
            Natives.SendRight(true);
        }else{
            Natives.SendRight(false);
        }
    }
    @Listener
    public void aim(EventRender2D e){
        if(mc.player==null){
            return;
        }
        PoseStack ps = e.getPoseStack();
     //   font.drawStringWithShadow(ps,"请注意后方安全",mc.screen.width/2,mc.screen.height/2, ColorUtils.rainbow(4,1).getRGB());
        if(y==-1111111){
            return;
        }
        mc.player.setYRot(y);
        mc.player.setXRot(pitch.getValue());
        if(!mc.player.isOnGround()){
            startaucr(true);
        }else if(mc.player.isOnGround()){
            startaucr(false);
        }
        try{
            float pressPercentageValue = 17 / 100f;
            TimeUnit.MILLISECONDS.sleep((long) (1000 / dealya * pressPercentageValue) + (int) ((Math.random() * 800) + -800));
        }catch (Throwable ev){
            Logger.exception(ev);
        }
    }
}
