package io.github.qe7.features.impl.modules.impl.render;

import io.github.qe7.events.render.RenderLivingLabelEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.utils.RenderUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;

public final class NameTagsModule extends Module {

    public NameTagsModule() {
        super("NameTags", "Displays a name plate that is easier to see.", ModuleCategory.RENDER);
    }

    @Subscribe
    public final Listener<RenderLivingLabelEvent> renderLivingLabelEventListener = new Listener<>(RenderLivingLabelEvent.class, event -> {
        if (Minecraft.getMinecraft().theWorld == null) {
            return;
        }

        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }

        final EntityPlayer player = (EntityPlayer) event.getEntity();

        RenderUtil.drawName(player, event.getRenderManager(), event.getX(), event.getY(), event.getZ(), 2.0F);
        event.cancel();
    });
}
