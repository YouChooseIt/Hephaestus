package io.github.qe7.features.impl.modules.impl.misc;

import io.github.qe7.events.UpdateEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.Packet10Flying;

public class FastPortalsModule extends Module {
    public FastPortalsModule() {
        super("FastPortals", "Makes portals tp faster", ModuleCategory.MISC);
    }

    @Subscribe
    public final Listener<UpdateEvent> onUpdate = new Listener<>(event -> {
        final Minecraft mc = Minecraft.getMinecraft();

        if(mc.theWorld.multiplayerWorld) {
            if (mc.thePlayer.timeInPortal > 0.0f && mc.thePlayer.timeUntilPortal == 0) {
                for(int i = 0; i < 20; i++)
                    ((EntityClientPlayerMP)mc.thePlayer).sendQueue.addToSendQueue(new Packet10Flying(true));
            }
        } else
            mc.thePlayer.timeInPortal = 1.0f;
    });
}
