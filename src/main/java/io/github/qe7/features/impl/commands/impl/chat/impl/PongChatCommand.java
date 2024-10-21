package io.github.qe7.features.impl.commands.impl.chat.impl;

import io.github.qe7.features.impl.commands.impl.chat.api.ChatCommand;
import io.github.qe7.utils.ChatUtil;

public class PongChatCommand extends ChatCommand {

    public PongChatCommand() {
        super("Ping", "Ping/Pong command");
    }

    @Override
    public void execute(String username, String[] args) {
        ChatUtil.sendMessage("Pong!");
    }
}
