package io.github.qe7.features.impl.panels.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.panels.api.Panel;

public class ModuleListPanel extends Panel {

    public ModuleListPanel() {
        super("ModuleList", "Displays enabled Modules");

        this.x = 10;
        this.y = 40;

        this.width = 100;
        this.height = 100;
    }

    @Override
    public void drawPanel(float mouseX, float mouseY) {
        super.drawPanel(mouseX, mouseY);

        fontRenderer.drawString("mouse pos: " + mouseX + ", " + mouseY, (int) x + 2, (int) y + 2, -1);

        int y = (int) this.y + 2;
        int longest = 0;

        for (Module module : Hephaestus.getInstance().getModuleManager().getRegistry().values()) {
            if (module.isEnabled()) {
                this.fontRenderer.drawStringWithShadow(module.getName(), (int) this.x + 2, y, -1);
                if (this.fontRenderer.getStringWidth(module.getName()) > longest) {
                    longest = this.fontRenderer.getStringWidth(module.getName());
                }
                y += 10;
            }
        }

        this.height = Math.max(10, y - (int) this.y);
        this.width = Math.max(100, longest + 4);
    }
}
