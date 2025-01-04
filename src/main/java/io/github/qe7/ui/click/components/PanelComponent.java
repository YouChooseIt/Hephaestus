package io.github.qe7.ui.click.components;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.managers.impl.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PanelComponent {

    private final List<ButtonComponent> buttonComponentList = new ArrayList<>();

    private final ModuleCategory moduleCategory;

    private final FontRenderer fontRenderer;

    private final int width, height;

    private int x, y, dragX, dragY, totalHeight;

    private boolean dragging, open;

    public PanelComponent(ModuleCategory moduleCategory, int width, int height, int x, int y) {
        this.moduleCategory = moduleCategory;
        this.width = width;
        this.height = height;
        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
        this.x = x;
        this.y = y;

        for (Module module : Hephaestus.getInstance().getModuleManager().getRegistry().values().stream().sorted(Comparator.comparing(Module::getName)).collect(Collectors.toList())) {
            if (module.getCategory() == moduleCategory) {
                buttonComponentList.add(new ButtonComponent(module, width, height));
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }

        Gui.drawRect(x, y, x + width, y + height, new Color(29, 34, 54, 255).getRGB());
        Gui.drawRect(x - 0.5f, y - 0.5f, x + width + 0.5f, y, new Color(0, 0, 0, 255).getRGB());
        Gui.drawRect(x - 0.5f, y + height, x + width + 0.5f, y + height + 0.5f, new Color(0, 0, 0, 255).getRGB());
        Gui.drawRect(x - 0.5f, y + totalHeight, x + width + 0.5f, y + totalHeight + 0.5f, new Color(0, 0, 0, 255).getRGB());
        Gui.drawRect(x - 0.5f, y - 0.5f, x, y + totalHeight + 0.5f, new Color(0, 0, 0, 255).getRGB());
        Gui.drawRect(x + width, y - 0.5f, x + width + 0.5f, y + totalHeight + 0.5f, new Color(0, 0, 0, 255).getRGB());

        this.fontRenderer.drawStringWithShadow(moduleCategory.getName(), x + 3, y + 3, -1);

        Gui.drawRect(x, y + height, x + width, y + totalHeight + 0.5f, new Color(0, 0, 0, 100).getRGB());

        totalHeight = height + 2;

        if (!open) {
            return;
        }

        for (ButtonComponent buttonComponent : buttonComponentList) {
            buttonComponent.drawScreen(mouseX, mouseY, partialTicks, x, y + totalHeight);
            totalHeight += buttonComponent.getHeight();
        }

        totalHeight += 2;
    }

    public void keyTyped(char c, int keyTyped) {
        if (!open) {
            return;
        }

        for (ButtonComponent buttonComponent : buttonComponentList) {
            buttonComponent.keyTyped(c, keyTyped);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            switch (button) {
                case 0:
                    dragging = true;
                    dragX = mouseX - x;
                    dragY = mouseY - y;
                    break;
                case 1:
                    open = !open;
                    break;
            }
        }

        if (!open) {
            return;
        }

        for (ButtonComponent buttonComponent : buttonComponentList) {
            buttonComponent.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        if (dragging) {
            dragging = false;
        }

        if (!open) {
            return;
        }

        for (ButtonComponent buttonComponent : buttonComponentList) {
            buttonComponent.mouseReleased(mouseX, mouseY, button);
        }
    }

    public boolean isChildFocused() {
        for (ButtonComponent buttonComponent : buttonComponentList) {
            if (buttonComponent.isChildFocused()) {
                return true;
            }
        }
        return false;
    }
}
