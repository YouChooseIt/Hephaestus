// Decompiled with: CFR 0.152
// Class Version: 6
package io.github.qe7.features.impl.modules.impl.auto;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.BooleanSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.DoubleSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.EnumSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.IntSetting;
import io.github.qe7.type.BlockPos;
import io.github.qe7.type.Direction;
import io.github.qe7.utils.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.PlayerControllerMP;
import net.minecraft.src.RenderManager;
import org.lwjgl.opengl.GL11;

public class AutoTunnelModule extends Module {
    private final Minecraft mc;
    public List<String> modes = Arrays.asList("Z+", "Z+X-", "X-", "Z-X-", "Z-", "Z-X+", "X+", "Z+X+");
    public DoubleSetting walkStart = new DoubleSetting("StartWalkingAfter", 2.0, 1.0, 8.0, 1.0);
    public DoubleSetting reach = new DoubleSetting("Reach", 4.0, 1.0, 8.0, 0.1);
    public EnumSetting direction = new EnumSetting("Direction", this.modes);
    public EnumSetting mineMode = new EnumSetting("MineMode", Arrays.asList("Legal", "InstantSingle", "InstantMulti"));
    public EnumSetting order = new EnumSetting("Order", Arrays.asList("UpDown", "DownUp", "DownOnly", "UpOnly"));
    public EnumSetting placeMode = new EnumSetting("PlaceMode", Arrays.asList("Single", "Multi"));
    public BooleanSetting disableClientPlace = new BooleanSetting("DisableClientPlace", true);
    public IntSetting packetsPerTick = new IntSetting("MaxPacketsPerTick", 1, 12, 40, 0);
    public IntSetting disableOnReconnect = new IntSetting(this, "DisableOnReconnectS", 0, 0, 10);
    public BooleanSetting swing = new BooleanSetting("Swing", false);
    public BooleanSetting alwaysCheckBoth = new BooleanSetting( "AlwaysCheckBoth", true);
    public int xCur;
    public int yCur;
    public int zCur;
    public boolean selected = false;
    public int timeout = 0;
    public int xPlace;
    public int yPlace;
    public int zPlace;
    public int facePlace;
    public boolean place = false;
    public HashSet<BlockPos> breaking = new HashSet();
    public HashSet<BlockPos> waiting = new HashSet();
    public boolean changedToBlock = false;
    public int previousSlot = 0;
    public boolean walking = false;
    public float prevYaw = 0.0f;
    public int packetsSent = 0;

    public AutoTunnelModule() {
        super("Auto Tunnel", "relique skid uwu~", ModuleCategory.AUTO);
        mc = Minecraft.getMinecraft();
    }

    @Override
    public void onDisable() {
        mc.playerController.field_1064_b = false;
        this.selected = false;
        this.timeout = 0;
    }

    public Direction getDirection() {
        String v = this.direction.getValue();
        if (v.equalsIgnoreCase(this.modes.get(0))) {
            return Direction.ZPOS;
        }
        if (v.equalsIgnoreCase(this.modes.get(1))) {
            return Direction.ZPOSXNEG;
        }
        if (v.equalsIgnoreCase(this.modes.get(2))) {
            return Direction.XNEG;
        }
        if (v.equalsIgnoreCase(this.modes.get(3))) {
            return Direction.ZNEGXNEG;
        }
        if (v.equalsIgnoreCase(this.modes.get(4))) {
            return Direction.ZNEG;
        }
        if (v.equalsIgnoreCase(this.modes.get(5))) {
            return Direction.ZNEGXPOS;
        }
        if (v.equalsIgnoreCase(this.modes.get(6))) {
            return Direction.XPOS;
        }
        if (v.equalsIgnoreCase(this.modes.get(7))) {
            return Direction.ZPOSXPOS;
        }
        return Direction.ZPOS;
    }

