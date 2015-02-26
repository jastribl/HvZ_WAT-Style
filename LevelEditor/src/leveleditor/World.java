package leveleditor;

import java.util.ArrayList;

public final class World extends ArrayList<Level> {

    private String name;
    private final Backup undo, redo;

    public World(String nameGiven) {
        name = nameGiven;
        undo = new Backup();
        redo = new Backup();
    }

    public final String getName() {
        return name;
    }

    public final void setName(String nameGiven) {
        name = nameGiven;
    }

    public final void addUndo(ItemBackup itemBackup) {
        undo.add(itemBackup);
    }

    public final ItemBackup peekUndo() {
        return undo.peek();
    }

    public final ItemBackup popUndo() {
        return undo.pop();
    }

    public final void clearUndo() {
        undo.clear();
    }

    public final void addRedo(ItemBackup itemBackup) {
        redo.add(itemBackup);
    }

    public final ItemBackup peekRedo() {
        return redo.peek();
    }

    public final ItemBackup popRedo() {
        return redo.pop();
    }

    public final void clearRedo() {
        redo.clear();
    }

    public final void shiftItems(int x, int y) {
        for (Level level : this) {
            for (Item item : level) {
                item.shiftLocation(x, y);
            }
        }
        for (ItemBackup item : undo) {
            item.shiftLocation(x, y);
        }
        for (ItemBackup item : redo) {
            item.shiftLocation(x, y);
        }
    }
}
