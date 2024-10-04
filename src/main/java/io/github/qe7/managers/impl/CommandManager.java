package io.github.qe7.managers.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.events.packet.OutgoingPacketEvent;
import io.github.qe7.features.impl.commands.api.Command;
import io.github.qe7.features.impl.commands.impl.*;
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

        commands.add(new ToggleCommand());
        commands.add(new PrefixCommand());
        commands.add(new BindCommand());
        commands.add(new CommandListCommand());
        commands.add(new ModuleListCommand());
        commands.add(new InfoCommand());

        commands.forEach(command -> register(command.getClass()));

        Hephaestus.getInstance().getModuleManager().getRegistry().values().forEach(module -> {
            this.getRegistry().putIfAbsent(module.getClass(), module);
        });

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
        if (!(event.getPacket() instanceof Packet3Chat)) return;

        final Packet3Chat chat = (Packet3Chat) event.getPacket();

        if (!chat.message.startsWith(Hephaestus.getInstance().getPrefix())) return;

        event.setCancelled(true);

        String[] args = chat.message.substring(1).split(" ");

        Command targetCommand = null;

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

        if (targetCommand != null) {
            targetCommand.execute(args);
            return;
        }

        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Command not found: " + args[0]);
    });
}
