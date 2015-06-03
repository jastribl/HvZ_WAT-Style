package Cache;

import Structures.BaseObject;
import java.awt.Point;

public final class BackupObject {

    private final int backupType, level, arrayIndex;
    private int repeats;
    private final BaseObject object;

    public BackupObject(int backupTypeG, int levelG, int arrayIndexG, int repeatsG, BaseObject objectG) {
        backupType = backupTypeG;
        level = levelG;
        arrayIndex = arrayIndexG;
        repeats = repeatsG;
        object = objectG.DeepCopy();
    }

    public final int getBackupType() {
        return backupType;
    }

    public final int getLevel() {
        return level;
    }

    public final int getGroup() {
        return object.getGroup();
    }

    public final int getType() {
        return object.getType();
    }

    public final int getArrayIndex() {
        return arrayIndex;
    }

    public final int getRepeats() {
        return repeats;
    }

    public final Point getLocation() {
        return object.getLocation();
    }

    public final BaseObject getObject() {
        return object;
    }

    public final void setRepeats(int repeatsG) {
        repeats = repeatsG;
    }

    public final void shiftLocation(int xShift, int yShift) {
        object.shiftLocation(xShift, yShift);
    }
}
