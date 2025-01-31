package io.github.qe7.type;

import io.github.qe7.features.impl.commands.impl.chat.api.ChatCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatBotSkid {
    private ChatCommand command;
    private String username;
    private String[] args;
}
