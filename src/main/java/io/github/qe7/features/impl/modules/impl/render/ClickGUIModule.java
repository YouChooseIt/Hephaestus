package io.github.qe7.features.impl.modules.impl.render;

import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.ui.click.ClickGUI;
import io.github.qe7.ui.editor.GuiHudEditor;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public final class ClickGUIModule extends Module {

    private ClickGUI clickGUI;

    public ClickGUIModule() {
        super("ClickGUI", "Displays a clickable ui where you can edit module's settings.", ModuleCategory.RENDER);

        this.setKeyBind(Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (clickGUI == null) {
            clickGUI = new ClickGUI();
        }

        Minecraft.getMinecraft().displayGuiScreen(clickGUI);
        this.setEnabled(false);
    }
}
