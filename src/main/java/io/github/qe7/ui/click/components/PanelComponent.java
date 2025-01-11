package io.github.qe7.ui.click.components;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.managers.impl.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Gui;
import net.minecraft.src.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

public class PanelComponent {
	private static final int STENCTIL_ALLOWED = 12;
    private final List<ButtonComponent> buttonComponentList = new ArrayList<>();

    private final ScrollBarComponent scrollbar;
    
    private final ModuleCategory moduleCategory;

    private final FontRenderer fontRenderer;

    private final int width, height;

    private int x, y, dragX, dragY, totalHeight;

    private boolean dragging, open;
    
    public int prevYOffset = 0;
    public int savedTotalHeight = 0;
    
    public PanelComponent(ModuleCategory moduleCategory, int width, int height, int x, int y) {
        this.moduleCategory = moduleCategory;
        this.width = width;
        this.height = height;
        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
        this.x = x;
        this.y = y;

        for (Module module : Hephaestus.getInstance().getModuleManager().getRegistry().values().stream().sorted(Comparator.comparing(Module::getName)).collect(Collectors.toList())) {
            if (module.getCategory() == moduleCategory) {
                buttonComponentList.add(new ButtonComponent(module, width, height));
            }
        }
        
        this.scrollbar = new ScrollBarComponent(this);
    }
    public int getYOffset() {
    	int max = this.getMaxHeight();
    	int min = this.getAvailableHeight();
    	
    	float off = this.scrollbar.scrollOffset * ((float)max/min);
    	return (int)-off;
    }
    public int getMaxHeight() {
    	return this.savedTotalHeight - this.height;
    }

    public int getAvailableHeight() {
    	Minecraft mc = Minecraft.getMinecraft();
    	ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
    	int maxY = this.y+this.savedTotalHeight;
    	if(sr.getScaledHeight() < maxY) {
    		maxY = sr.getScaledHeight();
    	}
    	return maxY-this.y-this.height;
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.prevYOffset = this.getYOffset();
    	if (dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
        
       
        Gui.drawRect(x, y, x + width, y + height, new Color(29, 34, 54, 255).getRGB());
        Gui.drawRect(x - 0.5f, y - 0.5f, x + width + 0.5f, y, new Color(0, 0, 0, 255).getRGB());
        Gui.drawRect(x - 0.5f, y + height, x + width + 0.5f, y + height + 0.5f, new Color(0, 0, 0, 255).getRGB());
        Gui.drawRect(x - 0.5f, y + totalHeight, x + width + 0.5f, y + totalHeight + 0.5f, new Color(0, 0, 0, 255).getRGB());
        Gui.drawRect(x - 0.5f, y - 0.5f, x, y + totalHeight + 0.5f, new Color(0, 0, 0, 255).getRGB());
        this.fontRenderer.drawStringWithShadow(moduleCategory.getName(), x + 3, y + 3, -1);
        
        Gui.drawRect(x + width, y - 0.5f, x + width + 0.5f, y + totalHeight + 0.5f, new Color(0, 0, 0, 255).getRGB());
        
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glStencilFunc(GL11.GL_ALWAYS, STENCTIL_ALLOWED, 0xff);
        Gui.drawRect(x, y + height, x + width, y + totalHeight + 0.5f, new Color(0, 0, 0, 150).getRGB());

        GL11.glStencilFunc(GL11.GL_EQUAL, STENCTIL_ALLOWED, 0xff);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        
        totalHeight = height + 2;

        if (!open) {
        	GL11.glDisable(GL11.GL_STENCIL_TEST);
            return;
        }
        
        
        int yOff = this.getYOffset();
        
        for (ButtonComponent buttonComponent : buttonComponentList) {
            buttonComponent.drawScreen(mouseX, mouseY, partialTicks, x, y + yOff + totalHeight);
            totalHeight += buttonComponent.getHeight();
        }
        
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        this.savedTotalHeight = totalHeight;
        totalHeight += 2;
        
        
        this.scrollbar.prepare(x, y+height, width, this.getAvailableHeight());
        if(this.scrollbar.isVisible()) {
        	this.scrollbar.drawScreen();
        }
    }

    public void keyTyped(char c, int keyTyped) {
        if (!open) {
            return;
        }

        for (ButtonComponent buttonComponent : buttonComponentList) {
            buttonComponent.keyTyped(c, keyTyped);
        }
    }
    
    public void mouseClicked(int mouseX, int mouseY, int button) {
    	int availableHeight = this.getAvailableHeight();
    	if(this.scrollbar.isVisible() && this.scrollbar.isInside(mouseX, mouseY, x, y+height, width, availableHeight)) {
    		this.scrollbar.mouseClicked(mouseX, mouseY);
    		return;
    	}
    	
    	if (!(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + totalHeight)) {
    		return;
    	}
    	
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            switch (button) {
                case 0:
                    dragging = true;
                    dragX = mouseX - x;
                    dragY = mouseY - y;
                    return;
                case 1:
                    open = !open;
                    return;
            }
        }

        if (!open) {
            return;
        }

        for (ButtonComponent buttonComponent : buttonComponentList) {
            buttonComponent.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
    	if(this.scrollbar.scrolling) {
    		this.scrollbar.mouseReleased(mouseX, mouseY, button);
    	}
        if (dragging) {
            dragging = false;
        }

        if (!open) {
            return;
        }

        for (ButtonComponent buttonComponent : buttonComponentList) {
            buttonComponent.mouseReleased(mouseX, mouseY, button);
        }
    }

    public boolean isChildFocused() {
        for (ButtonComponent buttonComponent : buttonComponentList) {
            if (buttonComponent.isChildFocused()) {
                return true;
            }
        }
        return false;
    }

	public void mouseMovedOrUp(int mx, int my, int button) {
		if(this.scrollbar.scrolling) {
			this.scrollbar.mouseMovedOrUp(mx, my, button);
		}
	}
}
