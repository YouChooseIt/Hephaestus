package io.github.qe7.features.impl.modules.impl.render;

import io.github.qe7.Hephaestus;
import io.github.qe7.events.render.RenderFogEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.BooleanSetting;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;

public final class NoRenderModule extends Module {

    private static final BooleanSetting fog = new BooleanSetting("Fog", true);

    public NoRenderModule() {
        super("NoRender", "Disables rendering of... stuff.", ModuleCategory.RENDER);
    }

    public static boolean isFogEnabled() {
        return Hephaestus.getInstance().getModuleManager().getRegistry().get(NoRenderModule.class).isEnabled() && fog.getValue();
    }
}
