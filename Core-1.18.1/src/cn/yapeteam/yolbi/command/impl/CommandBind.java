package cn.yapeteam.yolbi.command.impl;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.command.AbstractCommand;
import cn.yapeteam.yolbi.module.Module;
import com.mojang.blaze3d.platform.InputConstants;

public class CommandBind extends AbstractCommand {
    public CommandBind() {
        super("bind");
    }

    @Override
    public void process(String[] args) {
        if (args.length == 2) {
            Module module = YolBi.instance.getModuleManager().getModuleByName(args[0]);
            if (module == null) {
                printMessage("Module not found " + args[0]);
                return;
            }
            try {
                int code = InputConstants.getKey("key.keyboard." + args[1].toLowerCase()).getValue();
                module.setKey(code);
                printMessage("Bind " + module.getName() + " to Key " + code);
            } catch (IllegalArgumentException e) {
                printMessage("Key not found " + args[1]);
            }
        }
    }
}
