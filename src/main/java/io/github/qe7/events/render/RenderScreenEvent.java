package io.github.qe7.events.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.src.ScaledResolution;

@Getter
@AllArgsConstructor
public final class RenderScreenEvent {

    private final ScaledResolution scaledResolution;

    private float mouseX, mouseY;
}
