package io.github.qe7.features.impl.commands.impl.chat.api;

import io.github.qe7.features.api.Feature;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ChatCommand extends Feature {

    private String usage = "No usage provided";

    public ChatCommand(String name, String description) {
        super(name, description);
    }

    public abstract void execute(String username, String[] args);
}
