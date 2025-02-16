package io.github.qe7.ui.click;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.ui.click.components.PanelComponent;
import io.github.qe7.utils.config.FileUtil;
import net.minecraft.src.GuiScreen;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

public class ClickGUI extends GuiScreen {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final List<PanelComponent> panelComponentList = new ArrayList<>();

    public ClickGUI() {
        int x = 20;
        int y = 20;

        int width = 140;
        int height = 14;

        for (ModuleCategory moduleCategory : ModuleCategory.values()) {
            panelComponentList.add(new PanelComponent(moduleCategory, width, height, x, y));
            y += height + 5;
        }

        this.loadPlates();
    }

    @Override
    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);

        for (PanelComponent panelComponent : panelComponentList) {
            panelComponent.drawScreen(i, j, f);
        }
    }

    @Override
    protected void keyTyped(char c, int i) {
        for (PanelComponent panelComponent : panelComponentList) {
            panelComponent.keyTyped(c, i);
        }

        for (PanelComponent panelComponent : panelComponentList) {
            if (panelComponent.isChildFocused()) {
                return;
            }
        }

        super.keyTyped(c, i);
    }

    @Override
    protected void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);

        for (PanelComponent panelComponent : panelComponentList) {
            panelComponent.mouseClicked(i, j, k);
        }
    }

    @Override
    protected void mouseReleased(int i, int j, int k) {
        super.mouseReleased(i, j, k);

        for (PanelComponent panelComponent : panelComponentList) {
            panelComponent.mouseReleased(i, j, k);
        }
    }
    
    @Override
    protected void mouseMovedOrUp(int mx, int my, int button) {
    	super.mouseMovedOrUp(mx, my, button);
    	
    	 for (PanelComponent panelComponent : panelComponentList) {
             panelComponent.mouseMovedOrUp(mx, my, button);
         }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void savePlates() {
        final JsonObject jsonObject = new JsonObject();

        for (PanelComponent panelComponent : panelComponentList) {
            jsonObject.add(panelComponent.getModuleCategory().getName(), panelComponent.serialize());
        }
        
        FileUtil.writeFile("plates", GSON.toJson(jsonObject));
    }

    public void loadPlates() {
        final String config = FileUtil.readFile("plates");

        if (config == null) {
            return;
        }

        final JsonObject jsonObject = GSON.fromJson(config, JsonObject.class);

        for (PanelComponent panelComponent : panelComponentList) {
            if (jsonObject.has(panelComponent.getModuleCategory().getName())) {
                panelComponent.deserialize(jsonObject.getAsJsonObject(panelComponent.getModuleCategory().getName()));
            }
        }
    }
}
