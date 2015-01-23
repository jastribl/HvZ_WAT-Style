package leveleditor;

import java.util.ArrayList;
import java.util.Collections;

public class Level extends ArrayList<Item> {

    public void addItem(int xG, int yG, Item o) {
        add(new Item(xG, yG));
        sort();
    }

    public void removeItem(int i) {
        remove(i);
    }

    public void sort() {
        Collections.sort(this);
    }
}
