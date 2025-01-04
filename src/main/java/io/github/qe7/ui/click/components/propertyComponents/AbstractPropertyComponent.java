package io.github.qe7.ui.click.components.propertyComponents;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;

public abstract class AbstractPropertyComponent {

    protected final FontRenderer fontRenderer;

    protected int width;
    @Getter
    protected int height;

    public AbstractPropertyComponent(int width, int height) {
        this.width = width;
        this.height = height;
        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
    }

    public abstract void drawScreen(int mouseX, int mouseY, float partialTicks, int x, int y);

    public void keyTyped(char c, int keyTyped) {

    }

    public void mouseClicked(int mouseX, int mouseY, int button) {

    }

    public void mouseReleased(int mouseX, int mouseY, int button) {

    }

    public boolean isFocused() {
        return false;
    }
}
