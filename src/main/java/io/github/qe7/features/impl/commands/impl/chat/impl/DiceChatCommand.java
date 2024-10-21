package io.github.qe7.features.impl.commands.impl.chat.impl;

import io.github.qe7.features.impl.commands.impl.chat.api.ChatCommand;
import io.github.qe7.utils.ChatUtil;

public class DiceChatCommand extends ChatCommand {

    public DiceChatCommand() {
        super("Dice", "Roll a dice");
    }

    @Override
    public void execute(String username, String[] args) {
        int dice = (int) (Math.random() * 6) + 1;
        ChatUtil.sendMessage("You rolled a " + dice);
    }
}
