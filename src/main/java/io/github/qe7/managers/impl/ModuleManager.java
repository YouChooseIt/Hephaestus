package io.github.qe7.managers.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.qe7.Hephaestus;
import io.github.qe7.events.KeyPressEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.settings.api.Setting;
import io.github.qe7.features.impl.modules.impl.combat.*;
import io.github.qe7.features.impl.modules.impl.exploit.*;
import io.github.qe7.features.impl.modules.impl.movement.*;
import io.github.qe7.features.impl.modules.impl.render.*;
import io.github.qe7.features.impl.modules.impl.build.*;
import io.github.qe7.features.impl.modules.impl.misc.*;
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

        /* Combat */
        modules.add(new ForceFieldModule());
        modules.add(new AntiKnockBackModule());
        modules.add(new AutoHealModule());

        /* Render */
        modules.add(new HUDEditorGUIModule());
        modules.add(new NameTagsModule());
        modules.add(new FullBrightModule());
        modules.add(new XRayModule());
        modules.add(new ClickGUIModule());
        modules.add(new CameraModule());
        modules.add(new ViewModelModule());
        modules.add(new StorageESPModule());
        modules.add(new EntityESPModule());
        modules.add(new NoRenderModule());
        modules.add(new TracersModule());

        /* Movement */
        modules.add(new FlightModule());
        modules.add(new StepModule());
        modules.add(new InventoryMoveModule());
        modules.add(new SpeedModule());
        modules.add(new JesusModule());
        modules.add(new StrafeModule());
        modules.add(new SafeWalkModule());
        modules.add(new YawModule());
        modules.add(new FreezeYModule());

        /* Misc */
        modules.add(new AutoLoginModule());
        modules.add(new NoFallDamageModule());
        modules.add(new AutoInfinityModule());
        modules.add(new FreeCameraModule());
        modules.add(new FastBreakModule());
        modules.add(new AutoToolModule());
        modules.add(new AutoListModule());
        modules.add(new SchizoBotModule());
        modules.add(new ChatBotModule());
        modules.add(new GrammarModule());
        modules.add(new Auto127Module());
        modules.add(new NoPushModule());
        modules.add(new AutoTunnelModule());
        modules.add(new AutoWalkModule());

        /* Exploit */
        modules.add(new Slot9Module());
        modules.add(new FastPortalsModule());
        modules.add(new XCarryModule());

        /* Build */
        modules.add(new ScaffoldModule());
        modules.add(new ExtrudeModule());

        modules.forEach(this::register);

        this.loadModules();

        Hephaestus.getInstance().getEventBus().subscribe(this);
    }

    /**
     * Register a module
     *
     * @param module the module to register
     */
    public void register(final Module module) {
        try {
            this.getRegistry().putIfAbsent(module.getClass(), module);
            System.out.println("Registered module: " + module.getClass().getSimpleName());

            for (Field declaredField : module.getClass().getDeclaredFields()) {
                if (declaredField.getType().getSuperclass() == null) continue;
                if (!declaredField.getType().getSuperclass().equals(Setting.class)) continue;

                declaredField.setAccessible(true);

                this.addSetting(this.getRegistry().get(module.getClass()), (Setting<?>) declaredField.get(this.getRegistry().get(module.getClass())));
                System.out.println("Registered setting: " + declaredField.getName() + " for module: " + module.getClass().getSimpleName());
            }
        } catch (final Exception e) {
            System.out.println("Failed to register module: " + module.getClass().getSimpleName());
            System.out.println("Reason: " + e.getMessage());
        }
    }

    public <T extends Module> T getModule(Class<T> moduleClass) {
        return moduleClass.cast(getRegistry().get(moduleClass));
    }

    public List<Setting<?>> getSettingsByModule(Module module) {
        return setting.getOrDefault(module, Collections.emptyList());
    }

    public void addSetting(Module feature, Setting<?> property) {
        setting.putIfAbsent(feature, new ArrayList<>());
        setting.get(feature).add(property);
    }

    public void saveModules() {
        final JsonObject jsonObject = new JsonObject();

        for (Module module : this.getRegistry().values()) {
            jsonObject.add(module.getName(), module.serialize());
        }

        FileUtil.writeFile("modules", GSON.toJson(jsonObject));
    }

    public void loadModules() {
        final String config = FileUtil.readFile("modules");

        if (config == null) {
            return;
        }

        final JsonObject jsonObject = GSON.fromJson(config, JsonObject.class);

        for (final Module module : this.getRegistry().values()) {
            if (jsonObject.has(module.getName())) {
                module.deserialize(jsonObject.getAsJsonObject(module.getName()));
            }
        }
    }

    public void savePlates() {
        this.getModule(ClickGUIModule.class).savePlates();
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
