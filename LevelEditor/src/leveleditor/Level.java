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
            int comp;
            for (Item item : this) {
                comp = item.compareTo(i);
                if (comp > 0) {
                    break;
                } else if (comp == 0) {
                    return false;
                }
            }
            add(i);
            Collections.sort(this);
            return true;
        }
        return false;
    }

    public final Item removeItem(Item item) {
        if (visible) {
            int comp;
            for (int i = 0; i < size(); i++) {
                comp = get(i).compareTo(item);
                if (comp > 0) {
                    return null;
                } else if (comp == 0) {
                    try {
                        Item toBeReturned = (Item) get(i).clone();
                        remove(i);
                        return toBeReturned;
                    } catch (CloneNotSupportedException ex) {
                    }
                }
            }
        }
        return null;
    }
}
