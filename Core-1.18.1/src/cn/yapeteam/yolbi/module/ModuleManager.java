package cn.yapeteam.yolbi.module;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventKey;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.impl.combat.*;
import cn.yapeteam.yolbi.module.impl.misc.ChestStealer;
import cn.yapeteam.yolbi.module.impl.misc.InvClean;
import cn.yapeteam.yolbi.module.impl.misc.SelfDestruct;
import cn.yapeteam.yolbi.module.impl.movement.Eagle;
import cn.yapeteam.yolbi.module.impl.movement.GrimRunEat;
import cn.yapeteam.yolbi.module.impl.movement.Sprint;
import cn.yapeteam.yolbi.module.impl.visual.ClientTheme;
import cn.yapeteam.yolbi.module.impl.visual.ESP;
import cn.yapeteam.yolbi.module.impl.visual.HUD;
import cn.yapeteam.yolbi.module.impl.visual.YeShiEE;
import lombok.Getter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


@Getter
@SuppressWarnings({"unchecked", "unused"})
public class ModuleManager {
    private final List<Module> modules = new CopyOnWriteArrayList<>();
    public AbstractFontRenderer font = YolBi.instance.getFontManager().getMINE14();
    public void load() {
        modules.add(new SelfDestruct());
        modules.add(new ClientTheme());
        modules.add(new AutoClicker());
        modules.add(new Killaura());
        modules.add(new Aimbot());
        modules.add(new Eagle());
        modules.add(new HUD());
        modules.add(new Aimbot());
        modules.add(new AntiKb());
        modules.add(new ESP());
        modules.add(new Sprint());
        modules.add(new FuckBed());
        modules.add(new ChestStealer());
        modules.add(new InvClean());
        modules.add(new GrimRunEat());
        modules.add(new YeShiEE());
        modules.add(new Reach());
        modules.sort((m1, m2) -> -Integer.compare(m2.getName().charAt(0), m1.getName().charAt(0)));
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
