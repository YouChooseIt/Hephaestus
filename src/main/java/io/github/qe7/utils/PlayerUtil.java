package io.github.qe7.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Packet14BlockDig;
import net.minecraft.src.Packet15Place;

public final class PlayerUtil {

    public PlayerUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void destroyBlock(int x, int y, int z, int side) {
        Minecraft.getMinecraft().playerController.clickBlock(x, y, z, side);
        Minecraft.getMinecraft().playerController.sendBlockRemoving(x, y, z, side);
        Minecraft.getMinecraft().playerController.field_1064_b = Minecraft.getMinecraft().playerController.isBeingUsed();
        Minecraft.getMinecraft().playerController.isBeingUsed();
    }

    public static void placeBlockUnsafe(int x, int y, int z, int side) {
        Minecraft.getMinecraft().playerController.sendPlaceBlock(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem(), x, y, z, side);
        Minecraft.getMinecraft().playerController.isBeingUsed();
    }

    public static void placeBlock(int x, int y, int z, int side) {
        if (Minecraft.getMinecraft().isMultiplayerWorld()) {
            Minecraft.getMinecraft().getSendQueue().addToSendQueue(new Packet15Place(x, y, z, side, Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem()));
            return;
        }
        PlayerUtil.placeBlockUnsafe(x, y, z, side);
    }

    public static void destroyBlockInstant(int x, int y, int z, int side) {
        if (Minecraft.getMinecraft().isMultiplayerWorld()) {
            Minecraft.getMinecraft().getSendQueue().addToSendQueue(new Packet14BlockDig(0, x, y, z, side));
            Minecraft.getMinecraft().getSendQueue().addToSendQueue(new Packet14BlockDig(2, x, y, z, side));
            return;
        }
        Minecraft.getMinecraft().playerController.sendBlockRemoved(x, y, z, side);
    }
}