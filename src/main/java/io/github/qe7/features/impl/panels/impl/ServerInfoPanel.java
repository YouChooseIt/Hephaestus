package io.github.qe7.features.impl.panels.impl;

import io.github.qe7.features.impl.panels.api.Panel;
import io.github.qe7.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ScaledResolution;

public class ServerInfoPanel extends Panel {

    public ServerInfoPanel() {
        super("ServerInfo", "Displays information about the server");
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
                "IP: " + (Minecraft.getMinecraft().theWorld.multiplayerWorld ? Minecraft.getMinecraft().gameSettings.lastServer.replace("_", ":") : "N/a"),
                "Seed: " + Minecraft.getMinecraft().theWorld.getRandomSeed(),
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
