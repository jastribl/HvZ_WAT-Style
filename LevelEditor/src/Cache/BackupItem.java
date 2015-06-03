package Cache;

import java.awt.Point;

public final class BackupItem {

    private final int backupType, level, group, type, arrayIndex;
    private int repeats;
    private final Point location;

    public BackupItem(int backupTypeG, int levelG, int groupG, int typeG, int arrayIndexG, int repeatsG, Point locationG) {
        backupType = backupTypeG;
        level = levelG;
        group = groupG;
        type = typeG;
        arrayIndex = arrayIndexG;
        repeats = repeatsG;
        location = (Point) locationG.clone();
    }

    public final int getBackupType() {
        return backupType;
    }

    public final int getLevel() {
        return level;
    }

    public final int getGroup() {
        return group;
    }

    public final int getType() {
        return type;
    }

    public final int getArrayIndex() {
        return arrayIndex;
    }

    public final int getRepeats() {
        return repeats;
    }

    public final Point getLocation() {
        return location;
    }

    public final void setRepeats(int repeatsG) {
        repeats = repeatsG;
    }

    public final void shiftLocation(int xShift, int yShift) {
        location.x += xShift;
        location.y += yShift;
    }
}
