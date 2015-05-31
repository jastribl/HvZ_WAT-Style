package Structures;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.*;
import static leveleditor.Globals.*;

public final class World {

    private final ArrayList<Level> world = new ArrayList();
    private String name;
    private final Cache undo = new Cache(), redo = new Cache();
    private boolean isChanged = false;
    private final int UNDO = 0, REDO = 1;

    public World(String nameGiven, boolean load) {
        name = nameGiven;
        if (load) {
            try {
                Scanner reader = new Scanner(new File("Worlds/" + name + ".txt"));
                int xShift = screenWidth / 2 + menuWidth - reader.nextInt() / 2;
                int yShift = screenHeight / 2 - reader.nextInt() / 2;
                int numberOfLevels = reader.nextInt(), numberOfBlocks, group, type;
                for (int i = 0; i < numberOfLevels; i++) {
                    Level level = new Level();
                    numberOfBlocks = reader.nextInt();
                    for (int j = 0; j < numberOfBlocks; j++) {
                        group = reader.nextInt();
                        type = reader.nextInt();
                        Point point = snapToGrid(new Point((reader.nextInt() * halfObjectSize) + xShift, (reader.nextInt() * (objectSize / 8)) + yShift));
                        int transparency = reader.nextInt();
                        level.addUnchecked(new BaseObject(group, type, point));
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
        String levelText = String.valueOf((maxX - minX) + objectSize) + " " + String.valueOf((maxY - minY) + objectSize) + "\n" + String.valueOf(world.size()) + "\n";
        for (Level level : world) {
            int size = level.size();
            levelText += String.valueOf(size) + "\n";
            for (int i = 0; i < size; i++) {
                int transparency = 0;
                levelText += String.valueOf(level.getLevel().get(i).getGroup()) + " " + String.valueOf(level.getLevel().get(i).getType()) + " " + String.valueOf((level.getLevel().get(i).getX() - minX) / halfObjectSize) + " " + String.valueOf((level.getLevel().get(i).getY() - minY) / (levelOffset / 2)) + " " + String.valueOf(transparency) + "\n";
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

    public final void addToCurrentLevelChecked(BaseObject object) {
        int index = get(currentLevel).addChecked(object);
        if (index != -1) {
            undo.add(new BackupObject(ADD, currentLevel, index, 1, object));
            isChanged = true;
            redo.clear();
        }
    }

    public final void removeFromCurrentLevelChecked(BaseObject object) {
        BackupObject removed = get(currentLevel).remove(object);
        if (removed != null) {
            undo.add(removed);
            isChanged = true;
            redo.clear();
        }
    }

    public final BaseObject getFromCurrentLevel(BaseObject object) {
        BackupObject removed = get(currentLevel).remove(object);
        if (removed != null) {
            undo.add(removed);
            isChanged = true;
            redo.clear();
            return new BaseObject(removed.getGroup(), removed.getType(), removed.getLocation());
        }
        return null;
    }

    public final void applyGrid() {
        int startingSize = undo.getBackupSize();
        if (drawingGrid == true) {
            for (BaseObject object : grid.getLevel()) {
                addToCurrentLevelChecked(object);
            }
        } else {
            for (BaseObject object : grid.getLevel()) {
                removeFromCurrentLevelChecked(object);
            }
        }
        int diff = Math.abs(undo.getBackupSize() - startingSize);
        if (diff > 0) {
            undo.getCache().get(undo.getBackupSize() - 1).setRepeats(diff);
        }
        grid.clear();
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

    public final void undo() {
        Do(UNDO);
    }

    public final void redo() {
        Do(REDO);
    }

    private void Do(int type) {
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
        BackupObject backup = source.peek();
        if (backup != null && (backup.getBackupType() == ADD || backup.getBackupType() == REMOVE)) {
            int number = backup.getRepeats();
            locateBackup(source.peek());
            for (int i = 0; i < number; i++) {
                backup = source.peek();
                other.add(backup);
                BaseObject newObject = new BaseObject(backup.getGroup(), backup.getType(), backup.getLocation());
                if (backup.getBackupType() == (type == UNDO ? REMOVE : ADD)) {
                    get(currentLevel).addUncheckedAt(newObject, backup.getArrayIndex());
                } else {
                    get(currentLevel).removeUncheckedAt(backup.getArrayIndex());
                }
                source.pop();
            }
            if (type == UNDO) {
                other.getCache().get(other.getBackupSize() - 1).setRepeats(number);
            }
            isChanged = (source.getBackupSize() != 0);
        }
    }

    private void locateBackup(BackupObject backup) {
        while (backup.getLevel() < currentLevel) {
            chengleLevel(DOWN);
        }
        while (backup.getLevel() > currentLevel) {
            chengleLevel(UP);
        }
        get(currentLevel).setVisible(true);
        findLocation(backup.getLocation());
    }

    public final void shiftAll(int x, int y) {
        for (Level level : world) {
            for (BaseObject object : level.getLevel()) {
                object.shiftLocation(x, y);
            }
        }
        for (BackupObject object : undo.getCache()) {
            if (object.getBackupType() == ADD || object.getBackupType() == REMOVE) {
                object.shiftLocation(x, y);
            }
        }
        for (BackupObject object : redo.getCache()) {
            if (object.getBackupType() == ADD || object.getBackupType() == REMOVE) {
                object.shiftLocation(x, y);
            }
        }
    }

    public final void moveAll(int x, int y) {
        shiftAll(x * objectSize, y * halfObjectSize);
        if ((currentDrawingMode == RECTANGLE || currentDrawingMode == DIAMOND) && gridStart != null && gridEnd != null) {
            gridStart.translate(x * objectSize, y * halfObjectSize);
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
        while (location.x < menuWidth + objectSize) {
            moveAll(1, 0);
        }
        while (location.x > screenWidth - objectSize) {
            moveAll(-1, 0);
        }
        while (location.y < worldTabHeight + halfObjectSize) {
            moveAll(0, 1);
        }
        while (location.y > screenHeight - objectSize - bottomMenuHeight) {
            moveAll(0, -1);
        }
    }

    public final void findFirstObject() {
        get(currentLevel).setVisible(true);
        if (get(currentLevel).size() > 0) {
            findLocation(get(currentLevel).get(0).getLocation());
        }
    }

    public final void draw() {
        for (int i = 0; i < world.size(); i++) {
            Level level = world.get(i);
            if (level.isVisible()) {
                Level levelToDraw = new Level();
                for (int j = 0; j < level.size(); j++) {
                    BaseObject object = level.get(j);
                    Point p = object.getLocation();
                    if (p.x > menuWidth - objectSize && p.x < screenWidth && p.y > worldTabHeight - objectSize && p.y < screenHeight) {
                        levelToDraw.addUnchecked(object);
                    }
                }
                if (currentLevel == i) {
                    if (currentDrawingMode == POINT && currentLevelObject != null) {
                        levelToDraw.addChecked(currentLevelObject);
                    } else if (currentDrawingMode == RECTANGLE || currentDrawingMode == DIAMOND) {
                        for (int j = 0; j < grid.size(); j++) {
                            BaseObject object = grid.get(j);
                            Point p = object.getLocation();
                            if (p.x > menuWidth - objectSize && p.x < screenWidth && p.y > worldTabHeight - objectSize && p.y < screenHeight) {
                                if (drawingGrid) {
                                    levelToDraw.addChecked(object);
                                } else {
                                    levelToDraw.remove(object);
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
