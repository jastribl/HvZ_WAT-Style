package Cache;

import java.awt.Point;

public final class BackupItem {

    public final int backupType, level, group, type, arrayIndex;
    private int repeats;
    public final Point location;

    public BackupItem(int backupTypeG, int levelG, int groupG, int typeG, int arrayIndexG, int repeatsG, Point locationG) {
        backupType = backupTypeG;
        level = levelG;
        group = groupG;
        type = typeG;
        arrayIndex = arrayIndexG;
        repeats = repeatsG;
        location = (Point) locationG.clone();
    }

    public final int getRepeats() {
        return repeats;
    }

    public final void setRepeats(int repeatsG) {
        repeats = repeatsG;
    }

    public final void shiftLocation(int xShift, int yShift) {
        location.x += xShift;
        location.y += yShift;
    }
}
