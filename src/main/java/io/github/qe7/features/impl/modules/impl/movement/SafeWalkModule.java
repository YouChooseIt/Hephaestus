package io.github.qe7.features.impl.modules.impl.movement;

import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;

public final class SafeWalkModule extends Module {

    public SafeWalkModule() {
        super("SafeWalk", "For the people who can't walk without falling off a ledge", ModuleCategory.MOVEMENT);
    }
}
