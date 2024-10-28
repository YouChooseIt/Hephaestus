package io.github.qe7.features.impl.modules.impl.misc;

import io.github.qe7.events.UpdateEvent;
import io.github.qe7.events.render.RenderScreenEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ItemFood;

public class AutoHealModule extends Module {

    public AutoHealModule() {
        super("AutoHeal", "Automatically heals the player under a given health", ModuleCategory.MISC);
    }

    @Subscribe
    public final Listener<RenderScreenEvent> renderScreenEventListener = new Listener<>(event -> {
        this.setSuffix(12 + "/" + Minecraft.getMinecraft().thePlayer.health);
    });

    @Subscribe
    public final Listener<UpdateEvent> updateEventListener = new Listener<>(event -> {
        if (Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null) {
            return;
        }

        // check if the player has less then or equal to healHealth
        if (Minecraft.getMinecraft().thePlayer.health >= 12) {
            return;
        }

        // check if the player has food in the hotbar
        for (int i = 0; i < 9; i++) {
            if (Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(i) == null) {
                continue;
            }
            if (Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemFood) {
                final int oldSlot = Minecraft.getMinecraft().thePlayer.inventory.currentItem;
                Minecraft.getMinecraft().thePlayer.inventory.currentItem = i;
                Minecraft.getMinecraft().playerController.sendUseItem(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(i));
                Minecraft.getMinecraft().thePlayer.inventory.currentItem = oldSlot;
                return;
            }
        }
    });
}
