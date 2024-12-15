package cn.yapeteam.yolbi.event.impl.render;


import cn.yapeteam.yolbi.event.Event;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventRender2D extends Event {
    private final PoseStack poseStack;
}
