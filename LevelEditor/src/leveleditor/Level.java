package leveleditor;

import java.util.*;

public final class Level {

    private final ArrayList<Item> level = new ArrayList();
    private boolean visible = true;

    public final boolean isVisible() {
        return visible;
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

    public void addItemUnchecked(Item i) {
        level.add(i);
    }

    public boolean addItemChecked(Item i) {
        if (visible) {
            int comp;
            for (Item item : level) {
                comp = item.compareTo(i);
                if (comp > 0) {
                    break;
                } else if (comp == 0) {
                    return false;
                }
            }
            level.add(i);
            Collections.sort(level);
            return true;
        }
        return false;
    }

    public final Item removeItem(Item item) {
        if (visible) {
            int comp;
            for (int i = 0; i < level.size(); i++) {
                comp = level.get(i).compareTo(item);
                if (comp > 0) {
                    return null;
                } else if (comp == 0) {
                    try {
                        Item toBeReturned = (Item) level.get(i).clone();
                        level.remove(i);
                        return toBeReturned;
                    } catch (CloneNotSupportedException ex) {
                    }
                }
            }
        }
        return null;
    }

    public final void clear() {
        level.clear();
    }
}
