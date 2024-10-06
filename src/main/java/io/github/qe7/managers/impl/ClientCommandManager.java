package io.github.qe7.managers.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.events.packet.OutgoingPacketEvent;
import io.github.qe7.features.impl.commands.impl.client.api.ClientCommand;
import io.github.qe7.features.impl.commands.impl.client.impl.*;
import io.github.qe7.managers.api.Manager;
import io.github.qe7.utils.ChatUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.src.Packet3Chat;

import java.util.ArrayList;
import java.util.List;

public final class ClientCommandManager extends Manager<Class<? extends ClientCommand>, ClientCommand> implements Subscriber {

    public void initialize() {
        final List<ClientCommand> clientCommands = new ArrayList<>();

        clientCommands.add(new ToggleClientCommand());
        clientCommands.add(new PrefixClientCommand());
        clientCommands.add(new BindClientCommand());
        clientCommands.add(new CommandListClientCommand());
        clientCommands.add(new ModuleListClientCommand());
        clientCommands.add(new InfoClientCommand());

        clientCommands.forEach(command -> register(command.getClass()));

        Hephaestus.getInstance().getModuleManager().getRegistry().values().forEach(module -> {
            this.getRegistry().putIfAbsent(module.getClass(), module);
        });

        Hephaestus.getInstance().getEventBus().subscribe(this);
    }

    public void register(final Class<? extends ClientCommand> type) {
        try {
            ClientCommand clientCommand = type.newInstance();
            this.getRegistry().putIfAbsent(type, clientCommand);
            System.out.println("Registered clientCommand: " + type.getSimpleName());
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

        ClientCommand targetClientCommand = null;

        for (ClientCommand clientCommand : this.getRegistry().values()) {
            if (clientCommand.getName().equalsIgnoreCase(args[0])) {
                targetClientCommand = clientCommand;
                break;
            }

            for (String alias : clientCommand.getAliases()) {
                if (alias.equalsIgnoreCase(args[0])) {
                    targetClientCommand = clientCommand;
                    break;
                }
            }
        }

        if (targetClientCommand != null) {
            targetClientCommand.execute(args);
            return;
        }

        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "ClientCommand not found: " + args[0]);
    });
}
