package Structures;

import java.util.*;
import Cache.BackupItem;
import static leveleditor.Globals.REMOVE;
import static leveleditor.Globals.currentLevel;

public final class Level {

    private final ArrayList<Item> level = new ArrayList();
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

    public final ArrayList<Item> getLevel() {
        return level;
    }

    public final int size() {
        return level.size();
    }

    public final Item get(int i) {
        return level.get(i);
    }

    public final void shiftLevel(int x, int y) {
        for (Item item : level) {
            item.shiftLocation(x, y);
        }
    }

    public void addItemUnchecked(Item i) {
        level.add(i);
    }

    public void addItemUncheckedAt(Item item, int i) {
        level.add(i, item);
    }

    public int addItemChecked(Item item) {
        if (visible) {
            int comp;
            int i = 0;
            for (; i < level.size(); i++) {
                comp = item.compareTo(level.get(i));
                if (comp == 0) {
                    return -1;
                } else if (comp < 0) {
                    break;
                }
            }
            level.add(i, item);
            return i;
        }
        return -1;
    }

    public final BackupItem removeItem(Item item) {
        if (visible) {
            int comp;
            for (int i = 0; i < level.size(); i++) {
                comp = level.get(i).compareTo(item);
                if (comp == 0) {
                    Item toBeReturned = level.remove(i);
                    return new BackupItem(REMOVE, currentLevel, toBeReturned.getType(), i, 1, toBeReturned.getLocation());
                } else if (comp > 0) {
                    return null;
                }
            }
        }
        return null;
    }

    public void removeItemUncheckedAt(int i) {
        level.remove(i);
    }

    public final void clear() {
        level.clear();
    }
}
