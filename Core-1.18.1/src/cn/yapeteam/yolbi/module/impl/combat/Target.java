package cn.yapeteam.yolbi.module.impl.combat;


import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public class Target extends Module {
    public Entity target = null;
    public ArrayList<Entity> bots = new ArrayList<>();
    public BooleanValue onlyPlayer = new BooleanValue("OnlyPlayer", false);
    public BooleanValue antiBot = new BooleanValue("AntiBot",  false);
    public NumberValue<Double> range = new NumberValue("Range",  6.0, 0, 6.0, 0.1);
    public BooleanValue invisible = new BooleanValue("Invisible",  false);
    public double dist = Double.MAX_VALUE;

    @Override
    public void onDisable() {
        super.onDisable();
        target = null;
        dist = Double.MAX_VALUE;
    }


    @Listener
    public void onUpdate(EventUpdate event) {
        target = null;
        dist = Double.MAX_VALUE;
        LocalPlayer playersp = mc.player;

        for (Entity player : onlyPlayer.getValue() ? mc.level.players() : mc.level.entitiesForRendering()) {
            double d1 = mc.player.distanceTo(player);
            if (player != playersp && d1 < range.getValue() && d1 < dist && player.isAlive()
                    && player instanceof LivingEntity && (invisible.getValue() || !player.isInvisible())) {
                target = player;
                dist = d1;
            }
        }

        try {
            if (antiBot.getValue()) {
                bots.clear();
                for (Player p : mc.level.players()) {
                    if (p == null) continue;
                    if (p instanceof LocalPlayer) continue;

                    if (p.getDisplayName().getString().startsWith(mc.player.getDisplayName().getString().substring(1, 3))) {
                        bots.add(p);
                        continue;
                    }
                    if (mc.getConnection().getPlayerInfo(p.getUUID()) == null) {
                        bots.add(p);
                        continue;
                    }
                    if (p.isInvisible() && !invisible.getValue()) {
                        bots.add(p);
                        continue;
                    }
                    if (p.getTeam() != null && mc.player.getTeam() != null && p.getTeam().isAlliedTo(mc.player.getTeam())) {
                        bots.add(p);
                    }
                }
                if (target != null && bots.contains(target)) target = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Target() {
        super("Target", ModuleCategory.COMBAT);
        addValues(onlyPlayer, antiBot, range, invisible);
    }


}
