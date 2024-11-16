package cn.yapeteam.yolbi.event.impl.player;

import com.mojang.blaze3d.vertex.PoseStack;
import cn.yapeteam.yolbi.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Render3DEvent extends Event{
    private float partialTicks;
    private PoseStack poseStack;
}
