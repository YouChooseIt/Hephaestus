package io.github.qe7.features.impl.modules.impl.movement;

import io.github.qe7.events.UpdateEvent;
import io.github.qe7.events.render.RenderScreenEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.EnumSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.interfaces.IEnumSetting;
import io.github.qe7.utils.MovementUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;

public final class SpeedModule extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.PULSE);

    private int pulseTick = 0;

    public SpeedModule() {
        super("Speed", "Speeds up your life! :D.", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        this.pulseTick = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        Minecraft.getMinecraft().timer.timerSpeed = 1F;
    }

    @Subscribe
    public final Listener<RenderScreenEvent> renderScreenEventListener = new Listener<>(event -> {
        this.setSuffix(mode.getValue().getName());
    });

    @Subscribe
    public final Listener<UpdateEvent> updateEventListener = new Listener<>(event -> {
        if (Minecraft.getMinecraft().thePlayer == null) return;

        if (!MovementUtil.isMoving()) return;

        switch (mode.getValue()) {
            case PULSE:
                if (pulseTick <= 35) {
                    Minecraft.getMinecraft().timer.timerSpeed = 2F;
                } else if (pulseTick == 176) {
                    pulseTick = 0;
                } else {
                    Minecraft.getMinecraft().timer.timerSpeed = 1F;
                }

                pulseTick++;
                break;
            case CONSTANT:
                Minecraft.getMinecraft().timer.timerSpeed = 1.117F;
                break;
        }
    });

    private enum Mode implements IEnumSetting {
        PULSE("Pulse"),
        CONSTANT("Constant");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Enum<?> getEnum(String name) {
            return Mode.valueOf(name);
        }
    }
}
