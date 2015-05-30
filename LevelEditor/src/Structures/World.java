package Structures;

import java.io.*;
import java.util.*;
import Cache.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import static leveleditor.Globals.*;

public final class World {

    private final ArrayList<Level> world = new ArrayList();
    private String name;
    private final Cache undo = new Cache(), redo = new Cache();
    private boolean isChanged = false;

    public World(String nameGiven, boolean load) {
        name = nameGiven;
        if (load) {
            try {
                Scanner reader = new Scanner(new File("Worlds/" + name + ".txt"));
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
                        level.addItemUnchecked(new Item(0, type, point, true));//need to save group
                    }
                    world.add(level);
                }
            } catch (IOException ex) {
                world.add(new Level());
            }
        } else {
            world.add(new Level());
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
            int size = level.size();
            levelText += String.valueOf(size) + "\n";
            for (int i = 0; i < size; i++) {
                int transparency = 0;
                levelText += String.valueOf(level.getLevel().get(i).getType()) + " " + String.valueOf((level.getLevel().get(i).getX() - minX) / halfItemSize) + " " + String.valueOf((level.getLevel().get(i).getY() - minY) / (levelOffset / 2)) + " " + String.valueOf(transparency) + "\n";
            }
        }
        File file = new File("Worlds/" + getName() + ".txt");
        try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
            output.write(levelText);
        } catch (IOException ex) {
            return;
        }
        isChanged = false;
    }

    public final void addToCurrentLevelChecked(Item item) {
        int index = get(currentLevel).addItemChecked(item);
        if (index != -1) {
            undo.add(new BackupItem(ADD, currentLevel, item.getGroup(), item.getType(), index, 1, item.getLocation()));
            isChanged = true;
            redo.clear();
        }
    }

    public final void removeFromCurrentLevelChecked(Item item) {
        BackupItem removedItem = get(currentLevel).removeItem(item);
        if (removedItem != null) {
            undo.add(removedItem);
            isChanged = true;
            redo.clear();
        }
    }

    public final Item getFromCurrentLevel(Item item) {
        BackupItem removedItem = get(currentLevel).removeItem(item);
        if (removedItem != null) {
            undo.add(removedItem);
            isChanged = true;
            redo.clear();
            return new Item(removedItem.group, removedItem.type, removedItem.location, false);
        }
        return null;
    }

    public final void applyGrid() {
        int startingSize = undo.getBackupSize();
        if (drawingGrid == true) {
            for (Item item : gridItems.getLevel()) {
                addToCurrentLevelChecked(item);
            }
        } else {
            for (Item item : gridItems.getLevel()) {
                removeFromCurrentLevelChecked(item);
            }
        }
        int diff = Math.abs(undo.getBackupSize() - startingSize);
        if (diff > 0) {
            undo.getCache().get(undo.getBackupSize() - 1).setRepeats(diff);
        }
        gridItems.clear();
        gridStart = null;
        gridEnd = null;
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
            new File("Worlds/" + oldWorldName + ".txt").delete();
            saveLevelNames();
            save();
        }
    }

    public final void Do(int type) {
        Cache source, other;
        if (type == UNDO) {
            source = undo;
            other = redo;
        } else if (type == REDO) {
            source = redo;
            other = undo;
        } else {
            return;
        }
        BackupItem backup = source.peek();
        if (backup != null && (backup.backupType == ADD || backup.backupType == REMOVE)) {
            int number = backup.getRepeats();
            locateBackup(source.peek());
            for (int i = 0; i < number; i++) {
                backup = source.peek();
                other.add(new BackupItem(backup.backupType, backup.level, backup.group, backup.type, backup.arrayIndex, 1, backup.location));
                Item newItem = new Item(backup.group, backup.type, backup.location, false);
                if (backup.backupType == (type == UNDO ? REMOVE : ADD)) {
                    get(currentLevel).addItemUncheckedAt(newItem, backup.arrayIndex);
                } else {
                    get(currentLevel).removeItemUncheckedAt(backup.arrayIndex);
                }
                source.pop();
            }
            if (type == UNDO) {
                other.getCache().get(other.getBackupSize() - 1).setRepeats(number);
            }
            isChanged = (source.getBackupSize() != 0);
        }
    }

    private void locateBackup(BackupItem backup) {
        while (backup.level < currentLevel) {
            chengleLevel(DOWN);
        }
        while (backup.level > currentLevel) {
            chengleLevel(UP);
        }
        get(currentLevel).setVisible(true);
        findLocation(backup.location);
    }

    public final void shiftItems(int x, int y) {
        for (Level level : world) {
            for (Item item : level.getLevel()) {
                item.shiftLocation(x, y);
            }
        }
        for (BackupItem item : undo.getCache()) {
            if (item.backupType == ADD || item.backupType == REMOVE) {
                item.shiftLocation(x, y);
            }
        }
        for (BackupItem item : redo.getCache()) {
            if (item.backupType == ADD || item.backupType == REMOVE) {
                item.shiftLocation(x, y);
            }
        }
    }

    public final void moveItems(int x, int y) {
        shiftItems(x * itemSize, y * halfItemSize);
        if ((currentDrawingMode == RECTANGLE || currentDrawingMode == DIAMOND) && gridStart != null && gridEnd != null) {
            gridStart.translate(x * itemSize, y * halfItemSize);
            populateGrid();
        }
    }

    public final void chengleLevel(int direction) {
        if (direction == UP) {
            if (currentLevel + 1 == size()) {
                world.add(new Level());
            }
            currentLevel++;
        } else {
            if (currentLevel > 0) {
                if (get(currentLevel).size() == 0) {
                    world.remove(currentLevel);
                }
                currentLevel--;
            }
        }
    }

    private void findLocation(Point location) {
        while (location.x < menuWidth + itemSize) {
            moveItems(1, 0);
        }
        while (location.x > screenWidth - itemSize) {
            moveItems(-1, 0);
        }
        while (location.y < worldTabHeight + halfItemSize) {
            moveItems(0, 1);
        }
        while (location.y > screenHeight - itemSize - bottomMenuHeight) {
            moveItems(0, -1);
        }
    }

    public final void findFirstItem() {
        get(currentLevel).setVisible(true);
        if (get(currentLevel).size() > 0) {
            Item item = get(currentLevel).get(0);
            findLocation(item.getLocation());
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
                    if (p.x > menuWidth - itemSize && p.x < screenWidth && p.y > worldTabHeight - itemSize && p.y < screenHeight) {
                        levelToDraw.addItemUnchecked(item);
                    }
                }
                if (currentLevel == i) {
                    if (currentDrawingMode == POINT && currentLevelObject != null) {
                        levelToDraw.addItemChecked(currentLevelObject);
                    } else if (currentDrawingMode == RECTANGLE || currentDrawingMode == DIAMOND) {
                        for (int j = 0; j < gridItems.size(); j++) {
                            Item item = gridItems.get(j);
                            Point p = item.getLocation();
                            if (p.x > menuWidth - itemSize && p.x < screenWidth && p.y > worldTabHeight - itemSize && p.y < screenHeight) {
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
        Font defaultFont = memoryGraphics.getFontMetrics().getFont();
        int count = 0;
        for (int i = 0; i < worlds.size(); i++) {
            if (currentWorld == i) {
                memoryGraphics.setColor(Color.lightGray);
            } else {
                memoryGraphics.setColor(Color.gray);
            }
            memoryGraphics.fillRoundRect(menuWidth + (count * worldTabWidth) + 1, 0, worldTabWidth - 1, worldTabHeight, 10, 20);
            memoryGraphics.fillRect(menuWidth + (count * worldTabWidth) + 1, worldTabHeight / 2, worldTabWidth - 1, (worldTabHeight / 2) + 1);
            memoryGraphics.setColor(Color.black);
            if (worlds.get(i).hasChanges()) {
                memoryGraphics.setFont(new Font("default", Font.BOLD, defaultFont.getSize()));
            }
            Rectangle2D stringSize = memoryGraphics.getFontMetrics().getStringBounds(worlds.get(i).getName(), memoryGraphics);
            memoryGraphics.drawString(worlds.get(i).getName(), menuWidth + 1 + (count * worldTabWidth) + (int) ((worldTabWidth - stringSize.getWidth()) / 2), (worldTabHeight / 3) + (int) (stringSize.getHeight() / 2));
            memoryGraphics.setFont(defaultFont);
            count++;
        }
    }
}
