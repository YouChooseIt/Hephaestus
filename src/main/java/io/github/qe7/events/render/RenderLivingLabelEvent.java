package io.github.qe7.events.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.zero.alpine.event.CancellableEvent;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.RenderManager;

@Getter
@AllArgsConstructor
public class RenderLivingLabelEvent extends CancellableEvent {

    private final EntityLiving entity;

    private final RenderManager renderManager;

    private final double x, y, z;
}
