package io.github.qe7.features.impl.modules.impl.misc;

import io.github.qe7.Hephaestus;
import io.github.qe7.events.packet.OutgoingPacketEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.BooleanSetting;
import io.github.qe7.features.impl.modules.impl.exploit.Slot9Module;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet14BlockDig;

public final class AutoToolModule extends Module {

    private final BooleanSetting returnOnSlot9 = new BooleanSetting("Disable on Slot 9", false);

    public AutoToolModule() {
        super("AutoTool", "Automatically switches to the best tool.", ModuleCategory.MISC);
    }

    @Subscribe
    public final Listener<OutgoingPacketEvent> outgoingPacketEventListener = new Listener<>(event -> {
        if (returnOnSlot9.getValue() && Hephaestus.getInstance().getModuleManager().getRegistry().get(Slot9Module.class).isEnabled()) return;
        if (event.getPacket() instanceof Packet14BlockDig) {

            final Packet14BlockDig blockDig = (Packet14BlockDig) event.getPacket();

            final int blockID = Minecraft.getMinecraft().theWorld.getBlockId(blockDig.xPosition, blockDig.yPosition, blockDig.zPosition);

            if (blockID != 0) {
                float maxStrength = 0.1f;
                int bestToolSlot = Minecraft.getMinecraft().thePlayer.inventory.currentItem;

                for (int i = 0; i < 9; i++) {
                    ItemStack itemStack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(i);
                    if (itemStack != null) {
                        float strength = itemStack.getStrVsBlock(Block.blocksList[blockID]);
                        if (strength > maxStrength) {
                            maxStrength = strength;
                            bestToolSlot = i;
                        }
                    }
                }

                Minecraft.getMinecraft().thePlayer.inventory.currentItem = bestToolSlot;
            }
        }
    });
}
