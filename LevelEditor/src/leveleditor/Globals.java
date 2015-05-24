package leveleditor;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

public class Globals {

    public static Graphics memoryGraphics = null;
    public static int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public static int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    public static int currentWorld = 0;
    public static int currentLevel = 0;
    public static int currentItemType = 0;
    public static int paintingMode = 0;
    public static int tabWidth = 0;
    public static final int itemSize = 32;
    public static final int halfItemSize = itemSize / 2;
    public static final int levelOffset = itemSize / 4;
    public static final int menuWidth = itemSize * 4;
    public static final int iconSize = 40;
    public static final int iconPadding = 5;
    public static final int bottomMenuHeight = iconSize + (iconPadding * 2);
    public static final int numberOfMenuItems = 9;
    public static final int loadingSize = 140;
    public static final int tabHeight = 25;
    public static ArrayList<World> worlds = new ArrayList();
    public static final ArrayList<String> allWorlds = new ArrayList();
    public static Image itemImages[] = new Image[numberOfMenuItems];
    public static Level rectangleItems = new Level();
    public static Item currentLevelObject = null;
    public static Point rectangleStart = null, rectangleEnd = null;
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int ADD = 0;
    public static final int REMOVE = 1;
    public static final int PAINT = 0;
    public static final int POINT = 1;
    public static final int RECTANGLE = 2;
    public static boolean drawingRectangle = true;

    public static final Point snapToGrid(Point p) {
        int y = p.y / levelOffset * levelOffset + halfItemSize;
        if ((y / levelOffset) % 2 == 0) {
            return new Point(((p.x + halfItemSize) / itemSize * itemSize), y);
        } else {
            return new Point((p.x / itemSize * itemSize) + halfItemSize, y);
        }
    }

    public static final void loadAll() {
        try {
            Scanner worldReader = new Scanner(new File("levels.txt"));
            while (worldReader.hasNext()) {
                allWorlds.add(worldReader.next());
            }
        } catch (IOException ex) {
            try {
                new File("levels.txt").createNewFile();
            } catch (IOException ex1) {
            }
        }
    }

    public static final void loadOne(String name) {
        worlds.add(new World(name));
        World world = worlds.get(worlds.size() - 1);
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
                    reader.nextInt(); //for transparency
                    level.addItemUnchecked(new Item(point, type, true));
                }
                world.addLevelUnchecked(level);
            }
            world.setChages(false);
        } catch (IOException ex) {
            world.addLevelUnchecked(new Level());
        }
    }

    public static final void saveAll() {
        File worldFile = new File("levels.txt");
        try (BufferedWriter worldWriter = new BufferedWriter(new FileWriter(worldFile))) {
            for (String worldName : allWorlds) {
                worldWriter.write(worldName + "\n");
            }
            for (World world : worlds) {
                world.save();
            }
        } catch (IOException ex) {
        }
    }

    public static final void closeTab(int index) {
        if (worlds.get(index).hasChanges()) {
            if (JOptionPane.showConfirmDialog(null, "Would you like to save changes to \"" + worlds.get(index).getName() + "\"?", "Save Changes?", JOptionPane.YES_NO_OPTION) == 0) {
                worlds.get(index).save();
            }
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

    public static final void removeWorld(int index) {
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this World", "Remove?", JOptionPane.YES_NO_OPTION) == 0) {
            saveAll();
            new File(worlds.get(index).getName() + ".txt").delete();
            allWorlds.remove(worlds.get(index).getName());
            worlds.remove(index);
            if (currentWorld == worlds.size()) {
                currentWorld--;
            }
            saveAll();
        }
    }

    public static final void populateRectangle() {
        rectangleItems.clear();
        try {
            int xStop = (rectangleEnd.x - rectangleStart.x) / itemSize;
            int yStop = (rectangleEnd.y - rectangleStart.y) / levelOffset;
            int xStart, xEnd, yStart, yEnd;
            if (rectangleEnd.x > rectangleStart.x) {
                xStart = 0;
                xEnd = xStop;
            } else {
                xStart = xStop;
                xEnd = 0;
            }
            if (rectangleEnd.y > rectangleStart.y) {
                yStart = 0;
                yEnd = yStop;
            } else {
                yStart = yStop;
                yEnd = 0;
            }
            for (int i = yStart; i < yEnd; i++) {
                int y = rectangleStart.y + (levelOffset * i);
                int xShift = (i % 2 == 0 ? 0 : halfItemSize);
                for (int j = xStart; j < xEnd; j++) {
                    rectangleItems.addItemUnchecked(new Item(rectangleStart.x + (itemSize * j) + xShift, y, currentItemType, true));
                }
            }
        } catch (NullPointerException e) {
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
