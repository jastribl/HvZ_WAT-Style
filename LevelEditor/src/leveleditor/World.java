package leveleditor;

import java.util.ArrayList;

public final class World {

    private final ArrayList<Level> world = new ArrayList();
    private String name;
    private final Backup undo, redo;
    private boolean open = false, isChanges = false;

    public World(String nameGiven) {
        name = nameGiven;
        undo = new Backup();
        redo = new Backup();
    }

    public final ArrayList<Level> getWorld() {
        return world;
    }

    public final int size() {
        return world.size();
    }

    public final Level get(int i) {
        isChanges = true;
        return world.get(i);
    }

    public final boolean isOpen() {
        return open;
    }

    public final void setOpen(boolean o) {
        open = o;
    }

    public final boolean hasChanges() {
        return isChanges;
    }

    public final void setSaved() {
        isChanges = false;
    }

    public final void addLevelUnchecked(Level l) {
        isChanges = true;
        world.add(l);
    }

    public final void remove(int i) {
        isChanges = true;
        world.remove(i);
    }

    public final String getName() {
        return name;
    }

    public final void setName(String nameGiven) {
        isChanges = true;
        name = nameGiven;
    }

    public final void addUndo(ItemBackup itemBackup) {
        isChanges = true;
        undo.add(itemBackup);
    }

    public final ItemBackup peekUndo() {
        isChanges = true;
        return undo.peek();
    }

    public final ItemBackup popUndo() {
        isChanges = true;
        return undo.pop();
    }

    public final void clearUndo() {
        isChanges = true;
        undo.clear();
    }

    public final void addRedo(ItemBackup itemBackup) {
        isChanges = true;
        redo.add(itemBackup);
    }

    public final ItemBackup peekRedo() {
        isChanges = true;
        return redo.peek();
    }

    public final ItemBackup popRedo() {
        isChanges = true;
        return redo.pop();
    }

    public final void clearRedo() {
        isChanges = true;
        redo.clear();
    }

    public final void shiftItems(int x, int y) {
        isChanges = true;
        for (Level level : world) {
            for (Item item : level.getLevel()) {
                item.shiftLocation(x, y);
            }
        }
        for (ItemBackup item : undo.getBackup()) {
            if (item.backupType == 'a' || item.backupType == 'r') {
                item.shiftLocation(x, y);
            }
        }
        for (ItemBackup item : redo.getBackup()) {
            if (item.backupType == 'a' || item.backupType == 'r') {
                item.shiftLocation(x, y);
            }
        }
    }
}
