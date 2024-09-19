package io.github.qe7.managers.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.events.packet.OutgoingPacketEvent;
import io.github.qe7.features.impl.commands.api.Command;
import io.github.qe7.features.impl.commands.impl.BindCommand;
import io.github.qe7.features.impl.commands.impl.PrefixCommand;
import io.github.qe7.features.impl.commands.impl.ToggleCommand;
import io.github.qe7.managers.api.Manager;
import io.github.qe7.utils.ChatUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.src.Packet3Chat;

import java.util.ArrayList;
import java.util.List;

public final class CommandManager extends Manager<Class<? extends Command>, Command> implements Subscriber {

    public void initialize() {
        final List<Command> commands = new ArrayList<>();

        // Add commands to the list
        commands.add(new ToggleCommand());
        commands.add(new PrefixCommand());
        commands.add(new BindCommand());

        // Register commands
        commands.forEach(command -> register(command.getClass()));

        // Add modules to map, as they are commands
        Hephaestus.getInstance().getModuleManager().getRegistry().values().forEach(module -> {
            this.getRegistry().putIfAbsent(module.getClass(), module);
        });

        // Subscribe to the event bus for outgoing packet events
        Hephaestus.getInstance().getEventBus().subscribe(this);
    }

    public void register(final Class<? extends Command> type) {
        try {
            Command command = type.newInstance();
            this.getRegistry().putIfAbsent(type, command);
            System.out.println("Registered command: " + type.getSimpleName());
        } catch (Exception e) {
            System.out.println("Failed to register command: " + type.getSimpleName() + " - " + e.getMessage());
        }
    }

    @Subscribe
    public final Listener<OutgoingPacketEvent> outgoingPacketListener = new Listener<>(event -> {
        // Check if the packet is a chat packet
        if (!(event.getPacket() instanceof Packet3Chat)) return;

        // Get the chat packet
        final Packet3Chat chat = (Packet3Chat) event.getPacket();

        // Check if the message starts with the prefix
        if (!chat.message.startsWith(Hephaestus.getInstance().getPrefix())) return;

        // Cancel to prevent sending . commands to the server
        event.setCancelled(true);

        // Split the message into arguments
        String[] args = chat.message.substring(1).split(" ");

        // Find the target command
        Command targetCommand = null;

        // Loop through the registered commands to find the target command
        for (Command command : this.getRegistry().values()) {
            if (command.getName().equalsIgnoreCase(args[0])) {
                targetCommand = command;
                break;
            }

            for (String alias : command.getAliases()) {
                if (alias.equalsIgnoreCase(args[0])) {
                    targetCommand = command;
                    break;
                }
            }
        }

        // Execute the target command if found
        if (targetCommand != null) {
            targetCommand.execute(args);
            return;
        }

        // If no command was found, send a message to the chat
        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Command not found: " + args[0]);
    });
}
