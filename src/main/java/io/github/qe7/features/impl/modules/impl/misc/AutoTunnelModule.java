package io.github.qe7.features.impl.modules.impl.misc;

import java.util.HashSet;

import io.github.qe7.events.PlayerMoveSetEvent;
import io.github.qe7.events.UpdateEvent;
import io.github.qe7.events.render.RenderWorldEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.BooleanSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.DoubleSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.EnumSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.IntSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.interfaces.IEnumSetting;
import io.github.qe7.type.BlockPos;
import io.github.qe7.type.Direction;
import io.github.qe7.utils.PlayerUtil;
import io.github.qe7.utils.RenderUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public final class AutoTunnelModule extends Module {

    public final Minecraft mc = Minecraft.getMinecraft();

    public DoubleSetting walkStart = new DoubleSetting("StartWalkingAfter", 2.0D, 1.0D, 8.0D, 1.0D);
    public DoubleSetting reach = new DoubleSetting("Reach", 4.0D, 1.0D, 8.0D, 0.1D);

    public IntSetting packetsPerTick = new IntSetting("MaxPacketsPerTick", 12, 1, 40, 1);

    public EnumSetting<ModesEnum> direction = new EnumSetting<>("Direction", ModesEnum.ZPOS);
    public EnumSetting<MineModeEnum> mineMode = new EnumSetting<>("MineMode", MineModeEnum.LEGAL);
    public EnumSetting<OrderEnum> order = new EnumSetting<>("Order", OrderEnum.DOWN_UP);
    public EnumSetting<PlaceModeEnum> placeMode = new EnumSetting<>("PlaceMode", PlaceModeEnum.SINGLE);

    public BooleanSetting disableClientPlace = new BooleanSetting("DisableClientPlace", true);
    public BooleanSetting alwaysCheckBoth = new BooleanSetting( "AlwaysCheckBoth", true);
    public BooleanSetting render = new BooleanSetting("Render", false);

    private Direction dir;

    public HashSet<BlockPos> breaking = new HashSet<>();
    public HashSet<BlockPos> waiting = new HashSet<>();

    public int xCur, yCur, zCur;
    public int xPlace, yPlace, zPlace;
    public int timeout = 0;
    public int facePlace;
    public int previousSlot = 0;
    public int packetsSent = 0;

    public boolean selected = false;
    public boolean place = false;
    public boolean changedToBlock = false;
    public boolean walking = false;
    private boolean isDiagonal;


    public AutoTunnelModule() {
        super("AutoTunnel", "Automatically mines a tunnel. \nRecommended that you use the Slot9 module and turn off AutoTool with this module.", ModuleCategory.MISC);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (mc.thePlayer == null) return;
        mc.thePlayer.setPosition(Math.floor(mc.thePlayer.posX), mc.thePlayer.posY, Math.floor(mc.thePlayer.posZ));
    }

    @Override
    public void onDisable() {
        super.onDisable();

        mc.playerController.field_1064_b = false;
        this.selected = false;
        this.timeout = 0;
    }

    public Direction getDirection() {
        if (this.direction.getValue() == ModesEnum.ZPOS) {
            return Direction.ZPOS;
        }
        if (this.direction.getValue() == ModesEnum.ZPOSXNEG) {
            return Direction.ZPOSXNEG;
        }
        if (this.direction.getValue() == ModesEnum.XNEG) {
            return Direction.XNEG;
        }
        if (this.direction.getValue() == ModesEnum.ZNEGXNEG) {
            return Direction.ZNEGXNEG;
        }
        if (this.direction.getValue() == ModesEnum.ZNEG) {
            return Direction.ZNEG;
        }
        if (this.direction.getValue() == ModesEnum.ZNEGXPOS) {
            return Direction.ZNEGXPOS;
        }
        if (this.direction.getValue() == ModesEnum.XPOS) {
            return Direction.XPOS;
        }
        if (this.direction.getValue() == ModesEnum.ZPOSXPOS) {
            return Direction.ZPOSXPOS;
        }
        return Direction.ZPOS;
    }

    public Order getOrder() {
        if (this.order.getValue() == OrderEnum.UP_DOWN) {
            return Order.UP_DOWN;
        }
        if (this.order.getValue() == OrderEnum.DOWN_UP) {
            return Order.DOWN_UP;
        }
        if (this.order.getValue() == OrderEnum.UP_ONLY) {
            return Order.UP_ONLY;
        }
        if (this.order.getValue() == OrderEnum.DOWN_ONLY) {
            return Order.DOWN_ONLY;
        }
        return Order.UP_DOWN;
    }

    public int getPossiblePlaceSide(int xx, int yy, int zz) {
        ItemStack item = mc.thePlayer.getCurrentEquippedItem();
        if (item == null || !(item.getItem() instanceof ItemBlock)) {
            return 6;
        }
        int i = 0;
        while (i < 6) {
            int placeon;
            int x = xx;
            int y = yy;
            int z = zz;
            if (i == 0) {
                ++y;
            }
            if (i == 1) {
                --y;
            }
            if (i == 2) {
                ++z;
            }
            if (i == 3) {
                --z;
            }
            if (i == 4) {
                ++x;
            }
            if (i == 5) {
                --x;
            }
            if ((placeon = mc.theWorld.getBlockId(x, y, z)) != 0 && mc.theWorld.canBlockBePlacedAt(((ItemBlock)item.getItem()).blockID, xx, yy, zz, false, i)) {
                return i;
            }
            ++i;
        }
        return 6;
    }

    public boolean isBlockWalkable(int id) {
        Block b = Block.blocksList[id];
        if (b == null || this.isLiquid(id)) {
            return false;
        }
        return b.blockMaterial.getIsTranslucent() && b.renderAsNormalBlock();
    }

    public int findItemBlockInHotbar() {
        int i = 0;
        while (i < 9) {
            ItemStack item = mc.thePlayer.inventory.mainInventory[i];
            if (item != null && item.getItem() instanceof ItemBlock) {
                ItemBlock blk = (ItemBlock)item.getItem();
                if (this.isBlockWalkable(blk.blockID)) {
                    return i + 1;
                }
            }
            ++i;
        }
        return 0;
    }

    public boolean isPlayerHoldingBlock() {
        ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
        if (stack == null) {
            return false;
        }
        return stack.getItem() instanceof ItemBlock;
    }

    public void trySettingPlaceXYZ(int x, int y, int z) {
        if (!this.place) {
            Block b;
            int id;
            if (!this.changedToBlock) {
                int slot = this.findItemBlockInHotbar();
                if (slot != 0) {
                    this.changedToBlock = true;
                    this.previousSlot = mc.thePlayer.inventory.currentItem;
                    mc.thePlayer.inventory.currentItem = slot - 1;
                    if (mc.isMultiplayerWorld()) {
                        ((PlayerControllerMP)mc.playerController).syncCurrentPlayItem();
                    }
                } else {
                    return;
                }
            }
            if (!((id = mc.theWorld.getBlockId(x, y, z)) == 0 || this.isBlockWalkable(id) || (b = Block.blocksList[id]) != null && !b.canCollideCheck(mc.theWorld.getBlockMetadata(x, y, z), false) || b != null && this.isLiquid(id))) {
                if (!this.selected && this.trySettingDestroyXYZ(x, y, z)) {
                    return;
                }
                return;
            }
            int face = this.getPossiblePlaceSide(x, y, z);
            if (face != 6) {
                if (face == 0) {
                    ++y;
                }
                if (face == 1) {
                    --y;
                }
                if (face == 2) {
                    ++z;
                }
                if (face == 3) {
                    --z;
                }
                if (face == 4) {
                    ++x;
                }
                if (face == 5) {
                    --x;
                }
                if (this.placeMode.getValue() == PlaceModeEnum.MULTI) {
                    if (this.packetsSent > this.packetsPerTick.getValue()) {
                        return;
                    }
                    this.placeBlock(x, y, z, face);
                    ++this.packetsSent;
                } else {
                    this.xPlace = x;
                    this.yPlace = y;
                    this.zPlace = z;
                    this.facePlace = face;
                    this.place = true;
                }
            }
        }
    }

    public void placeBlock(int x, int y, int z, int face) {
        if (this.disableClientPlace.getValue()) {
            PlayerUtil.placeBlock(x, y, z, face);
        } else {
            PlayerUtil.placeBlockUnsafe(x, y, z, face);
        }
    }

    public void destroyBlock(int x, int y, int z, int face) {
        if (this.mineMode.getValue() == MineModeEnum.LEGAL) {
            PlayerUtil.destroyBlock(this.xCur, this.yCur, this.zCur, this.getHitSide());
        } else {
            PlayerUtil.destroyBlockInstant(this.xCur, this.yCur, this.zCur, this.getHitSide());
        }
    }

    public boolean isLiquid(int id) {
        return id == Block.waterMoving.blockID || id == Block.waterStill.blockID || id == Block.lavaMoving.blockID || id == Block.lavaStill.blockID;
    }

    public boolean trySettingDestroyXYZ(int x, int y, int z) {
        return this.trySettingDestroyXYZ(x, y, z, false);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean trySettingDestroyXYZ(int x, int y, int z, boolean onlyCheck) {
        int id = mc.theWorld.getBlockId(x, y, z);
        if (id != 0) {
            float hardness = Block.blocksList[id].blockHardness;
            if (!(hardness >= 0.0f)) return true;
            if (!this.isLiquid(id)) {
                if (onlyCheck) {
                    return true;
                }
                if (this.mineMode.getValue() == MineModeEnum.INSTANT_MULTI) {
                    this.xCur = x;
                    this.yCur = y;
                    this.zCur = z;
                    BlockPos bl = new BlockPos(x, y, z);
                    this.destroyBlock(x, y, z, this.getHitSide());
                    this.breaking.add(bl);
                    this.packetsSent += 2;
                    return this.packetsSent > this.packetsPerTick.getValue();
                }
                this.selected = true;
                this.xCur = x;
                this.yCur = y;
                this.zCur = z;
                return this.selected;
            } else {
                this.trySettingPlaceXYZ(x, y, z);
                this.selected = false;
            }
            return this.selected;
        } else {
            if (onlyCheck) {
                return false;
            }
            this.selected = false;
        }
        return this.selected;
    }

    public void renderBlock(int x, int y, int z) {
        this.renderBlock(x, y, z, false);
    }

    public int getHitSide() {
        int x = MathHelper.floor_double(mc.thePlayer.posX);
        int y = MathHelper.floor_double(mc.thePlayer.posY);
        int z = MathHelper.floor_double(mc.thePlayer.posZ);
        int offX = x - this.xCur;
        int offY = y - this.yCur;
        int offZ = z - this.zCur;
        if (offX > 0) {
            offX = 1;
        }
        if (offX < 0) {
            offX = -1;
        }
        if (offY > 0) {
            offY = 1;
        }
        if (offY < 0) {
            offY = -1;
        }
        if (offZ > 0) {
            offZ = 1;
        }
        if (offZ < 0) {
            offZ = -1;
        }
        if (offX == 1) {
            return 4;
        }
        if (offX == -1) {
            return 5;
        }
        if (offZ == 1) {
            return 2;
        }
        if (offZ == -1) {
            return 3;
        }
        return 0;
    }

    public void renderBlock(int x, int y, int z, boolean force) {
        if (!render.getValue())
            return;
        if (force || !this.selected) {
            RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB((double)x - RenderManager.renderPosX, (double)y - RenderManager.renderPosY, (double)z - RenderManager.renderPosZ, (double)(x + 1) - RenderManager.renderPosX, (double)(y + 1) - RenderManager.renderPosY, (double)(z + 1) - RenderManager.renderPosZ));
        }
        if (this.selected && x == this.xCur && y == this.yCur && z == this.zCur) {
            return;
        }
        RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB((double)x - RenderManager.renderPosX, (double)y - RenderManager.renderPosY, (double)z - RenderManager.renderPosZ, (double)(x + 1) - RenderManager.renderPosX, (double)(y + 1) - RenderManager.renderPosY, (double)(z + 1) - RenderManager.renderPosZ));
    }

    public void tryRemovingLiquids(int x, int y, int z) {
        if (this.isLiquid(mc.theWorld.getBlockId(x - 1, y, z))) {
            this.trySettingPlaceXYZ(x - 1, y, z);
        }
        if (this.isLiquid(mc.theWorld.getBlockId(x - 1, y - 1, z))) {
            this.trySettingPlaceXYZ(x - 1, y - 1, z);
        }
        if (this.isLiquid(mc.theWorld.getBlockId(x - 1, y + 1, z))) {
            this.trySettingPlaceXYZ(x - 1, y + 1, z);
        }
        if (this.isLiquid(mc.theWorld.getBlockId(x + 1, y, z))) {
            this.trySettingPlaceXYZ(x + 1, y, z);
        }
        if (this.isLiquid(mc.theWorld.getBlockId(x + 1, y - 1, z))) {
            this.trySettingPlaceXYZ(x + 1, y - 1, z);
        }
        if (this.isLiquid(mc.theWorld.getBlockId(x + 1, y + 1, z))) {
            this.trySettingPlaceXYZ(x + 1, y + 1, z);
        }
        if (this.isLiquid(mc.theWorld.getBlockId(x, y, z - 1))) {
            this.trySettingPlaceXYZ(x, y, z - 1);
        }
        if (this.isLiquid(mc.theWorld.getBlockId(x, y - 1, z - 1))) {
            this.trySettingPlaceXYZ(x, y - 1, z - 1);
        }
        if (this.isLiquid(mc.theWorld.getBlockId(x, y + 1, z - 1))) {
            this.trySettingPlaceXYZ(x, y + 1, z - 1);
        }
        if (this.isLiquid(mc.theWorld.getBlockId(x, y, z + 1))) {
            this.trySettingPlaceXYZ(x, y, z + 1);
        }
        if (this.isLiquid(mc.theWorld.getBlockId(x, y - 1, z + 1))) {
            this.trySettingPlaceXYZ(x, y - 1, z + 1);
        }
        if (this.isLiquid(mc.theWorld.getBlockId(x, y + 1, z + 1))) {
            this.trySettingPlaceXYZ(x, y + 1, z + 1);
        }
    }
    
    @Subscribe
    public final Listener<PlayerMoveSetEvent> playerMoveSetListener = new Listener<>(event -> {
    	event.setCancelled(true);
    	mc.thePlayer.moveStrafing = 0;
    	mc.thePlayer.moveForward = this.walking ? 1 : 0;
    });
    
    @Subscribe
    public final Listener<UpdateEvent> updateListener = new Listener<>(event -> {
        mc.thePlayer.rotationYaw = this.getDirection().getYaw();

        if (this.timeout > 0) {
            --this.timeout;
            return;
        }
        this.packetsSent = 0;
        dir = this.getDirection();
        isDiagonal = dir.ordinal() % 2 == 1;
        boolean success = false;
        int x = MathHelper.floor_double(mc.thePlayer.posX);
        int y = MathHelper.floor_double(mc.thePlayer.posY);
        int yBelow = y - 2;
        int z = MathHelper.floor_double(mc.thePlayer.posZ);
        int walkAfter = this.walkStart.getValue().intValue();
        this.walking = false;
        this.breaking.clear();
        boolean walkSwitch = true;
        boolean walkSwitched = false;
        Order breakOrder = this.getOrder();
        boolean success2 = breakOrder != Order.DOWN_ONLY && breakOrder != Order.UP_ONLY;
        boolean m = this.alwaysCheckBoth.getValue() && (breakOrder == Order.DOWN_ONLY || breakOrder == Order.UP_ONLY);
        int i = 0;
        while ((double)i < this.reach.getValue() && !success) {
            boolean b;
            if (isDiagonal) {
                y += dir.yOffset;
                if (breakOrder == Order.DOWN_UP) {
                    success = this.trySettingDestroyXYZ(x + dir.xOffset, y - 1, z);
                    if (!success) {
                        success = this.trySettingDestroyXYZ(x + dir.xOffset, y, z);
                    }
                } else if (breakOrder == Order.UP_DOWN) {
                    success = this.trySettingDestroyXYZ(x + dir.xOffset, y, z);
                    if (!success) {
                        success = this.trySettingDestroyXYZ(x + dir.xOffset, y - 1, z);
                    }
                } else if (breakOrder == Order.DOWN_ONLY) {
                    success = this.trySettingDestroyXYZ(x + dir.xOffset, y - 1, z);
                    if (!success2) {
                        success2 = this.trySettingDestroyXYZ(x + dir.xOffset, y, z, true);
                    }
                } else if (breakOrder == Order.UP_ONLY) {
                    success = this.trySettingDestroyXYZ(x + dir.xOffset, y, z);
                    if (!success2) {
                        success2 = this.trySettingDestroyXYZ(x + dir.xOffset, y - 1, z, true);
                    }
                }
                this.tryRemovingLiquids(x + dir.xOffset, y, z);
                if (breakOrder == Order.DOWN_UP) {
                    if (!success) {
                        success = this.trySettingDestroyXYZ(x, y - 1, z + dir.zOffset);
                    }
                    if (!success) {
                        success = this.trySettingDestroyXYZ(x, y, z + dir.zOffset);
                    }
                } else if (breakOrder == Order.UP_DOWN) {
                    if (!success) {
                        success = this.trySettingDestroyXYZ(x, y, z + dir.zOffset);
                    }
                    if (!success) {
                        success = this.trySettingDestroyXYZ(x, y - 1, z + dir.zOffset);
                    }
                } else if (breakOrder == Order.DOWN_ONLY) {
                    if (!success) {
                        success = this.trySettingDestroyXYZ(x, y - 1, z + dir.zOffset);
                    }
                    if (!success2) {
                        success2 = this.trySettingDestroyXYZ(x, y - 1, z + dir.zOffset, true);
                    }
                } else if (breakOrder == Order.UP_ONLY) {
                    if (!success) {
                        success = this.trySettingDestroyXYZ(x, y, z + dir.zOffset);
                    }
                    if (!success2) {
                        success2 = this.trySettingDestroyXYZ(x, y - 1, z + dir.zOffset, true);
                    }
                }
                this.tryRemovingLiquids(x, y, z + dir.zOffset);
                x += dir.xOffset;
                z += dir.zOffset;
                if (breakOrder == Order.DOWN_UP) {
                    if (!success) {
                        success = this.trySettingDestroyXYZ(x, y - 1, z);
                    }
                    if (!success) {
                        success = this.trySettingDestroyXYZ(x, y, z);
                    }
                } else if (breakOrder == Order.UP_DOWN) {
                    if (!success) {
                        success = this.trySettingDestroyXYZ(x, y, z);
                    }
                    if (!success) {
                        success = this.trySettingDestroyXYZ(x, y - 1, z);
                    }
                } else if (breakOrder == Order.DOWN_ONLY) {
                    if (!success) {
                        success = this.trySettingDestroyXYZ(x, y - 1, z);
                    }
                    if (!success2) {
                        success2 = this.trySettingDestroyXYZ(x, y, z, true);
                    }
                } else if (breakOrder == Order.UP_ONLY) {
                    if (!success) {
                        success = this.trySettingDestroyXYZ(x, y, z);
                    }
                    if (!success2) {
                        success2 = this.trySettingDestroyXYZ(x, y - 1, z, true);
                    }
                }
                this.tryRemovingLiquids(x, y, z);
            } else {
                x += dir.xOffset;
                z += dir.zOffset;
                if (breakOrder == Order.DOWN_UP) {
                    success = this.trySettingDestroyXYZ(x, y - 1, z);
                    if (!success) {
                        success = this.trySettingDestroyXYZ(x, y, z);
                    }
                } else if (breakOrder == Order.UP_DOWN) {
                    success = this.trySettingDestroyXYZ(x, y, z);
                    if (!success) {
                        success = this.trySettingDestroyXYZ(x, y - 1, z);
                    }
                } else if (breakOrder == Order.DOWN_ONLY) {
                    success = this.trySettingDestroyXYZ(x, y - 1, z);
                    if (!success2) {
                        success2 = this.trySettingDestroyXYZ(x, y, z, true);
                    }
                } else if (breakOrder == Order.UP_ONLY) {
                    success = this.trySettingDestroyXYZ(x, y, z);
                    if (!success2) {
                        success2 = this.trySettingDestroyXYZ(x, y - 1, z, true);
                    }
                }
                this.tryRemovingLiquids(x, y, z);
            }
            boolean bl = b = !success;
            if (isDiagonal) {
                int idBelow1 = mc.theWorld.getBlockId(x - dir.xOffset, yBelow, z);
                int idBelow2 = mc.theWorld.getBlockId(x, yBelow, z - dir.zOffset);
                int idBelow3 = mc.theWorld.getBlockId(x, yBelow, z);
                if (!success && !this.isBlockWalkable(idBelow1)) {
                    this.trySettingPlaceXYZ(x - dir.xOffset, yBelow, z);
                } else if (!success && !this.isBlockWalkable(idBelow2)) {
                    this.trySettingPlaceXYZ(x, yBelow, z - dir.zOffset);
                } else if (!success && !this.isBlockWalkable(idBelow3)) {
                    this.trySettingPlaceXYZ(x, yBelow, z);
                }
                if (this.isLiquid(mc.theWorld.getBlockId(x, y + 1, z))) {
                    this.trySettingPlaceXYZ(x, y + 1, z);
                    walkSwitch = false;
                }
                if (b && i >= walkAfter && !this.walking && !walkSwitched) {
                    if (!this.isBlockWalkable(idBelow1)) {
                        walkSwitch = false;
                    }
                    if (!this.isBlockWalkable(idBelow2)) {
                        walkSwitch = false;
                    }
                    if (!this.isBlockWalkable(idBelow3)) {
                        walkSwitch = false;
                    }
                    if (m && success2) {
                        walkSwitch = false;
                    }
                    this.walking = walkSwitch;
                    walkSwitched = !walkSwitch;
                }
            } else {
                int idbelow3 = mc.theWorld.getBlockId(x, yBelow, z);
                if (!success && !this.isBlockWalkable(idbelow3)) {
                    this.trySettingPlaceXYZ(x, yBelow, z);
                }
                success = this.selected;
                if (b && i >= walkAfter && !this.walking && !walkSwitched) {
                    if (!this.isBlockWalkable(idbelow3)) {
                        walkSwitch = false;
                    }
                    if (m && success2) {
                        walkSwitch = false;
                    }
                    this.walking = walkSwitch;
                    walkSwitched = !walkSwitch;
                }
            }
            ++i;
        }
        if (this.selected) {
            this.destroyBlock(this.xCur, this.yCur, this.zCur, this.getHitSide());
        }
        if (this.place) {
            this.placeBlock(this.xPlace, this.yPlace, this.zPlace, this.facePlace);
            this.place = false;
        }
        if (this.changedToBlock) {
            mc.thePlayer.inventory.currentItem = this.previousSlot;
            this.changedToBlock = false;
            if (mc.isMultiplayerWorld()) {
                ((PlayerControllerMP)mc.playerController).syncCurrentPlayItem();
            }
        }
    });

    @Subscribe
    public final Listener<RenderWorldEvent> renderScreenEventListener = new Listener<>(event -> {
        dir = this.getDirection();
        isDiagonal = dir.ordinal() % 2 == 1;
        int x = MathHelper.floor_double(mc.thePlayer.posX);
        int y = MathHelper.floor_double(mc.thePlayer.posY);
        int z = MathHelper.floor_double(mc.thePlayer.posZ);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glLineWidth(1.0f);
        int i = 0;
        while ((double)i < this.reach.getValue()) {
            GL11.glColor4f(255.0f, 0.0f, 0.0f, 1.0f);
            if (isDiagonal) {
                this.renderBlock(x + dir.xOffset, y += dir.yOffset, z);
                this.renderBlock(x + dir.xOffset, y - 1, z);
                this.renderBlock(x, y, z + dir.zOffset);
                this.renderBlock(x, y - 1, z + dir.zOffset);
                this.renderBlock(x += dir.xOffset, y, z += dir.zOffset);
                this.renderBlock(x, y - 1, z);
            } else {
                this.renderBlock(x += dir.xOffset, y += dir.yOffset, z += dir.zOffset);
                this.renderBlock(x, y - 1, z);
            }
            GL11.glColor4f(0.0f, 0.0f, 255.0f, 1.0f);
            this.renderBlock(x, y - 2, z);
            ++i;
        }
        GL11.glColor4f(0.0f, 255.0f, 0.0f, 1.0f);
        if (this.selected) {
            this.renderBlock(this.xCur, this.yCur, this.zCur, true);
        }
        for (BlockPos pos : this.breaking) {
            this.renderBlock(pos.x, pos.y, pos.z, true);
        }
        GL11.glEnable(2929);
        GL11.glEnable(3553);
    });

    public enum Order {
        UP_DOWN,
        DOWN_UP,
        UP_ONLY,
        DOWN_ONLY;
    }

    public enum ModesEnum implements IEnumSetting {
        ZPOS("Z+"),
        ZPOSXNEG("Z+X-"),
        XNEG("X-"),
        ZNEGXNEG("Z-X-"),
        ZNEG("Z-"),
        ZNEGXPOS("Z-X+"),
        XPOS("X+"),
        ZPOSXPOS("Z+X+");

        private final String name;

        ModesEnum(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Enum<?> getEnum(String name) {
            return ModesEnum.valueOf(name);
        }
    }

    public enum MineModeEnum implements IEnumSetting {
        LEGAL("Legal"),
        INSTANT_SINGLE("InstantSingle"),
        INSTANT_MULTI("InstantMulti");

        private final String name;

        MineModeEnum(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Enum<?> getEnum(String name) {
            return MineModeEnum.valueOf(name);
        }
    }

    public enum OrderEnum implements IEnumSetting {
        UP_DOWN("UpDown"),
        DOWN_UP("DownUp"),
        UP_ONLY("UpOnly"),
        DOWN_ONLY("DownOnly");

        private final String name;

        OrderEnum(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Enum<?> getEnum(String name) {
            return OrderEnum.valueOf(name);
        }
    }

    public enum PlaceModeEnum implements IEnumSetting {
        SINGLE("Single"),
        MULTI("Multi");

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
