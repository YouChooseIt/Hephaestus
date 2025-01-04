package io.github.qe7.utils.math;

import net.minecraft.client.Minecraft;

public final class MathUtil {

    private MathUtil() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);

        return (double) tmp / factor;
    }

    public static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);

        return (float) tmp / factor;
    }

    public static float playerYaw180() {
        float newYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;

        if (newYaw >= 180.0f) {
            newYaw -= 360.0f;
        }

        if (newYaw <= -180.0f) {
            newYaw += 360.0f;
        }

        return newYaw;
    }
}
