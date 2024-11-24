package cn.yapeteam.yolbi.ui.webui.impl.handlers;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.values.Value;
import cn.yapeteam.yolbi.module.values.impl.*;
import cn.yapeteam.yolbi.ui.webui.impl.Handler;
import net.montoyo.mcef.api.IJSQueryCallback;

import java.awt.*;

public class SetHandler implements Handler {
    @Override
    public void handle(String[] args, IJSQueryCallback callback) {
        if (args.length == 3) {//modname valuename value
            Module module = YolBi.instance.getModuleManager().getModuleByName(args[0]);
            String[] type_name = args[1].split(":");
            String type = type_name[0];
            String name = type_name[1];
            String val = args[2];
            switch (name) {
                case "enabled":
                    module.setEnabled(Boolean.parseBoolean(val));
                    break;
                case "key":
                    module.setKey(Integer.parseInt(val));
                    break;
                default: {
                    Value<?> value = module.getValueByName(name);

                    switch (type) {
                        case "bool":
                            ((BooleanValue) value).setValue(Boolean.parseBoolean(val));
                            break;
                        case "color":
                            ((ColorValue) value).setValue(new Color(Integer.parseInt(val)));
                            break;
                        case "mode":
                            ((ModeValue<?>) value).setMode(val);
                            break;
                        case "number":
                            ((NumberValue<?>) value).setValue(val);
                            break;
                        case "text":
                            ((TextValue) value).setValue(val);
                            break;
                    }
                }
            }
            callback.success("");
        } else callback.failure(0, "invalid args length");
    }
}
