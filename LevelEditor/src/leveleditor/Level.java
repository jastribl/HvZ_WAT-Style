package leveleditor;

import java.util.ArrayList;
import java.util.Collections;

public class Level extends ArrayList<Item> {

    private boolean visible = true;

    public final boolean isVisible() {
        return visible;
    }

    public final void switchVisibility() {
        visible = !visible;
    }

    public boolean addItem(Item i) {
        if (visible) {
            for (Item item : this) {
                if (item.compareTo(i) == 0) {
                    return false;
                }
            }
            add(i);
            sort();
            return true;
        }
        return false;
    }

    public final boolean removeItem(int i) {
        if (visible) {
            remove(i);
            return true;
        }
        return false;
    }

    public final boolean removeItem(Item item) {
        if (visible) {
            int match;
            for (int i = 0; i < size(); i++) {
                match = get(i).compareTo(item);
                if (match > 0) {
                    return false;
                } else if (match == 0) {
                    remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    private final void sort() {
        Collections.sort(this);
    }

}
