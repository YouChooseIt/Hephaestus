package io.github.qe7.utils;

import net.minecraft.client.Minecraft;

public final class MovementUtil {

    public static boolean isMoving() {
        return !(Minecraft.getMinecraft().thePlayer.motionX == 0 && Minecraft.getMinecraft().thePlayer.motionZ == 0);
    }

    public static void setSpeed(double moveSpeed) {
        setSpeed(moveSpeed, Minecraft.getMinecraft().thePlayer.rotationYaw, Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe, Minecraft.getMinecraft().thePlayer.movementInput.moveForward);
    }

    public static void setSpeed(double moveSpeed, float yaw, double strafe, double forward) {
        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += ((forward > 0.0D) ? -45 : 45);
            } else if (strafe < 0.0D) {
                yaw += ((forward > 0.0D) ? 45 : -45);
            }
            strafe = 0.0D;
            if (forward > 0.0D) {
                forward = 1.0D;
            } else if (forward < 0.0D) {
                forward = -1.0D;
            }
        }
        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }
        double motionX = Math.cos(Math.toRadians((yaw + 90.0F)));
        double motionZ = Math.sin(Math.toRadians((yaw + 90.0F)));
        Minecraft.getMinecraft().thePlayer.motionX = forward * moveSpeed * motionX + strafe * moveSpeed * motionZ;
        Minecraft.getMinecraft().thePlayer.motionZ = forward * moveSpeed * motionZ - strafe * moveSpeed * motionX;
    }
}
