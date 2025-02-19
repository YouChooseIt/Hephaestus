package io.github.qe7.type;

public enum Direction {
    Z_POS(0, 0, 1),
    Z_POS_X_NEG(-1, 0, 1),
    X_NEG(-1, 0, 0),
    Z_NEG_X_NEG(-1, 0, -1),
    Z_NEG(0, 0, -1),
    Z_NEG_X_POS(1, 0, -1),
    X_POS(1, 0, 0),
    Z_POS_X_POS(1, 0, 1),
    NONE(0, 0, 0);

    public final int xOffset;
    public final int yOffset;
    public final int zOffset;

    Direction(int x, int y, int z) {
        this.xOffset = x;
        this.yOffset = y;
        this.zOffset = z;
    }

    public int getYaw() {
        return 45 * this.ordinal();
    }
}
