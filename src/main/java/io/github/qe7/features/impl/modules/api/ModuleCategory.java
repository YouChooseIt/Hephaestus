package io.github.qe7.features.impl.modules.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public enum ModuleCategory {
    COMBAT("Combat", new Color(255, 105, 97)),
    MOVEMENT("Movement", new Color(119, 221, 119)),
    PLAYER("Player", new Color(174, 198, 207)),
    RENDER("Render", new Color(253, 253, 150)),
    MISC("Misc", new Color(216, 191, 216));

    private final String name;
    private final Color color;
}
