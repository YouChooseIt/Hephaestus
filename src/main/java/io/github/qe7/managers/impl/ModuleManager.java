package io.github.qe7.managers.impl;

import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion;
import io.github.qe7.Hephaestus;
import io.github.qe7.events.KeyPressEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.settings.api.Setting;
import io.github.qe7.features.impl.modules.impl.movement.FlightModule;
import io.github.qe7.features.impl.modules.impl.render.HUDModule;
import io.github.qe7.managers.api.Manager;
import lombok.Getter;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;

import java.lang.reflect.Field;
import java.util.*;

@Getter
public final class ModuleManager extends Manager<Class<? extends Module>, Module> implements Subscriber {

    private final Map<Module, List<Setting<?>>> setting = new HashMap<>();

    public void initialize() {
        final List<Module> modules = new ArrayList<>();

        modules.add(new HUDModule());
        modules.add(new FlightModule());

        modules.forEach(this::register);
        Hephaestus.getInstance().getEventBus().subscribe(this);
    }

    public void register(final Module module) {
        try {
            this.getRegistry().putIfAbsent(module.getClass(), module);
            System.out.println("Registered module: " + module.getClass().getSimpleName());

            for (final Field declaredField : module.getClass().getDeclaredFields()) {
                if (declaredField.getType().getSuperclass() == null) continue;
                if (!declaredField.getType().getSuperclass().equals(ManagementAssertion.Setting.class)) continue;

                declaredField.setAccessible(true);

                this.addSetting(this.getRegistry().get(module.getClass()), (Setting<?>) declaredField.get(this.getRegistry().get(module.getClass())));
                System.out.println("Registered setting: " + declaredField.getName() + " for module: " + module.getClass().getSimpleName());
            }
        } catch (final Exception e) {
            System.out.println("Failed to register module: " + module.getClass().getSimpleName());
            System.out.println("Reason: " + e.getMessage());
        }
    }

    public List<Setting<?>> getSettingsByModule(Module module) {
        return setting.getOrDefault(module, Collections.emptyList());
    }

    public void addSetting(Module feature, Setting<?> property) {
        setting.putIfAbsent(feature, new ArrayList<>());
        setting.get(feature).add(property);
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
