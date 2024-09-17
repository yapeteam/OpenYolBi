package cn.yapeteam.yolbi.wrappers;



import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;

public class ScaledResolutionWrapper {
    private Window scaledResolutionObj;

    public ScaledResolutionWrapper(Minecraft mc) {
        scaledResolutionObj = mc.getWindow();//ReflectionUtils.newInstance(getClazz(), new Class[]{Classes.Minecraft.getClazz()}, mc.getMinecraftObj());
    }

    public int getScaleFactor() {
        return (int) scaledResolutionObj.getGuiScale();//(Integer) ReflectionUtils.invokeMethod(scaledResolutionObj, Mappings.getObfMethod("func_78325_e"));
    }

    public int getScaledWidth() {
        return scaledResolutionObj.getGuiScaledWidth();//(Integer) ReflectionUtils.invokeMethod(scaledResolutionObj, Mappings.getObfMethod("func_78326_a"));
    }

    public int getScaledHeight() {
        return scaledResolutionObj.getGuiScaledHeight();//(Integer) ReflectionUtils.invokeMethod(scaledResolutionObj, Mappings.getObfMethod("func_78328_b"));
    }

    public double getScaledWidth_double() {
        return scaledResolutionObj.getGuiScaledWidth();//(Integer) ReflectionUtils.invokeMethod(scaledResolutionObj, Mappings.getObfMethod("func_78327_c"));
    }

    public double getScaledHeight_double() {
        return scaledResolutionObj.getGuiScaledHeight();//(Integer) ReflectionUtils.invokeMethod(scaledResolutionObj, Mappings.getObfMethod("func_78324_d"));
    }

}
