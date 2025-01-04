package io.github.qe7.features.impl.modules.impl.misc;

import io.github.qe7.Hephaestus;
import io.github.qe7.accounts.Account;
import io.github.qe7.events.packet.IncomingPacketEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.utils.ChatUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Packet3Chat;

public final class AutoLoginModule extends Module {

    public AutoLoginModule() {
        super("AutoLogin", "Automatically logs you in when you join.", ModuleCategory.MISC);
    }

    @Subscribe
    public final Listener<IncomingPacketEvent> incomingPacketEvent = new Listener<>(IncomingPacketEvent.class, event -> {
        if (event.getPacket() instanceof Packet3Chat) {
            Packet3Chat chat = (Packet3Chat) event.getPacket();

            if (chat.message.startsWith("§c") && chat.message.contains("Please login with")) {
                final String normalizedName = Minecraft.getMinecraft().session.username;

                Account account = Hephaestus.getInstance().getAccountManager().getRegistry().get(normalizedName);

                if (account != null) {
                    ChatUtil.sendMessage("/login " + account.getPassword());
                    ChatUtil.addPrefixedMessage("Auto Login", "Logged in as " + normalizedName);
                } else {
                    ChatUtil.addPrefixedMessage("Auto Login", "No account found for " + normalizedName);
                }
            }
        }
    });
}
