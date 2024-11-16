package cn.yapeteam.yolbi.module;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventKey;
import cn.yapeteam.yolbi.module.impl.combat.*;
import cn.yapeteam.yolbi.module.impl.misc.AntiInvisible;
import cn.yapeteam.yolbi.module.impl.misc.ClientSpoof;
import cn.yapeteam.yolbi.module.impl.misc.NoteBot;
import cn.yapeteam.yolbi.module.impl.misc.SelfDestruct;
import cn.yapeteam.yolbi.module.impl.movement.Sprint;
import cn.yapeteam.yolbi.module.impl.movement.StrafeFix;
import cn.yapeteam.yolbi.module.impl.player.AutoArmor;
import cn.yapeteam.yolbi.module.impl.player.ChestStealer;
import cn.yapeteam.yolbi.module.impl.player.MurdererFinder;
import cn.yapeteam.yolbi.module.impl.player.SlotHandler;
import cn.yapeteam.yolbi.module.impl.visual.*;
import cn.yapeteam.yolbi.module.impl.world.FastPlace;
import cn.yapeteam.yolbi.module.impl.world.GameSpeed;
import cn.yapeteam.yolbi.module.impl.world.LegitScaffold;
import cn.yapeteam.yolbi.module.impl.world.Scaffold;
import cn.yapeteam.yolbi.notification.Notification;
import cn.yapeteam.yolbi.notification.NotificationType;
import cn.yapeteam.yolbi.utils.player.animation.Easing;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Getter
@SuppressWarnings({"unchecked", "unused"})
public class ModuleManager {
    private final List<Module> modules = new CopyOnWriteArrayList<>();

    public void load() {
        modules.add(new VanillaAura());
        modules.add(new Criticals());
        modules.add(new AimAssist());
        modules.add(new AntiBot());
        modules.add(new AutoClicker());
        modules.add(new KillAura());
        modules.add(new CombatSettings());
        modules.add(new Velocity());
        modules.add(new WTap());
        modules.add(new AntiInvisible());
        modules.add(new AutoArmor());
        modules.add(new LegitScaffold());
        modules.add(new Scaffold());
        modules.add(new SlotHandler());
        modules.add(new ChestStealer());
        modules.add(new ClientSpoof());
        modules.add(new NoteBot());
        modules.add(new GameSpeed());
        modules.add(new SelfDestruct());
        modules.add(new Sprint());
        modules.add(new StrafeFix());
        modules.add(new ClickUI());
        modules.add(new WebBrowser());
        modules.add(new ClientTheme());
        modules.add(new BAHalo());
//        modules.add(new OldAnimation());
        modules.add(new ESP());
        modules.add(new HeadUpDisplay());
        modules.add(new PacketDebug());
        modules.add(new TargetHud());
        modules.add(new MurdererFinder());
        modules.add(new NameTags());
        modules.add(new FastPlace());
        modules.add(new ClickAssist());
        modules.add(new HitSelect());
        modules.sort((m1, m2) -> -Integer.compare(m2.getName().charAt(0), m1.getName().charAt(0)));
    }

    @Listener
    private void onKey(EventKey e) {
        modules.stream().filter(m -> m.getKey() == e.getKey()).collect(Collectors.toList()).forEach(module -> {
            module.toggle();
            YolBi.instance.getNotificationManager().post(new Notification(
                    module.getName() + (module.isEnabled() ? " Enabled" : " Disabled"),
                    Easing.EASE_OUT_BACK, Easing.EASE_IN_OUT_CUBIC,
                    3000, module.isEnabled() ? NotificationType.SUCCESS : NotificationType.FAILED
            ));
        });
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
