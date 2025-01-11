package io.github.qe7.ui.click.components;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.settings.api.Setting;
import io.github.qe7.features.impl.modules.api.settings.impl.BooleanSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.DoubleSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.EnumSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.IntSetting;
import io.github.qe7.ui.click.components.propertyComponents.*;
import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ButtonComponent {

    private final List<AbstractPropertyComponent> propertyComponentList = new ArrayList<>();

    private final Module module;

    private final FontRenderer fontRenderer;

    private final int width, height;

    private int x, y, totalHeight;

    private boolean open = false;

    public ButtonComponent(Module module, int width, int height) {
        this.module = module;
        this.width = width;
        this.height = height;
        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;

        propertyComponentList.add(new DescriptionComponent(width, height, module));

        propertyComponentList.add(new KeyBindComponent(width, height, module));

        for (Setting<?> setting : Hephaestus.getInstance().getModuleManager().getSettingsByModule(module)) {
            if (setting instanceof BooleanSetting) {
                propertyComponentList.add(new BooleanComponent(width, height, (BooleanSetting) setting));
            }
            if (setting instanceof EnumSetting) {
                propertyComponentList.add(new EnumComponent(width, height, (EnumSetting<?>) setting));
            }
            if (setting instanceof DoubleSetting) {
                propertyComponentList.add(new DoubleComponent(width, height, (DoubleSetting) setting));
            }
            if (setting instanceof IntSetting) {
                propertyComponentList.add(new IntComponent(width, height, (IntSetting) setting));
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks, int x, int y) {
        this.x = x;
        this.y = y;

        this.fontRenderer.drawStringWithShadow(module.getName(), x + 5, y + 3, this.module.isEnabled() ? new Color(243, 243, 243, 255).getRGB() : new Color(103, 103, 103, 255).getRGB());
        this.fontRenderer.drawStringWithShadow(this.open ? "-" : "+", x + width - fontRenderer.getStringWidth(this.open ? "-" : "+") - 5, y + 3,  this.module.isEnabled() ? new Color(243, 243, 243, 255).getRGB() : new Color(103, 103, 103, 255).getRGB());

        totalHeight = height;

        if (!this.open) {
            return;
        }

        for (AbstractPropertyComponent abstractPropertyComponent : propertyComponentList) {
            abstractPropertyComponent.drawScreen(mouseX, mouseY, partialTicks, x, y + totalHeight);
            totalHeight += abstractPropertyComponent.getHeight();
        }

        Gui.drawRect(x + 2, y + height, x + 3, y + totalHeight, -1);
    }

    public void keyTyped(char c, int keyTyped) {
        if (!this.open) {
            return;
        }

        for (AbstractPropertyComponent abstractPropertyComponent : propertyComponentList) {
            abstractPropertyComponent.keyTyped(c, keyTyped);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + height) {
            switch (button) {
                case 0:
                    this.module.setEnabled(!this.module.isEnabled());
                    break;
                case 1:
                    this.open = !this.open;
                    break;
            }
        }

        if (!this.open) {
            return;
        }

        for (AbstractPropertyComponent abstractPropertyComponent : propertyComponentList) {
            abstractPropertyComponent.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        if (!this.open) {
            return;
        }

        for (AbstractPropertyComponent abstractPropertyComponent : propertyComponentList) {
            abstractPropertyComponent.mouseReleased(mouseX, mouseY, button);
        }
    }

    public int getHeight() {
        return totalHeight;
    }

    public boolean isChildFocused() {
        for (AbstractPropertyComponent abstractPropertyComponent : propertyComponentList) {
            if (abstractPropertyComponent.isFocused()) {
                return true;
            }
        }
        return false;
    }
}
