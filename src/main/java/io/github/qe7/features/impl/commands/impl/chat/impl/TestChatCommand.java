package io.github.qe7.features.impl.commands.impl.chat.impl;

import io.github.qe7.features.impl.commands.impl.chat.api.ChatCommand;
import io.github.qe7.utils.ChatUtil;

public class TestChatCommand extends ChatCommand {

    public TestChatCommand() {
        super("OtherCommandTestShit", "Test command");
    }

    @Override
    public void execute(String username, String[] args) {
        System.out.println("Test command executed by " + username);
    }
}
