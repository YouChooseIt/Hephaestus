package io.github.qe7.features.impl.modules.impl.misc;

import io.github.qe7.events.KeyPressEvent;
import io.github.qe7.events.UpdateEvent;
import io.github.qe7.events.render.RenderScreenEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.BooleanSetting;
import io.github.qe7.utils.PacketUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet101CloseWindow;
import org.lwjgl.input.Keyboard;

public final class Auto127Module extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    private final BooleanSetting requireQ = new BooleanSetting("Require Q", true);

    public Auto127Module() {
        super("Auto127", "Automatically drops an infinite item to the rollover amount (-129).", ModuleCategory.MISC);
    }

    @Subscribe
    public final Listener<UpdateEvent> updateEventListener = new Listener<>(event -> {
        int slot = 36 + mc.thePlayer.inventory.currentItem;
        ItemStack stack = mc.thePlayer.getCurrentEquippedItem();

        if (requireQ.getValue() && !Keyboard.isKeyDown(Keyboard.KEY_Q)) {
            return;
        }

        if (stack == null) {
            return;
        }

        if (!(stack.stackSize < 0 && stack.stackSize > -129)) {
            return;
        }

        mc.playerController.func_27174_a(mc.thePlayer.inventorySlots.windowId, slot, 0, false, mc.thePlayer);

        int i = 0;

        do {
            mc.playerController.func_27174_a(mc.thePlayer.inventorySlots.windowId, -999, 1, false, mc.thePlayer);
        } while (stack.stackSize > -129 && ++i < 5);

        mc.playerController.func_27174_a(mc.thePlayer.inventorySlots.windowId, slot, 0, false, mc.thePlayer);

        if (mc.isMultiplayerWorld()) {
            PacketUtil.sendPacket(new Packet101CloseWindow(mc.thePlayer.inventorySlots.windowId));
        }
    });

    @Subscribe
    public final Listener<KeyPressEvent> keyPressEventListener = new Listener<>(event -> {
        if (!requireQ.getValue()) {
            return;
        }

        if (event.getKeyCode() != Keyboard.KEY_Q) {
            return;
        }

        if (mc.theWorld == null) {
            return;
        }

        ItemStack stack = mc.thePlayer.getCurrentEquippedItem();

        if (stack == null) {
            return;
        }

        if (!(stack.stackSize < 0 && stack.stackSize > -129)) {
            return;
        }

        event.cancel();
    });
}
