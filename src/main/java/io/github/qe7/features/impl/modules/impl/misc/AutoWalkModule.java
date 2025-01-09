package io.github.qe7.features.impl.modules.impl.misc;

import io.github.qe7.events.PlayerMoveSetEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;

public final class AutoWalkModule extends Module {

    public AutoWalkModule() {
        super("AutoWalk", "Automatically walks forwards for you", ModuleCategory.MISC);
    }

    @Subscribe
    public final Listener<PlayerMoveSetEvent> playerMoveSetListener = new Listener<>(event -> {
        event.setCancelled(true);
        Minecraft.getMinecraft().thePlayer.moveStrafing = 0;
        Minecraft.getMinecraft().thePlayer.moveForward = 1;
    });
}
