package io.github.qe7.features.impl.modules.impl.render;

import io.github.qe7.events.render.RenderCameraClipEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.BooleanSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.DoubleSetting;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;

public final class CameraModule extends Module {

    private final BooleanSetting clip = new BooleanSetting("Clip", true);

    public static DoubleSetting distance = new DoubleSetting("Distance", 4.0, 0.1, 10.0, 0.1);

    public CameraModule() {
        super("Camera", "Modifies attributes of the camera (third person view).", ModuleCategory.RENDER);
    }

    @Subscribe
    public final Listener<RenderCameraClipEvent> renderCameraClipEventListener = new Listener<>(event -> {
        event.setCancelled(clip.getValue());
    });
}
