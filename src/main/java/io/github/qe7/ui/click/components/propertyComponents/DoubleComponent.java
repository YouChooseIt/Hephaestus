package io.github.qe7.ui.click.components.propertyComponents;

import io.github.qe7.features.impl.modules.api.settings.impl.DoubleSetting;
import net.minecraft.src.Gui;

import java.awt.*;

public class DoubleComponent extends AbstractPropertyComponent {

    private final DoubleSetting setting;

    private int x, y;

    private boolean dragging = false;

    public DoubleComponent(int width, int height, DoubleSetting setting) {
        super(width, height);
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks, int x, int y) {
        this.x = x;
        this.y = y;

        this.fontRenderer.drawStringWithShadow(this.setting.getName(), x + 7, y + 3, -1);

        this.fontRenderer.drawStringWithShadow(String.format("%.1f", setting.getValue()), x + width - this.fontRenderer.getStringWidth(String.format("%.1f", setting.getValue())) - 6, y + 3, -1);

        if (dragging) {
            updateSlider(mouseX);
        }

        int sliderY = y + height - 2;
        int sliderWidth = (int) ((width - 10) * (setting.getValue() - setting.getMinimum()) / (setting.getMaximum() - setting.getMinimum()));

        Gui.drawRect(x + 5, sliderY, x + width - 5, sliderY + 2, new java.awt.Color(100, 100, 100).getRGB());

        Gui.drawRect(x + 5, sliderY, x + 5 + sliderWidth, sliderY + 2, new Color(29, 34, 54, 255).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (button == 0) {
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                dragging = true;
                updateSlider(mouseX);
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);

        if (dragging) {
            dragging = false;
        }
    }

    private void updateSlider(int mouseX) {
        double relativeMouseX = Math.min(Math.max(mouseX - x - 5, 0), width - 10);
        double newValue = setting.getMinimum() + (relativeMouseX / (float)(width - 10)) * (setting.getMaximum() - setting.getMinimum());

        // round to nearest 0.1
        newValue = Math.round(newValue * 10.0) / 10.0;

        if (newValue < setting.getMinimum()) {
            newValue = setting.getMinimum();
        } else if (newValue > setting.getMaximum()) {
            newValue = setting.getMaximum();
        }

        setting.setValue(newValue);
    }
}
