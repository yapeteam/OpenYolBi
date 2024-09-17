package cn.yapeteam.yolbi.utils.misc;

public class TimerUtil {

    // The last recorded time in milliseconds
    private long lastTime;

    // The number of updates per second
    private float updates;

    // Cached value to avoid recalculating the same result
    private float cached;

    // Constructor that initializes the timer and resets it
    public TimerUtil() {
        reset();
    }

    // Constructor that sets the updates per second and resets the timer
    public TimerUtil(float updates) {
        this.updates = updates;
        reset();
    }

    // Returns the time elapsed since the last reset in milliseconds
    public long getTimeElapsed() {
        return System.currentTimeMillis() - lastTime;
    }

    // Sets the elapsed time to a specific value
    public void setTimeElapsed(long time) {
        this.lastTime = System.currentTimeMillis() - time;
    }

    // Checks if a certain amount of time has passed since the last reset
    public boolean hasTimePassed(long time) {
        return getTimeElapsed() >= time;
    }

    // Resets the timer and cached value
    public void reset() {
        lastTime = System.currentTimeMillis();
        this.cached = 0.0F;
    }

    // Calculates a float value based on the elapsed time, using different easing functions
    public float getValueFloat(float begin, float end, int type) {
        if (this.cached == end) {
            return this.cached;
        } else {
            float t = (float) (System.currentTimeMillis() - this.lastTime) / this.updates;
            switch (type) {
                case 1:
                    t = t < 0.5F ? 4.0F * t * t * t : (t - 1.0F) * (2.0F * t - 2.0F) * (2.0F * t - 2.0F) + 1.0F;
                    break;
                case 2:
                    t = (float) (1.0D - Math.pow((double) (1.0F - t), 5.0D));
                    break;
                case 3:
                    t = this.bounce(t);
            }

            float value = begin + t * (end - begin);
            if (end < value) {
                value = end;
            }

            if (value == end) {
                this.cached = value;
            }

            return value;
        }
    }

    // Calculates an integer value based on the elapsed time, using different easing functions
    public int getValueInt(int begin, int end, int type) {
        return Math.round(this.getValueFloat((float) begin, (float) end, type));
    }

    // Easing function that simulates a bouncing effect
    private float bounce(float t) {
        float i = 0.0F;
        double i2 = 7.5625D;
        double i3 = 2.75D;
        if ((double) t < 1.0D / i3) {
            i = (float) (i2 * (double) t * (double) t);
        } else if ((double) t < 2.0D / i3) {
            i = (float) (i2 * (double) (t = (float) ((double) t - 1.5D / i3)) * (double) t + 0.75D);
        } else if ((double) t < 2.5D / i3) {
            i = (float) (i2 * (double) (t = (float) ((double) t - 2.25D / i3)) * (double) t + 0.9375D);
        } else {
            i = (float) (i2 * (double) (t = (float) ((double) t - 2.625D / i3)) * (double) t + 0.984375D);
        }

        return i;
    }
}