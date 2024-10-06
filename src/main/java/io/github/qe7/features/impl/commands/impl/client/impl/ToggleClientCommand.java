package io.github.qe7.features.impl.commands.impl.client.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.commands.impl.client.api.ClientCommand;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.utils.ChatUtil;

public class ToggleClientCommand extends ClientCommand {

    public ToggleClientCommand() {
        super("Toggle", "Toggles a module on or off");

        this.setUsage(".Toggle <module>");
        this.setAliases(new String[] { "T" });
    }

    @Override
    public void execute(final String[] args) {
        if (args.length != 2) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Usage: " + this.getUsage());
            return;
        }

        final String moduleName = args[1];

        Module targetModule = null;

        for (Module module : Hephaestus.getInstance().getModuleManager().getRegistry().values()) {
            if (module.getName().equalsIgnoreCase(moduleName)) {
                targetModule = module;
                break;
            }
        }

        if (targetModule == null) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Module not found: " + moduleName);
            return;
        }

        targetModule.toggle();
        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Toggled module: " + targetModule.getName() + " to " + (targetModule.isEnabled() ? "enabled" : "disabled"));
    }
}
