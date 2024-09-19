package io.github.qe7.events;

import lombok.Getter;
import me.zero.alpine.event.CancellableEvent;

@Getter
public final class ChatMousePressEvent extends CancellableEvent {

    private final int mouseX, mouseY;

    public ChatMousePressEvent(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
