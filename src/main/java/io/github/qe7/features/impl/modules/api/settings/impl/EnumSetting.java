package io.github.qe7.features.impl.modules.api.settings.impl;

import io.github.qe7.features.impl.modules.api.settings.api.Setting;
import io.github.qe7.features.impl.modules.api.settings.impl.interfaces.IEnumSetting;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public final class EnumSetting<T extends IEnumSetting> extends Setting<T> {

    private final List<IEnumSetting> values;

    public EnumSetting(String name, T defaultValue) {
        super(name, defaultValue);
        this.values = new ArrayList<>(Arrays.asList(defaultValue.getClass().getEnumConstants()));
    }

    public int getIndex() {
        return values.indexOf(getValue());
    }

    public void setIndex(int index) {
        if (index < values.size()) {
            setValue((T) values.get(index));
        }
    }
}
