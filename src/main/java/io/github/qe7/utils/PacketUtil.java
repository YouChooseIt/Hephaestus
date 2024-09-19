package io.github.qe7.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Packet;

public final class PacketUtil {

    private PacketUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void sendPacket(final Packet packet) {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld != null && mc.theWorld.multiplayerWorld) {
            mc.getSendQueue().addToSendQueue(packet);
        }
    }
}
