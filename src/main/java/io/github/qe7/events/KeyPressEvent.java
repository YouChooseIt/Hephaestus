package io.github.qe7.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class KeyPressEvent {

    private final int keyCode;
}
