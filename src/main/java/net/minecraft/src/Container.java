// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.modules.impl.misc.Auto127Module;
import io.github.qe7.features.impl.modules.impl.misc.AutoInfinityModule;
import java.util.*;

// Referenced classes of package net.minecraft.src:
//            Slot, ItemStack, ICrafting, EntityPlayer, 
//            InventoryPlayer, IInventory

public abstract class Container
{

    public Container()
    {
        field_20123_d = new ArrayList();
        slots = new ArrayList();
        windowId = 0;
        field_20917_a = 0;
        field_20121_g = new ArrayList();
        field_20918_b = new HashSet();
    }

    protected void addSlot(Slot slot)
    {
        slot.slotNumber = slots.size();
        slots.add(slot);
        field_20123_d.add(null);
    }

    public void updateCraftingResults()
    {
        for(int i = 0; i < slots.size(); i++)
        {
            ItemStack itemstack = ((Slot)slots.get(i)).getStack();
            ItemStack itemstack1 = (ItemStack)field_20123_d.get(i);
            if(ItemStack.areItemStacksEqual(itemstack1, itemstack))
            {
                continue;
            }
            itemstack1 = itemstack != null ? itemstack.copy() : null;
            field_20123_d.set(i, itemstack1);
            for(int j = 0; j < field_20121_g.size(); j++)
            {
                ((ICrafting)field_20121_g.get(j)).func_20159_a(this, i, itemstack1);
            }

        }

    }

    public Slot getSlot(int i)
    {
        return (Slot)slots.get(i);
    }

    public ItemStack getStackInSlot(int i)
    {
        Slot slot = (Slot)slots.get(i);
        if(slot != null)
        {
            return slot.getStack();
        } else
        {
            return null;
        }
    }

    public ItemStack handleSlotClick(int slotId, int clickType, boolean isShiftClick, EntityPlayer player) {
        AutoInfinityModule AutoInfinityModule = Hephaestus.getInstance().getModuleManager().getModule(AutoInfinityModule.class);

        ItemStack resultStack = null;
        // Handle click: 0 (left-click), 1 (right-click)
        if (clickType == 0 || clickType == 1) {
            InventoryPlayer playerInventory = player.inventory;
            
            // Clicking outside the inventory
            if (slotId == -999) {
                if (playerInventory.getItemStack() != null) {
                    if (clickType == 0) {
                        player.dropPlayerItem(playerInventory.getItemStack());
                        playerInventory.setItemStack(null);
                    }
                    if (clickType == 1) {
                        player.dropPlayerItem(playerInventory.getItemStack().splitStack(1));
                        if (playerInventory.getItemStack().stackSize == 0) {
                            if(
                                !(AutoInfinityModule.isEnabled()))
                            {
                                playerInventory.setItemStack(null);
                            }
                        }
                    }
                }
            } else if (isShiftClick) {
                // Shift-click: operate on the stack in the slot.
                ItemStack slotStack = getStackInSlot(slotId);
                if (slotStack != null) {
                    int originalStackSize = slotStack.stackSize;
                    resultStack = slotStack.copy();
                    Slot currentSlot = (Slot) slots.get(slotId);
                    if (currentSlot != null && currentSlot.getStack() != null) {
                        int currentSlotStackSize = currentSlot.getStack().stackSize;
                        if (currentSlotStackSize < originalStackSize) {
                            handleSlotClick(slotId, clickType, isShiftClick, player);
                        }
                    }
                }
            } else {
                Slot slot = (Slot) slots.get(slotId);
                if (slot != null) {
                    slot.onSlotChanged();
                    ItemStack clickedStack = slot.getStack();
                    ItemStack carriedStack = playerInventory.getItemStack();
                    
                    if (clickedStack != null) {
                        resultStack = clickedStack.copy();
                    }
                    
                    // If the slot is empty
                    if (clickedStack == null) {
                        if (carriedStack != null && slot.isItemValid(carriedStack)) {
                            // The selected item is transferred to the item in the inventory
                            int amountToMove = (clickType != 0) ? 1 : carriedStack.stackSize;
                            if (amountToMove > slot.getSlotStackLimit()) {
                                amountToMove = slot.getSlotStackLimit();
                            }
                            slot.putStack(carriedStack.splitStack(amountToMove));
                            if (carriedStack.stackSize == 0) {
                                playerInventory.setItemStack(null);
                            }
                        }
                    }
                    else if (carriedStack == null) {
                        int removeAmount = (clickType != 0) ? (clickedStack.stackSize + 1) / 2 : clickedStack.stackSize;
                        ItemStack removedStack = slot.decrStackSize(removeAmount);
                        playerInventory.setItemStack(removedStack);
                        if (clickedStack.stackSize == 0) {
                            slot.putStack(null);
                        }
                        slot.onPickupFromSlot(playerInventory.getItemStack());
                    }
                    else if (slot.isItemValid(carriedStack)) {
                        // If the items are different or have different metadata.
                        if (clickedStack.itemID != carriedStack.itemID ||
                            (clickedStack.getHasSubtypes() && clickedStack.getItemDamage() != carriedStack.getItemDamage())) {
                            if (carriedStack.stackSize <= slot.getSlotStackLimit()) {
                                ItemStack tempStack = clickedStack;
                                slot.putStack(carriedStack);
                                playerInventory.setItemStack(tempStack);
                            }
                        } else {
                            // Items are the same; merge stacks as much as possible.
                            int transferAmount = (clickType != 0) ? 1 : carriedStack.stackSize;
                            int maxTransferBySlotLimit = slot.getSlotStackLimit() - clickedStack.stackSize;
                            if (transferAmount > maxTransferBySlotLimit) {
                                transferAmount = maxTransferBySlotLimit;
                            }
                            int maxTransferByItemLimit = carriedStack.getMaxStackSize() - clickedStack.stackSize;
                            if (transferAmount > maxTransferByItemLimit) {
                                transferAmount = maxTransferByItemLimit;
                            }
                            carriedStack.splitStack(transferAmount);
                            if (carriedStack.stackSize == 0) {
                                playerInventory.setItemStack(null);
                            }
                            clickedStack.stackSize += transferAmount;
                        }
                    }
                    else if (clickedStack.itemID == carriedStack.itemID &&
                             carriedStack.getMaxStackSize() > 1 &&
                             (!clickedStack.getHasSubtypes() || clickedStack.getItemDamage() == carriedStack.getItemDamage())) {
                        int clickedStackSize = clickedStack.stackSize;
                        if (clickedStackSize > 0 && clickedStackSize + carriedStack.stackSize <= carriedStack.getMaxStackSize()) {
                            carriedStack.stackSize += clickedStackSize;
                            clickedStack.splitStack(clickedStackSize);
                            if (clickedStack.stackSize == 0) {
                                slot.putStack(null);
                            }
                            slot.onPickupFromSlot(playerInventory.getItemStack());
                        }
                    }
                }
            }
        }
        return resultStack;
    }

