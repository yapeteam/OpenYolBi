package cn.yapeteam.yolbi.utils.vector;

public final class Vector2f {
    public float x, y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float angle(Vector2f nextPos) {
        float dx = nextPos.x - x;
        float dy = nextPos.y - y;
        return (float) Math.atan2(dy, dx);
    }

    public float distance(Vector2f nextPos) {
        float dx = nextPos.x - x;
        float dy = nextPos.y - y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector2f)) return false;
        Vector2f other = (Vector2f) obj;
        return x == other.x && y == other.y;
    }
}
