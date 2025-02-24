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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class ExtrudeModule extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    private final IntSetting radius = new IntSetting("Radius", 4, 1, 5, 1);

    private final IntSetting height = new IntSetting("Height", 5, 1, 7, 1);

    private final IntSetting delay = new IntSetting("Delay (ms)", 100, 10, 250, 10);

    private final BooleanSetting ignoreWater = new BooleanSetting("Ignore water", false);

    private final BooleanSetting ignoreLava = new BooleanSetting("Ignore lava", false);

    private final TimerUtil timerUtil = new TimerUtil();

    private boolean processing = false;

    public ExtrudeModule() {
        super("Extrude", "Copies the pattern on plane up the Y-axis, duplicating the blocks layer by layer.", ModuleCategory.BUILD);
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
            System.currentTimeMillis() - lastPlaceTime >= 100) {
            
            BlockPlacement placement = pendingPlacements.poll();
            if (placement != null) {
                processing = true;

                // System.out.println("[Extrude Module] <<<<<<<<<<<<<BLOCK PLACED>>>>>>>>>>>>>");
                // System.out.println("[Extrude Module] Block placed: " + placement.x + ", " + placement.y + ", " + placement.z);
                // placeBlock(placement.x, placement.y, placement.z);
                lastPlaceTime += 100;
            }
        } else if (pendingPlacements.isEmpty() && processing &&
            System.currentTimeMillis() - lastPlaceTime >= 300) {

            // System.out.println("[Extrude Module] Proccess finished.");
            processing = false;
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

        if (processing) {
            // System.out.println("[Extrude Module] Skipped.");
            return;
        }

        //module
        processing = true;
        lastPlaceTime = System.currentTimeMillis();

        //block
        int placedBlockId = blockPlace.itemStack.itemID;
        int[] placedBlockCoords = getPlacedBlockCoords(blockPlace.xPosition, blockPlace.yPosition, blockPlace.zPosition, blockPlace.direction);

        //player
        int centerX = MathHelper.floor_double(mc.thePlayer.posX);
        int centerZ = MathHelper.floor_double(mc.thePlayer.posZ);
        int baseY = placedBlockCoords[1] - 1;

        // System.out.println("[Extrude Module] Placed block: "
        //     + placedBlockCoords[0] + ", " + placedBlockCoords[1] + ", " + placedBlockCoords[2]
        // );
        // System.out.println("[Extrude Module] Player at: "
        //     + centerX + ", " + MathHelper.floor_double(mc.thePlayer.posY) + ", " + centerZ
        // );
        // System.out.println("[Extrude Module] Starting searching from Y: " + baseY);

        int rad = this.radius.getValue();

        for (int x = centerX - rad; x <= centerX + rad; x++) {
            for (int z = centerZ - rad; z <= centerZ + rad; z++) {
                if (isBlockEqual(x, baseY, z, placedBlockId)) {
                    int radius = Math.abs(x - centerX);
                    int globalMaxHeight = this.height.getValue();
                    int columnMaxHeight = (globalMaxHeight <= 5) ? globalMaxHeight : Math.max(7 - Math.max(0, radius - 2), 5);
                    buildColumn(x, baseY + 1, z, placedBlockId, columnMaxHeight);
                }
            }
        }
    });

    public int[] getPlacedBlockCoords(int x, int y, int z, int side) {
        Map<Integer, int[]> directions = new HashMap<>();
        directions.put(1, new int[]{x, y + 1, z}); // up
        directions.put(0, new int[]{x, y - 1, z}); // down
        directions.put(5, new int[]{x + 1, y, z}); // east
        directions.put(4, new int[]{x - 1, y, z}); // west
        directions.put(3, new int[]{x, y, z + 1}); // south
        directions.put(2, new int[]{x, y, z - 1}); // north

        return directions.getOrDefault(side, new int[] {x, y, z});
    }

    private boolean isBlockEqual(int x, int y, int z, int blockID) {
        return mc.theWorld.getBlockId(x, y, z) == blockID;
    }

    private void buildColumn(int x, int y, int z, int blockID, int columnMaxHeight) {
        // System.out.println("[Extrude Module] Building column at " + x + ", " + y + ", " + z);

        int centerX = MathHelper.floor_double(mc.thePlayer.posX);
        int centerZ = MathHelper.floor_double(mc.thePlayer.posZ);
        int centerY = MathHelper.floor_double(mc.thePlayer.posY);
        // System.out.println("[Extrude Module] centerY " + centerY);

        int calcMaxHeight = Math.min(y + columnMaxHeight - 1, centerY + 7);
        // System.out.println("[Extrude Module] calcMaxHeight " + calcMaxHeight);

        int rad = this.radius.getValue();

        if (Math.abs(x - centerX) > rad || Math.abs(z - centerZ) > rad) {
            return;
        }

        for (int currentY = y; currentY <= calcMaxHeight; currentY++) {
            if (canPlaceBlock(x, currentY, z)) {
                // System.out.println("[Extrude Module] Added to queue: " + x + ", " + currentY + ", " + z);
                pendingPlacements.offer(new BlockPlacement(x, currentY, z));
            } else {
                // System.out.println("[Extrude Module] Cant place block at: " + x + ", " + currentY + ", " + z);
            }
        }
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

        int centerX = MathHelper.floor_double(player.posX);
        int centerZ = MathHelper.floor_double(player.posZ);
        int centerY = MathHelper.floor_double(player.posY);
        if (x == centerX && (y == centerY || y == centerY - 1) && z == centerZ) {
            return;
        }

        int[][] directions = {
            {x, y - 1, z, 1}, // up
            {x, y + 1, z, 0}, // down
            {x - 1, y, z, 5}, // east
            {x + 1, y, z, 4}, // west
            {x, y, z - 1, 3}, // south
            {x, y, z + 1, 2}  // north
        };

        for (int[] dir : directions) {
            int px = dir[0], py = dir[1], pz = dir[2], side = dir[3];
            if (!canPlaceBlock(px, py, pz)) {
                mc.playerController.sendPlaceBlock(player, world, currentItem, px, py, pz, side);
                return;
            }
        }
    }
}