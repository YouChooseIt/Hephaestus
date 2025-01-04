package io.github.qe7.features.impl.panels.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.panels.api.Panel;
import io.github.qe7.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ScaledResolution;

import java.util.Objects;

public final class OnlinePlayersPanel extends Panel {

    public OnlinePlayersPanel() {
        super("OnlinePlayers", "Displays online players");

        this.x = 200;
        this.y = 40;
    }

    @Override
    public void drawPanel(float mouseX, float mouseY, ScaledResolution scaledResolution) {
        super.drawPanel(mouseX, mouseY, scaledResolution);

        int y = 0;

        for (String player : Hephaestus.getInstance().getPlayerManager().getOnlinePlayers()) {
            RenderUtil.renderFancyString(Objects.equals(player, Minecraft.getMinecraft().session.username) ? "§6" + player : player, this.x + 2, this.y + 2 + y, 0xFFFFFF);
            y += 10;
        }

        this.height = Math.max(10, y + 2);
        this.width = 100;
    }
}
