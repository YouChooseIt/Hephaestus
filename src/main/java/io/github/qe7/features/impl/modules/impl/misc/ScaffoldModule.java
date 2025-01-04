package io.github.qe7.features.impl.modules.impl.misc;

import io.github.qe7.events.UpdateEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.IntSetting;
import io.github.qe7.utils.PlayerUtil;
import io.github.qe7.utils.math.TimerUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

public final class ScaffoldModule extends Module {

    private final IntSetting radius = new IntSetting("Radius", 1, 1, 5, 1);

    private final TimerUtil timerUtil = new TimerUtil();

    private int oldSlot = -1;

    public ScaffoldModule() {
        super("Scaffold", "Places blocks under the player.", ModuleCategory.MISC);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.oldSlot != -1) {
            Minecraft.getMinecraft().thePlayer.inventory.currentItem = this.oldSlot;
            this.oldSlot = -1;
        }
    }

    @Subscribe
    public final Listener<UpdateEvent> updateEventListener = new Listener<>(event -> {
        if (Minecraft.getMinecraft().thePlayer == null) return;

        int x = MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posX);
        int y = MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posY) - 2;
        int z = MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posZ);

        int radius = this.radius.getValue();

        if (!this.timerUtil.hasTimeElapsed(100, true)) {
            return;
        }

        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = z - radius; j <= z + radius; j++) {
                if (canPlaceBlock(i, y, j)) {
                    placeBlock(i, y, j);
                }
            }
        }
    });

    private boolean canPlaceBlock(int x, int y, int z) {
        int id = Minecraft.getMinecraft().theWorld.getBlockId(x, y, z);
        return id == 0 || id == 10 || id == 11 || id == 8 || id == 9;
    }

    public void placeBlock(int x, int y, int z) {
        if (!canPlaceBlock(x - 1, y, z)) {  // 5 = east
            Minecraft.getMinecraft().playerController.sendPlaceBlock(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem(), x - 1, y, z, 5);
            PlayerUtil.placeBlock(x - 1, y, z, 5);
            return;
        }
        if (!canPlaceBlock(x + 1, y, z)) { // 4 = west
            Minecraft.getMinecraft().playerController.sendPlaceBlock(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem(), x + 1, y, z, 4);
            PlayerUtil.placeBlock(x + 1, y, z, 4);
            return;
        }
        if (!canPlaceBlock(x, y, z - 1)) { // 3 = south
            Minecraft.getMinecraft().playerController.sendPlaceBlock(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem(), x, y, z - 1, 3);
            PlayerUtil.placeBlock(x, y, z - 1, 3);
            return;
        }
        if (!canPlaceBlock(x, y, z + 1)) { // 2 = north
            Minecraft.getMinecraft().playerController.sendPlaceBlock(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem(), x, y, z + 1, 2);
            PlayerUtil.placeBlock(x, y, z + 1, 2);
            return;
        }
        if (!canPlaceBlock(x, y - 1, z)) { // 1 = up
            Minecraft.getMinecraft().playerController.sendPlaceBlock(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem(), x, y - 1, z, 1);
            PlayerUtil.placeBlock(x, y - 1, z, 1);
        }
    }
}
