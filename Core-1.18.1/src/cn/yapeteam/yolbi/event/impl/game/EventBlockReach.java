package cn.yapeteam.yolbi.event.impl.game;

import cn.yapeteam.yolbi.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EventBlockReach extends Event {
    private float reach;

}
