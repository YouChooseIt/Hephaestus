package io.github.qe7.managers.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion;
import io.github.qe7.Hephaestus;
import io.github.qe7.events.KeyPressEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.settings.api.Setting;
import io.github.qe7.features.impl.modules.impl.movement.FlightModule;
import io.github.qe7.features.impl.modules.impl.movement.StepModule;
import io.github.qe7.features.impl.modules.impl.render.HUDModule;
import io.github.qe7.managers.api.Manager;
import io.github.qe7.utils.config.FileUtil;
import lombok.Getter;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;

import java.lang.reflect.Field;
import java.util.*;

@Getter
public final class ModuleManager extends Manager<Class<? extends Module>, Module> implements Subscriber {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Map<Module, List<Setting<?>>> setting = new HashMap<>();

    public void initialize() {
        final List<Module> modules = new ArrayList<>();

        // Add modules to the list
        modules.add(new HUDModule());

        modules.add(new FlightModule());
        modules.add(new StepModule());

        // Register modules
        modules.forEach(this::register);

        this.loadModules();

        // Subscribe to the event bus for key press events
        Hephaestus.getInstance().getEventBus().subscribe(this);
    }

    /**
     * Register a module
     *
     * @param module the module to register
     */
    public void register(final Module module) {
        try {
            // Register the module
            this.getRegistry().putIfAbsent(module.getClass(), module);
            System.out.println("Registered module: " + module.getClass().getSimpleName());

            // Register settings for the module
            // Iterate through declared fields of the module class
            for (final Field declaredField : module.getClass().getDeclaredFields()) {
                // Check if the field is a Setting
                if (declaredField.getType().getSuperclass() == null) continue;
                if (!declaredField.getType().getSuperclass().equals(ManagementAssertion.Setting.class)) continue;

                // Make the field accessible
                declaredField.setAccessible(true);

                // Add the setting to the map (register it)
                this.addSetting(this.getRegistry().get(module.getClass()), (Setting<?>) declaredField.get(this.getRegistry().get(module.getClass())));
                System.out.println("Registered setting: " + declaredField.getName() + " for module: " + module.getClass().getSimpleName());
            }
        } catch (final Exception e) {
            System.out.println("Failed to register module: " + module.getClass().getSimpleName());
            System.out.println("Reason: " + e.getMessage());
        }
    }

    /**
     * Get all settings for a module
     *
     * @param module the module to get settings for
     * @return a list of settings for the specified module
     */
    public List<Setting<?>> getSettingsByModule(Module module) {
        return setting.getOrDefault(module, Collections.emptyList());
    }

    /**
     * Add a setting to a module
     *
     * @param feature  the module to which the setting belongs
     * @param property the setting to be added
     */
    public void addSetting(Module feature, Setting<?> property) {
        setting.putIfAbsent(feature, new ArrayList<>());
        setting.get(feature).add(property);
    }

    public void saveModules() {
        JsonObject jsonObject = new JsonObject();

        for (Module module : this.getRegistry().values()) {
            jsonObject.add(module.getName(), module.serialize());
        }

        FileUtil.writeFile("modules", GSON.toJson(jsonObject));
    }

    public void loadModules() {
        String config = FileUtil.readFile("modules");

        if (config == null) {
            return;
        }

        JsonObject jsonObject = GSON.fromJson(config, JsonObject.class);

        for (Module module : this.getRegistry().values()) {
            if (jsonObject.has(module.getName())) {
                try {
                    module.deserialize(jsonObject.getAsJsonObject(module.getName()));
                } catch (Exception e) {
                    System.out.println("Failed to load config for module: " + module.getName() + " - " + e.getMessage());
                }
            }
        }
    }

    @Subscribe
    public final Listener<KeyPressEvent> keyPressEventListener = new Listener<>(event -> {
        for (final Module module : this.getRegistry().values()) {
            if (module.getKeyBind() == event.getKeyCode()) {
                module.toggle();
            }
        }
    });
}
