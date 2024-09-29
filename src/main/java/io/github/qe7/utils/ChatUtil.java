package io.github.qe7.utils;

import net.minecraft.client.Minecraft;

public final class ChatUtil {

    private ChatUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void addMessage(final String message) {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null) return;

        mc.thePlayer.addChatMessage(message);
    }

    public static void addPrefixedMessage(final String prefix, final String message) {
        addMessage("§7(" + prefix + ") §r§f" + message);
    }

    public static void sendMessage(final String message) {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null) return;

        mc.thePlayer.sendChatMessage(message);
    }
}
