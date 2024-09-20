package io.github.qe7.managers.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.qe7.Hephaestus;
import io.github.qe7.accounts.Account;
import io.github.qe7.events.packet.IncomingPacketEvent;
import io.github.qe7.events.packet.OutgoingPacketEvent;
import io.github.qe7.managers.api.Manager;
import io.github.qe7.utils.ChatUtil;
import io.github.qe7.utils.config.FileUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet3Chat;

import java.util.Objects;

public final class AccountManager extends Manager<String, Account> implements Subscriber {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private String possiblePassword = "";

    public void initialize() {
        this.loadAccounts();

        Hephaestus.getInstance().getEventBus().subscribe(this);
    }

    private void register(Account type) {
        this.getRegistry().put(type.getUsername(), type);
    }

    public void saveAccounts() {
        final JsonObject object = new JsonObject();

        for (final Account account : this.getRegistry().values()) {
            object.add(account.getUsername(), GSON.toJsonTree(account));
        }

        FileUtil.writeFile("accounts", GSON.toJson(object));
    }

    public void loadAccounts() {
        final String config = FileUtil.readFile("accounts");

        if (config == null) {
            return;
        }

        final JsonObject object = GSON.fromJson(config, JsonObject.class);

        object.entrySet().forEach(entry -> {
            final String password = entry.getValue().getAsJsonObject().get("password").getAsString();
            final Account account = new Account(entry.getKey());
            account.setPassword(password);
            this.register(account);
        });
    }

    @Subscribe
    public final Listener<IncomingPacketEvent> incomingPacketListener = new Listener<>(IncomingPacketEvent.class, event -> {
        final Packet packet = event.getPacket();

        if (packet instanceof Packet3Chat) {
            final Packet3Chat chat = (Packet3Chat) packet;

            final String normalMessage = chat.message.replaceAll("§.", "");

            if (normalMessage.equalsIgnoreCase("Successful login!")) {
                if (this.possiblePassword.isEmpty()) {
                    ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Failed to find password.");
                    return;
                }
                Account account;
                if (this.getRegistry().get(Minecraft.getMinecraft().session.username) != null) {
                    account = this.getRegistry().get(Minecraft.getMinecraft().session.username);
                    if (account == null) return;
                    if (account.getPassword() != null && !account.getPassword().isEmpty() && Objects.equals(account.getPassword(), this.possiblePassword))
                        return;
                    account.setPassword(this.possiblePassword);
                    ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Updated password for " + account.getUsername());
                } else {
                    account = new Account(Minecraft.getMinecraft().session.username);
                    account.setPassword(this.possiblePassword);
                    this.register(account);
                    ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Added account " + account.getUsername());
                }
                this.saveAccounts();
            } else if (normalMessage.equalsIgnoreCase("Wrong password.")) {
                this.possiblePassword = "";
            }
        }
    });

    @Subscribe
    public final Listener<OutgoingPacketEvent> outgoingPacketListener = new Listener<>(OutgoingPacketEvent.class, event -> {
        final Packet packet = event.getPacket();

        if (packet instanceof Packet3Chat) {
            final Packet3Chat chat = (Packet3Chat) packet;

            // format "/login password"
            if (chat.message.startsWith("/login") || chat.message.startsWith("/register")) {
                String[] args = chat.message.split(" ");
                if (args.length == 2) {
                    this.possiblePassword = args[1];
                }
            }
        }
    });
}
