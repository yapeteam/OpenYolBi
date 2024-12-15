package cn.yapeteam.yolbi.ui.webui.impl.handlers;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.ui.webui.impl.Handler;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import net.montoyo.mcef.api.IJSQueryCallback;

public class ModuleHandler implements Handler {
    @Override
    public void handle(String[] args, IJSQueryCallback callback) {
        if (args.length == 1) {
            JsonArray array = new JsonArray();
            for (Module module : YolBi.instance.getModuleManager().getModulesByCategory(ModuleCategory.valueOf(args[0])))
                array.add(new JsonPrimitive(module.getName()));
            callback.success(array.toString());
        } else callback.failure(404, "invalid args length");
    }
}
