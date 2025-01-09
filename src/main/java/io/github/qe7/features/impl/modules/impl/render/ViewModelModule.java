package io.github.qe7.features.impl.modules.impl.render;

import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.BooleanSetting;
import io.github.qe7.features.impl.modules.api.settings.impl.DoubleSetting;

public final class ViewModelModule extends Module {

    public static final DoubleSetting xOffset = new DoubleSetting("X Offset", 0.0, -10.0, 10.0, 0.1);
    public static final DoubleSetting yOffset = new DoubleSetting("Y Offset", 0.0, -10.0, 10.0, 0.1);
    public static final DoubleSetting zOffset = new DoubleSetting("Z Offset", 0.0, -10.0, 10.0, 0.1);

    public static final BooleanSetting thirdPerson = new BooleanSetting("Third Person", false);
    public static final BooleanSetting autism = new BooleanSetting("Rotate", false);

    public ViewModelModule() {
        super("ViewModel", "Modifies attributes of the view model.", ModuleCategory.RENDER);
    }
}
