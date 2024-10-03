package io.github.qe7.features.impl.commands.api;

import io.github.qe7.features.api.Feature;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Command extends Feature {

    private String usage = "No usage provided";

    private String[] aliases = new String[0];

    public Command(String name, String description) {
        super(name, description);
    }

    public abstract void execute(String[] args);
}
