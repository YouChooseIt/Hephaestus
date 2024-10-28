package io.github.qe7.features.impl.modules.impl.render;

import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import net.minecraft.client.Minecraft;

public class FullBrightModule extends Module {

    public FullBrightModule() {
        super("FullBright", "Brightens your life, like that one girl does ;)", ModuleCategory.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }
}
