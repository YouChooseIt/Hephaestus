package io.github.qe7.features.impl.panels.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.panels.api.Panel;
import net.minecraft.src.ScaledResolution;

public class ModuleListPanel extends Panel {

    public ModuleListPanel() {
        super("ModuleList", "Displays enabled Modules");

        this.x = 10;
        this.y = 40;
    }

    @Override
    public void drawPanel(float mouseX, float mouseY, ScaledResolution scaledResolution) {
        super.drawPanel(mouseX, mouseY, scaledResolution);

        int y = (int) this.y + 2;
        int longest = 0;

        for (Module module : Hephaestus.getInstance().getModuleManager().getRegistry().values()) {
            if (module.isEnabled()) {
                this.fontRenderer.drawStringWithShadow(module.getName(), (int) this.x + 2, y, module.getCategory().getColor().getRGB());
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
