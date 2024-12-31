package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventLook;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.level.BlockAABBUtils;
import cn.yapeteam.yolbi.utils.player.RotationUtilsBlock;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.InputConstants;
import lombok.experimental.SuperBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.yapeteam.yolbi.module.impl.combat.AutoClicker.generate;

public class YolBiTelly extends Module {
    public YolBiTelly() {
        super("Scaffold",ModuleCategory.MOVEMENT,InputConstants.KEY_R);
        addValues(a);
    }
  //  public AbstractFontRenderer font = YolBi.instance.getFontManager().getMINE14();
    private BlockPos block;
    private Vec3 vc3;
    private NumberValue<Integer> a = new NumberValue<Integer>("Debug",0,0,1,1);
    private float []r;
    @Override
    protected void onEnable() {
        if(mc.player == null)return;
        block = findBedsAround(mc.player.blockPosition(), 2);
        if(block.equals(new BlockPos(983978,983978,983978))){
            return;
        }
        vc3 = BlockAABBUtils.FindPosOnWallOfTheMiddle(block);
        r = RotationUtilsBlock.getSimpleRotations(vc3.x,vc3.y,vc3.z);
        selectBlock();
        daly = 1000 / generate(13, 5);
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

    double daly = -1;
    public void startAutoClicker(boolean b){
        if(b){
            Natives.SendRight(true);
        }else{
            Natives.SendRight(false);
        }
    }
    public boolean hasBlockAt( BlockPos pos) {
        if (mc.level == null) {
            return false;
        }
        BlockState blockState = null;
        blockState = mc.level.getBlockState(pos);
        return !blockState.isAir();
    }
    private double GetRange(BlockPos a,BlockPos b){
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }
    private BlockPos findBedsAround(BlockPos center, int radius) {
        List<BlockPos> bedPositions = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = mc.level.getBlockState(pos);
                    Block block = state.getBlock();
                    if (hasBlockAt(pos)) {
                        bedPositions.add(pos);
                    }
                }
            }
        }
        int min = 911111111;
        BlockPos mi = new BlockPos(983978,983978,983978);
        BlockPos b = new BlockPos(mc.player.getX(),mc.player.getY(),mc.player.getZ());
        for(int i=0;i<bedPositions.size();i++){
            //  mc.gui.getChat().addMessage(new TextComponent("Find bed in "+ bedPositions.get(i).getX()+" " + bedPositions.get(i).getY() +" "+ bedPositions.get(i).getZ()));
            if(GetRange(bedPositions.get(i),b)<min){
                if(GetRange(bedPositions.get(i),b)<=radius){
                    mi = bedPositions.get(i);
                }
            }

        }

        return mi;
    }
    @Listener
    public void aim(EventLook e){
        if(mc.player==null||r == null||block == null||vc3==null){
            return;
        }
        if(a.getValue().intValue() == 0){
            mc.player.setYRot(r[0]);
            mc.player.setXRot(r[1]);
            mc.gui.getChat().addMessage(new TextComponent("runed"));
        }else {
            e.setRotation(new Vector2f(r[0],r[1]));
        }

        startAutoClicker(true);
        try{
            float pressPercentageValue = 17 / 100f;
            Thread.sleep((long) (1000 / daly * pressPercentageValue) + (int) ((Math.random() * 800) + -800));
        }catch (Throwable ev){
            Logger.exception(ev);
        }
        startAutoClicker(false);
    }
}
