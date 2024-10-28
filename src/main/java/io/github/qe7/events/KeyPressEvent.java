package io.github.qe7.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.zero.alpine.event.CancellableEvent;

@Getter
@AllArgsConstructor
public final class KeyPressEvent extends CancellableEvent {

    private final int keyCode;
}
