package io.github.qe7.managers.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.events.packet.IncomingPacketEvent;
import io.github.qe7.features.impl.commands.impl.chat.api.ChatCommand;
import io.github.qe7.features.impl.commands.impl.chat.impl.*;
import io.github.qe7.features.impl.modules.impl.misc.ChatBotModule;
import io.github.qe7.managers.api.Manager;
import io.github.qe7.utils.ChatUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Packet3Chat;

import java.util.*;

public final class ChatCommandManager extends Manager<Class<? extends ChatCommand>, ChatCommand> implements Subscriber {

    public void initialize() {
        final List<ChatCommand> chatCommand = new ArrayList<>();

        chatCommand.add(new PongChatCommand());
        chatCommand.add(new QuoteChatCommand());
        chatCommand.add(new JokeChatCommand());
        chatCommand.add(new DiceChatCommand());
        chatCommand.add(new HelpChatCommand());

        chatCommand.forEach(command -> register(command.getClass()));

        Hephaestus.getInstance().getEventBus().subscribe(this);
    }

    public void register(final Class<? extends ChatCommand> type) {
        try {
            ChatCommand chatCommand = type.newInstance();
            this.getRegistry().putIfAbsent(type, chatCommand);
            System.out.println("Registered chatCommand: " + type.getSimpleName());
        } catch (Exception e) {
            System.out.println("Failed to register command: " + type.getSimpleName() + " - " + e.getMessage());
        }
    }

    @Subscribe
    public final Listener<IncomingPacketEvent> incomingPacketEventListener = new Listener<>(event -> {
        if (!Hephaestus.getInstance().getModuleManager().getRegistry().get(ChatBotModule.class).isEnabled()) return;
        if (Minecraft.getMinecraft().thePlayer == null) return;
        if (Minecraft.getMinecraft().theWorld == null) return;
        if (!(event.getPacket() instanceof Packet3Chat)) return;

        final Packet3Chat chat = (Packet3Chat) event.getPacket();

        String normalizedMessage = chat.message.toLowerCase();

        // detect the username, formatted "<username> message"
        if (!normalizedMessage.startsWith("<") || !normalizedMessage.contains(">")) return;

        String username = normalizedMessage.substring(1, normalizedMessage.indexOf(">"));
        String message = normalizedMessage.substring(normalizedMessage.indexOf(">") + 1).trim();

        if (!message.startsWith("$")) return;

        String[] args = message.split(" ");

        if (args.length == 0) return;

        ChatCommand targetCommand = null;

        for (ChatCommand command : this.getRegistry().values()) {
            if (command.getName().equalsIgnoreCase(args[0].substring(1))) {
                targetCommand = command;
                break;
            }
        }

        if (targetCommand == null) {
            ChatUtil.sendMessage("Command not found.");
            return;
        }

        targetCommand.execute(username, args);
    });
}
