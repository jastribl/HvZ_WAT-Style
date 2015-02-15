package leveleditor;

//import java.awt.AlphaComposite;
//import java.awt.Color;
//import java.awt.Composite;
//import java.awt.Dimension;
//import java.awt.Graphics;
//import java.awt.Image;
//import java.awt.Point;
//import java.awt.Rectangle;
//import java.awt.Toolkit;
//import java.awt.Graphics2D;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.awt.event.MouseMotionListener;
//import java.awt.event.MouseWheelEvent;
//import java.awt.event.MouseWheelListener;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Scanner;
//import java.util.ArrayList;
//import javax.swing.ImageIcon;
//import javax.swing.JFrame;
//import javax.swing.SwingUtilities;import java.awt.AlphaComposite;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public final class LevelEditor extends JFrame implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {

    private final Image memoryImage;
    private final Graphics memoryGraphics;
    private final int numberOfItems = 9, screenWidth, screenHeight, itemWidth = 64, itemHeight = itemWidth, levelOffset = itemWidth / 4, menuWidth = itemWidth * 4;
    private Item currentLevelObject;
    private int currentItemType = 0, currentLevel = 0;
    private boolean levelUpKeyIsDown = false, levelDownKeyIsDown = false, saveIsDown = false, colouring = true;
    private final World world;
    private final Image[] images;
    private final Item[] menuItems;
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
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
        screenWidth = (int) screenSize.getWidth();
        screenHeight = (int) screenSize.getHeight();
        setLocation(0, 0);
        setUndecorated(true);
        setSize(screenWidth, screenHeight);
        setResizable(false);
        setFocusable(true);
        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);
        currentLevelObject = null;
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
            int numberOfLevels = reader.nextInt();
            for (int i = 0; i < numberOfLevels; i++) {
                Level level = new Level();
                int numberOfBlocks = reader.nextInt();
                for (int j = 0; j < numberOfBlocks; j++) {
                    int type = reader.nextInt(), x = reader.nextInt(), y = reader.nextInt();
                    reader.nextInt();
                    level.add(new Item((x * (itemWidth / 2)) + menuWidth, (y * (levelOffset / 2)), itemWidth, itemHeight, type));
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
        drawMenu();
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
            undoCache.add(new ItemBackup('a', level, item));
            if (wipeRedoCache) {
                redoCache.clear();
            }
        }
    }

    public final void removeFromLevelChecked(int level, Item item, boolean setUndo, boolean wipeRedoCache) {
        if (world.get(level).removeItem(item) && setUndo) {
            undoCache.add(new ItemBackup('r', level, item));
            if (wipeRedoCache) {
                redoCache.clear();
            }
        }
    }

    public Item getSelectedMenuItem(Point testLocation) {
        Item testObject;
        for (Item menuItem : menuItems) {
            testObject = menuItem;
            Rectangle rectangle = new Rectangle(testObject.getX(), testObject.getY(), testObject.getWidth(), testObject.getHeight());
            if (rectangle.contains(testLocation)) {
                try {
                    return (Item) testObject.clone();
                } catch (CloneNotSupportedException ex) {
                }
            }
        }
        return null;
    }

    public final void scroll(int amount) {
        int move = amount * 15;
        if (menuItems[0].getY() - move < 0 && menuItems[menuItems.length - 1].getY() - move < screenSize.getHeight() || menuItems[menuItems.length - 1].getY() - move + menuItems[menuItems.length - 1].getHeight() > screenSize.getHeight() && menuItems[0].getY() - move > 0) {
            return;
        }
        for (Item item : menuItems) {
            item.shiftLocation(0, -move);
        }
    }

    public void moveItems(int x, int y) {
        for (Level level : world) {
            for (Item item : level) {
                item.shiftLocation(x * itemWidth, y * itemHeight / 2);
            }
        }
        for (ItemBackup item : redoCache) {
            item.item.shiftLocation(x * itemWidth, y * itemHeight / 2);
        }
    }

    public final void drawMenu() {
        for (Item item : menuItems) {
            drawItem(item);
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
            redoCache.add(backup);
            undoCache.remove(undoCache.size() - 1);
            if (backup.type == 'r') {
                addToLevelChecked(backup.level, backup.item, false, false);
            } else if (backup.type == 'a') {
                removeFromLevelChecked(backup.level, backup.item, false, false);
            }
        }
    }

    public final void redo() {
        if (redoCache.size() > 0) {
            ItemBackup backup = redoCache.get(redoCache.size() - 1);
            undoCache.add(backup);
            redoCache.remove(backup);
            if (backup.type == 'a') {
                addToLevelChecked(backup.level, backup.item, false, false);
            } else if (backup.type == 'r') {
                removeFromLevelChecked(backup.level, backup.item, false, false);
            }
        }
    }

    private final class ItemBackup {

        public char type;
        public int level;
        public Item item;

        public ItemBackup(char typeGiven, int levelGiven, Item itemGiven) {
            type = typeGiven;
            level = levelGiven;
            item = itemGiven;
        }
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        Point point = snapToLocation(me.getLocationOnScreen());;
        if (colouring) {
            if (SwingUtilities.isRightMouseButton(me)) {
                removeFromLevelChecked(currentLevel, new Item(point.x, point.y, itemWidth, itemHeight, currentItemType), true, true);
            } else if (SwingUtilities.isLeftMouseButton(me)) {
                addToLevelChecked(currentLevel, new Item(point.x, point.y, itemWidth, itemHeight, currentItemType), true, true);
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
            } else if (SwingUtilities.isLeftMouseButton(me)) {
                currentLevelObject = new Item(point.x, point.y, itemWidth, itemHeight, currentItemType);
            }
        } else {
            Item item = getSelectedMenuItem(point);
            if (item != null) {
                currentItemType = item.getType();
                currentLevelObject = item;
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
            scroll(mwe.getWheelRotation());
            drawGame();
        }
    }
}
