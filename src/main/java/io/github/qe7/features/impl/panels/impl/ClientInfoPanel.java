package io.github.qe7.features.impl.panels.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.panels.api.Panel;
import io.github.qe7.utils.RenderUtil;
import net.minecraft.src.ScaledResolution;

public class ClientInfoPanel extends Panel {

    public ClientInfoPanel() {
        super("ClientInfo", "Information related to the client");

        this.x = 10;
        this.y = 10;
    }

    @Override
    public void drawPanel(float mouseX, float mouseY, ScaledResolution scaledResolution) {
        super.drawPanel(mouseX, mouseY, scaledResolution);

        float x = this.x + 2;
        float y = this.y + 2;

        float longest = 100;

        final String[] strings = new String[]{
                Hephaestus.getInstance().getName(),
                "Version: " + Hephaestus.getInstance().getVersion(),
        };

        for (String string : strings) {
            RenderUtil.renderFancyString(string, x, y, 0xFFFFFF);
            longest = Math.max(longest, this.getFontRenderer().getStringWidth(string) + 4);
            y += 10;
        }

        this.height = (int) (y - this.y + 2);
        this.width = (int) (longest);
    }
}
