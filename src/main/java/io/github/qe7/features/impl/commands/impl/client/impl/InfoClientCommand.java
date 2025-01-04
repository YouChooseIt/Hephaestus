package io.github.qe7.features.impl.commands.impl.client.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.commands.impl.client.api.ClientCommand;
import io.github.qe7.utils.ChatUtil;

public final class InfoClientCommand extends ClientCommand {

    public InfoClientCommand() {
        super("Info", "Prints information related to a desired module or command");

        this.setUsage(".Info <module/command>");
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Invalid arguments");
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Usage: " + this.getUsage());
            return;
        }

        final String targetName = args[1];

        ClientCommand targetClientCommand = null;

        for (ClientCommand clientCommand : Hephaestus.getInstance().getClientCommandManager().getRegistry().values()) {
            if (clientCommand.getName().equalsIgnoreCase(targetName)) {
                targetClientCommand = clientCommand;
                break;
            }
        }

        if (targetClientCommand != null) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Name: " + targetClientCommand.getName());
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Description: " + targetClientCommand.getDescription());
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Usage: " + targetClientCommand.getUsage());
        }
    }
}
