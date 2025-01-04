package io.github.qe7.ui.click;

import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.ui.click.components.PanelComponent;
import net.minecraft.src.GuiScreen;

import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends GuiScreen {

    private final List<PanelComponent> panelComponentList = new ArrayList<>();

    public ClickGUI() {
        int x = 20;
        int y = 20;

        int width = 140;
        int height = 14;

        for (ModuleCategory moduleCategory : ModuleCategory.values()) {
            panelComponentList.add(new PanelComponent(moduleCategory, width, height, x, y));
            y += height + 5;
        }
    }

    @Override
    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);

        for (PanelComponent panelComponent : panelComponentList) {
            panelComponent.drawScreen(i, j, f);
        }
    }

    @Override
    protected void keyTyped(char c, int i) {
        for (PanelComponent panelComponent : panelComponentList) {
            panelComponent.keyTyped(c, i);
        }

        for (PanelComponent panelComponent : panelComponentList) {
            if (panelComponent.isChildFocused()) {
                return;
            }
        }

        super.keyTyped(c, i);
    }

    @Override
    protected void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);

        for (PanelComponent panelComponent : panelComponentList) {
            panelComponent.mouseClicked(i, j, k);
        }
    }

    @Override
    protected void mouseReleased(int i, int j, int k) {
        super.mouseReleased(i, j, k);

        for (PanelComponent panelComponent : panelComponentList) {
            panelComponent.mouseReleased(i, j, k);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
