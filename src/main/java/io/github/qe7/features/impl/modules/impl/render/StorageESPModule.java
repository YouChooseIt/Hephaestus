package io.github.qe7.features.impl.modules.impl.render;

import io.github.qe7.events.render.RenderWorldEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.BooleanSetting;
import io.github.qe7.utils.RenderUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

import java.awt.*;

public class StorageESPModule extends Module {

    private final BooleanSetting chest = new BooleanSetting("Chest", true);
    private final BooleanSetting dispenser = new BooleanSetting("Dispenser", true);
    private final BooleanSetting furnace = new BooleanSetting("Furnace", true);

    public StorageESPModule() {
        super("StorageESP", "ESP for storage.", ModuleCategory.RENDER);
    }

    @Subscribe
    public final Listener<RenderWorldEvent> renderWorldEventListener = new Listener<>(event -> {
        for (Object tileEntity : Minecraft.getMinecraft().theWorld.loadedTileEntityList) {
            if (!(tileEntity instanceof TileEntity)) {
                continue;
            }

            TileEntity tileEntity1 = (TileEntity) tileEntity;

            final double x = tileEntity1.xCoord - RenderManager.renderPosX;
            final double y = tileEntity1.yCoord - RenderManager.renderPosY;
            final double z = tileEntity1.zCoord - RenderManager.renderPosZ;

            if (tileEntity instanceof TileEntityChest && chest.getValue()) {
                RenderUtil.drawBlockESP(x, y, z, new Color(255, 255, 0), 0.2f);
            } else if (tileEntity instanceof TileEntityDispenser && dispenser.getValue()) {
                RenderUtil.drawBlockESP(x, y, z, new Color(103, 103, 103), 0.2f);
            } else if (tileEntity1 instanceof TileEntityFurnace && furnace.getValue()) {
                RenderUtil.drawBlockESP(x, y, z, new Color(103, 103, 103), 0.2f);
            }
        }
    });
}
