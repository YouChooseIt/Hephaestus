package io.github.qe7.features.impl.modules.impl.movement;

import io.github.qe7.events.UpdateEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;

public final class YawModule extends Module {

    public YawModule() {
        super("Yaw", "Locks the player's Yaw to the closest 45* cardinal", ModuleCategory.MOVEMENT);
    }

    @Subscribe
    public final Listener<UpdateEvent> livingUpdateEventListener = new Listener<>(event -> {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null) return;

        mc.thePlayer.rotationYaw = (Math.round(mc.thePlayer.rotationYaw / 45) * 45);
    });
}
