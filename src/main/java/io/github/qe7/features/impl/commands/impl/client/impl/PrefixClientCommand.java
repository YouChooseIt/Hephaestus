package io.github.qe7.features.impl.commands.impl.client.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.commands.impl.client.api.ClientCommand;
import io.github.qe7.utils.ChatUtil;

public final class PrefixClientCommand extends ClientCommand {

    public PrefixClientCommand() {
        super("Prefix", "Change the command prefix");

        this.setUsage(".prefix <newPrefix>");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Please provide a new prefix.");
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Usage: " + this.getUsage());
            return;
        }

        if (args.length != 2) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Invalid number of arguments.");
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Usage: " + this.getUsage());
            return;
        }

        String newPrefix = args[1];

        if (newPrefix.isEmpty()) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Prefix cannot be empty.");
            return;
        }

        if (newPrefix.length() > 1) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Prefix cannot be longer than 1 characters.");
            return;
        }

        Hephaestus.getInstance().setPrefix(newPrefix);
        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Prefix changed to: " + newPrefix);
    }
}
