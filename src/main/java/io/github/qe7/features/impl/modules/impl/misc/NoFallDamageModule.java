package io.github.qe7.features.impl.modules.impl.misc;

import io.github.qe7.events.UpdateEvent;
import io.github.qe7.events.packet.OutgoingPacketEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Packet10Flying;

public final class NoFallDamageModule extends Module {

    public NoFallDamageModule() {
        super("NoFallDamage", "Removes fall damage... duh.", ModuleCategory.MISC);
    }

    @Subscribe
    public final Listener<OutgoingPacketEvent> outgoingPacketEventListener = new Listener<>(event -> {
        if (!(event.getPacket() instanceof Packet10Flying)) return;
        Packet10Flying packet = (Packet10Flying) event.getPacket();
        packet.onGround = true;
    });

    @Subscribe
    public final Listener<UpdateEvent> onUpdate = new Listener<>(event -> {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) mc.thePlayer.fallDistance = 0.0f;
    });
}
