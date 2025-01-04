package io.github.qe7.ui.editor;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.panels.api.Panel;
import net.minecraft.src.GuiScreen;

public class GuiHudEditor extends GuiScreen {

    @Override
    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);

        this.fontRenderer.drawStringWithShadow("Hud Editor", this.width / 2 - fontRenderer.getStringWidth("Hud Editor") / 2, 14, -1);
    }

    @Override
    protected void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);

        for (Panel panel : Hephaestus.getInstance().getPanelManager().getRegistry().values()) {
            boolean cancel = panel.handleMouseClick(i, j, k);
            if(cancel) break;
        }
    }

    @Override
    protected void mouseReleased(int i, int j, int k) {
        super.mouseReleased(i, j, k);

        for (Panel panel : Hephaestus.getInstance().getPanelManager().getRegistry().values()) {
            panel.handleMouseRelease(i, j);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
