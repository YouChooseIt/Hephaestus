package io.github.qe7.features.impl.modules.impl.build;

import io.github.qe7.Hephaestus;
import io.github.qe7.events.UpdateEvent;
import io.github.qe7.events.packet.OutgoingPacketEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.BooleanSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.IntSetting;
import io.github.qe7.utils.PlayerUtil;
import io.github.qe7.utils.math.TimerUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import java.util.Queue;
import java.util.LinkedList;

public final class ExtrudeModule extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    private final IntSetting radius = new IntSetting("Radius", 4, 1, 7, 1);

    private final IntSetting height = new IntSetting("Height", 4, 1, 7, 1);

    private final IntSetting delay = new IntSetting("Delay (ms)", 100, 10, 250, 10);

    private final BooleanSetting ignoreWater = new BooleanSetting("Ignore water", false);

    private final BooleanSetting ignoreLava = new BooleanSetting("Ignore lava", false);

    private final TimerUtil timerUtil = new TimerUtil();

    public ExtrudeModule() {
        super("Extrude", "Completes placed block up in the places where such a block is located on the plane.", ModuleCategory.BUILD);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        pendingPlacements.clear();
    }

    private final Queue<BlockPlacement> pendingPlacements = new LinkedList<>();
    private long lastPlaceTime = 0;
    
    private static class BlockPlacement {
        final int x, y, z;
        
        BlockPlacement(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    @Subscribe
    public final Listener<UpdateEvent> updateEventListener = new Listener<>(event -> {
        if (!pendingPlacements.isEmpty() && 
            System.currentTimeMillis() - lastPlaceTime >= 50) {
            
            BlockPlacement placement = pendingPlacements.poll();
            if (placement != null) {
                placeBlock(placement.x, placement.y, placement.z);
                lastPlaceTime = System.currentTimeMillis();
            }
        }
    });

    @Subscribe
    public final Listener<OutgoingPacketEvent> outgoingPacketEventListener = new Listener<>(event -> {
        if (!(event.getPacket() instanceof Packet15Place)) return;
        if (mc.thePlayer == null) return;

        ItemStack currentItem = mc.thePlayer.inventory.getCurrentItem();
        if (currentItem == null) {
            return;
        }

        if (!this.timerUtil.hasTimeElapsed(this.delay.getValue(), true)) {
            return;
        }

        Packet15Place blockPlace = (Packet15Place) event.getPacket();

        int placedBlockId = blockPlace.itemStack.itemID;

        int centerX = MathHelper.floor_double(mc.thePlayer.posX);
        int centerZ = MathHelper.floor_double(mc.thePlayer.posZ);
        int baseY = blockPlace.yPosition;

        int rad = this.radius.getValue();

        for (int x = centerX - rad; x <= centerX + rad; x++) {
            for (int z = centerZ - rad; z <= centerZ + rad; z++) {
                if (isBlockEqual(x, baseY, z, placedBlockId)) {
                    buildColumn(x, baseY + 1, z, placedBlockId);
                }
            }
        }
    });

    private boolean isBlockEqual(int x, int y, int z, int blockID) {
        return mc.theWorld.getBlockId(x, y, z) == blockID;
    }

    private void buildColumn(int x, int y, int z, int blockID) {
        System.out.println("[Extrude Module] Building column at " + x + ", " + y + ", " + z);

        int centerY = MathHelper.floor_double(mc.thePlayer.posY);
        int maxHeight = Math.min(y + this.height.getValue() - 1, centerY + this.height.getValue() - 1);
        
        for (int currentY = y; currentY <= maxHeight; currentY++) {
            if (canPlaceBlock(x, currentY, z)) {
                System.out.println("[Extrude Module] Placing block at " + x + ", " + currentY + ", " + z);
                pendingPlacements.offer(new BlockPlacement(x, currentY, z));
            } else {
                System.out.println("[Extrude Module] Cant place block at: " + x + ", " + currentY + ", " + z);
            }
        }
        System.out.println("[Extrude Module] - Column built at Y: " + y + "-");
    }

    private boolean canPlaceBlock(int x, int y, int z) {
        int id = mc.theWorld.getBlockId(x, y, z);
    
        if (
            ((id == Block.waterMoving.blockID || id == Block.waterStill.blockID) && this.ignoreWater.getValue()) ||
            ((id == Block.lavaMoving.blockID || id == Block.lavaStill.blockID) && this.ignoreLava.getValue())
        ) {
            return false;
        }
    
        return id == 0 || id == Block.waterMoving.blockID || id == Block.waterStill.blockID 
            || id == Block.lavaMoving.blockID || id == Block.lavaStill.blockID;
    }

    public void placeBlock(int x, int y, int z) {
        EntityPlayerSP player = mc.thePlayer;
        World world = mc.theWorld;
        ItemStack currentItem = player.inventory.getCurrentItem();

        if (currentItem == null) {
            return;
        }

        int[][] directions = {
            {x - 1, y, z, 5}, // east
            {x + 1, y, z, 4}, // west
            {x, y, z - 1, 3}, // south
            {x, y, z + 1, 2}, // north
            {x, y - 1, z, 1}, // up
            {x, y + 1, z, 0}  // down
        };

        for (int[] dir : directions) {
            int px = dir[0], py = dir[1], pz = dir[2], side = dir[3];
            if (!canPlaceBlock(px, py, pz)) {
                mc.playerController.sendPlaceBlock(player, world, currentItem, px, py, pz, side);
                PlayerUtil.placeBlock(px, py, pz, side);
                return;
            }
        }
    }
}