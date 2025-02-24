package io.github.qe7.features.impl.modules.impl.misc;

import io.github.qe7.events.KeyPressEvent;
import io.github.qe7.events.UpdateEvent;
import io.github.qe7.events.render.RenderScreenEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.BooleanSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.IntSetting;
import io.github.qe7.utils.PacketUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet101CloseWindow;

import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.input.Keyboard;

public final class AutoInfinityModule extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    private boolean isDropping = false;

    private long lastDropTime = 0;

    private long dropDelay = 500L;

    private final Queue<DropTask> dropQueue = new LinkedList<>();

    public BooleanSetting requireQ = new BooleanSetting("Require Q", true);

    private final IntSetting maxAttempts = new IntSetting("Drop blocks per Q", 5, 5, 140, 5);

    public AutoInfinityModule() {
        super("AutoInfinity", "Automatically drops an infinite positive item to negative.", ModuleCategory.MISC);
    }

    @Subscribe
    public final Listener<KeyPressEvent> keyPressEventListener = new Listener<>(event -> {
        if (!requireQ.getValue() || event.getKeyCode() != Keyboard.KEY_Q || mc.theWorld == null) {
            return;
        }

        ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
        if (stack == null || (stack.stackSize < 0 && stack.stackSize > -129)) {
            return;
        }

        event.cancel();

        if(!isDropping) autoDrop();
    });

    @Subscribe
    public final Listener<UpdateEvent> updateEventListener = new Listener<>(event -> {
        if (isDropping) {
            processDropQueue();
        }
    });

    public void autoDrop() {
        int slot = 36 + mc.thePlayer.inventory.currentItem;
        dropQueue.clear();
        dropQueue.add(new DropTask(slot, 0));
        isDropping = true;
    }

    private void processDropQueue() {
        if (dropQueue.isEmpty()) {
            isDropping = false;
            return;
        }

        if (System.currentTimeMillis() - lastDropTime < dropDelay) {
            return;
        }

        DropTask task = dropQueue.poll();
        lastDropTime = System.currentTimeMillis();

        //System.out.println("[AutoInf~] Dropping attempt: " + task.attempt);
        //ItemStack stack = mc.thePlayer.inventory.getItemStack();
        //System.out.println("[AutoInf~] Curr item stack: " + (stack != null ? stack.stackSize : 0));


        if (task.attempt == 0) {
            mc.playerController.func_27174_a(mc.thePlayer.inventorySlots.windowId, task.slot, 0, false, mc.thePlayer);

            dropQueue.add(new DropTask(task.slot, task.attempt + 1));
        } else if (task.attempt <= maxAttempts.getValue()) {
            if (task.attempt == 1) dropDelay = 100L;

            mc.playerController.func_27174_a(mc.thePlayer.inventorySlots.windowId, -999, 1, false, mc.thePlayer);

            dropQueue.add(new DropTask(task.slot, task.attempt + 1));
        } else {
            mc.playerController.func_27174_a(mc.thePlayer.inventorySlots.windowId, task.slot, 0, false, mc.thePlayer);

            if (mc.isMultiplayerWorld()) {
                PacketUtil.sendPacket(new Packet101CloseWindow(mc.thePlayer.inventorySlots.windowId));
            }

            isDropping = false;
            dropDelay = 500L;
        }
    }

    private static class DropTask {
        final int slot;
        final int attempt;

        DropTask(int slot, int attempt) {
            this.slot = slot;
            this.attempt = attempt;
        }
    }
}
