package io.github.qe7.features.impl.panels.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.api.Feature;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.panels.api.Panel;
import io.github.qe7.utils.RenderUtil;
import net.minecraft.src.ScaledResolution;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleListPanel extends Panel {

    public ModuleListPanel() {
        super("ModuleList", "Displays enabled Modules");

        this.x = 10;
        this.y = 40;
    }

    @Override
    public void drawPanel(float mouseX, float mouseY, ScaledResolution scaledResolution) {
        super.drawPanel(mouseX, mouseY, scaledResolution);

        final List<Module> modules = Hephaestus.getInstance().getModuleManager().getRegistry().values().stream().sorted(
                Comparator.comparing(Feature::getName)
        ).collect(Collectors.toList());

        int y = (int) this.y + 2;
        int longest = 0;

        for (Module module : modules) {
            if (module.isEnabled()) {
                RenderUtil.renderFancyString(module.getName(), (int) this.x + 2, y, module.getCategory().getColor().getRGB());
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
