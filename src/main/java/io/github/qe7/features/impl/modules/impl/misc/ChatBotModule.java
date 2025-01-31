package io.github.qe7.features.impl.modules.impl.misc;

import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.features.impl.modules.api.settings.impl.IntSetting;

public final class ChatBotModule extends Module {
    public IntSetting ticksDelay = new IntSetting("Delay (ticks)", 18, 0, 60, 1);
    public ChatBotModule() {
        super("ChatBot", "Controls the state for the chat bot.", ModuleCategory.MISC);
    }
}
