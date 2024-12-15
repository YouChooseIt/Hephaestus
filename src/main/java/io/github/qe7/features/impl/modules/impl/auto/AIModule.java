package io.github.qe7.features.impl.modules.impl.auto;

import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;

public class AIModule extends Module {

    public AIModule() {
        super("AI", "Automatically plays Minecraft for you.", ModuleCategory.AUTO);
    }
}
