package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.player.RotationUtilsBlock;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.yapeteam.yolbi.module.impl.combat.AutoClicker.generate;

public class YolBiTelly extends Module {
    public YolBiTelly() {
        super("Scaffold",ModuleCategory.MOVEMENT,InputConstants.KEY_R);
        addValues();
    }
  //  public AbstractFontRenderer font = YolBi.instance.getFontManager().getMINE14();
    @Override
    protected void onEnable() {
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
    public void aim(EventRender2D e){
        if(mc.player==null){
            return;
        }
        BlockPos block = findBedsAround(mc.player.blockPosition(), 2);
        if(block.equals(new BlockPos(983978,983978,983978))){
            return;
        }
        float []r = RotationUtilsBlock.getSimpleRotations(block.getX()-0.5d,block.getY()+0.5d,block.getZ()-0.5d);
        mc.player.setYRot(r[0]);
        mc.player.setXRot(r[1]);
            startAutoClicker(true);

        try{
            float pressPercentageValue = 17 / 100f;
            TimeUnit.MILLISECONDS.sleep((long) (1000 / daly * pressPercentageValue) + (int) ((Math.random() * 800) + -800));
        }catch (Throwable ev){
            Logger.exception(ev);
        }
        startAutoClicker(false);
    }
}
