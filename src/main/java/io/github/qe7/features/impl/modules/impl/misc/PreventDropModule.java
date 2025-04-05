package io.github.qe7.features.impl.modules.impl.misc;

import org.lwjgl.input.Keyboard;

import io.github.qe7.events.KeyPressEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ItemStack;

public class PreventDropModule extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public PreventDropModule() {
        super("PreventDrop", "Prevents any drop", ModuleCategory.MISC);
    }

    @Subscribe
    public final Listener<KeyPressEvent> keyPressEventListener = new Listener<>(event -> {
        if (event.getKeyCode() != Keyboard.KEY_Q || mc.theWorld == null) {
            return;
        }

        ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
        if (stack == null) {
            return;
        }

        event.cancel();
    });
}
