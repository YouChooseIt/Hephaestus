package io.github.qe7.features.impl.panels.impl;

import io.github.qe7.features.impl.panels.api.Panel;
import io.github.qe7.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ScaledResolution;

public class PlayerInfoPanel extends Panel {

    public PlayerInfoPanel() {
        super("PlayerInfo", "Displays Player Info");

        this.x = 200;
        this.y = 40;
    }

    @Override
    public void drawPanel(float mouseX, float mouseY, ScaledResolution scaledResolution) {
        super.drawPanel(mouseX, mouseY, scaledResolution);

        RenderUtil.renderFancyString("Welcome, " + Minecraft.getMinecraft().thePlayer.username, (int) this.x + 2, (int) this.y + 2, -1);
        RenderUtil.renderFancyString("Health: " + Minecraft.getMinecraft().thePlayer.health, (int) this.x + 2, (int) this.y + 12, -1);
        RenderUtil.renderFancyString("X: " + ((int) Minecraft.getMinecraft().thePlayer.posX), (int) this.x + 2, (int) this.y + 22, -1);
        RenderUtil.renderFancyString("Y: " + ((int) Minecraft.getMinecraft().thePlayer.posY), (int) this.x + 2, (int) this.y + 32, -1);
        RenderUtil.renderFancyString("Z: " + ((int) Minecraft.getMinecraft().thePlayer.posZ), (int) this.x + 2, (int) this.y + 42, -1);

        this.height = 52;
        this.width = 100;
    }
}
