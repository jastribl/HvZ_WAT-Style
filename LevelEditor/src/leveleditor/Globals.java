package leveleditor;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class Globals {

    public static Graphics memoryGraphics = null;
    public static int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(), currentItemType = 0, currentLevel = 0, currentWorld = 0, numberOfWorldsOpen = 0, tabWidth = 0;
    public static final int itemSize = 64, levelOffset = itemSize / 4, menuWidth = itemSize * 4, tabHeight = 25, iconSize = 40, iconPadding = 5, bottomMenuHeight = iconSize + (iconPadding * 2), numberOfItemsTypes = 9, numberOfIconImages = 4;
    public static Item currentLevelObject = null;
    public static boolean levelUpKeyIsDown = false, levelDownKeyIsDown = false, saveKeyIsDown = false, painting = true, canDraw = false, drawOpen = false;
    public static ArrayList<World> worlds = new ArrayList();
    public static final Item[] menuItems = new Item[numberOfItemsTypes];
    public static final JPopupMenu mainAreaRightClickMenu = new JPopupMenu(), mainMenuRightClickMenu = new JPopupMenu(), bottomMenuRightClickMenu = new JPopupMenu(), tabsRightClickMenu = new JPopupMenu();
    public static final JMenuItem mainAreaRightClickMenuItems[] = new JMenuItem[1], mainMenuRightClickMenuItems[] = new JMenuItem[1], bottomMenuRightClickMenuItems[] = new JMenuItem[1], tabsRightClickMenuItems[] = new JMenuItem[5];
    public static final OpenWindow openWindow = new OpenWindow();
    public static Image itemImages[] = new Image[numberOfItemsTypes], iconImages[] = new Image[numberOfIconImages];

    //find the appropriate location to put the item baed on the mouse location
    public static Point snapToGrid(Point p) {
        int yy = p.y / levelOffset * levelOffset + (itemSize / 2);
        if ((yy / levelOffset) % 2 == 0) {
            return new Point(((p.x + (itemSize / 2)) / itemSize * itemSize), yy);
        } else {
            return new Point((p.x / itemSize * itemSize) + (itemSize / 2), yy);
        }
    }

    //load all worlds from files
    public static void load() {
        Scanner worldReader;
        try {
            worldReader = new Scanner(new File("levels.txt"));
        } catch (IOException ex) {
            worlds.add(new World("World_1"));
            worlds.get(0).addLevelUnchecked(new Level());
            worlds.get(0).setOpen(false);
            return;
        }
        while (worldReader.hasNext()) {
            worlds.add(new World(worldReader.next()));
        }
        for (World world : worlds) {
            Scanner reader;
            try {
                reader = new Scanner(new File(world.getName() + ".txt"));
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
            world.setOpen(false);
        }
    }

    //saves all worlds to files
    public static void saveAll() {
        File worldFile = new File("levels.txt");
        try (BufferedWriter worldWriter = new BufferedWriter(new FileWriter(worldFile))) {
            for (World world : worlds) {
                worldWriter.write(world.getName() + "\n");
                saveOne(world);
            }
        } catch (IOException ex) {
        }
    }

    //saves one world to file
    public static void saveOne(int i) {
        saveOne(worlds.get(i));
        
    }

    //saves one world to file
    public static void saveOne(World world) {
        int minX = 999999999, minY = 999999999, maxX = -999999999, maxY = -999999999;
        for (Level level : world.getWorld()) {
            for (Item item : level.getLevel()) {
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
        for (Level level : world.getWorld()) {
            levelText += String.valueOf(level.size()) + "\n";
            for (Item item : level.getLevel()) {
                int trans = 0;
                levelText += String.valueOf(item.getType()) + " " + String.valueOf((item.getX() - minX) / (itemSize / 2)) + " " + String.valueOf((item.getY() - minY) / (levelOffset / 2)) + " " + String.valueOf(trans) + "\n";
            }
        }
        File file = new File(world.getName() + ".txt");
        try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
            output.write(levelText);
        } catch (IOException ex) {
        }
        world.setSaved();
    }
}
