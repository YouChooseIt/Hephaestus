package io.github.qe7.features.impl.modules.api.settings.impl;

import io.github.qe7.features.impl.modules.api.settings.api.Setting;

public final class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, Boolean defaultValue) {
        super(name, defaultValue);
    }
}
