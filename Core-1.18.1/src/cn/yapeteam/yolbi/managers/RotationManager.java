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

    public void setRation(Vector2f ration) {
        if (ration.equals(getLocalPlayer())) return;
        this.ration = ration;
        active = true;
    }

    public void setRotation(Vector2f rotation) {
        setRation(rotation);
    }

    public Vector2f getRation() {
        return isActive() ? ration : new Vector2f(mc.player.getXRot(), mc.player.getYRot());
    }

    public Vector2f getRotation() {
        return getRation();
    }

    public Vector2f getLocalPlayer() {
        return new Vector2f(mc.player.getXRot(), mc.player.getYRot());
    }

    @Listener
    public void onMotion(EventMotion event) {
        event.setYaw(getRation().y);
        event.setPitch(getRation().x);

        float mouseSensitivity = (float) (mc.options.sensitivity * 0.6F + 0.2F);
        float gcd = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15F;
        
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
}
