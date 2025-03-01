package io.github.qe7.features.impl.modules.impl.build;

import io.github.qe7.events.UpdateEvent;
import io.github.qe7.events.render.RenderScreenEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.BooleanSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.EnumSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.IntSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.interfaces.IEnumSetting;
import io.github.qe7.utils.PlayerUtil;
import io.github.qe7.utils.math.TimerUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

public final class ScaffoldModule extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    private final EnumSetting<PlaceModeEnum> placeMode = new EnumSetting<>("Place mode", PlaceModeEnum.RECTANGLE);

    private final IntSetting radius = new IntSetting("Radius", 1, 1, 5, 1);

    private final IntSetting delay = new IntSetting("Delay (ms)", 100, 10, 250, 10);

    private final IntSetting yOffset = new IntSetting("Y offset", 0, -5, 5, 1);

    private final BooleanSetting ignoreWater = new BooleanSetting("Ignore water", false);

    private final BooleanSetting ignoreLava = new BooleanSetting("Ignore lava", false);

    private final TimerUtil timerUtil = new TimerUtil();

    private int oldSlot = -1;

    public ScaffoldModule() {
        super("Scaffold", "Places blocks under the player.", ModuleCategory.BUILD);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.oldSlot != -1) {
            mc.thePlayer.inventory.currentItem = this.oldSlot;
            this.oldSlot = -1;
        }
    }

    @Subscribe
    public final Listener<RenderScreenEvent> renderScreenEventListener = new Listener<>(event -> {
        this.setSuffix(this.placeMode.getValue().getName());
    });

    @Subscribe
    public final Listener<UpdateEvent> updateEventListener = new Listener<>(event -> {
        if (mc.thePlayer == null) return;

        int centerX = MathHelper.floor_double(mc.thePlayer.posX);
        int centerY = MathHelper.floor_double(mc.thePlayer.posY) - 2 + this.yOffset.getValue();
        int centerZ = MathHelper.floor_double(mc.thePlayer.posZ);
        int radius = this.radius.getValue();

        ItemStack currentItem = mc.thePlayer.inventory.getCurrentItem();
        if (currentItem == null) {
            return;
        }

        if (!this.timerUtil.hasTimeElapsed(this.delay.getValue(), true)) {
            return;
        }

        switch (this.placeMode.getValue()) {
            case RECTANGLE:
                for (int i = centerX - radius; i <= centerX + radius; i++) {
                    for (int j = centerZ - radius; j <= centerZ + radius; j++) {
                        if (canPlaceBlock(i, centerY, j)) {
                            placeBlock(i, centerY, j);
                        }
                    }
                }

                break;
            case TRIANGLE:
                int maxLayers = radius * 2;

                for (int layer = 0; layer <= maxLayers; layer++) {
                    for (int offset = 0; offset <= layer; offset++) {
                        for (int i = 0; i <= offset; i++) {
                            int[][] positions = {
                                {centerX + i, centerZ + (offset - i)},
                                {centerX + i, centerZ - (offset - i)},
                                {centerX - i, centerZ + (offset - i)},
                                {centerX - i, centerZ - (offset - i)}
                            };
                            
                            for (int[] pos : positions) {
                                int x = pos[0];
                                int z = pos[1];
                                
                                if (Math.abs(x - centerX) <= radius && 
                                    Math.abs(z - centerZ) <= radius) {
                                    
                                    if (canPlaceBlock(x, centerY, z)) {
                                        placeBlock(x, centerY, z);
                                    }
                                }
                            }
                        }
                    }
                }

                break;
        }
    });

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
                return;
            }
        }
    }

    public enum PlaceModeEnum implements IEnumSetting {
        RECTANGLE("Rectangle"),
        TRIANGLE("Triangle");

        private final String name;

        PlaceModeEnum(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Enum<?> getEnum(String name) {
            return PlaceModeEnum.valueOf(name);
        }
    }
}
