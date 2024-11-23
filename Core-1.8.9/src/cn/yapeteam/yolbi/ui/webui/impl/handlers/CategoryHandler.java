package cn.yapeteam.yolbi.ui.webui.impl.handlers;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.ui.webui.impl.Handler;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import net.montoyo.mcef.api.IJSQueryCallback;

public class CategoryHandler implements Handler {
    @Override
    public void handle(String[] args, IJSQueryCallback callback) {
        JsonArray array = new JsonArray();
        for (ModuleCategory value : ModuleCategory.values())
            if (!YolBi.instance.getModuleManager().getModulesByCategory(value).isEmpty())
                array.add(new JsonPrimitive(value.name()));
        callback.success(array.toString());
    }
}
