package Structures;

import Cache.BackupObject;
import java.util.ArrayList;
import static leveleditor.Globals.*;

public final class Level {

    private final ArrayList<BaseObject> level = new ArrayList();
    private boolean visible = true;

    public final boolean isVisible() {
        return visible;
    }

    public final void setVisible(boolean visibleGiven) {
        visible = visibleGiven;
    }

    public final void switchVisibility() {
        visible = !visible;
    }

    public final ArrayList<BaseObject> getLevel() {
        return level;
    }

    public final int size() {
        return level.size();
    }

    public final BaseObject get(int i) {
        return level.get(i);
    }

    public final void shiftLevel(int x, int y) {
        for (BaseObject object : level) {
            object.shiftLocation(x, y);
        }
    }

    public void addUnchecked(BaseObject o) {
        level.add(o);
    }

    public void addUncheckedAt(BaseObject object, int i) {
        level.add(i, object);
    }

    public int addChecked(BaseObject object) {
        if (visible) {
            int comp;
            int i = 0;
            for (; i < level.size(); i++) {
                comp = object.compareTo(level.get(i));
                if (comp == 0) {
                    return -1;
                } else if (comp < 0) {
                    break;
                }
            }
            level.add(i, object);
            return i;
        }
        return -1;
    }

    public final BackupObject remove(BaseObject object) {
        if (visible) {
            int comp;
            for (int i = 0; i < level.size(); i++) {
                comp = level.get(i).compareTo(object);
                if (comp == 0) {
                    return new BackupObject(REMOVE, currentLevel, i, 1, level.remove(i));
                } else if (comp > 0) {
                    return null;
                }
            }
        }
        return null;
    }

    public void removeUncheckedAt(int i) {
        level.remove(i);
    }

    public final void clear() {
        level.clear();
    }
}
