package io.github.qe7.features.impl.modules.impl.render;

import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.ui.editor.GuiHudEditor;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public final class HUDEditorGUIModule extends Module {

    public HUDEditorGUIModule() {
        super("HUDEditor", "Displays GUI for editing the HUD panels.", ModuleCategory.RENDER);

        this.setKeyBind(Keyboard.KEY_GRAVE);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        Minecraft.getMinecraft().displayGuiScreen(new GuiHudEditor());
        this.setEnabled(false);
    }
}
