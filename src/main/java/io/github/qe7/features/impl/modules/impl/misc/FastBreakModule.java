package io.github.qe7.features.impl.modules.impl.misc;


import io.github.qe7.events.UpdateEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.PlayerControllerMP;
import net.minecraft.src.PlayerControllerSP;

public class FastBreakModule extends Module {

    //private final DoubleSetting minDamage = new DoubleSetting("Start damage", 0.5, 0.1, 1.0, 0.1);

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

        if(mc.playerController instanceof PlayerControllerMP) {
            PlayerControllerMP playerController = (PlayerControllerMP) mc.playerController;

            if (playerController.curBlockDamageMP == 0.0F) {
                return;
            }

//            if (playerController.curBlockDamageMP < minDamage.getValue()) {
//                playerController.curBlockDamageMP = minDamage.getValue().floatValue();
//            }
            if (playerController.curBlockDamageMP < 1.0f) {
                playerController.curBlockDamageMP = 1.0f;
            }
        } else if(mc.playerController instanceof PlayerControllerSP) {
            PlayerControllerSP playerController = (PlayerControllerSP) mc.playerController;

            if (playerController.curBlockDamage == 0.0F) {
                return;
            }
//            if (playerController.curBlockDamage < minDamage.getValue()) {
//                playerController.curBlockDamage = minDamage.getValue().floatValue();
//            }
            if (playerController.curBlockDamage < 1.0f) {
                playerController.curBlockDamage = 1.0f;
            }
        }
    });
}
