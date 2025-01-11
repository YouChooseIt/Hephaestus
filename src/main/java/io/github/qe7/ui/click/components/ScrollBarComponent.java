package io.github.qe7.ui.click.components;

import java.awt.Color;

import net.minecraft.src.Gui;

public class ScrollBarComponent {
	
	private int x, y, width, height;
	public PanelComponent scrollable;
	public float scrollOffset = 0;
	public boolean scrolling = false;
	public boolean visible = true;
	private float toRender;
	
	public ScrollBarComponent(PanelComponent scrollable) {
		this.scrollable = scrollable;
	}
	
	public boolean isVisible() {
		return this.visible;
	}
	
	public void prepare(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		
		int mh = this.scrollable.getMaxHeight();
		int ah = this.scrollable.getAvailableHeight();
		this.toRender = ah*((float)ah/mh);
		
		int yOffNew = this.scrollable.getYOffset();
		int yOffOld = this.scrollable.prevYOffset;
		if(yOffOld != yOffNew) {
		    this.scrollOffset = (-yOffOld) / ((float)mh/ah);
		}
		if(this.scrollOffset < 0) this.scrollOffset = 0;
		if((y+toRender + this.scrollOffset) > y+height) this.scrollOffset = height-toRender;
		this.visible = mh > ah;
	}
	
	public void drawScreen() {
		int maxX = x + width;
		int minX = maxX - 4;
		int minY = y;
		int maxY = y + height;
		
		Gui.drawRect(minX, minY, maxX, maxY, new Color(29, 34, 54, (int)(0.25*255)).getRGB());
		Gui.drawRect(minX, y + this.scrollOffset, maxX, y+toRender + this.scrollOffset, new Color(29, 34, 54, (int)(0.75*255)).getRGB());
	}

	public boolean isInside(int mx, int my, int x, int y, int width, int height) {
		return mx >= x+width-4 && mx <= x+width && my >= y && my <= y+height;
	}
	
	private int clickX = -1, clickY = -1;
	public void mouseClicked(int mx, int my) {
		int mh = this.scrollable.getMaxHeight();
		int ah = this.scrollable.getAvailableHeight();
		float toRender = ah*((float)ah/mh);
		
		int maxX = x + width;
		int minX = maxX - 4;
		
		if(!(mx >= minX && mx <= maxX && my >= (y + this.scrollOffset) && my <= (y+toRender + this.scrollOffset))) {
			int start = this.y + (int)this.scrollOffset;
			int end = this.y + (int)(toRender + this.scrollOffset);
			int mid = start + ((end - start) / 2);
			
			this.scrollOffset += my - mid;
		}
		
		this.clickX = mx;
		this.clickY = my;
		this.scrolling = true;
	}

	public void mouseMovedOrUp(int mx, int my, int button) {
		int mh = this.scrollable.getMaxHeight();
		int ah = this.scrollable.getAvailableHeight();
		float toRender = ah*((float)ah/mh);
		
		int diff = my - this.clickY;
		this.scrollOffset += diff;
		if(this.scrollOffset < 0) this.scrollOffset = 0;
		if((y+toRender + this.scrollOffset) > y+height) this.scrollOffset = height-toRender;
		this.clickX = mx;
		this.clickY = my;
	}

	public void mouseReleased(int mx, int my, int button) {
		this.scrolling = false;
	}


}
