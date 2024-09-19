package io.github.qe7.features.impl.modules.impl.render;

import io.github.qe7.Hephaestus;
import io.github.qe7.events.render.RenderScreenEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;

public class HUDModule extends Module {

    public HUDModule() {
        super("HUD", "Displays information related to the client", ModuleCategory.RENDER);

        if (!this.isEnabled()) {
            this.setEnabled(true);
        }
    }

    @Subscribe
    public final Listener<RenderScreenEvent> renderScreenEventListener = new Listener<>(event -> {
        final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        fontRenderer.drawStringWithShadow(Hephaestus.getInstance().getName(), 2, 2, -1);
    });
}
