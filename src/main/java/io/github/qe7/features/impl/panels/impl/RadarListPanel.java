package io.github.qe7.features.impl.panels.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.panels.api.Panel;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ScaledResolution;

public class RadarListPanel extends Panel {

    public RadarListPanel() {
        super("Radar List", "Displays a list of players around you.");

        this.width = 100;
        this.height = 100;

        this.x = 100;
        this.y = 100;
    }

    @Override
    public void drawPanel(float mouseX, float mouseY, ScaledResolution scaledResolution) {
        super.drawPanel(mouseX, mouseY, scaledResolution);

        for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (entity != Minecraft.getMinecraft().thePlayer && entity instanceof EntityPlayer) {
                this.fontRenderer.drawStringWithShadow(((EntityPlayer) entity).username, (int) this.x + 2, (int) this.y, -1);
                y += 10;
            }
        }
    }
}
