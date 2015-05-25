package leveleditor;

import java.awt.Point;
import java.io.*;
import java.util.*;
import static leveleditor.Globals.*;

public final class World {

    private final ArrayList<Level> world = new ArrayList();
    private String name;
    private final Backup undo = new Backup(), redo = new Backup();
    private boolean isChanged = false;

    public World(String nameGiven, boolean load) {
        name = nameGiven;
        if (load) {
            try {
                Scanner reader = new Scanner(new File(name + ".txt"));
                int xShift = screenWidth / 2 + menuWidth - reader.nextInt() / 2;
                int yShift = screenHeight / 2 - reader.nextInt() / 2;
                int numberOfLevels = reader.nextInt(), numberOfBlocks, type;
                for (int i = 0; i < numberOfLevels; i++) {
                    Level level = new Level();
                    numberOfBlocks = reader.nextInt();
                    for (int j = 0; j < numberOfBlocks; j++) {
                        type = reader.nextInt();
                        Point point = snapToGrid(new Point((reader.nextInt() * halfItemSize) + xShift, (reader.nextInt() * (itemSize / 8)) + yShift));
                        int transparency = reader.nextInt();
                        level.addItemUnchecked(new Item(point, type, true));
                    }
                    addLevelUnchecked(level);
                }
            } catch (IOException ex) {
                addLevelUnchecked(new Level());
            }
        } else {
            addLevelUnchecked(new Level());
        }
        save();
    }

    public final int size() {
        return world.size();
    }

    public final Level get(int i) {
        return world.get(i);
    }

    public final boolean hasChanges() {
        return isChanged;
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
        isChanged = false;
    }

    public final void addToCurrentLevelChecked(Item item, boolean setUndo) {
        if (get(currentLevel).addItemChecked(item)) {
            if (setUndo) {
                addUndo(new ItemBackup(ADD, currentLevel, item.getType(), (Point) item.getLocation().clone()));
                clearRedo();
            }
        }
    }

    public final void removeFromCurrentLevelChecked(Item item, boolean setUndo) {
        Item removedItem = get(currentLevel).removeItem(item);
        if (removedItem != null) {
            if (setUndo) {
                addUndo(new ItemBackup(REMOVE, currentLevel, removedItem.getType(), (Point) removedItem.getLocation().clone()));
                clearRedo();
            }
        }
    }

    public final Item getFromCurrentLevel(Item item) {
        Item removedItem = get(currentLevel).removeItem(item);
        if (removedItem != null) {
            addUndo(new ItemBackup(REMOVE, currentLevel, removedItem.getType(), (Point) removedItem.getLocation().clone()));
            clearRedo();
            try {
                return (Item) removedItem.clone();
            } catch (CloneNotSupportedException ex) {
            }
        }
        return null;
    }

    public final void applyGrid() {
        if (drawingGrid == true) {
            for (Item item : gridItems.getLevel()) {
                addToCurrentLevelChecked(item, true);
            }
        } else {
            for (Item item : gridItems.getLevel()) {
                removeFromCurrentLevelChecked(item, true);
            }
        }
        gridItems.clear();
        gridStart = null;
        gridEnd = null;
    }

    public final void addLevelUnchecked(Level l) {
        isChanged = true;
        world.add(l);
    }

    public final void remove(int i) {
        isChanged = true;
        world.remove(i);
    }

    public final String getName() {
        return name;
    }

    public final void rename() {
        String newWorldName = getNewWorldName();
        if (newWorldName != null) {
            String oldWorldName = name;
            name = newWorldName;
            allWorlds.set(allWorlds.indexOf(oldWorldName), newWorldName);
            new File(oldWorldName + ".txt").delete();
            saveLevelNames();
            save();
        }
    }

    private void addUndo(ItemBackup itemBackup) {
        isChanged = true;
        undo.add(itemBackup);
    }

    private int undoSize() {
        return undo.getBackupSize();
    }

    private int redoSize() {
        return redo.getBackupSize();
    }

    private ItemBackup peekUndo() {
        return undo.peek();
    }

    private ItemBackup popUndo() {
        isChanged = true;
        return undo.pop();
    }

    private void clearUndo() {
        isChanged = true;
        undo.clear();
    }

    private void addRedo(ItemBackup itemBackup) {
        isChanged = true;
        redo.add(itemBackup);
    }

    private ItemBackup peekRedo() {
        return redo.peek();
    }

    private ItemBackup popRedo() {
        isChanged = true;
        return redo.pop();
    }

    private void clearRedo() {
        isChanged = true;
        redo.clear();
    }

    public final void undo() {
        ItemBackup backup = peekUndo();
        if (backup == null) {
            return;
        } else if (backup.backupType == ADD || backup.backupType == REMOVE) {
            locate(backup);
            backup = peekUndo();
            addRedo(new ItemBackup(backup.backupType, backup.level, backup.type, backup.location));
            Item newItem = new Item(backup.location, backup.type, false);
            if (backup.backupType == REMOVE) {
                addToCurrentLevelChecked(newItem, false);
            } else {
                removeFromCurrentLevelChecked(newItem, false);
            }
        }
        popUndo();
        if (undoSize() == 0) {
            isChanged = false;
        }
    }

    public final void redo() {
        ItemBackup backup = peekRedo();
        if (backup == null) {
            return;
        } else if (backup.backupType == ADD || backup.backupType == REMOVE) {
            locate(backup);
            backup = peekRedo();
            addUndo(new ItemBackup(backup.backupType, backup.level, backup.type, backup.location));
            Item newItem = new Item(backup.location, backup.type, false);
            if (backup.backupType == ADD) {
                addToCurrentLevelChecked(newItem, false);
            } else {
                removeFromCurrentLevelChecked(newItem, false);
            }
        }
        popRedo();
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

    public final void chengleLevel(int direction) {
        if (direction == UP) {
            if (currentLevel + 1 == size()) {
                addLevelUnchecked(new Level());
            }
            currentLevel++;
        } else {
            if (currentLevel > 0) {
                if (get(currentLevel).size() == 0) {
                    remove(currentLevel);
                }
                currentLevel--;
            }
        }
    }

    public final void moveItems(int x, int y) {
        shiftItems(x * itemSize, y * halfItemSize);
        if ((paintingMode == RECTANGLE || paintingMode == DIAMOND) && gridStart != null) {
            gridStart.translate(x * itemSize, y * itemSize);
            populateGrid();
        }
    }

    public final void locate(ItemBackup backup) {
        while (backup.level < currentLevel) {
            chengleLevel(DOWN);
        }
        while (backup.level > currentLevel) {
            chengleLevel(UP);
        }
        get(currentLevel).setVisible(true);
        while (backup.location.x < menuWidth + itemSize) {
            moveItems(1, 0);
        }
        while (backup.location.x > screenWidth - itemSize) {
            moveItems(-1, 0);
        }
        while (backup.location.y < tabHeight + itemSize) {
            moveItems(0, 1);
        }
        while (backup.location.y > screenHeight - itemSize - bottomMenuHeight) {
            moveItems(0, -1);
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
                    if (paintingMode == POINT && currentLevelObject != null) {
                        levelToDraw.addItemChecked(currentLevelObject);
                    } else if (paintingMode == RECTANGLE || paintingMode == DIAMOND) {
                        for (int j = 0; j < gridItems.size(); j++) {
                            Item item = gridItems.get(j);
                            Point p = item.getLocation();
                            if (p.x > menuWidth - itemSize && p.x < screenWidth && p.y > tabHeight - itemSize && p.y < screenHeight) {
                                if (drawingGrid) {
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
