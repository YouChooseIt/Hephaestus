package io.github.qe7.features.impl.modules.impl.misc;

import io.github.qe7.events.packet.IncomingPacketEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.utils.ChatUtil;
import io.github.qe7.utils.math.TimerUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.src.Packet1Login;

public class AutoListModule extends Module {

    private final TimerUtil timerUtil = new TimerUtil();

    private boolean shouldSendList = false;

    public AutoListModule() {
        super("AutoList", "Automatically prints the \"list\" command when joining.", ModuleCategory.MISC);
    }

    @Subscribe
    public final Listener<IncomingPacketEvent> incomingPacketEvent = new Listener<>(IncomingPacketEvent.class, event -> {
        if (event.getPacket() instanceof Packet1Login) {
            shouldSendList = true;
            timerUtil.reset();
        }

        if (shouldSendList && timerUtil.hasTimeElapsed(10000, false)) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Sending /list command...");
            ChatUtil.sendMessage("/list");
            shouldSendList = false;
        }
    });
}
