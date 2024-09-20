package io.github.qe7;

import io.github.qe7.managers.impl.*;
import lombok.Getter;
import lombok.Setter;
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
    private final AccountManager accountManager;
    private final ModuleManager moduleManager;
    private final CommandManager commandManager;
    private final PanelManager panelManager;
    private final PlayerManager playerManager;

    // Client related variables
    private final String name;

    @Setter
    private String prefix;

    // Client authors
    private final String[] authors = new String[] { "qe7", "woooow" };

    // Private constructor, to prevent instantiation
    private Hephaestus() {
        // Build the event bus
        this.eventBus = EventManager.builder().setName("client").build();

        // Create instances of the managers
        this.accountManager = new AccountManager();
        this.moduleManager = new ModuleManager();
        this.commandManager = new CommandManager();
        this.panelManager = new PanelManager();
        this.playerManager = new PlayerManager();

        // Set the name of the client
        this.name = "Hephaestus";
        this.prefix = ".";
    }

    /**
     * Initialize the client
     */
    public void initialize() {
        System.out.println("Initializing " + this.getName() + "...");

        this.getAccountManager().initialize();
        this.getModuleManager().initialize();
        this.getCommandManager().initialize();
        this.getPanelManager().initialize();
        this.getPlayerManager().initialise();

        // register shutdown hook, save configs on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    /**
     * Shutdown the client
     */
    public void shutdown() {
        System.out.println("Shutting down " + this.getName() + "...");

        this.getAccountManager().saveAccounts();
        this.getModuleManager().saveModules();
        this.getPanelManager().savePanels();
    }
}
