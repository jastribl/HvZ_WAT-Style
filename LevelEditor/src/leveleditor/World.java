package leveleditor;

import java.awt.Point;
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
            for (int i = 0; i < level.getLevel().size(); i++) {
                if (level.getLevel().get(i).getX() < minX) {
                    minX = level.getLevel().get(i).getX();
                }
                if (level.getLevel().get(i).getX() > maxX) {
                    maxX = level.getLevel().get(i).getX();
                }
                if (level.getLevel().get(i).getY() < minY) {
                    minY = level.getLevel().get(i).getY();
                }
                if (level.getLevel().get(i).getY() > maxY) {
                    maxY = level.getLevel().get(i).getY();
                }
            }
        }
        String levelText = String.valueOf((maxX - minX) + itemSize) + " " + String.valueOf((maxY - minY) + itemSize) + "\n" + String.valueOf(world.size()) + "\n";
        for (Level level : world) {
            levelText += String.valueOf(level.size()) + "\n";
            int size = level.getLevel().size();
            for (int i = 0; i < size; i++) {
                int trans = 0;
                levelText += String.valueOf(level.getLevel().get(i).getType()) + " " + String.valueOf((level.getLevel().get(i).getX() - minX) / halfItemSize) + " " + String.valueOf((level.getLevel().get(i).getY() - minY) / (levelOffset / 2)) + " " + String.valueOf(trans) + "\n";
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

    public final void rename() {
        String newWorldName = getNewWorldName();
        if (newWorldName != null) {
            String oldWorldName = name;
            isChanges = true;
            name = newWorldName;
            allWorlds.set(allWorlds.indexOf(oldWorldName), newWorldName);
            new File(oldWorldName + ".txt").delete();
            saveAll();
        }
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
            if (item.backupType == ADD || item.backupType == REMOVE) {
                item.shiftLocation(x, y);
            }
        }
        for (ItemBackup item : redo.getBackup()) {
            if (item.backupType == ADD || item.backupType == REMOVE) {
                item.shiftLocation(x, y);
            }
        }
    }

    public final void draw() {
        for (int i = 0; i < world.size(); i++) {
            Level level = world.get(i);
            if (level.isVisible()) {
                Level levelToDraw = new Level();
                for (int j = 0; j < level.size(); j++) {
                    Item item = level.get(j);
                    Point p = item.getLocation();
                    if (p.x > menuWidth - itemSize && p.x < screenWidth && p.y > tabHeight - itemSize && p.y < screenHeight) {
                        levelToDraw.addItemUnchecked(item);
                    }
                }
                if (currentLevel == i) {
                    if ((paintingMode == PAINT || paintingMode == POINT) && currentLevelObject != null) {
                        levelToDraw.addItemChecked(currentLevelObject);
                    } else if (paintingMode == RECTANGLE) {
                        for (int j = 0; j < rectangleItems.size(); j++) {
                            Item item = rectangleItems.get(j);
                            Point p = item.getLocation();
                            if (p.x > menuWidth - itemSize && p.x < screenWidth && p.y > tabHeight - itemSize && p.y < screenHeight) {
                                if (drawingRectangle) {
                                    levelToDraw.addItemChecked(item);
                                } else {
                                    levelToDraw.removeItem(item);
                                }
                            }
                        }
                    }
                }
                for (int j = 0; j < levelToDraw.size(); j++) {
                    if (i == currentLevel) {
                        levelToDraw.get(j).draw();
                    } else {
                        levelToDraw.get(j).drawFaded();
                    }
                }
            }
        }
    }
}
