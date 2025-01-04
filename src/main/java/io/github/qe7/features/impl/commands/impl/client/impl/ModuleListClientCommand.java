package io.github.qe7.features.impl.commands.impl.client.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.commands.impl.client.api.ClientCommand;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.utils.ChatUtil;

public final class ModuleListClientCommand extends ClientCommand {

    public ModuleListClientCommand() {
        super("Modules", "Prints a list of all modules.");

        this.setUsage("Modules");
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Invalid arguments");
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Usage: " + this.getUsage());
            return;
        }

        StringBuilder moduleList = new StringBuilder();

        for (Module module : Hephaestus.getInstance().getModuleManager().getRegistry().values()) {
            moduleList.append(module.getName()).append(", ");
        }

        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Modules: " + moduleList);
    }
}
