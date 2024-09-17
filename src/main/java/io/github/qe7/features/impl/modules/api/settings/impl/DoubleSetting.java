package io.github.qe7.features.impl.modules.api.settings.impl;

import io.github.qe7.features.impl.modules.api.settings.api.Setting;
import lombok.Getter;

@Getter
public final class DoubleSetting extends Setting<Double> {

    private final double minimum, maximum, step;

    public DoubleSetting(String name, Double defaultValue, double minimum, double maximum, double step) {
        super(name, defaultValue);
        this.minimum = minimum;
        this.maximum = maximum;
        this.step = step;
    }
}
