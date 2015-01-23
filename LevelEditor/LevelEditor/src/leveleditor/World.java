package leveleditor;

import java.util.ArrayList;

public class World extends ArrayList<Level> {

    public World() {
        add(new Level());
    }

    public void addLevel(Level level) {
        add(level);
    }

    public void removeLevel(int i) {
        remove(i);
    }
}
