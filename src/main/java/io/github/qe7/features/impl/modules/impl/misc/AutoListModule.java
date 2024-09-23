package io.github.qe7.features.impl.modules.impl.misc;

import io.github.qe7.events.packet.IncomingPacketEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.utils.ChatUtil;
import io.github.qe7.utils.math.TimerUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.src.Packet3Chat;

public class AutoListModule extends Module {

    private final TimerUtil timerUtil = new TimerUtil();

    private boolean shouldSendList = false;

    public AutoListModule() {
        super("AutoList", "Automatically prints the \"list\" command when joining.", ModuleCategory.MISC);
    }

    @Subscribe
    public final Listener<IncomingPacketEvent> incomingPacketEvent = new Listener<>(IncomingPacketEvent.class, event -> {
        if (event.getPacket() instanceof Packet3Chat) {
            Packet3Chat chat = (Packet3Chat) event.getPacket();

            System.out.println(chat.message);

            if (chat.message.startsWith("§c") && (chat.message.contains("Successful login!") || chat.message.contains("Session login")) ) {
                shouldSendList = true;
                timerUtil.reset();
            }
        }

        if (shouldSendList && timerUtil.hasTimeElapsed(1000, false)) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Sending /list command...");
            ChatUtil.sendMessage("/list");
            shouldSendList = false;
        }
    });
}
