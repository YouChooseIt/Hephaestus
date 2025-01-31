package io.github.qe7.features.impl.commands.impl.chat.impl;

import io.github.qe7.features.impl.commands.impl.chat.api.ChatCommand;
import io.github.qe7.utils.ChatUtil;

public final class WBotSkiddingCommand extends ChatCommand {

    public WBotSkiddingCommand() {
        super("@~cliff", "Evil WBot skid");
    }

    @Override
    public void execute(String username, String[] args) {
        ChatUtil.sendMessage("Free cliff -> download-cliff.org");
    }
}