    public Order getOrder() {
        String v = this.order.getValue();
        if (v.equalsIgnoreCase("UpDown")) {
            return Order.UPDOWN;
        }
        if (v.equalsIgnoreCase("DownUp")) {
            return Order.DOWNUP;
        }
        if (v.equalsIgnoreCase("UpOnly")) {
            return Order.UPONLY;
        }
        if (v.equalsIgnoreCase("DownOnly")) {
            return Order.DOWNONLY;
        }
        return Order.UPDOWN;
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

    public boolean trySettingPlaceXYZ(int x, int y, int z) {
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
                    return false;
                }
            }
            if (!((id = mc.theWorld.getBlockId(x, y, z)) == 0 || this.isBlockWalkable(id) || (b = Block.blocksList[id]) != null && !b.canCollideCheck(mc.theWorld.getBlockMetadata(x, y, z), false) || b != null && this.isLiquid(id))) {
                boolean success;
                if (!this.selected && (success = this.trySettingDestroyXYZ(x, y, z))) {
                    return false;
                }
                return false;
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
                if (this.placeMode.getValue().equalsIgnoreCase("Multi")) {
                    if (this.packetsSent > this.packetsPerTick.getValue()) {
                        return false;
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
                return true;
            }
            return false;
        }
        return false;
    }

    public void placeBlock(int x, int y, int z, int face) {
        if (this.disableClientPlace.getValue().booleanValue()) {
            PlayerUtils.placeBlock(x, y, z, face);
        } else {
            PlayerUtils.placeBlockUnsafe(x, y, z, face);
        }
    }

