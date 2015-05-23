package leveleditor;

import java.awt.*;
import java.io.*;
import java.util.*;

public class Globals {

    public static Graphics memoryGraphics = null;
    public static int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(), currentWorld = 0;
    public static final int itemSize = 32, halfItemSize = itemSize / 2, levelOffset = itemSize / 4, menuWidth = itemSize * 4, iconSize = 40, iconPadding = 5, bottomMenuHeight = iconSize + (iconPadding * 2), numberOfMenuItems = 9, loadingSize = 140;
    public static ArrayList<World> worlds = new ArrayList();
    public static final ArrayList<String> allWorlds = new ArrayList();
    public static Image itemImages[] = new Image[numberOfMenuItems];
    public static final int UP = 0, DOWN = 1;
    public static final int ADD = 0, REMOVE = 1;
    public static final int PAINT = 0, POINT = 1, RECTANGLE = 2;
//    public static boolean loading = false;
//    public static final LoadingScreen loadingScreen = new LoadingScreen();

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
                    level.addItemUnchecked(new Item(point.x, point.y, itemSize, type));
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

}
