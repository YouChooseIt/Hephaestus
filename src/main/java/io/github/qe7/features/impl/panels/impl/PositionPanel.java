package io.github.qe7.features.impl.panels.impl;

import io.github.qe7.features.impl.panels.api.Panel;
import io.github.qe7.utils.RenderUtil;
import io.github.qe7.utils.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ScaledResolution;

public class PositionPanel extends Panel {

    public PositionPanel() {
        super("Position", "Displays the coordinates of the player");

        this.x = 100;
        this.y = 100;
    }

    @Override
    public void drawPanel(float mouseX, float mouseY, ScaledResolution scaledResolution) {
        super.drawPanel(mouseX, mouseY, scaledResolution);

        if (Minecraft.getMinecraft().thePlayer == null) {
            return;
        }

        if (Minecraft.getMinecraft().theWorld == null) {
            return;
        }

        if (Minecraft.getMinecraft().theWorld.worldProvider == null) {
            return;
        }

        double[] overWorldPos = getOverWorldPos(Minecraft.getMinecraft().theWorld.worldProvider.isHellWorld);
        double[] netherPos = getNetherPos(Minecraft.getMinecraft().theWorld.worldProvider.isHellWorld);

        float x = this.x + 2;
        float y = this.y + 2;

        RenderUtil.renderFancyString("Overworld: " + String.format("%.1f", overWorldPos[0]) + ", " + String.format("%.1f", overWorldPos[1]) + ", " + String.format("%.1f", overWorldPos[2]), x, y, 0xFFFFFF);
        RenderUtil.renderFancyString("Nether: " + String.format("%.1f", netherPos[0]) + ", " + String.format("%.1f", netherPos[1]) + ", " + String.format("%.1f", netherPos[2]), x, y + 10, 0xFFFFFF);

        this.width = Math.max(this.getFontRenderer().getStringWidth("Overworld: " + String.format("%.1f", overWorldPos[0]) + ", " + String.format("%.1f", overWorldPos[1]) + ", " + String.format("%.1f", overWorldPos[2])), this.getFontRenderer().getStringWidth("Nether: " + String.format("%.1f", netherPos[0]) + ", " + String.format("%.1f", netherPos[1]) + ", " + String.format("%.1f", netherPos[2]))) + 4;
        this.height = 22;
    }

    private double[] getOverWorldPos(boolean isNether) {
        Minecraft mc = Minecraft.getMinecraft();

        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        if (isNether) {
            x *= 8;
            z *= 8;
        }

        return new double[] {MathUtil.round(x, 2), MathUtil.round(y, 2), MathUtil.round(z, 2)};
    }

    private double[] getNetherPos(boolean isNether) {
        Minecraft mc = Minecraft.getMinecraft();

        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        if (!isNether) {
            x /= 8;
            z /= 8;
        }

        return new double[] {MathUtil.round(x, 2), MathUtil.round(y, 2), MathUtil.round(z, 2)};
    }
}
