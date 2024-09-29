package io.github.qe7.features.impl.commands.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.commands.api.Command;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.utils.ChatUtil;

public class ToggleCommand extends Command {

    public ToggleCommand() {
        super("Toggle", "Toggles a module on or off");

        this.setUsage(".toggle <module>");
        this.setAliases(new String[] { "t" });
    }

    @Override
    public void execute(final String[] args) {
        if (args.length < 1) {
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
