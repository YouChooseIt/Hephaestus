package io.github.qe7.managers.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.qe7.Hephaestus;
import io.github.qe7.events.render.RenderScreenEvent;
import io.github.qe7.features.impl.panels.api.Panel;
import io.github.qe7.features.impl.panels.impl.*;
import io.github.qe7.managers.api.Manager;
import io.github.qe7.ui.click.ClickGUI;
import io.github.qe7.ui.editor.GuiHudEditor;
import io.github.qe7.utils.config.FileUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public final class PanelManager extends Manager<Class<? extends Panel>, Panel> implements Subscriber {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public void initialize() {
        final List<Panel> panels = new ArrayList<>();

        panels.add(new ClientInfoPanel());
        panels.add(new ModuleListPanel());
        panels.add(new OnlinePlayersPanel());
        panels.add(new ArmourHudPanel());
        panels.add(new PositionPanel());
        panels.add(new PlayerInfoPanel());
        panels.add(new ServerInfoPanel());
        panels.add(new InventoryPanel());

        panels.forEach(this::register);

        this.loadPanels();

        Hephaestus.getInstance().getEventBus().subscribe(this);
    }

    public void register(final Panel panel) {
        this.getRegistry().putIfAbsent(panel.getClass(), panel);

        Hephaestus.getInstance().getEventBus().subscribe(panel);

        System.out.println("Registered panel: " + panel.getClass().getSimpleName());
    }

    public void savePanels() {
        final JsonObject jsonObject = new JsonObject();

        for (Panel panel : this.getRegistry().values()) {
            jsonObject.add(panel.getName(), panel.serialize());
        }

        FileUtil.writeFile("panels", GSON.toJson(jsonObject));
    }

    public void loadPanels() {
        final String config = FileUtil.readFile("panels");

        if (config == null) {
            return;
        }

        final JsonObject jsonObject = GSON.fromJson(config, JsonObject.class);

        for (Panel panel : this.getRegistry().values()) {
            if (jsonObject.has(panel.getName())) {
                panel.deserialize(jsonObject.getAsJsonObject(panel.getName()));
            }
        }
    }

    @Subscribe
    public final Listener<RenderScreenEvent> renderScreenEventListener = new Listener<>(event -> {
    	ArrayList<Panel> list = new ArrayList<>(this.getRegistry().values());
    	for(int i = list.size() - 1; i >= 0; --i) {
    		Panel panel = list.get(i);
    		if (panel.isEnabled() || (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiHudEditor)) {
                panel.drawPanel(event.getMouseX(), event.getMouseY(), event.getScaledResolution());
            }
    	}
    });
}
