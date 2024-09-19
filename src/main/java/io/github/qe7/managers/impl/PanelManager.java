package io.github.qe7.managers.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.events.render.RenderScreenEvent;
import io.github.qe7.features.impl.panels.api.Panel;
import io.github.qe7.features.impl.panels.impl.ModuleListPanel;
import io.github.qe7.managers.api.Manager;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiChat;

import java.util.ArrayList;
import java.util.List;

public final class PanelManager extends Manager<Class<? extends Panel>, Panel> implements Subscriber {

    public void initialize() {
        final List<Panel> panels = new ArrayList<>();

        // Add panels to the list
        panels.add(new ModuleListPanel());

        // Register panels
        panels.forEach(this::register);

        Hephaestus.getInstance().getEventBus().subscribe(this);
    }

    public void register(final Panel panel) {
        this.getRegistry().putIfAbsent(panel.getClass(), panel);

        Hephaestus.getInstance().getEventBus().subscribe(panel);

        System.out.println("Registered panel: " + panel.getClass().getSimpleName());
    }

    @Subscribe
    public final Listener<RenderScreenEvent> renderScreenEventListener = new Listener<>(event -> {
        this.getRegistry().values().forEach(panel -> {
            if (panel.isEnabled() || (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
                panel.drawPanel(event.getMouseX(), event.getMouseY());
            }
        });
    });
}
