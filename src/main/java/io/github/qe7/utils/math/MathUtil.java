package io.github.qe7.utils.math;

public final class MathUtil {

    private MathUtil() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
