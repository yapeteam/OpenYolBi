package cn.yapeteam.yolbi.event.impl.render;

import cn.yapeteam.yolbi.event.Event;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventRender3D extends Event {
    private PoseStack poseStack;
    private float partialTicks;

    public PoseStack poseStack() {
        return poseStack;
    }
}
