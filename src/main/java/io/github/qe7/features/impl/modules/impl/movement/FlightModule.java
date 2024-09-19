package io.github.qe7.features.impl.modules.impl.movement;

import io.github.qe7.events.UpdateEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;

public class FlightModule extends Module {

    public FlightModule() {
        super("Flight", "Allows you to fly like super man.", ModuleCategory.MOVEMENT);
    }

    @Subscribe
    public final Listener<UpdateEvent> onUpdate = new Listener<>(event -> {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer != null) {
            mc.thePlayer.motionY = 0;
            mc.thePlayer.onGround = true;
        }
    });
}
