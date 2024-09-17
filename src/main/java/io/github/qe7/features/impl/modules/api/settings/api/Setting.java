package io.github.qe7.features.impl.modules.api.settings.api;

import lombok.Getter;
import lombok.Setter;

import java.util.function.BooleanSupplier;

public abstract class Setting<T> {

    private BooleanSupplier supplier;

    @Getter
    private final T defaultValue;
    @Setter
    @Getter
    private T value;

    @Getter
    private final String name;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public boolean shouldHide() {
        return supplier != null && !supplier.getAsBoolean();
    }

    public <V extends Setting<?>> V supplyIf(BooleanSupplier supplier) {
        this.supplier = supplier;
        return (V) this;
    }
}
