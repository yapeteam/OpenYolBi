package cn.yapeteam.yolbi.managers;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import net.minecraft.client.Minecraft;

public class RotationManager {
    public Vector2f ration = new Vector2f(0, 0);
    public Minecraft mc = Minecraft.getInstance();
    private boolean active;
    private int tick = 0;
    private Vector2f serverRotation = null;

    public void setRation(Vector2f ration) {
        if (ration.equals(getLocalPlayer())) return;
        this.ration = ration;
        active = true;
    }

    // 兼容接口，供Killaura等模块使用
    public void setRotation(Vector2f rotation) {
        if (rotation == null) return;
        setServerRotation(rotation);
    }

    public Vector2f getRation() {
        return isActive() ? ration : new Vector2f(mc.player.getXRot(), mc.player.getYRot());
    }

    // 兼容接口，供Killaura等模块使用
    public Vector2f getRotation() {
        return getServerRotation();
    }

    public Vector2f getLocalPlayer() {
        return new Vector2f(mc.player.getXRot(), mc.player.getYRot());
    }

    @Listener
    public void onMotion(EventMotion event) {
        if (serverRotation != null) {
            event.setYaw(serverRotation.x);
            event.setPitch(serverRotation.y);
        }
        
        // 应用GCD修正
        float mouseSensitivity = (float) (mc.options.sensitivity * 0.6F + 0.2F);
        float gcd = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15F;
        
        // 修正pitch
        float pitch = event.getPitch();
        pitch = Math.round(pitch / gcd) * gcd;
        event.setPitch(pitch);
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        setActive(active);
        active = false;
    }

    public boolean isActive() {
        return tick > 0;
    }

    public void setActive(boolean active) {
        tick = active ? 1 : 0;
    }

    public void setServerRotation(Vector2f rotation) {
        if (rotation == null) return;
        this.serverRotation = rotation;
    }

    public void resetServerRotation() {
        this.serverRotation = null;
    }

    public Vector2f getServerRotation() {
        return serverRotation;
    }
}
