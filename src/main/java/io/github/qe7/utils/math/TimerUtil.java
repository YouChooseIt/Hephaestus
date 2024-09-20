package io.github.qe7.utils.math;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TimerUtil {

    private long lastMS = System.currentTimeMillis();

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - lastMS > time) {
            if (reset) {
                reset();
            }
            return true;
        }
        return false;
    }

    public void reset() {
        lastMS = System.currentTimeMillis();
    }

    public long getTime() {
        return System.currentTimeMillis() - lastMS;
    }

}
