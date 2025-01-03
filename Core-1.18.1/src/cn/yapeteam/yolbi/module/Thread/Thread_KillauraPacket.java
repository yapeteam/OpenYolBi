package cn.yapeteam.yolbi.module.Thread;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.impl.combat.Killaura;
import cn.yapeteam.yolbi.utils.misc.InventoryUtils2;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.*;

import java.util.concurrent.TimeUnit;

import static cn.yapeteam.yolbi.module.impl.combat.AutoClicker.generate;
import static cn.yapeteam.yolbi.utils.IMinecraft.mc;

public class Thread_KillauraPacket extends Thread{
    private static boolean running = false;
    private static boolean paused = true;
    private static int cps = 10;
    private static int ab = 4;
    private static LivingEntity target = null;
    //开启
    public static void OpenThread(boolean tf){
        running = tf;

    }
    //设置target
    public static void setTarget(LivingEntity tf,int cps2,int ab2){
        target = tf;
        cps = cps;
        ab =ab2;
    }

    //继续
    public synchronized void ThreadContinue() {
        paused = true;
    }
    //暂停
    public synchronized void ThreadStop() {
        paused = false;
        notify();
    }

    //退出
    public synchronized void stopExit() {
        running = false;
        notify();
    }
    private static double delay = 10;
    @Override
    public void run() {
        while (running) {
            try {
                delay = 1000 / generate(cps, ab);
                if (paused) {
                    synchronized (this) {
                        wait();
                    }
                }
                Cclear();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private static void Cclear(){
        onUpdate();
    }
    public static void onUpdate(){
        if(mc.player==null||Killaura.jztargetrange(target)>3){
            return;
        }
        float pressPercentageValue = 0.2f;
        Natives.SendLeft(true);
        try {
            Thread.sleep((long) (1000 / delay * pressPercentageValue));
            if(target instanceof Entity){
                mc.player.attack((Entity) target);
            }
            mc.player.swing(InteractionHand.MAIN_HAND);
            Thread.sleep((long) (1000 / delay * (1 - pressPercentageValue)));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Natives.SendLeft(false);
    }

}
