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
    private final int screenWidth = (int) screenSize.getWidth(), screenHeight = (int) screenSize.getHeight(), itemSize = 64, levelOffset = itemSize / 4, menuWidth = itemSize * 4, iconSize = 40;
    private Item currentLevelObject = null;
    private int currentItemType = 0, currentLevel = 0;
    private boolean levelUpKeyIsDown = false, levelDownKeyIsDown = false, saveKeyIsDown = false, closeButtonIsDown = false, painting = true;
    private final World world;
    private final Image itemImages[], iconImages[];
    private final Item[] menuItems;
    ArrayList<ItemBackup> undoCache = new ArrayList(), redoCache = new ArrayList();
    private boolean canDraw = false;

    LevelEditor() {
        world = new World();
        menuItems = new Item[9];
        itemImages = new Image[menuItems.length];
        iconImages = new Image[6];
        for (int i = 0; i < itemImages.length; i++) {
            menuItems[i] = new Item(itemSize * ((i % 3) + 1), itemSize * ((i / 3) + 1), itemSize, i);
            try {
                itemImages[i] = new ImageIcon(getClass().getResource("/media/items/" + i + ".png")).getImage().getScaledInstance(itemSize, itemSize, Image.SCALE_SMOOTH);
            } catch (Exception e) {
            }
        }
        for (int i = 0; i < iconImages.length; i++) {
            try {
                iconImages[i] = new ImageIcon(getClass().getResource("/media/icons/" + i + ".png")).getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
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
        load();
        canDraw = true;
        drawGame();
    }

    public static void main(String[] args) {
        LevelEditor levelEditor = new LevelEditor();
    }

    private void save() {
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
        String levelText = String.valueOf((maxX - minX) + itemSize) + " " + String.valueOf((maxY - minY) + itemSize) + "\n" + String.valueOf(world.size()) + "\n";
        for (Level level : world) {
            levelText += String.valueOf(level.size()) + "\n";
            for (Item item : level) {
                int trans = 0;
                levelText += String.valueOf(item.getType()) + " " + String.valueOf((item.getX() - minX) / (itemSize / 2)) + " " + String.valueOf((item.getY() - minY) / (levelOffset / 2)) + " " + String.valueOf(trans) + "\n";
            }
        }
        File file = new File("level.txt");
        try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
            output.write(levelText);
        } catch (IOException ex) {
        }
    }

    private void load() {
        Scanner reader;
        try {
            reader = new Scanner(new File("level.txt"));
        } catch (IOException ex) {
            world.add(new Level());
            return;
        }
        reader.nextInt();
        reader.nextInt();
        int numberOfLevels = reader.nextInt(), numberOfBlocks, type, x, y;
        for (int i = 0; i < numberOfLevels; i++) {
            Level level = new Level();
            numberOfBlocks = reader.nextInt();
            for (int j = 0; j < numberOfBlocks; j++) {
                type = reader.nextInt();
                Point point = new Point(reader.nextInt() * (itemSize / 2), reader.nextInt() * (levelOffset / 2));
                point = snapToLocation(point);
                reader.nextInt();
                //to open and save old levels
                //level.add(new Item(x, y, itemWidth, itemHeight, type));
                level.add(new Item(point.x, point.y, itemSize, type));
            }
            world.add(level);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawGame();
    }

    public final void drawGame() {
        if (canDraw) {
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
                            if (location.x > menuWidth - itemSize && location.x < screenWidth && location.y > -itemSize && location.y < screenHeight) {
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
            }
            memoryGraphics.setColor(Color.gray);
            memoryGraphics.fillRect(0, 0, menuWidth, screenHeight);
            memoryGraphics.setColor(Color.white);
            memoryGraphics.drawLine(menuWidth, 0, menuWidth, screenHeight);
            for (Item item : menuItems) {
                drawItem(item);
            }
            memoryGraphics.drawImage(iconImages[closeButtonIsDown ? 5 : 4], screenWidth - iconSize - 10, 10, this);
            memoryGraphics.drawString("Level: " + String.valueOf(currentLevel), screenWidth - 60, screenHeight - 10);
            memoryGraphics.drawImage(iconImages[world.get(currentLevel).isVisible() ? 0 : 1], menuWidth + 10, screenHeight - iconSize - (iconSize / 4), this);
            memoryGraphics.drawImage(iconImages[painting ? 2 : 3], menuWidth + iconSize + 20, screenHeight - iconSize - (iconSize / 4), this);
            getGraphics().drawImage(memoryImage, 0, 0, this);
            getGraphics().dispose();
        }
    }

    public final void drawItem(Item item) {
        memoryGraphics.drawImage(itemImages[item.getType()], item.getX(), item.getY(), null);
    }

    public final void drawFaddedItem(Item item) {
        Composite savedComposite = ((Graphics2D) memoryGraphics).getComposite();
        ((Graphics2D) memoryGraphics).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        ((Graphics2D) memoryGraphics).drawImage(itemImages[item.getType()], item.getX(), item.getY(), null);
        ((Graphics2D) memoryGraphics).setComposite(savedComposite);
    }

    private Point snapToLocation(Point p) {
        int yy = p.y / levelOffset * levelOffset + (itemSize / 2);
        if ((yy / levelOffset) % 2 == 0) {
            return new Point(((p.x + (itemSize / 2)) / itemSize * itemSize), yy);
        } else {
            return new Point((p.x / itemSize * itemSize) + (itemSize / 2), yy);
        }
    }

    public final void addToLevelChecked(int level, Item item, boolean setUndo, boolean wipeRedoCache) {
        if (world.get(level).addItem(item)) {
            drawGame();
            if (setUndo) {
                undoCache.add(new ItemBackup('a', level, item.getType(), (Point) item.getLocation().clone()));
                if (wipeRedoCache) {
                    redoCache.clear();
                }
            }
        }
    }

    public final void removeFromLevelChecked(int level, Item item, boolean setUndo, boolean wipeRedoCache) {
        Item removedItem = world.get(level).removeItem(item);
        if (removedItem != null) {
            drawGame();
            if (setUndo) {
                undoCache.add(new ItemBackup('r', level, removedItem.getType(), (Point) removedItem.getLocation().clone()));
                if (wipeRedoCache) {
                    redoCache.clear();
                }
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
            Rectangle rectangle = new Rectangle(menuItem.getX(), menuItem.getY(), itemSize, itemSize);
            if (rectangle.contains(point)) {
                return menuItem.getType();
            }
        }
        return -1;
    }

    public final void moveItems(int x, int y) {
        for (Level level : world) {
            for (Item item : level) {
                item.shiftLocation(x * itemSize, y * itemSize / 2);
            }
        }
        for (ItemBackup backup : redoCache) {
            if (backup.backupType == 'a' || backup.backupType == 'r') {
                backup.shiftLocation(x * itemSize, y * itemSize / 2);
            }
        }
        for (ItemBackup backup : undoCache) {
            if (backup.backupType == 'a' || backup.backupType == 'r') {
                backup.shiftLocation(x * itemSize, y * itemSize / 2);
            }
        }
    }

    public final void undo() {
        if (undoCache.size() > 0) {
            ItemBackup backup = undoCache.get(undoCache.size() - 1);
            if (backup.backupType == 'a' || backup.backupType == 'r') {
                while (backup.location.x < menuWidth + (itemSize * 2)) {
                    moveItems(1, 0);
                }
                while (backup.location.x > screenWidth - (itemSize * 2)) {
                    moveItems(-1, 0);
                }
                while (backup.location.y < itemSize * 2) {
                    moveItems(0, 1);
                }
                while (backup.location.y > screenHeight - (itemSize * 2)) {
                    moveItems(0, -1);
                }
                backup = undoCache.get(undoCache.size() - 1);
                redoCache.add(new ItemBackup(backup.backupType, backup.level, backup.type, backup.location));
                if (backup.backupType == 'r') {
                    addToLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false);
                } else {
                    removeFromLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false);
                }
            } else if (backup.backupType == 'u' || backup.backupType == 'd') {
                if (backup.backupType == 'u') {
                    chengleLevel('d', false, false);
                } else {
                    chengleLevel('u', false, false);
                }
                redoCache.add(backup);
            } else if (backup.backupType == 'h') {
                hideCurrentLevel();
                redoCache.add(backup);
            }
            undoCache.remove(undoCache.size() - 1);
        }
    }

    public final void redo() {
        if (redoCache.size() > 0) {
            ItemBackup backup = redoCache.get(redoCache.size() - 1);
            if (backup.backupType == 'a' || backup.backupType == 'r') {
                while (backup.location.x < menuWidth + itemSize) {
                    moveItems(1, 0);
                }
                while (backup.location.x > screenWidth - itemSize) {
                    moveItems(-1, 0);
                }
                while (backup.location.y < itemSize) {
                    moveItems(0, 1);
                }
                while (backup.location.y > screenHeight - itemSize) {
                    moveItems(0, -1);
                }
                backup = redoCache.get(redoCache.size() - 1);
                undoCache.add(new ItemBackup(backup.backupType, backup.level, backup.type, backup.location));
                if (backup.backupType == 'a') {
                    addToLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false);
                } else {
                    removeFromLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false);
                }
            } else if (backup.backupType == 'u' || backup.backupType == 'd') {
                chengleLevel(backup.backupType, false, false);
                undoCache.add(backup);
            } else if (backup.backupType == 'h') {
                hideCurrentLevel();
                undoCache.add(backup);
            }
            redoCache.remove(backup);
        }
    }

    private final class ItemBackup {

        public char backupType;
        public int type, level;
        public Point location;

        public ItemBackup(char backupTypeGiven, int levelGiven, int typeGiven, Point locationGiven) {
            backupType = backupTypeGiven;
            level = levelGiven;
            type = typeGiven;
            location = locationGiven;
        }

        public ItemBackup(char backupTypeGiven) {
            backupType = backupTypeGiven;
            level = 0;
            type = 0;
            location = null;
        }

        public final void shiftLocation(int xShift, int yShift) {
            location.x += xShift;
            location.y += yShift;
        }
    }

    private void chengleLevel(char direction, boolean setUndo, boolean wipeRedoCache) {
        boolean moved = true;
        if (direction == 'u') {
            if (currentLevel + 1 == world.size()) {
                world.add(new Level());
            }
            currentLevel++;
        } else {
            if (currentLevel > 0) {
                if (world.get(currentLevel).size() == 0) {
                    world.remove(currentLevel);
                }
                currentLevel--;
                moved = true;
            } else {
                moved = false;
            }
        }
        if (setUndo && moved) {
            drawGame();
            undoCache.add(new ItemBackup(direction));
            if (wipeRedoCache) {
                redoCache.clear();
            }
        }
    }

    private void hideCurrentLevel() {
        world.get(currentLevel).switchVisibility();
        drawGame();
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        Point point = me.getLocationOnScreen();
        if ((point.x > screenWidth - iconSize - 10 || point.x > screenWidth - 10 && point.y < 10 && point.y > iconSize + 10) && closeButtonIsDown) {
            closeButtonIsDown = false;
            drawGame();
            return;
        }
        point = snapToLocation(point);
        if (painting && me.getLocationOnScreen().x > menuWidth) {
            if (SwingUtilities.isRightMouseButton(me)) {
                removeFromLevelChecked(currentLevel, new Item(point.x, point.y, itemSize, currentItemType), true, true);
            } else if (SwingUtilities.isLeftMouseButton(me)) {
                addToLevelChecked(currentLevel, new Item(point.x, point.y, itemSize, currentItemType), true, true);
            }
        } else {
            if (currentLevelObject != null && SwingUtilities.isLeftMouseButton(me)) {
                currentLevelObject.setLocationAndFix(point);
                drawGame();
            }
        }
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
            if (point.x > screenWidth - iconSize - 10 && point.x < screenWidth - 10 && point.y > 10 && point.y < iconSize + 10) {
                closeButtonIsDown = true;
                drawGame();
            } else {
                point = snapToLocation(point);
                if (SwingUtilities.isRightMouseButton(me)) {
                    removeFromLevelChecked(currentLevel, new Item(point.x, point.y, itemSize, currentItemType), true, true);
                } else if (SwingUtilities.isLeftMouseButton(me)) {
                    if (painting) {
                        addToLevelChecked(currentLevel, new Item(point.x, point.y, itemSize, currentItemType), true, true);
                    } else {
                        Item item = getFromLevelChecked(currentLevel, new Item(point.x, point.y, itemSize, currentItemType));
                        if (item != null) {
                            currentItemType = item.getType();
                            currentLevelObject = item;
                        } else {
                            currentLevelObject = new Item(point.x, point.y, itemSize, currentItemType);
                        }
                    }
                }
            }
        } else {
            int itemType = getSelectedMenuItemType(point);
            if (itemType >= 0) {
                currentItemType = itemType;
                point = snapToLocation(point);
                currentLevelObject = new Item(point.x, point.y, itemSize, itemType);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        Point point = me.getLocationOnScreen();
        if (point.x > screenWidth - iconSize - 10 && point.x < screenWidth - 10 && point.y > 10 && point.y < iconSize + 10 && closeButtonIsDown) {
            System.exit(0);
        } else if (currentLevelObject != null && !painting) {
            if (me.getLocationOnScreen().x > menuWidth) {
                addToLevelChecked(currentLevel, currentLevelObject, true, true);
            }
            currentLevelObject = null;
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
        if (key == KeyEvent.VK_PAGE_UP) {
            levelUpKeyIsDown = true;
        } else if (key == KeyEvent.VK_PAGE_DOWN) {
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
        } else if (key == KeyEvent.VK_P) {
            painting = !painting;
            drawGame();
        } else if (key == KeyEvent.VK_H) {
            hideCurrentLevel();
            undoCache.add(new ItemBackup('h'));
            redoCache.clear();
        } else if (key == KeyEvent.VK_Z && ke.isControlDown()) {
            undo();
        } else if (key == KeyEvent.VK_Y && ke.isControlDown()) {
            redo();
        } else if (key == KeyEvent.VK_S && ke.isControlDown()) {
            saveKeyIsDown = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        int key = ke.getKeyCode();
        if (key == KeyEvent.VK_PAGE_UP && levelUpKeyIsDown) {
            levelUpKeyIsDown = false;
            chengleLevel('u', true, true);
        } else if (key == KeyEvent.VK_PAGE_DOWN && levelDownKeyIsDown) {
            levelDownKeyIsDown = false;
            chengleLevel('d', true, true);
        } else if (key == KeyEvent.VK_S && ke.isControlDown() && saveKeyIsDown == true) {
            saveKeyIsDown = false;
            save();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        if (mwe.getX() < menuWidth) {
            int amount = mwe.getWheelRotation() * 15;
            if ((menuItems[0].getY() - amount > 0 || menuItems[menuItems.length - 1].getY() - amount > screenSize.getHeight()) && (menuItems[menuItems.length - 1].getY() - amount + itemSize < screenSize.getHeight() || menuItems[0].getY() - amount < 0)) {
                for (Item item : menuItems) {
                    item.shiftLocation(0, -amount);
                }
                drawGame();
            }
        }
    }
}
