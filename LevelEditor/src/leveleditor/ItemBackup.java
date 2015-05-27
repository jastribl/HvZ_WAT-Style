package leveleditor;

import java.awt.Point;

public final class ItemBackup {

    public int backupType, type, level, arrayIndex;
    public Point location;

    public ItemBackup(int backupTypeGiven, int levelGiven, int typeGiven, int arrayIndexGiven, Point locationGiven) {
        backupType = backupTypeGiven;
        level = levelGiven;
        type = typeGiven;
        arrayIndex = arrayIndexGiven;
        location = (Point) locationGiven.clone();
    }

    public final void shiftLocation(int xShift, int yShift) {
        location.x += xShift;
        location.y += yShift;
    }
}