    public void destroyBlock(int x, int y, int z, int face) {
        if (this.mineMode.getValue().equalsIgnoreCase("Legal")) {
            PlayerUtils.destroyBlock(this.xCur, this.yCur, this.zCur, this.getHitSide());
        } else {
            PlayerUtils.destroyBlockInstant(this.xCur, this.yCur, this.zCur, this.getHitSide());
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
                if (this.mineMode.getValue().equalsIgnoreCase("InstantMulti")) {
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
        if (force || !this.selected) {
            RenderUtils.drawOutlinedBlockBB((double)x - RenderManager.renderPosX, (double)y - RenderManager.renderPosY, (double)z - RenderManager.renderPosZ);
        }
        if (this.selected && x == this.xCur && y == this.yCur && z == this.zCur) {
            return;
        }
        RenderUtils.drawOutlinedBlockBB((double)x - RenderManager.renderPosX, (double)y - RenderManager.renderPosY, (double)z - RenderManager.renderPosZ);
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

    public void handleEvent(Event e) {
        boolean isDiagonal;
        Direction dir;
        if (e instanceof EventPlayerUpdateAfterInput) {
            this.prevYaw = mc.thePlayer.rotationYaw;
            mc.thePlayer.rotationYaw = this.getDirection().getYaw();
        }
        if (e instanceof EventPlayerUpdate && !e.isPre) {
            mc.thePlayer.rotationYaw = this.prevYaw;
        }
        if (e instanceof EventPlayerUpdate && e.isPre) {
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
            boolean success2 = breakOrder != Order.DOWNONLY && breakOrder != Order.UPONLY;
            boolean m = this.alwaysCheckBoth.getValue() != false && (breakOrder == Order.DOWNONLY || breakOrder == Order.UPONLY);
            String comment = "you think this code is better than whatever u will be able to write, enchantiledev?";
            int i = 0;
            while ((double)i < this.reach.getValue() && !success) {
                boolean b;
                if (isDiagonal) {
                    y += dir.yOffset;
                    if (breakOrder == Order.DOWNUP) {
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x + dir.xOffset, y - 1, z);
                        }
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x + dir.xOffset, y, z);
                        }
                    } else if (breakOrder == Order.UPDOWN) {
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x + dir.xOffset, y, z);
                        }
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x + dir.xOffset, y - 1, z);
                        }
                    } else if (breakOrder == Order.DOWNONLY) {
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x + dir.xOffset, y - 1, z);
                        }
                        if (!success2) {
                            success2 = this.trySettingDestroyXYZ(x + dir.xOffset, y, z, true);
                        }
                    } else if (breakOrder == Order.UPONLY) {
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x + dir.xOffset, y, z);
                        }
                        if (!success2) {
                            success2 = this.trySettingDestroyXYZ(x + dir.xOffset, y - 1, z, true);
                        }
                    }
                    this.tryRemovingLiquids(x + dir.xOffset, y, z);
                    if (breakOrder == Order.DOWNUP) {
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x, y - 1, z + dir.zOffset);
                        }
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x, y, z + dir.zOffset);
                        }
                    } else if (breakOrder == Order.UPDOWN) {
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x, y, z + dir.zOffset);
                        }
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x, y - 1, z + dir.zOffset);
                        }
                    } else if (breakOrder == Order.DOWNONLY) {
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x, y - 1, z + dir.zOffset);
                        }
                        if (!success2) {
                            success2 = this.trySettingDestroyXYZ(x, y - 1, z + dir.zOffset, true);
                        }
                    } else if (breakOrder == Order.UPONLY) {
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
                    if (breakOrder == Order.DOWNUP) {
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x, y - 1, z);
                        }
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x, y, z);
                        }
                    } else if (breakOrder == Order.UPDOWN) {
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x, y, z);
                        }
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x, y - 1, z);
                        }
                    } else if (breakOrder == Order.DOWNONLY) {
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x, y - 1, z);
                        }
                        if (!success2) {
                            success2 = this.trySettingDestroyXYZ(x, y, z, true);
                        }
                    } else if (breakOrder == Order.UPONLY) {
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
                    if (breakOrder == Order.DOWNUP) {
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x, y - 1, z);
                        }
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x, y, z);
                        }
                    } else if (breakOrder == Order.UPDOWN) {
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x, y, z);
                        }
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x, y - 1, z);
                        }
                    } else if (breakOrder == Order.DOWNONLY) {
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x, y - 1, z);
                        }
                        if (!success2) {
                            success2 = this.trySettingDestroyXYZ(x, y, z, true);
                        }
                    } else if (breakOrder == Order.UPONLY) {
                        if (!success) {
                            success = this.trySettingDestroyXYZ(x, y, z);
                        }
                        if (!success2) {
                            success2 = this.trySettingDestroyXYZ(x, y - 1, z, true);
                        }
                    }
                    this.tryRemovingLiquids(x, y, z);
                }
                boolean bl = b = !success;
                if (isDiagonal) {
                    int idbelow1 = mc.theWorld.getBlockId(x - dir.xOffset, yBelow, z);
                    int idbelow2 = mc.theWorld.getBlockId(x, yBelow, z - dir.zOffset);
                    int idbelow3 = mc.theWorld.getBlockId(x, yBelow, z);
                    if (!success && !this.isBlockWalkable(idbelow1)) {
                        this.trySettingPlaceXYZ(x - dir.xOffset, yBelow, z);
                    } else if (!success && !this.isBlockWalkable(idbelow2)) {
                        this.trySettingPlaceXYZ(x, yBelow, z - dir.zOffset);
                    } else if (!success && !this.isBlockWalkable(idbelow3)) {
                        this.trySettingPlaceXYZ(x, yBelow, z);
                    }
                    if (this.isLiquid(mc.theWorld.getBlockId(x, y + 1, z))) {
                        this.trySettingPlaceXYZ(x, y + 1, z);
                        walkSwitch = false;
                    }
                    if (b && i >= walkAfter && !this.walking && !walkSwitched) {
                        if (!this.isBlockWalkable(idbelow1)) {
                            walkSwitch = false;
                        }
                        if (!this.isBlockWalkable(idbelow2)) {
                            walkSwitch = false;
                        }
                        if (!this.isBlockWalkable(idbelow3)) {
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
        }
        if (e instanceof EventOnEntityRender) {
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
        }
    }

    static enum Order {
        UPDOWN,
        DOWNUP,
        UPONLY,
        DOWNONLY;

    }
}
