package cn.yapeteam.injector;

import java.util.Arrays;
import java.util.Objects;

public class Pair<A, B> {
    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A a;
    public B b;

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{a, b});
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) obj;
        return Objects.equals(pair.a, a) && Objects.equals(pair.b, b);
    }
}
