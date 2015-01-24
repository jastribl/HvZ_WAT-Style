package leveleditor;

import java.util.ArrayList;
import java.util.Collections;

public class Level extends ArrayList<Item> {

    public void addObject(Item o) {
        add(o);
        sort();
    }

    public void removeObject(int i) {
        remove(i);
        sort();
    }

    public final void sort() {
        Collections.sort(this);
    }
}
