package io.github.qe7.features.impl.modules.impl.misc;


import io.github.qe7.events.UpdateEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.BooleanSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.DoubleSetting;
import io.github.qe7.utils.PacketUtil;
import io.github.qe7.utils.PlayerUtil;
import it.unimi.dsi.fastutil.booleans.BooleanSet;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.PlayerControllerMP;
import org.lwjgl.input.Mouse;

import java.util.function.BooleanSupplier;

public final class FastBreakModule extends Module {

    private final BooleanSetting instant = new BooleanSetting("Instant", true);

    private final BooleanSupplier instantSupplier = () -> !instant.getValue();

    private final DoubleSetting damage = new DoubleSetting("Damage", 1.0, 0.1, 1.0, 0.1).supplyIf(instantSupplier);

    public FastBreakModule() {
        super("FastBreak", "Mines blocks faster or instantly.", ModuleCategory.MISC);
    }

    @Subscribe
    public final Listener<UpdateEvent> livingUpdateListener = new Listener<>(event -> {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.objectMouseOver == null) {
            return;
        }

        if (mc.currentScreen != null) {
            return;
        }

        if (mc.objectMouseOver.typeOfHit != EnumMovingObjectType.TILE) {
            return;
        }

        if (instant.getValue()) {
            if (Mouse.isButtonDown(0)) {
                PlayerUtil.destroyBlockInstant(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ, mc.objectMouseOver.sideHit);
            }
        } else {
            mc.leftClickCounter = 0;

            if (mc.playerController instanceof PlayerControllerMP) {
                PlayerControllerMP playerController = (PlayerControllerMP) mc.playerController;

                if (playerController.curBlockDamageMP == 0.0F) {
                    return;
                }

                if (playerController.curBlockDamageMP < damage.getValue().floatValue()) {
                    playerController.curBlockDamageMP = damage.getValue().floatValue();
                }
            }
        }

    });
}
