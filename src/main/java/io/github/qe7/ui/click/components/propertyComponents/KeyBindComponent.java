package io.github.qe7.ui.click.components.propertyComponents;

import io.github.qe7.features.impl.modules.api.Module;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Gui;
import org.lwjgl.input.Keyboard;

public class KeyBindComponent extends AbstractPropertyComponent {

    private final Module module;

    private boolean isListening = false;

    private int x, y;

    public KeyBindComponent(int width, int height, Module module) {
        super(width, height);
        this.module = module;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks, int x, int y) {
        this.x = x;
        this.y = y;

        String display = this.isListening ? "Listening..." : "KeyBind: ";

        this.fontRenderer.drawStringWithShadow(display, x + 7, y + 3, -1);

        if (this.isListening) {
            return;
        }

        this.fontRenderer.drawStringWithShadow(Keyboard.getKeyName(this.module.getKeyBind()), x + width - this.fontRenderer.getStringWidth(Keyboard.getKeyName(this.module.getKeyBind())) - 6, y + 3, -1);
    }

    @Override
    public void keyTyped(char c, int keyTyped) {
        super.keyTyped(c, keyTyped);

        if (this.isListening) {
            if (keyTyped == 1) {
                this.isListening = false;
                return;
            }
            if (keyTyped == this.module.getKeyBind()) {
                this.isListening = false;
                return;
            }
            if (keyTyped == Keyboard.KEY_BACK) {
                this.module.setKeyBind(0);
                this.isListening = false;
                return;
            }
            this.module.setKeyBind(keyTyped);
            this.isListening = false;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height && button == 0) {
            this.isListening = !this.isListening;
        }
    }

    @Override
    public boolean isFocused() {
        return this.isListening;
    }
}
