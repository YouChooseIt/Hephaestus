package io.github.qe7.utils;

import net.minecraft.client.Minecraft;

import java.awt.*;

public final class RenderUtil {

    private RenderUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void renderFancyString(String text, int x, int y, int color) {
        Minecraft.getMinecraft().fontRenderer.drawString(text.replaceAll("§[0-9a-fA-F]", ""), x + 0.5f, y + 0.5f, new Color(0, 0, 0, 255).getRGB());
        Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, color);
    }
}
