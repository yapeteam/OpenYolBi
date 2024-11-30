package cn.yapeteam.yolbi.module;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventKey;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.impl.combat.AntiKb;
import cn.yapeteam.yolbi.module.impl.combat.AutoClicker;
import cn.yapeteam.yolbi.module.impl.combat.FuckBed;
import cn.yapeteam.yolbi.module.impl.combat.Killaura;
import cn.yapeteam.yolbi.module.impl.misc.SelfDestruct;
import cn.yapeteam.yolbi.module.impl.movement.Eagle;
import cn.yapeteam.yolbi.module.impl.movement.Sprint;
import cn.yapeteam.yolbi.module.impl.movement.YolBiTelly;
import cn.yapeteam.yolbi.module.impl.visual.ClientTheme;
import cn.yapeteam.yolbi.module.impl.visual.ESP;
import cn.yapeteam.yolbi.module.impl.visual.HUD;
import cn.yapeteam.yolbi.module.impl.visual.TargetHUD;
import lombok.Getter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static cn.yapeteam.yolbi.utils.IMinecraft.mc;

@Getter
@SuppressWarnings({"unchecked", "unused"})
public class ModuleManager {
    private final List<Module> modules = new CopyOnWriteArrayList<>();
    public AbstractFontRenderer font = YolBi.instance.getFontManager().getMINE14();
    public void load() {
        modules.add(new SelfDestruct());
        modules.add(new ClientTheme());
        modules.add(new AutoClicker());
        modules.add(new Eagle());
        modules.add(new HUD());
        modules.add(new Killaura());
        modules.add(new AntiKb());
        modules.add(new ESP());
        modules.add(new YolBiTelly());
        modules.add(new Sprint());
        modules.add(new TargetHUD());
        modules.add(new FuckBed());
        modules.sort((m1, m2) -> -Integer.compare(m2.getName().charAt(0), m1.getName().charAt(0)));
    }
    private List<LivingEntity> targets = new ArrayList<>();
    private LivingEntity target;
    public final boolean check(LivingEntity a) {
        return !a.isDeadOrDying() && !a.isInvisible() && a != mc.player;
    }
    public LivingEntity FindTarget() {
        targets.clear();
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (target == null) {
                    if (livingEntity != mc.player && !livingEntity.isInvisible()) {
                        return livingEntity;
                    }
                }
                if (Range(livingEntity) < 5) {
                    if (target != null) {
                        if (check(livingEntity) && Range(livingEntity) < Range(target)) {
                            return livingEntity;
                        }
                    } else {
                        if (check(livingEntity)) {
                            return livingEntity;
                        }
                    }
                }
            }
        }
        return null;
    }
    public final double Range(LivingEntity a) {
        if (mc.player != null) {
            return Math.abs(a.getX() - mc.player.getX()) + Math.abs(a.getZ() - mc.player.getZ());
        }
        return -1;
    }
    @Listener
    private void onKey(EventKey e) {
        modules.stream().filter(m -> m.getKey() == e.getKey()).toList().forEach(Module::toggle);
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        return (T) modules.stream().filter(m -> m.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public <T extends Module> T getModuleByName(String name) {
        return (T) modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Module> getModulesByCategory(ModuleCategory category) {
        return modules.stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
    }
}
