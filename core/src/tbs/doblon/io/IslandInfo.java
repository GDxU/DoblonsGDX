package tbs.doblon.io;

public class IslandInfo {
    public final int sides;
    public final int color;
    public final float[] offsets;

    public IslandInfo(int sides, int color, float[] offsets) {
        this.color = color;
        this.sides = sides;
        this.offsets = offsets;

    }
}
