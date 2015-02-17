package leveleditor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public final class LevelEditor extends JFrame implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {

    private final Image memoryImage;
    private final Graphics memoryGraphics;
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final int numberOfItems = 9, screenWidth = (int) screenSize.getWidth(), screenHeight = (int) screenSize.getHeight(), itemWidth = 64, itemHeight = itemWidth, levelOffset = itemWidth / 4, menuWidth = itemWidth * 4;
    private Item currentLevelObject = null;
    private int currentItemType = 0, currentLevel = 0;
    private boolean levelUpKeyIsDown = false, levelDownKeyIsDown = false, saveIsDown = false, colouring = true;
    private final World world;
    private final Image[] images;
    private final Item[] menuItems;
    ArrayList<ItemBackup> undoCache = new ArrayList(), redoCache = new ArrayList();

    LevelEditor() {
        world = new World();
        menuItems = new Item[numberOfItems];
        images = new Image[numberOfItems];
        for (int i = 0; i < numberOfItems; i++) {
            menuItems[i] = new Item(itemWidth * ((i % 3) + 1), itemHeight * ((i / 3) + 1), itemWidth, itemHeight, i);
            try {
                images[i] = new ImageIcon(getClass().getResource("/media/" + i + ".png")).getImage().getScaledInstance(itemWidth, itemHeight, Image.SCALE_SMOOTH);
            } catch (Exception e) {
            }
        }
        setTitle("LevelUpGame - 2015 - Justin Stribling");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(0, 0);
        setUndecorated(true);
        setSize(screenWidth, screenHeight);
        setResizable(false);
        setFocusable(true);
        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);
        setVisible(true);
        memoryImage = createImage(screenWidth, screenHeight);
        memoryGraphics = (Graphics2D) memoryImage.getGraphics();
        openLevel();
        drawGame();
    }

    public static void main(String[] args) {
        LevelEditor levelEditor = new LevelEditor();
    }

    private void saveLevel() {
        int minX = 999999, minY = 999999, maxX = -999999, maxY = -999999;
        for (Level layer : world) {
            for (Item item : layer) {
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
        String levelText = String.valueOf((maxX - minX) + itemWidth) + " " + String.valueOf((maxY - minY) + itemHeight) + "\n" + String.valueOf(world.size()) + "\n";
        for (Level level : world) {
            levelText += String.valueOf(level.size()) + "\n";
            for (Item item : level) {
                int trans = 0;
                levelText += String.valueOf(item.getType()) + " " + String.valueOf((item.getX() - minX) / (itemWidth / 2)) + " " + String.valueOf((item.getY() - minY) / (levelOffset / 2)) + " " + String.valueOf(trans) + "\n";
            }
        }
        File file = new File("level.txt");
        try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
            output.write(levelText);
        } catch (IOException ex) {
        }
    }

    private void openLevel() {
        try {
            Scanner reader = new Scanner(new File("level.txt"));
            reader.nextInt();
            reader.nextInt();
            int numberOfLevels = reader.nextInt(), numberOfBlocks, type, x, y;
            for (int i = 0; i < numberOfLevels; i++) {
                Level level = new Level();
                numberOfBlocks = reader.nextInt();
                for (int j = 0; j < numberOfBlocks; j++) {
                    type = reader.nextInt();
                    Point point = new Point(reader.nextInt() * (itemWidth / 2), reader.nextInt() * (levelOffset / 2));
                    point = snapToLocation(point);
                    reader.nextInt();
                    //to open and save old levels
//                    level.add(new Item(x, y, itemWidth, itemHeight, type));
                    level.add(new Item(point.x, point.y, itemWidth, itemHeight, type));
                }
                world.add(level);
            }
        } catch (IOException ex) {
            world.add(new Level());
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawGame();
    }

    public final void drawGame() {
        memoryGraphics.setColor(Color.black);
        memoryGraphics.fillRect(menuWidth, 0, screenWidth - menuWidth, screenHeight);
        boolean printedLive = null == currentLevelObject;
        for (int i = 0; i < world.size(); i++) {
            if (!printedLive && world.get(i).size() == 0) {
                drawItem(currentLevelObject);
                printedLive = true;
            } else {
                for (int j = 0; j < world.get(i).size(); j++) {
                    if (world.get(i).isVisible()) {
                        if (!printedLive && currentLevel == i && currentLevelObject.getY() < world.get(i).get(j).getY()) {
                            drawItem(currentLevelObject);
                            printedLive = true;
                        }
                        Point location = world.get(i).get(j).getLocation();
                        if (location.x > menuWidth - itemWidth && location.x < screenWidth && location.y > -itemHeight && location.y < screenHeight) {
                            if (currentLevel == i) {
                                drawItem(world.get(i).get(j));
                            } else {
                                drawFaddedItem(world.get(i).get(j));
                            }
                        }
                    }
                }
            }
            if (!printedLive && currentLevel == i) {
                drawItem(currentLevelObject);
                printedLive = true;
            }
            memoryGraphics.setColor(Color.red);
        }
        memoryGraphics.setColor(Color.gray);
        memoryGraphics.fillRect(0, 0, menuWidth, screenHeight);
        memoryGraphics.setColor(Color.white);
        memoryGraphics.drawLine(menuWidth, 0, menuWidth, screenHeight);
        for (Item item : menuItems) {
            drawItem(item);
        }
        memoryGraphics.drawString("Level: " + String.valueOf(currentLevel), screenWidth - 60, screenHeight - 10);
        getGraphics().drawImage(memoryImage, 0, 0, this);
        getGraphics().dispose();
    }

    private Point snapToLocation(Point p) {
        int yy = p.y / levelOffset * levelOffset + (itemHeight / 2);
        if ((yy / levelOffset) % 2 == 0) {
            return new Point(((p.x + (itemWidth / 2)) / itemWidth * itemWidth), yy);
        } else {
            return new Point((p.x / itemWidth * itemWidth) + (itemWidth / 2), yy);
        }
    }

    public final void addToLevelChecked(int level, Item item, boolean setUndo, boolean wipeRedoCache) {
        if (world.get(level).addItem(item) && setUndo) {
            undoCache.add(new ItemBackup('a', level, item.getType(), (Point) item.getLocation().clone()));
            if (wipeRedoCache) {
                redoCache.clear();
            }
        }
    }

    public final void removeFromLevelChecked(int level, Item item, boolean setUndo, boolean wipeRedoCache) {
        Item removedItem = world.get(level).removeItem(item);
        if (removedItem != null && setUndo) {
            undoCache.add(new ItemBackup('r', level, removedItem.getType(), (Point) removedItem.getLocation().clone()));
            if (wipeRedoCache) {
                redoCache.clear();
            }
        }
    }

    public final Item getFromLevelChecked(int level, Item item) {
        Item removedItem = world.get(level).removeItem(item);
        if (removedItem != null) {
            undoCache.add(new ItemBackup('r', level, removedItem.getType(), (Point) removedItem.getLocation().clone()));
            redoCache.clear();
            try {
                return (Item) removedItem.clone();
            } catch (CloneNotSupportedException ex) {
            }
        }
        return null;
    }

    public final int getSelectedMenuItemType(Point point) {
        for (Item menuItem : menuItems) {
            Rectangle rectangle = new Rectangle(menuItem.getX(), menuItem.getY(), itemWidth, itemHeight);
            if (rectangle.contains(point)) {
                return menuItem.getType();
            }
        }
        return -1;
    }

    public final void scroll(int amount) {
        if (menuItems[0].getY() - amount < 0 && menuItems[menuItems.length - 1].getY() - amount < screenSize.getHeight() || menuItems[menuItems.length - 1].getY() - amount + itemHeight > screenSize.getHeight() && menuItems[0].getY() - amount > 0) {
            return;
        }
        for (Item item : menuItems) {
            item.shiftLocation(0, -amount);
        }
    }

    public final void moveItems(int x, int y) {
        for (Level level : world) {
            for (Item item : level) {
                item.shiftLocation(x * itemWidth, y * itemHeight / 2);
            }
        }
        for (ItemBackup backup : redoCache) {
            backup.shiftLocation(x * itemWidth, y * itemHeight / 2);
        }
        for (ItemBackup backup : undoCache) {
            backup.shiftLocation(x * itemWidth, y * itemHeight / 2);
        }
    }

    public final void drawItem(Item item) {
        memoryGraphics.drawImage(images[item.getType()], item.getX(), item.getY(), null);
    }

    public final void drawFaddedItem(Item item) {
        Composite savedComposite = ((Graphics2D) memoryGraphics).getComposite();
        ((Graphics2D) memoryGraphics).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        ((Graphics2D) memoryGraphics).drawImage(images[item.getType()], item.getX(), item.getY(), null);
        ((Graphics2D) memoryGraphics).setComposite(savedComposite);
    }

    public final void undo() {
        if (undoCache.size() > 0) {
            ItemBackup backup = undoCache.get(undoCache.size() - 1);
            while (backup.location.x < menuWidth + (itemWidth * 2)) {
                moveItems(1, 0);
            }
            while (backup.location.x > screenWidth - (itemWidth * 2)) {
                moveItems(-1, 0);
            }
            while (backup.location.y < itemHeight * 2) {
                moveItems(0, 1);
            }
            while (backup.location.y > screenHeight - (itemHeight * 2)) {
                moveItems(0, -1);
            }
            drawGame();
            backup = undoCache.get(undoCache.size() - 1);
            redoCache.add(new ItemBackup(backup.backupBype, backup.level, backup.type, backup.location));
            if (backup.backupBype == 'r') {
                addToLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false);
            } else if (backup.backupBype == 'a') {
                removeFromLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false);
            }
            drawGame();
            undoCache.remove(undoCache.size() - 1);
        }
    }

    public final void redo() {
        if (redoCache.size() > 0) {
            ItemBackup backup = redoCache.get(redoCache.size() - 1);
            while (backup.location.x < menuWidth + itemWidth) {
                moveItems(1, 0);
            }
            while (backup.location.x > screenWidth - itemWidth) {
                moveItems(-1, 0);
            }
            while (backup.location.y < itemHeight) {
                moveItems(0, 1);
            }
            while (backup.location.y > screenHeight - itemHeight) {
                moveItems(0, -1);
            }
            drawGame();
            backup = redoCache.get(redoCache.size() - 1);
            undoCache.add(new ItemBackup(backup.backupBype, backup.level, backup.type, backup.location));
            if (backup.backupBype == 'a') {
                addToLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false);
            } else if (backup.backupBype == 'r') {
                removeFromLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false);
            }
            drawGame();
            redoCache.remove(backup);
        }
    }

    private final class ItemBackup {

        public char backupBype;
        public int type, level;
        public Point location;

        public ItemBackup(char backupTypeGiven, int levelGiven, int typeGiven, Point locationGiven) {
            backupBype = backupTypeGiven;
            level = levelGiven;
            type = typeGiven;
            location = locationGiven;
        }

        public final void shiftLocation(int xShift, int yShift) {
            location.x += xShift;
            location.y += yShift;
        }
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        Point point = snapToLocation(me.getLocationOnScreen());;
        if (colouring && me.getLocationOnScreen().x > menuWidth) {
            if (SwingUtilities.isRightMouseButton(me)) {
                removeFromLevelChecked(currentLevel, new Item(point.x, point.y, itemWidth, itemHeight, currentItemType), true, true);
            } else if (SwingUtilities.isLeftMouseButton(me)) {
                addToLevelChecked(currentLevel, new Item(point.x, point.y, itemWidth, itemHeight, currentItemType), true, true);
            } else {
                return;
            }
        } else {
            if (currentLevelObject != null && SwingUtilities.isLeftMouseButton(me)) {
                currentLevelObject.setLocationAndFix(point);
            } else {
                return;
            }
        }
        drawGame();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
        Point point = me.getLocationOnScreen();
        if (point.x > menuWidth) {
            point = snapToLocation(point);
            if (SwingUtilities.isRightMouseButton(me)) {
                removeFromLevelChecked(currentLevel, new Item(point.x, point.y, itemWidth, itemHeight, currentItemType), true, true);
            } else if (SwingUtilities.isLeftMouseButton(me) && !colouring) {
                Item item = getFromLevelChecked(currentLevel, new Item(point.x, point.y, itemWidth, itemHeight, currentItemType));
                if (item != null) {
                    currentItemType = item.getType();
                    currentLevelObject = item;
                } else {
                    currentLevelObject = new Item(point.x, point.y, itemWidth, itemHeight, currentItemType);
                }
            }
        } else {
            int itemType = getSelectedMenuItemType(point);
            if (itemType >= 0) {
                currentItemType = itemType;
                point = snapToLocation(point);
                currentLevelObject = new Item(point.x, point.y, itemWidth, itemHeight, itemType);
            }
        }
        drawGame();
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (currentLevelObject != null) {
            if (me.getLocationOnScreen().x > menuWidth) {
                addToLevelChecked(currentLevel, currentLevelObject, true, true);
            }
            currentLevelObject = null;
            drawGame();
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int key = ke.getKeyCode();
        if (key == 107) {
            levelUpKeyIsDown = true;
        } else if (key == 109) {
            levelDownKeyIsDown = true;
        } else if (key == KeyEvent.VK_UP) {
            moveItems(0, 1);
            drawGame();
        } else if (key == KeyEvent.VK_RIGHT) {
            moveItems(-1, 0);
            drawGame();
        } else if (key == KeyEvent.VK_DOWN) {
            moveItems(0, -1);
            drawGame();
        } else if (key == KeyEvent.VK_LEFT) {
            moveItems(1, 0);
            drawGame();
        } else if (key == KeyEvent.VK_S && ke.isControlDown()) {
            saveIsDown = true;
        } else if (key == KeyEvent.VK_P) {
            colouring = !colouring;
        } else if (key == KeyEvent.VK_H) {
            world.get(currentLevel).switchVisibility();
        } else if (key == KeyEvent.VK_Z && ke.isControlDown()) {
            undo();
        } else if (key == KeyEvent.VK_Y && ke.isControlDown()) {
            redo();
        } else {
            return;
        }
        drawGame();
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        int key = ke.getKeyCode();
        if (key == 107 && levelUpKeyIsDown) {
            levelUpKeyIsDown = false;
            if (currentLevel + 1 == world.size()) {
                world.add(new Level());
            }
            currentLevel++;
            drawGame();
        } else if (key == 109 && levelDownKeyIsDown) {
            levelDownKeyIsDown = false;
            if (currentLevel > 0) {
                if (world.get(currentLevel).size() == 0) {
                    world.remove(currentLevel);
                }
                currentLevel--;
            }
            drawGame();
        } else if (key == KeyEvent.VK_S && ke.isControlDown() && saveIsDown == true) {
            saveIsDown = false;
            saveLevel();
        } else {
            return;
        }
        drawGame();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        if (mwe.getX() < menuWidth) {
            scroll(mwe.getWheelRotation() * 15);
            drawGame();
        }
    }
}
