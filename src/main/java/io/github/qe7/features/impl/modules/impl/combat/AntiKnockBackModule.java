package io.github.qe7.features.impl.modules.impl.combat;

import io.github.qe7.events.packet.IncomingPacketEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Packet28EntityVelocity;

public class AntiKnockBackModule extends Module {

    public AntiKnockBackModule() {
        super("NoKnockBack", "Negates all knock back given to the player", ModuleCategory.COMBAT);
    }

    @Subscribe
    public final Listener<IncomingPacketEvent> incomingPacketEventListener = new Listener<>(event -> {
        if (event.getPacket() instanceof Packet28EntityVelocity && Minecraft.getMinecraft().thePlayer != null) {
            Packet28EntityVelocity packet = (Packet28EntityVelocity) event.getPacket();
            if (packet.entityId == Minecraft.getMinecraft().thePlayer.entityId) {
                event.cancel();
            }
        }
    });
}
