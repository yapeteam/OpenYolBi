package cn.yapeteam.yolbi.utils.vector;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Vector2d {

    public double x, y;

    public Vector2d() {
    }

    public Vector2d(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d offset(final double x, final double y) {
        return new Vector2d(this.x + x, this.y + y);
    }

    public Vector2d offset(Vector2d xy) {
        return offset(xy.x, xy.y);
    }

    /**
     * Returns the length or magnitude of this vector squared.
     * This is used as an optimization to avoid multiple sqrt when a single sqrt will do.
     *
     * @return the length or magnitude of this vector squared.
     */
    public double lengthSquared() {
        return x * x + y * y;
    }

    /**
     * Returns the length or magnitude of this vector.
     *
     * @return the length or magnitude of this vector.
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }
}
