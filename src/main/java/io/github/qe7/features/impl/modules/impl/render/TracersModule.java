package io.github.qe7.features.impl.modules.impl.render;

import io.github.qe7.Hephaestus;
import io.github.qe7.events.render.RenderGlobalEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.BooleanSetting;
import io.github.qe7.utils.RenderUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

import java.awt.*;

public final class TracersModule extends Module {

    private final BooleanSetting player = new BooleanSetting("Player", true);
    private final BooleanSetting mob = new BooleanSetting("Mob", true);
    private final BooleanSetting animal = new BooleanSetting("Animal", true);
    private final BooleanSetting item = new BooleanSetting("Item", true);

    public TracersModule() {
        super("Tracers", "Renders a line to the feet (feet lover) of entities.", ModuleCategory.RENDER);
    }

    @Subscribe
    public final Listener<RenderGlobalEvent> renderGlobalEventListener = new Listener<>(event -> {
        for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (entity == Minecraft.getMinecraft().thePlayer) {
                continue;
            }

            final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * Minecraft.getMinecraft().timer.renderPartialTicks - RenderManager.renderPosX;
            final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * Minecraft.getMinecraft().timer.renderPartialTicks - RenderManager.renderPosY;
            final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * Minecraft.getMinecraft().timer.renderPartialTicks - RenderManager.renderPosZ;

            if (entity instanceof EntityPlayer && player.getValue()) {
                final Color color = Hephaestus.getInstance().getRelationManager().isFriend(((EntityPlayer) entity).username) ? new Color(63, 124, 182) : Hephaestus.getInstance().getRelationManager().isEnemy(((EntityPlayer) entity).username) ? new Color(255, 0, 0) : Color.WHITE;
                RenderUtil.drawTracerLine(x, y, z, color, 0.2f);
            } else if (entity instanceof EntityMob && mob.getValue()) {
                RenderUtil.drawTracerLine(x, y, z, new Color(255, 150, 0), 0.2f);
            } else if (entity instanceof EntityAnimal && animal.getValue()) {
                RenderUtil.drawTracerLine(x, y, z, new Color(0, 255, 0), 0.2f);
            } else if (entity instanceof EntityItem && item.getValue()) {
                RenderUtil.drawTracerLine(x, y, z, new Color(103, 103, 103), 0.2f);
            }
        }
    });
}
