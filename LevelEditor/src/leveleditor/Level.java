package leveleditor;

import java.util.ArrayList;
import java.util.Collections;

public class Level extends ArrayList<Item> {

    public void addObject(Item o) {
        for (Item item : this) {
            if (item.compareTo(o) == 0) {
                return;
            }
        }
        add(o);
        sort();
    }

    public void removeObject(int i) {
        remove(i);
    }

    public final void sort() {
        Collections.sort(this);
    }
}
