package io.github.qe7.features.impl.modules.impl.misc;


import io.github.qe7.events.UpdateEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.DoubleSetting;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.PlayerControllerMP;

public class FastBreakModule extends Module {

    public FastBreakModule() {
        super("FastBreak", "Mines blocks faster", ModuleCategory.MISC);
    }

    @Subscribe
    public final Listener<UpdateEvent> livingUpdateListener = new Listener<>(event -> {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.objectMouseOver == null) {
            return;
        }

        mc.leftClickCounter = 0;

        if (mc.playerController instanceof PlayerControllerMP) {
            PlayerControllerMP playerController = (PlayerControllerMP) mc.playerController;

            if (playerController.curBlockDamageMP == 0.0F) {
                return;
            }

            if (playerController.curBlockDamageMP < 1.0f) {
                playerController.curBlockDamageMP = 1.0f;
            }
        }
    });
}
