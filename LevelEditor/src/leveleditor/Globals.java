package leveleditor;

import Structures.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

public class Globals {

    public static Graphics memoryGraphics = null;

    public static int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public static int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    public static int currentWorld = 0, currentLevel = 0, currentItemGroup = 0, currentItemType = 0, currentDrawingMode = 0;

    public static final int numberOfMenuGroups = 2;
    public static final int numberOfBlocks = 9, numberOfSpecials = 6;
    public static final int worldTabHeight = 25, menuTabHeight = 25;
    public static int worldTabWidth = 0, menuTabWidth = 0;

    public static final int itemSize = 32;
    public static final int halfItemSize = itemSize / 2;
    public static final int levelOffset = itemSize / 4;
    public static final int menuWidth = (itemSize * 4) + menuTabHeight;
    public static final int iconSize = 40;
    public static final int iconPadding = 5;
    public static final int bottomMenuHeight = iconSize + (iconPadding * 2);
    public static ArrayList<World> worlds = new ArrayList();
    public static final ArrayList<String> allWorlds = new ArrayList();
    public static Level gridItems = new Level();
    public static Item currentLevelObject = null;
    public static Point gridStart = null, gridEnd = null;
    public static final int UP = 0, DOWN = 1;
    public static final int ADD = 0, REMOVE = 1;
    public static final int PAINT = 0, POINT = 1, RECTANGLE = 2, DIAMOND = 3;
    public static final int UNDO = 0, REDO = 1;

    public static boolean drawingGrid = true;

    public static final Point snapToGrid(Point p) {
        int y = p.y / levelOffset * levelOffset + halfItemSize;
        if ((y / levelOffset) % 2 == 0) {
            return new Point(((p.x + halfItemSize) / itemSize * itemSize), y);
        } else {
            return new Point((p.x / itemSize * itemSize) + halfItemSize, y);
        }
    }

    public static final void preloadLevelNames() {
        try {
            Scanner worldReader = new Scanner(new File("Worlds/Worlds.Worlds"));
            while (worldReader.hasNext()) {
                allWorlds.add(worldReader.next());
            }
        } catch (IOException ex) {
            try {
                new File("Worlds").mkdir();
                new File("Worlds/Worlds.Worlds").createNewFile();
            } catch (IOException ex1) {
            }
        }
    }

    public static void saveLevelNames() {
        try (BufferedWriter worldWriter = new BufferedWriter(new FileWriter(new File("worlds/Worlds.Worlds")))) {
            for (String worldName : allWorlds) {
                worldWriter.write(worldName + "\n");
            }
        } catch (IOException ex) {
        }
    }

    public static final void saveAll() {
        saveLevelNames();
        for (World world : worlds) {
            world.save();
        }
    }

    public static final void exportAll() {
        saveLevelNames();
        for (World world : worlds) {
            world.export();
        }
    }

    public static final void closeTab(int index) {
        if (worlds.get(index).hasChanges() && JOptionPane.showConfirmDialog(null, "Would you like to save changes to \"" + worlds.get(index).getName() + "\"?", "Save Changes?", JOptionPane.YES_NO_OPTION) == 0) {
            worlds.get(index).save();
        }
        worlds.remove(index);
        if (currentWorld == worlds.size()) {
            currentWorld--;
        }
    }

    public static final void closeAllTabs() {
        while (worlds.size() > 0) {
            closeTab(currentWorld);
        }
        currentWorld = 0;
    }

    public static final void removeCurrentWorld() {
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this World", "Remove?", JOptionPane.YES_NO_OPTION) == 0) {
            new File("Worlds/" + worlds.get(currentWorld).getName() + ".Level").delete();
            allWorlds.remove(worlds.get(currentWorld).getName());
            worlds.remove(currentWorld);
            if (currentWorld == worlds.size()) {
                currentWorld--;
            }
            saveLevelNames();
        }
    }

    public static final void populateGrid() {
        gridItems.clear();
        if (currentDrawingMode == RECTANGLE) {
            int xStop = (gridEnd.x - gridStart.x) / itemSize;
            int yStop = (gridEnd.y - gridStart.y) / levelOffset;
            int xStart, xEnd, yStart, yEnd;
            if (gridEnd.x > gridStart.x) {
                xStart = 0;
                xEnd = xStop;
            } else {
                xStart = xStop;
                xEnd = 0;
            }
            if (gridEnd.y > gridStart.y) {
                yStart = 0;
                yEnd = yStop;
            } else {
                yStart = yStop;
                yEnd = 0;
            }
            for (int i = yStart; i < yEnd; i++) {
                int y = gridStart.y + (levelOffset * i);
                int xShift = (i % 2 == 0 ? 0 : halfItemSize);
                for (int j = xStart; j < xEnd; j++) {
                    gridItems.addItemUnchecked(new Item(currentItemGroup, currentItemType, gridStart.x + (itemSize * j) + xShift, y));
                }
            }
        } else if (currentDrawingMode == DIAMOND) {
            Point top = (gridStart.y < gridEnd.y ? gridStart : gridEnd);
            Point bottom = (gridStart.y >= gridEnd.y ? gridStart : gridEnd);
            int heightInHalves = (bottom.y - top.y) / levelOffset;
            int widthInHalves = (top.x - bottom.x) / halfItemSize;
            int topRightDiagonal = (heightInHalves - widthInHalves) / 2;
            int bottomRightDiagonal = (heightInHalves - topRightDiagonal);
            Point point = new Point();
            int iAdd = (bottomRightDiagonal > 0 ? 1 : -1);
            int jAdd = (topRightDiagonal > 0 ? 1 : -1);
            for (int i = 1; i != bottomRightDiagonal + iAdd; i += iAdd) {
                point.x = top.x - (i * halfItemSize);
                point.y = top.y + (i * levelOffset);
                for (int j = 1; j != topRightDiagonal + jAdd; j += jAdd) {
                    point.x += jAdd * halfItemSize;
                    point.y += jAdd * levelOffset;
                    gridItems.addItemUnchecked(new Item(currentItemGroup, currentItemType, point));
                }
            }
        }
    }

    public static final String getNewWorldName() {
        String name;
        boolean bad;
        do {
            bad = false;
            try {
                name = JOptionPane.showInputDialog(null, "Name the new world", "New", JOptionPane.QUESTION_MESSAGE).replaceAll(" ", "_");
                if (name.equals("")) {
                    bad = true;
                } else {
                    for (String world : allWorlds) {
                        if (world.equals(name)) {
                            bad = true;
                            break;
                        }
                    }
                }
            } catch (NullPointerException ex) {
                return null;
            }
        } while (bad);
        return name;
    }
}
