package io.github.qe7.ui.click.components.propertyComponents;

import io.github.qe7.features.impl.modules.api.settings.impl.BooleanSetting;
import net.minecraft.src.Gui;

import java.awt.*;

public class BooleanComponent extends AbstractPropertyComponent {

    private final BooleanSetting setting;

    private int x, y;

    public BooleanComponent(int width, int height, BooleanSetting setting) {
        super(width, height);
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks, int x, int y) {
        this.x = x;
        this.y = y;

        this.fontRenderer.drawStringWithShadow(this.setting.getName(), x + 7, y + 3, -1);

        Gui.drawRect(x + width - 14, y + 3, x + width - 6, y + height - 3, this.setting.getValue() ? new Color(29, 34, 54, 255).getRGB() : new Color(21, 21, 21, 128).getRGB());
        
        Gui.drawRect(x + width - 14 - 0.5f, y + 3 - 0.5f, x + width - 6 + 0.5f, y + 3, new Color(0, 0, 0, 255).getRGB());
        Gui.drawRect(x + width - 14 - 0.5f, y + height - 3, x + width - 6 + 0.5f, y + height - 3 + 0.5f, new Color(0, 0, 0, 255).getRGB());
        Gui.drawRect(x + width - 14 - 0.5f, y + 3 - 0.5f, x + width - 14, y + height - 3 + 0.5f, new Color(0, 0, 0, 255).getRGB());
        Gui.drawRect(x + width - 6, y + 3 - 0.5f, x + width - 6 + 0.5f, y + height - 3 + 0.5f, new Color(0, 0, 0, 255).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (mouseX >= x + width - 14 && mouseX <= x + width - 6 && mouseY > y + 3 && mouseY <= y + height - 3) {
            this.setting.setValue(!this.setting.getValue());
        }
    }
}
