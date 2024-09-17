package io.github.qe7;

import io.github.qe7.managers.impl.ModuleManager;
import lombok.Getter;
import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;

/**
 * Hephaestus is the god of fire, metalworking, and crafts...
 * And this will be our main class for the client! :)
 */
@Getter
public final class Hephaestus {

    // Singleton instance, so we can access it from anywhere
    @Getter
    private static final Hephaestus instance = new Hephaestus();

    // Event bus for event handling
    private final EventBus eventBus;

    // Managers
    private final ModuleManager moduleManager;

    // Client related variables
    private final String name, prefix;

    // Client authors
    private final String[] authors = new String[] { "qe7", "woooow" };

    private Hephaestus() {
        // Build the event bus
        this.eventBus = EventManager.builder().setName("client").build();

        // Create instances of the managers
        this.moduleManager = new ModuleManager();

        // Set the name of the client
        this.name = "Hephaestus";
        this.prefix = ".";
    }

    /**
     * Initialize the client
     */
    public void initialize() {
        System.out.println("Initializing " + this.getName() + "...");

        this.getModuleManager().initialize();
    }
}