    public void onCraftGuiClosed(EntityPlayer entityplayer)
    {
        InventoryPlayer inventoryplayer = entityplayer.inventory;
        if(inventoryplayer.getItemStack() != null)
        {
            entityplayer.dropPlayerItem(inventoryplayer.getItemStack());
            inventoryplayer.setItemStack(null);
        }
    }

    public void onCraftMatrixChanged(IInventory iinventory)
    {
        updateCraftingResults();
    }

    public void putStackInSlot(int i, ItemStack itemstack)
    {
        getSlot(i).putStack(itemstack);
    }

    public void putStacksInSlots(ItemStack aitemstack[])
    {
        for(int i = 0; i < aitemstack.length; i++)
        {
            getSlot(i).putStack(aitemstack[i]);
        }

    }

    public void func_20112_a(int i, int j)
    {
    }

    public short func_20111_a(InventoryPlayer inventoryplayer)
    {
        field_20917_a++;
        return field_20917_a;
    }

    public void func_20113_a(short word0)
    {
    }

    public void func_20110_b(short word0)
    {
    }

    public abstract boolean isUsableByPlayer(EntityPlayer entityplayer);

    protected void func_28125_a(ItemStack itemstack, int i, int j, boolean flag)
    {
        int k = i;
        if(flag)
        {
            k = j - 1;
        }
        if(itemstack.isStackable())
        {
            while(itemstack.stackSize > 0 && (!flag && k < j || flag && k >= i)) 
            {
                Slot slot = (Slot)slots.get(k);
                ItemStack itemstack1 = slot.getStack();
                if(itemstack1 != null && itemstack1.itemID == itemstack.itemID && (!itemstack.getHasSubtypes() || itemstack.getItemDamage() == itemstack1.getItemDamage()))
                {
                    int i1 = itemstack1.stackSize + itemstack.stackSize;
                    if(i1 <= itemstack.getMaxStackSize())
                    {
                        itemstack.stackSize = 0;
                        itemstack1.stackSize = i1;
                        slot.onSlotChanged();
                    } else
                    if(itemstack1.stackSize < itemstack.getMaxStackSize())
                    {
                        itemstack.stackSize -= itemstack.getMaxStackSize() - itemstack1.stackSize;
                        itemstack1.stackSize = itemstack.getMaxStackSize();
                        slot.onSlotChanged();
                    }
                }
                if(flag)
                {
                    k--;
                } else
                {
                    k++;
                }
            }
        }
        if(itemstack.stackSize > 0)
        {
            int l;
            if(flag)
            {
                l = j - 1;
            } else
            {
                l = i;
            }
            do
            {
                if((flag || l >= j) && (!flag || l < i))
                {
                    break;
                }
                Slot slot1 = (Slot)slots.get(l);
                ItemStack itemstack2 = slot1.getStack();
                if(itemstack2 == null)
                {
                    slot1.putStack(itemstack.copy());
                    slot1.onSlotChanged();
                    itemstack.stackSize = 0;
                    break;
                }
                if(flag)
                {
                    l--;
                } else
                {
                    l++;
                }
            } while(true);
        }
    }

    public List field_20123_d;
    public List slots;
    public int windowId;
    private short field_20917_a;
    protected List field_20121_g;
    private Set field_20918_b;
}
