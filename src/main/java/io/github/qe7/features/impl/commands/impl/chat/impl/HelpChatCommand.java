package io.github.qe7.features.impl.commands.impl.chat.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.commands.impl.chat.api.ChatCommand;
import io.github.qe7.utils.ChatUtil;

public final class HelpChatCommand extends ChatCommand {

    public HelpChatCommand() {
        super("Help", "Displays a list of commands");
    }

    @Override
    public void execute(String username, String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Commands: ");
        for (ChatCommand chatCommand : Hephaestus.getInstance().getChatCommandManager().getRegistry().values()) {
            stringBuilder.append(chatCommand.getName()).append(", ");
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        ChatUtil.sendMessage(stringBuilder.toString());
    }
}
