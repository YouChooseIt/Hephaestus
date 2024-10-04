package io.github.qe7.features.impl.commands.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.commands.api.Command;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.utils.ChatUtil;

public class CommandListCommand extends Command {

    public CommandListCommand() {
        super("Commands", "Prints a list of all commands");

        this.setUsage("Commands");
        this.setAliases(new String[] { "Cmds", "Help" });
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Invalid arguments");
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Usage: " + this.getUsage());
            return;
        }

        StringBuilder commandList = new StringBuilder();

        for (Command command : Hephaestus.getInstance().getCommandManager().getRegistry().values()) {
            if (command instanceof Module) {
                continue;
            }
            commandList.append(command.getName()).append(", ");
        }

        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Commands: " + commandList);
    }
}
