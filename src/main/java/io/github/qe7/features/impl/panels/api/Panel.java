package io.github.qe7.features.impl.panels.api;

import io.github.qe7.features.api.Feature;
import lombok.Getter;
import lombok.Setter;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Gui;

import java.awt.*;

@Getter
@Setter
public abstract class Panel extends Feature implements Subscriber {

    protected float x, y, mouseX, mouseY, dragX, dragY;

    protected int width, height;

    protected final int titleHeight = 12;

    protected boolean enabled, dragging, shouldHandleMouse;

    protected FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    public Panel(String name, String description) {
        super(name, description);

        this.enabled = true;
    }

    public void drawPanel(float mouseX, float mouseY) {
        Gui.drawRect(x, y - titleHeight, x + width, y, new Color(29, 34, 54, 200).getRGB());
        Gui.drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 150).getRGB());

        Gui.drawRect(x + width - 10, y - titleHeight + 2, x + width - 2, y - 2, new Color(0, 0, 0, 150).getRGB());

        this.fontRenderer.drawStringWithShadow(this.getName(), (int) x + 2, (int) y - titleHeight + 2, -1);
    }

    public boolean isMouseOverPanel() {
        return mouseX >= x && mouseX <= x + width && mouseY >= y - titleHeight && mouseY <= y + height;
    }
}
