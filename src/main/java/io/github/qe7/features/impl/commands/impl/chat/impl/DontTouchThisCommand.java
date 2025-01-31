package io.github.qe7.features.impl.commands.impl.chat.impl;

import io.github.qe7.features.impl.commands.impl.chat.api.ChatCommand;
import io.github.qe7.utils.ChatUtil;

public final class DontTouchThisCommand extends ChatCommand {

    public DontTouchThisCommand() {
        super("", "/kill");
    }

    @Override
    public void execute(String username, String[] args) {
        ChatUtil.sendMessage("Command not found.");
    }
}
