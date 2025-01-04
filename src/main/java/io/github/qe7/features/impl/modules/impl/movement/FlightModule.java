package io.github.qe7.features.impl.modules.impl.movement;

import io.github.qe7.events.UpdateEvent;
import io.github.qe7.events.packet.OutgoingPacketEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Packet10Flying;
import org.lwjgl.input.Keyboard;

public final class FlightModule extends Module {

    public FlightModule() {
        super("Flight", "Allows you to fly like super man.", ModuleCategory.MOVEMENT);
    }

    @Subscribe
    public final Listener<UpdateEvent> onUpdate = new Listener<>(event -> {
        final Minecraft mc = Minecraft.getMinecraft();

        mc.thePlayer.fallDistance = 0.0f;
        mc.thePlayer.motionY = 0;

        if (mc.currentScreen != null) return;

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            mc.thePlayer.motionY = -0.15;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            mc.thePlayer.motionY = 0.15;
        } else {
            // setting it here stops the player from jumping when trying to fly up
            mc.thePlayer.onGround = true;
        }
    });

    @Subscribe
    public final Listener<OutgoingPacketEvent> outgoingPacketEventListener = new Listener<>(event -> {
        if (!(event.getPacket() instanceof Packet10Flying)) return;
        Packet10Flying packet = (Packet10Flying) event.getPacket();
        packet.onGround = true;
    });
}
