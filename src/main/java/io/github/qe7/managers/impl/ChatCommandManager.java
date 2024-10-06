package io.github.qe7.managers.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.events.packet.IncomingPacketEvent;
import io.github.qe7.features.impl.commands.impl.chat.api.ChatCommand;
import io.github.qe7.features.impl.commands.impl.chat.impl.TestChatCommand;
import io.github.qe7.features.impl.modules.impl.misc.ChatBotModule;
import io.github.qe7.managers.api.Manager;
import io.github.qe7.utils.ChatUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Packet3Chat;

import java.util.ArrayList;
import java.util.List;

public final class ChatCommandManager extends Manager<Class<? extends ChatCommand>, ChatCommand> implements Subscriber {

    public void initialize() {
        final List<ChatCommand> chatCommand = new ArrayList<>();

        chatCommand.add(new TestChatCommand());

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

        System.out.println("username: " + username);
        System.out.println("message: " + message);

        if (!message.startsWith("$")) return;

        String[] args = chat.message.substring(1).split(" ");

        ChatCommand targetChatCommand = null;

        for (ChatCommand chatCommand : this.getRegistry().values()) {
            if (chatCommand.getName().equalsIgnoreCase(args[0])) {
                targetChatCommand = chatCommand;
                break;
            }
        }

        if (targetChatCommand != null) {
            targetChatCommand.execute(username, args);
            return;
        }

        ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "ChatCommand not found: " + args[0]);
    });
}
