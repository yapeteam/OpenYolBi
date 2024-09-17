package cn.yapeteam.yolbi.event.impl.player;


import cn.yapeteam.yolbi.event.Event;

public class EventStrafe extends Event {
    public float forward,strafe, friction;
    public  float yaw;
    public float factor=Float.NaN;
    public EventStrafe(float forward, float strafe, float yaw, float friction) {
        super();
        this.forward=forward;
        this.strafe=strafe;
        this.yaw=yaw;
        this.friction = friction;
    }
    public EventStrafe(){

    }
    public void slow(double slowIn){
        this.forward*= (float) slowIn;
        this.strafe*= (float) slowIn;
    }


}
