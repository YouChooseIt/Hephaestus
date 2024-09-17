package io.github.qe7.features.impl.commands.api;

import io.github.qe7.features.api.Feature;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Command extends Feature {

    // Usage
    private String usage = "";

    // Aliases
    private String[] aliases = new String[0];

    /**
     * Creates a new command
     *
     * @param name        the name of the command
     * @param description the description of the command
     */
    public Command(String name, String description) {
        super(name, description);
    }
}
