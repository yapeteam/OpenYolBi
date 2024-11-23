package cn.yapeteam.yolbi.ui.webui.impl.handlers;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.values.Value;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.ui.webui.impl.Handler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.montoyo.mcef.api.IJSQueryCallback;

public class ValueHandler implements Handler {
    @Override
    public void handle(String[] args, IJSQueryCallback callback) {
        if (args.length == 1) {
            JsonArray array = new JsonArray();
            JsonObject base = new JsonObject();
            Module module = YolBi.instance.getModuleManager().getModuleByName(args[0]);
            base.addProperty("bool:enabled", module.isEnabled());
            base.addProperty("number:key", module.getKey());
            array.add(base);
            /*
            [
               {
                   "bool:flag": true,
                   "number:cps": {
                       "min": 0,
                       "max": 10,
                       "inc": 1,
                       "value": 5
                   },
                   "mode:Mode": {
                       "modes": ["mode1", "mode2"],
                       "value": "mode1"
                   }
               },
               ...
            ]
            */
            for (Value<?> value : module.getValues()) {//values
                JsonObject object = new JsonObject();
                switch (value.getType()) {
                    case "number":
                        NumberValue<?> numberValue = (NumberValue<?>) value;
                        JsonObject numberObject = new JsonObject();
                        object.add("number:" + numberValue.getName(), numberObject);
                        numberObject.addProperty("min", numberValue.getMin());
                        numberObject.addProperty("max", numberValue.getMax());
                        numberObject.addProperty("inc", numberValue.getInc());
                        numberObject.addProperty("value", numberValue.getValue());
                        break;
                    case "mode":
                        JsonArray mode_array = new JsonArray();
                        ModeValue<?> modeValue = (ModeValue<?>) value;
                        JsonObject modeObject = new JsonObject();
                        object.add("mode:" + modeValue.getName(), modeObject);
                        for (String mode : modeValue.getModesAsString())
                            mode_array.add(new JsonPrimitive(mode));
                        modeObject.add("modes", mode_array);
                        modeObject.addProperty("value", modeValue.toString());
                        break;
                    default:
                        object.addProperty(value.getType() + ":" + value.getName(), value.toString());
                }
                array.add(object);
            }
            callback.success(array.toString());
        } else callback.failure(0, "invalid args length");
    }
}
