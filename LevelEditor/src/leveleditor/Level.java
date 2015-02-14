package leveleditor;

import java.util.ArrayList;
import java.util.Collections;

public class Level extends ArrayList<Item> {

    public boolean addItem(Item o) {
        for (Item item : this) {
            if (item.compareTo(o) == 0) {
                return false;
            }
        }
        add(o);
//        sort();
        return true;
    }

    public void removeItem(int i) {
        remove(i);
    }

//    public final void sort() {
//        Collections.sort(this);
//    }
}
