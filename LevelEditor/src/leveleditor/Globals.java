package leveleditor;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class Globals {

    public static Graphics memoryGraphics = null;
    public static int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(), currentItemType = 0, currentLevel = 0, currentWorld = 0, tabWidth = 0, paintingMode = 0;
    public static final int itemSize = 32, levelOffset = itemSize / 4, menuWidth = itemSize * 4, tabHeight = 25, iconSize = 40, iconPadding = 5, bottomMenuHeight = iconSize + (iconPadding * 2), numberOfItemsTypes = 9, numberOfIcons = 5, numberOfPaintingTools = 3;
    public static Item currentLevelObject = null;
    public static boolean canDraw = false, drawOpen = false;
    public static ArrayList<World> worlds = new ArrayList();
    public static final Item[] menuItems = new Item[numberOfItemsTypes];
    public static final JPopupMenu tabsRightClickMenu = new JPopupMenu();
    public static final String[] tabsRightClickText = {"Close", "Close All", "Rename", "Save", "Save All", "Delete"};
    public static final JMenuItem tabsRightClickMenuItems[] = new JMenuItem[tabsRightClickText.length];
    public static final OpenWindow openWindow = new OpenWindow();
    public static Image itemImages[] = new Image[numberOfItemsTypes], iconImages[] = new Image[numberOfIcons];
    public static final ArrayList<String> allWorlds = new ArrayList();

    public static Point snapToGrid(Point p) {
        int yy = p.y / levelOffset * levelOffset + (itemSize / 2);
        if ((yy / levelOffset) % 2 == 0) {
            return new Point(((p.x + (itemSize / 2)) / itemSize * itemSize), yy);
        } else {
            return new Point((p.x / itemSize * itemSize) + (itemSize / 2), yy);
        }
    }

    public static void loadAll() {
        Scanner worldReader;
        try {
            worldReader = new Scanner(new File("levels.txt"));
        } catch (IOException ex) {
            try {
                new File("levels.txt").createNewFile();
            } catch (IOException ex1) {
            }
            return;
        }
        while (worldReader.hasNext()) {
            allWorlds.add(worldReader.next());
        }
    }

    public static final void loadOne(String name) {
        worlds.add(new World(name));
        World world = worlds.get(worlds.size() - 1);
        Scanner reader;
        try {
            reader = new Scanner(new File(name + ".txt"));
        } catch (IOException ex) {
            world.addLevelUnchecked(new Level());
            return;
        }
        int xShift = screenWidth / 2 + menuWidth - reader.nextInt() / 2;
        int yShift = screenHeight / 2 - reader.nextInt() / 2;
        int numberOfLevels = reader.nextInt(), numberOfBlocks, type;
        for (int i = 0; i < numberOfLevels; i++) {
            Level level = new Level();
            numberOfBlocks = reader.nextInt();
            for (int j = 0; j < numberOfBlocks; j++) {
                type = reader.nextInt();
                Point point = new Point((reader.nextInt() * (itemSize / 2)) + xShift, (reader.nextInt() * (levelOffset / 2)) + yShift);
                point = snapToGrid(point);
                reader.nextInt();
                level.addItemUnchecked(new Item(point.x, point.y, itemSize, type));
            }
            world.addLevelUnchecked(level);
        }
        world.setChages(false);
    }

    public static void saveAll() {
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

    public static final void closeAllRightClickMenus() {
        tabsRightClickMenu.setVisible(false);
    }
}
