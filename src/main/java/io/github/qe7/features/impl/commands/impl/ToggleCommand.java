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
        // Check if the user provided a module name
        if (args.length < 1) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Usage: " + this.getUsage());
            return;
        }

        // Get the module name from the arguments
        final String moduleName = args[1];

        // Find the target module by name
        Module targetModule = null;

        for (Module module : Hephaestus.getInstance().getModuleManager().getRegistry().values()) {
            if (module.getName().equalsIgnoreCase(moduleName)) {
                targetModule = module;
                break;
            }
        }

        // Check if the module was found
        if (targetModule == null) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Module not found: " + moduleName);
            return;
        }

        // Toggle the module
        targetModule.toggle();
        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Toggled module: " + targetModule.getName() + " to " + (targetModule.isEnabled() ? "enabled" : "disabled"));
    }
}
