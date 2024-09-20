package io.github.qe7.features.impl.modules.impl.movement;

import io.github.qe7.events.UpdateEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;

public class StepModule extends Module {

    public StepModule() {
        super("Step", "Allows the local player to step up higher", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        Minecraft.getMinecraft().thePlayer.stepHeight = 0.5f;
    }

    @Subscribe
    public final Listener<UpdateEvent> onUpdate = new Listener<>(event -> {
        Minecraft.getMinecraft().thePlayer.stepHeight = 1.0f;
    });
}
