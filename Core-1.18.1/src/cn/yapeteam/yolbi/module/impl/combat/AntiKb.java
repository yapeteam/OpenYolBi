package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventPacket;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.math.MathUtils;
import cn.yapeteam.yolbi.utils.misc.VirtualKeyBoard;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.profiling.jfr.event.PacketEvent;
import net.minecraft.world.entity.MoverType;



public class AntiKb extends Module {
    public AntiKb (){
        super("AntiKB", ModuleCategory.COMBAT, InputConstants.KEY_R);
    }
    @Listener
    public void onPacket(EventPacket event) {
        double x = mc.player.getX(),z = mc.player.getZ();
        if (mc.player != null) return;
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            Natives.SetKeyBoard(VirtualKeyBoard.VK_SPACE, true);
            mc.player.move(MoverType.PLAYER,mc.player.position().add(0.01,0,0.01));
            mc.player.moveTo(x,mc.player.getY(),z);
        }
    }
}

