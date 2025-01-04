package io.github.qe7.features.impl.modules.impl.misc;

import io.github.qe7.events.PushOutOfEntityEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;

public final class NoPushModule extends Module {

    public NoPushModule() {
        super("NoPush", "Prevents the player from being pushed by other entities.", ModuleCategory.MISC);
    }

    @Subscribe
    public final Listener<PushOutOfEntityEvent> pushOutOfEntityEventListener = new Listener<>(event -> event.setCancelled(true));
}
