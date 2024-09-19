package io.github.qe7.features.impl.modules.impl.combat;

import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;

public class ForceFieldModule extends Module {

    public ForceFieldModule() {
        super("ForceField", "Automatically attacks entities in a given radius", ModuleCategory.COMBAT);
    }
}
