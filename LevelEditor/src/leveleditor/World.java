package leveleditor;

import java.io.*;
import java.util.ArrayList;
import static leveleditor.Globals.*;

public final class World {

    private final ArrayList<Level> world = new ArrayList();
    private String name;
    private final Backup undo, redo;
    private boolean isChanges = false;

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
        return world.get(i);
    }

    public final boolean hasChanges() {
        return isChanges;
    }

    public final void setChages(boolean set) {
        isChanges = set;
    }

    public final void save() {
        int minX = 999999999, minY = 999999999, maxX = -999999999, maxY = -999999999;
        for (Level level : world) {
            for (Item item : level.getLevel()) {
                item.snap();
                if (item.getX() < minX) {
                    minX = item.getX();
                }
                if (item.getX() > maxX) {
                    maxX = item.getX();
                }
                if (item.getY() < minY) {
                    minY = item.getY();
                }
                if (item.getY() > maxY) {
                    maxY = item.getY();
                }
            }
        }
        String levelText = String.valueOf((maxX - minX) + itemSize) + " " + String.valueOf((maxY - minY) + itemSize) + "\n" + String.valueOf(world.size()) + "\n";
        for (Level level : world) {
            levelText += String.valueOf(level.size()) + "\n";
            for (Item item : level.getLevel()) {
                int trans = 0;
                levelText += String.valueOf(item.getType()) + " " + String.valueOf((item.getX() - minX) / halfItemSize) + " " + String.valueOf((item.getY() - minY) / (levelOffset / 2)) + " " + String.valueOf(trans) + "\n";
            }
        }
        File file = new File(getName() + ".txt");
        try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
            output.write(levelText);
        } catch (IOException ex) {
            return;
        }
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

    public final int undoSize() {
        return undo.getBackupSize();
    }

    public final int redoSize() {
        return redo.getBackupSize();
    }

    public final ItemBackup peekUndo() {
        return undo.peek();
    }

    public final ItemBackup popUndo() {
        isChanges = true;
        return undo.pop();
    }

//    public final void clearUndo() {
//        isChanges = true;
//        undo.clear();
//    }
    public final void addRedo(ItemBackup itemBackup) {
        isChanges = true;
        redo.add(itemBackup);
    }

    public final ItemBackup peekRedo() {
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
