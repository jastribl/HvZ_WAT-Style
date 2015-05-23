package leveleditor;

import java.awt.Point;

public final class ItemBackup {

    public int backupType, type, level;
    public Point location;

    public ItemBackup(int backupTypeGiven, int levelGiven, int typeGiven, Point locationGiven) {
        backupType = backupTypeGiven;
        level = levelGiven;
        type = typeGiven;
        location = locationGiven;
    }

    public ItemBackup(int backupTypeGiven) {
        backupType = backupTypeGiven;
        level = 0;
        type = 0;
        location = null;
    }

    public final void shiftLocation(int xShift, int yShift) {
        location.x += xShift;
        location.y += yShift;
    }
}
