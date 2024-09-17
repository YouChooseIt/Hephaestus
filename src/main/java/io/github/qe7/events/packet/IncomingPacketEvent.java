package io.github.qe7.events.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.zero.alpine.event.CancellableEvent;
import net.minecraft.src.Packet;

@Getter
@AllArgsConstructor
public final class IncomingPacketEvent extends CancellableEvent {

    private final Packet packet;
}
