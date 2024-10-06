package io.github.qe7.features.impl.commands.impl.client.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.commands.impl.client.api.ClientCommand;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.utils.ChatUtil;

public class CommandListClientCommand extends ClientCommand {

    public CommandListClientCommand() {
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

        for (ClientCommand clientCommand : Hephaestus.getInstance().getClientCommandManager().getRegistry().values()) {
            if (clientCommand instanceof Module) {
                continue;
            }
            commandList.append(clientCommand.getName()).append(", ");
        }

        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Commands: " + commandList);
    }
}
