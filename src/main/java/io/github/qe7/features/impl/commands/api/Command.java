package io.github.qe7.features.impl.commands.api;

import io.github.qe7.features.api.Feature;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Command extends Feature {

    // Usage
    private String usage = "No usage provided";

    // Aliases
    private String[] aliases = new String[0];

    // Constructor
    public Command(String name, String description) {
        super(name, description);
    }

    // Abstract method to execute the command
    public abstract void execute(String[] args);
}
