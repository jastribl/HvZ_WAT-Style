package leveleditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Level extends ArrayList {

    public Level() {
    }

    public void addObject(LevelObject o) {
        add(o);
        sort();
    }

    public void removeObject(int i) {
        remove(i);
        sort();
    }

    public final void sort() {
        Collections.sort(this, new Comparator<LevelObject>() {
            @Override
            public int compare(LevelObject o1, LevelObject o2) {
                if (o1.getY() == o2.getY()) {
                    return 0;
                } else {
                    return o1.getY() > o2.getY() ? 1 : -1;
                }
            }
        });
    }
}
