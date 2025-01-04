package io.github.qe7.features.impl.modules.api;

import com.google.gson.JsonObject;
import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.commands.impl.client.api.ClientCommand;
import io.github.qe7.features.impl.modules.api.settings.api.Setting;
import io.github.qe7.features.impl.modules.api.settings.impl.BooleanSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.DoubleSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.EnumSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.IntSetting;
import io.github.qe7.utils.ChatUtil;
import io.github.qe7.utils.config.Serialized;
import lombok.Getter;
import lombok.Setter;
import me.zero.alpine.listener.Subscriber;

import java.util.Objects;

@Getter
@Setter
public abstract class Module extends ClientCommand implements Subscriber, Serialized {

    private final ModuleCategory category;

    private String suffix = "";

    private int keyBind;

    private boolean enabled;

    public Module(final String name, final String description, final ModuleCategory category) {
        super(name, description);
        this.category = category;
    }

    public void onEnable() {
        Hephaestus.getInstance().getEventBus().subscribe(this);
    }

    public void onDisable() {
        Hephaestus.getInstance().getEventBus().unsubscribe(this);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }

        this.enabled = enabled;

        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void toggle() {
        enabled = !enabled;

        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            if (Objects.equals(this.getUsage(), "No usage provided")) {
                StringBuilder usage = new StringBuilder(this.getName());

                if (Hephaestus.getInstance().getModuleManager().getSettingsByModule(this).isEmpty()) {
                    this.setUsage("No settings available");
                } else {
                    for (Setting<?> setting : Hephaestus.getInstance().getModuleManager().getSettingsByModule(this)) {
                        usage.append(" <").append(setting.getName().replace(" ", "")).append(">");
                    }
                    this.setUsage(usage.toString());
                }

                ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Usage: " + this.getUsage());
            } else {
                ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Usage: " + this.getUsage());
            }
            return;
        }

        if (args.length == 2 || args.length == 3) {
            for (Setting<?> setting : Hephaestus.getInstance().getModuleManager().getSettingsByModule(this)) {
                if (!setting.getName().replace(" ", "").equalsIgnoreCase(args[1])) {
                    continue;
                }

                if (args.length == 2) {
                    ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), setting.getName() + ": " + setting.getValue());
                    return;
                }

                if (setting instanceof BooleanSetting) {
                    BooleanSetting booleanSetting = (BooleanSetting) setting;

                    if (!args[2].equalsIgnoreCase("true") && !args[2].equalsIgnoreCase("false")) {
                        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Invalid value");
                        return;
                    }

                    booleanSetting.setValue(Boolean.parseBoolean(args[2]));
                    ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Set " + setting.getName() + " to " + args[2]);
                    return;
                }
                if (setting instanceof IntSetting) {
                    IntSetting intSetting = (IntSetting) setting;

                    if (!args[2].matches("[0-9]+")) {
                        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Invalid value");
                        return;
                    }

                    if (intSetting.getMinimum() != Integer.MIN_VALUE && Integer.parseInt(args[2]) < intSetting.getMinimum()) {
                        intSetting.setValue(intSetting.getMinimum());
                        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Set " + setting.getName() + " to " + intSetting.getMinimum());
                        return;
                    }

                    if (intSetting.getMaximum() != Integer.MAX_VALUE && Integer.parseInt(args[2]) > intSetting.getMaximum()) {
                        intSetting.setValue(intSetting.getMaximum());
                        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Set " + setting.getName() + " to " + intSetting.getMaximum());
                        return;
                    }

                    intSetting.setValue(Integer.parseInt(args[2]));
                    ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Set " + setting.getName() + " to " + args[2]);
                    return;
                }
                if (setting instanceof DoubleSetting) {
                    DoubleSetting doubleSetting = (DoubleSetting) setting;

                    if (!args[2].matches("[-+]?[0-9]*\\.?[0-9]+")) {
                        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Invalid value");
                        return;
                    }

                    if (doubleSetting.getMinimum() != Double.MIN_VALUE && Double.parseDouble(args[2]) < doubleSetting.getMinimum()) {
                        doubleSetting.setValue(doubleSetting.getMinimum());
                        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Set " + setting.getName() + " to " + doubleSetting.getMinimum());
                        return;
                    }

                    if (doubleSetting.getMaximum() != Double.MAX_VALUE && Double.parseDouble(args[2]) > doubleSetting.getMaximum()) {
                        doubleSetting.setValue(doubleSetting.getMaximum());
                        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Set " + setting.getName() + " to " + doubleSetting.getMaximum());
                        return;
                    }

                    doubleSetting.setValue(Double.parseDouble(args[2]));
                    ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Set " + setting.getName() + " to " + args[2]);
                    return;
                }
                if (setting instanceof EnumSetting) {
                    EnumSetting<?> enumSetting = (EnumSetting<?>) setting;

                    if (enumSetting.getIndex(args[2]) == -1) {
                        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Invalid value");
                        return;
                    }

                    enumSetting.setIndex(enumSetting.getIndex(args[2]));
                    ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Set " + setting.getName() + " to " + args[2]);
                    return;
                }
                ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Invalid setting type");
                return;
            }
        }
    }

    @Override
    public JsonObject serialize() {
        final JsonObject object = new JsonObject();

        object.addProperty("enabled", enabled);
        object.addProperty("keyBind", keyBind);

        if (!Hephaestus.getInstance().getModuleManager().getSettingsByModule(this).isEmpty()) {
            final JsonObject settings = new JsonObject();

            for (Setting<?> setting : Hephaestus.getInstance().getModuleManager().getSettingsByModule(this)) {
                settings.add(setting.getName(), setting.serialize());
            }

            object.add("settings", settings);
        }

        return object;
    }

    @Override
    public void deserialize(JsonObject object) {
        this.setEnabled(object.get("enabled").getAsBoolean());
        keyBind = object.get("keyBind").getAsInt();

        if (object.has("settings")) {
            final JsonObject settings = object.getAsJsonObject("settings");

            for (Setting<?> setting : Hephaestus.getInstance().getModuleManager().getSettingsByModule(this)) {
                if (settings.has(setting.getName())) {
                    try {
                        setting.deserialize(settings.getAsJsonObject(setting.getName()));
                    } catch (Exception e) {
                        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Failed to load config for setting: " + setting.getName() + " - " + e.getMessage());
                    }
                }
            }
        }
    }
}
