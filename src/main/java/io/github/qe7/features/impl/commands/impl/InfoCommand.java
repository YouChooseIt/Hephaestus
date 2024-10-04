package io.github.qe7.features.impl.commands.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.commands.api.Command;
import io.github.qe7.utils.ChatUtil;

public class InfoCommand extends Command {

    public InfoCommand() {
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

        Command targetCommand = null;

        for (Command command : Hephaestus.getInstance().getCommandManager().getRegistry().values()) {
            if (command.getName().equalsIgnoreCase(targetName)) {
                targetCommand = command;
                break;
            }
        }

        if (targetCommand != null) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Name: " + targetCommand.getName());
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Description: " + targetCommand.getDescription());
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Usage: " + targetCommand.getUsage());
        }
    }
}
