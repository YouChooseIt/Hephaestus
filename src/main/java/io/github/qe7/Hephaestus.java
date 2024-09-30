package io.github.qe7;

import io.github.qe7.managers.impl.*;
import lombok.Getter;
import lombok.Setter;
import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;

@Getter
public final class Hephaestus {

    @Getter
    private static final Hephaestus instance = new Hephaestus();

    private final EventBus eventBus;

    private final AccountManager accountManager;
    private final ModuleManager moduleManager;
    private final CommandManager commandManager;
    private final PanelManager panelManager;
    private final PlayerManager playerManager;

    private final String name;

    @Setter
    private String prefix;

    private final String[] authors;

    private Hephaestus() {
        this.eventBus = EventManager.builder().setName("client").build();

        this.accountManager = new AccountManager();
        this.moduleManager = new ModuleManager();
        this.commandManager = new CommandManager();
        this.panelManager = new PanelManager();
        this.playerManager = new PlayerManager();

        this.name = "Hephaestus";
        this.prefix = ".";

        this.authors = new String[]{"qe7"};
    }

    public void initialize() {
        System.out.println("Initializing " + this.getName() + "...");

        this.getAccountManager().initialize();
        this.getModuleManager().initialize();
        this.getCommandManager().initialize();
        this.getPanelManager().initialize();
        this.getPlayerManager().initialise();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void shutdown() {
        System.out.println("Shutting down " + this.getName() + "...");

        this.getAccountManager().saveAccounts();
        this.getModuleManager().saveModules();
        this.getPanelManager().savePanels();
    }
}
