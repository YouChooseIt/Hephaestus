package io.github.qe7.features.impl.modules.impl.movement;

import io.github.qe7.events.UpdateEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.utils.MovementUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;

public class StrafeModule extends Module {

    public StrafeModule() {
        super("Strafe", "Strafe like you're in counter-strike: source", ModuleCategory.MOVEMENT);
    }

    @Subscribe
    public final Listener<UpdateEvent> updateEventListener = new Listener<>(event -> {
        if (Minecraft.getMinecraft().thePlayer == null) return;

        if (Minecraft.getMinecraft().thePlayer.onGround) return;

        if (!MovementUtil.isMoving()) return;

        MovementUtil.setSpeed(0.14D);
    });
}
