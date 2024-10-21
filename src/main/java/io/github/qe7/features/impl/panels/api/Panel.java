package io.github.qe7.features.impl.panels.api;

import com.google.gson.JsonObject;
import io.github.qe7.features.api.Feature;
import io.github.qe7.utils.RenderUtil;
import io.github.qe7.utils.config.Serialized;
import io.github.qe7.utils.math.MathUtil;
import lombok.Getter;
import lombok.Setter;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Gui;
import net.minecraft.src.ScaledResolution;

import java.awt.*;

@Getter
@Setter
public abstract class Panel extends Feature implements Subscriber, Serialized {

    protected float x, y, dragX, dragY;

    protected int width, height;

    protected final int titleHeight = 12;

    protected boolean enabled, dragging;

    protected FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    public Panel(String name, String description) {
        super(name, description);

        this.enabled = true;
    }

    public void drawPanel(float mouseX, float mouseY, ScaledResolution scaledResolution) {
        if (dragging) {
            this.x = MathUtil.clamp(mouseX - dragX, 0, scaledResolution.getScaledWidth() - width);
            this.y = MathUtil.clamp(mouseY - dragY, titleHeight, scaledResolution.getScaledHeight() - height);
        }

        Gui.drawRect(x, y - titleHeight, x + width, y, new Color(29, 34, 54, 255).getRGB());
        Gui.drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 150).getRGB());

        Gui.drawRect(x + width - 10, y - titleHeight + 2, x + width - 2, y - 2, this.isEnabled() ? new Color(0, 0, 0, 150).getRGB() : new Color(0, 0, 0, 50).getRGB());

        RenderUtil.renderFancyString(this.getName(), (int) x + 2, (int) y - titleHeight + 2, -1);
    }

    public void handleMouseClick(float mouseX, float mouseY, int mouseButton) {
        if (mouseButton != 0) {
            return;
        }

        if (!this.isMouseOverPanel(mouseX, mouseY)) {
            return;
        }

        if (mouseX >= x + width - 10 && mouseX <= x + width - 2 && mouseY >= y - titleHeight + 2 && mouseY <= y - 2) {
            this.setEnabled(!this.isEnabled());
        } else {
            dragging = !dragging;
            dragX = mouseX - x;
            dragY = mouseY - y;
        }
    }

    public void handleMouseRelease(float mouseX, float mouseY) {
        dragging = false;
    }

    public boolean isMouseOverPanel(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y - titleHeight && mouseY <= y + height;
    }

    @Override
    public JsonObject serialize() {
        final JsonObject object = new JsonObject();

        object.addProperty("x", x);
        object.addProperty("y", y);
        object.addProperty("enabled", enabled);

        return object;
    }

    @Override
    public void deserialize(JsonObject object) {
        x = object.get("x").getAsFloat();
        y = object.get("y").getAsFloat();
        enabled = object.get("enabled").getAsBoolean();
    }
}
