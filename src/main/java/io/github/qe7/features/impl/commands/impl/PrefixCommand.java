package io.github.qe7.features.impl.commands.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.commands.api.Command;
import io.github.qe7.utils.ChatUtil;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        super("Prefix", "Change the command prefix");

        this.setUsage(".prefix <newPrefix>");
    }

    @Override
    public void execute(String[] args) {
        // Check if the user provided a new prefix
        if (args.length == 1) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Please provide a new prefix.");
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Usage: " + this.getUsage());
            return;
        }

        // Check if the command is being used correctly
        if (args.length != 2) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Invalid number of arguments.");
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Usage: " + this.getUsage());
            return;
        }

        // Sanitize and validate the new prefix
        String newPrefix = args[1];

        if (newPrefix.isEmpty()) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Prefix cannot be empty.");
            return;
        }

        if (newPrefix.length() > 1) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Prefix cannot be longer than 1 characters.");
            return;
        }

        // Set the new prefix
        Hephaestus.getInstance().setPrefix(newPrefix);
        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Prefix changed to: " + newPrefix);
    }
}
