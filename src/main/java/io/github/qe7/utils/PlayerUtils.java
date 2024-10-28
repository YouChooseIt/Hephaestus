package io.github.qe7.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Packet14BlockDig;
import net.minecraft.src.Packet15Place;

public final class PlayerUtils {

    public static Minecraft mc = Minecraft.getMinecraft();

    public PlayerUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void destroyBlock(int x, int y, int z, int side) {
        PlayerUtils.mc.playerController.clickBlock(x, y, z, side);
        PlayerUtils.mc.playerController.sendBlockRemoving(x, y, z, side);
        PlayerUtils.mc.playerController.field_1064_b = PlayerUtils.mc.playerController.isBeingUsed();
        PlayerUtils.mc.playerController.isBeingUsed();
    }

    public static void placeBlockUnsafe(int x, int y, int z, int side) {
        PlayerUtils.mc.playerController.sendPlaceBlock(PlayerUtils.mc.thePlayer, PlayerUtils.mc.theWorld, PlayerUtils.mc.thePlayer.getCurrentEquippedItem(), x, y, z, side);
        PlayerUtils.mc.playerController.isBeingUsed();
    }

    public static void placeBlock(int x, int y, int z, int side) {
        if (mc.isMultiplayerWorld()) {
            mc.getSendQueue().addToSendQueue(new Packet15Place(x, y, z, side, PlayerUtils.mc.thePlayer.getCurrentEquippedItem()));
            return;
        }
        PlayerUtils.placeBlockUnsafe(x, y, z, side);
    }

    public static void destroyBlockInstant(int x, int y, int z, int side) {
        if (mc.isMultiplayerWorld()) {
            mc.getSendQueue().addToSendQueue(new Packet14BlockDig(0, x, y, z, side));
            mc.getSendQueue().addToSendQueue(new Packet14BlockDig(2, x, y, z, side));
            return;
        }
        PlayerUtils.mc.playerController.sendBlockRemoved(x, y, z, side);
    }
}