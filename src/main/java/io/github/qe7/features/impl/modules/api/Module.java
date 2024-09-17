package io.github.qe7.features.impl.modules.api;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.commands.api.Command;
import lombok.Getter;
import lombok.Setter;
import me.zero.alpine.listener.Subscriber;

@Getter
@Setter
public abstract class Module extends Command implements Subscriber {

    // Module category
    private final ModuleCategory category;

    // Key bind
    private int keyBind;

    // State
    private boolean enabled;

    // Constructor
    public Module(final String name, final String description, final ModuleCategory category) {
        super(name, description);
        this.category = category;
    }

    /**
     * Called when the module is enabled
     */
    public void onEnable() {
        Hephaestus.getInstance().getEventBus().subscribe(this);
    }

    /**
     * Called when the module is disabled
     */
    public void onDisable() {
        Hephaestus.getInstance().getEventBus().unsubscribe(this);
    }

    /**
     * Sets the module to enabled or disabled
     *
     * @param enabled the new state of the module
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    /**
     * Toggles the module
     */
    public void toggle() {
        enabled = !enabled;

        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }
}
