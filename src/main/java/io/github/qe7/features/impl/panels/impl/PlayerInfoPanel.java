package io.github.qe7.features.impl.panels.impl;

import io.github.qe7.features.impl.panels.api.Panel;
import io.github.qe7.utils.RenderUtil;
import io.github.qe7.utils.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ScaledResolution;

public final class PlayerInfoPanel extends Panel {

    public PlayerInfoPanel() {
        super("PlayerInfo", "Displays information about the player");

        this.x = 150;
        this.y = 10;
    }

    @Override
    public void drawPanel(float mouseX, float mouseY, ScaledResolution scaledResolution) {
        super.drawPanel(mouseX, mouseY, scaledResolution);

        if (Minecraft.getMinecraft().thePlayer == null) {
            return;
        }

        float x = this.x + 2;
        float y = this.y + 2;

        final String[] strings = new String[]{
                "Name: " + Minecraft.getMinecraft().thePlayer.username,
                "Health: " + Minecraft.getMinecraft().thePlayer.health,
                "Armor: " + Minecraft.getMinecraft().thePlayer.getPlayerArmorValue(),
                "Item: " + (Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() != null ? Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItem().getStatName() : "N/a"),
                "Pitch: " + MathUtil.round(Minecraft.getMinecraft().thePlayer.rotationPitch, 2),
                "Yaw: " + MathUtil.round(MathUtil.playerYaw180(), 2),
        };

        float longest = 100;

        for (String string : strings) {
            RenderUtil.renderFancyString(string, x, y, 0xFFFFFF);
            longest = Math.max(longest, this.getFontRenderer().getStringWidth(string) + 4);
            y += 10;
        }

        this.height = (int) (y - this.y + 2);
        this.width = (int) (longest);
    }
}
