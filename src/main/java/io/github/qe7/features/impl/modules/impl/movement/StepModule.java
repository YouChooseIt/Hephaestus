package io.github.qe7.features.impl.modules.impl.movement;

import io.github.qe7.events.UpdateEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;

public class StepModule extends Module {

    private float oldStepHeight;

    public StepModule() {
        super("Step", "Allows the local player to step up higher", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        oldStepHeight = Minecraft.getMinecraft().thePlayer.stepHeight;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        Minecraft.getMinecraft().thePlayer.stepHeight = oldStepHeight;
    }

    @Subscribe
    public final Listener<UpdateEvent> onUpdate = new Listener<>(event -> Minecraft.getMinecraft().thePlayer.stepHeight = 1.0f);
}
