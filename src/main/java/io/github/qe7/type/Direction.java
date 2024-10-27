package io.github.qe7.type;

import net.minecraft.src.MathHelper;

public enum Direction {
    ZPOS(0, 0, 1),
    ZPOSXNEG(-1, 0, 1),
    XNEG(-1, 0, 0),
    ZNEGXNEG(-1, 0, -1),
    ZNEG(0, 0, -1),
    ZNEGXPOS(1, 0, -1),
    XPOS(1, 0, 0),
    ZPOSXPOS(1, 0, 1),
    NONE(0, 0, 0);

    public int xOffset;
    public int yOffset;
    public int zOffset;

    private Direction(int x, int y, int z) {
        this.xOffset = x;
        this.yOffset = y;
        this.zOffset = z;
    }

    public double getCenterX(double x) {
        double xx = MathHelper.floor_double(x);
        switch (this) {
            case ZPOS:
            case ZNEG: {
                return xx + 0.5;
            }
        }
        return x;
    }

    public double getCenterZ(double z) {
        double zz = MathHelper.floor_double(z);
        switch (this) {
            case XNEG:
            case XPOS: {
                return zz + 0.5;
            }
        }
        return z;
    }

    public int getYaw() {
        return 45 * this.ordinal();
    }
}
