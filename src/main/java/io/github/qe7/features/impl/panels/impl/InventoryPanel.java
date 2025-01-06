package io.github.qe7.features.impl.panels.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.modules.impl.exploit.Slot9Module;
import io.github.qe7.features.impl.panels.api.Panel;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiInGame;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.ScaledResolution;
import org.lwjgl.opengl.GL11;

public final class InventoryPanel extends Panel {

    public InventoryPanel() {
        super("Inventory", "Displays your inventory on screen");

        this.x = 10;
        this.y = 40;
    }

    @Override
    public void drawPanel(float mouseX, float mouseY, ScaledResolution scaledResolution) {
        super.drawPanel(mouseX, mouseY, scaledResolution);

        int startX = (int) this.x + 2;
        int startY = (int) this.y + 2;

        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_LIGHTING);

        int x = startX;
        int y = startY;

        for (int i = 9; i < 36; i++) {
            ItemStack itemStack = Minecraft.getMinecraft().thePlayer.inventory.mainInventory[i];

            if (i == 9 && Hephaestus.getInstance().getModuleManager().getRegistry().get(Slot9Module.class).isEnabled()) {
                GuiInGame.drawRect(x, y, x + 17, y + 17, new java.awt.Color(150, 150, 0, 100).getRGB());
            }

            if (itemStack != null) {
                GuiInGame.itemRenderer.renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, itemStack, x, y);
                GuiInGame.itemRenderer.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, itemStack, x, y);
            }
            x += 20;
            if ((i - 8) % 9 == 0) {
                x = startX;
                y += 20;
            }
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        this.height = y - (int) this.y;
        this.width = 20 * 9;
    }
}
