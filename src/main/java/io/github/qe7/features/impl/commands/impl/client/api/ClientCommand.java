package io.github.qe7.features.impl.commands.impl.client.api;

import io.github.qe7.features.api.Feature;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ClientCommand extends Feature {

    private String usage = "No usage provided";

    private String[] aliases = new String[0];

    public ClientCommand(String name, String description) {
        super(name, description);
    }

    public abstract void execute(String[] args);
}
