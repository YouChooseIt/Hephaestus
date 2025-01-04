package io.github.qe7.features.impl.panels.impl;

import io.github.qe7.features.impl.panels.api.Panel;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public final class ArmourHudPanel extends Panel {

    public ArmourHudPanel() {
        super("ArmourHud", "Displays your armor on the screen");

        this.x = 10;
        this.y = 40;
    }

    @Override
    public void drawPanel(float mouseX, float mouseY, ScaledResolution scaledResolution) {
        super.drawPanel(mouseX, mouseY, scaledResolution);

        int x = (int) this.x + 2;
        int y = (int) this.y + 2;

        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_LIGHTING);

        ItemStack helmet = Minecraft.getMinecraft().thePlayer.inventory.armorInventory[3];
        ItemStack chest = Minecraft.getMinecraft().thePlayer.inventory.armorInventory[2];
        ItemStack legs = Minecraft.getMinecraft().thePlayer.inventory.armorInventory[1];
        ItemStack boots = Minecraft.getMinecraft().thePlayer.inventory.armorInventory[0];

        ItemStack[] armor = {helmet, chest, legs, boots};

        for (ItemStack itemStack : armor) {
            if (itemStack != null) {
                GuiInGame.itemRenderer.renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, itemStack, x, y);
                GuiInGame.itemRenderer.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, itemStack, x, y);
                x += 20;
            }
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        this.height = y - (int) this.y + 20;
        this.width = 80;
    }
}
