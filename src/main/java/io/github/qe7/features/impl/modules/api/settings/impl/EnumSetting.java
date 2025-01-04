package io.github.qe7.features.impl.modules.api.settings.impl;

import com.google.gson.JsonObject;
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

    public int getIndex(String name) {
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }

        return -1;
    }


    public int getIndex() {
        return values.indexOf(getValue());
    }

    public void setIndex(int index) {
        if (index < values.size()) {
            setValue((T) values.get(index));
        }
    }

    public void cycleForward() {
        int currentIndex = values.indexOf(getValue());
        if (currentIndex == values.size() - 1) {
            setValue((T) values.get(0));
        } else {
            setValue((T) values.get(currentIndex + 1));
        }
    }

    public void cycleBackward() {
        int currentIndex = values.indexOf(getValue());
        if (currentIndex == 0) {
            setValue((T) values.get(values.size() - 1));
        } else {
            setValue((T) values.get(currentIndex - 1));
        }
    }

    @Override
    public JsonObject serialize() {
        final JsonObject object = new JsonObject();

        object.addProperty("value", this.getValue().getName());

        return object;
    }

    @Override
    public void deserialize(JsonObject object) {
        if (!object.has("value") || !object.get("value").isJsonPrimitive()) {
            return;
        }

        final String name = object.get("value").getAsString();

        for (IEnumSetting value : values) {
            if (value.getName().equals(name)) {
                this.setValue((T) value);
                break;
            }
        }
    }
}
