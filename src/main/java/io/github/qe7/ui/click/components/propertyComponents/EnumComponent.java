package io.github.qe7.ui.click.components.propertyComponents;

import io.github.qe7.features.impl.modules.api.settings.impl.EnumSetting;
import net.minecraft.src.Gui;

public class EnumComponent extends AbstractPropertyComponent {

    private final EnumSetting<?> setting;

    private int x, y;

    public EnumComponent(int width, int height, EnumSetting<?> setting) {
        super(width, height);
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks, int x, int y) {
        this.x = x;
        this.y = y;

        this.fontRenderer.drawStringWithShadow(this.setting.getName(), x + 7, y + 3, -1);

        this.fontRenderer.drawStringWithShadow(this.setting.getValue().getName(), x + width - this.fontRenderer.getStringWidth(this.setting.getValue().getName()) - 6, y + 3, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            switch (button) {
                case 0:
                    this.setting.cycleForward();
                    break;
                case 1:
                    this.setting.cycleBackward();
                    break;
            }
        }
    }
}
