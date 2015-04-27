package leveleditor;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.*;
import javax.swing.*;

public final class LevelEditor extends JFrame implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener, WindowListener, ComponentListener {

    private final Image memoryImage;
    private final Graphics memoryGraphics;
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int screenWidth = (int) screenSize.getWidth(), screenHeight = (int) screenSize.getHeight();
    private final int itemSize = 64, levelOffset = itemSize / 4, menuWidth = itemSize * 4, tabHeight = 25, iconSize = 40, iconPadding = 5, bottomMenuHeight = iconSize + (iconPadding * 2);
    private Item currentLevelObject = null;
    private int currentItemType = 6, currentLevel = 0, currentWorld = 0;
    private boolean levelUpKeyIsDown = false, levelDownKeyIsDown = false, saveKeyIsDown = false, painting = true;
    private final ArrayList<World> worlds;
    private final Image itemImages[], iconImages[];
    private final Item[] menuItems;
    private boolean canDraw = false;

    LevelEditor() {
        worlds = new ArrayList();
        menuItems = new Item[9];
        MediaTracker imageTracker = new MediaTracker(this);
        itemImages = new Image[menuItems.length];
        iconImages = new Image[4];
        for (int i = 0; i < itemImages.length; i++) {
            menuItems[i] = new Item(itemSize * ((i % 3) + 1), itemSize * ((i / 3) + 1), itemSize, i);
            try {
                itemImages[i] = new ImageIcon(getClass().getResource("/media/o" + i + ".png")).getImage().getScaledInstance(itemSize, itemSize, Image.SCALE_SMOOTH);
                imageTracker.addImage(itemImages[i], 0);
            } catch (Exception e) {
            }
        }
        for (int i = 0; i < iconImages.length; i++) {
            try {
                iconImages[i] = new ImageIcon(getClass().getResource("/media/i" + i + ".png")).getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                imageTracker.addImage(iconImages[i], 1);
            } catch (Exception e) {
            }
        }
        try {
            imageTracker.waitForID(0);
            imageTracker.waitForID(1);
        } catch (InterruptedException e) {
        }
        setTitle("LevelUpGame - 2015 - Justin Stribling");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//        setLocation(0, 0);
        setSize(999999999, 999999999);
        getContentPane().setSize(99999999, 99999999);
        setFocusable(true);
        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);
        addWindowListener(this);
        addComponentListener(this);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setFocusTraversalKeysEnabled(false);
        setVisible(true);
        memoryImage = createImage(screenWidth, screenHeight);
        memoryGraphics = memoryImage.getGraphics();
        load();
        canDraw = true;
        setBackground(Color.black);
        drawGame();
    }

    public static void main(String[] args) {
        LevelEditor levelEditor = new LevelEditor();
    }

    private void load() {
        Scanner worldReader;
        try {
            worldReader = new Scanner(new File("levels.txt"));
        } catch (IOException ex) {
            worlds.add(new World("World_1"));
            worlds.get(0).add(new Level());
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
                world.add(new Level());
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
                    point = snapToLocation(point);
                    reader.nextInt();
                    level.add(new Item(point.x, point.y, itemSize, type));
                }
                world.add(level);
            }
        }
    }

    private void save() {
        File worldFile = new File("levels.txt");
        try (BufferedWriter worldWriter = new BufferedWriter(new FileWriter(worldFile))) {
            for (World world : worlds) {
                worldWriter.write(world.getName() + "\n");
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
                File file = new File(world.getName() + ".txt");
                try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
                    output.write(levelText);
                } catch (IOException ex) {
                }
            }
        } catch (IOException ex) {
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (canDraw) {
            drawGame();
        }
    }

    public final void drawGame() {
        memoryGraphics.setColor(Color.black);
        memoryGraphics.fillRect(menuWidth, 0, screenWidth - menuWidth, screenHeight);
        boolean printedLive = null == currentLevelObject;
        for (int i = 0; i < worlds.get(currentWorld).size(); i++) {
            if (!printedLive && worlds.get(currentWorld).get(i).size() == 0) {
                drawItem(currentLevelObject);
                printedLive = true;
            } else {
                for (int j = 0; j < worlds.get(currentWorld).get(i).size(); j++) {
                    if (worlds.get(currentWorld).get(i).isVisible()) {
                        if (!printedLive && currentLevel == i && currentLevelObject.getY() < worlds.get(currentWorld).get(i).get(j).getY()) {
                            drawItem(currentLevelObject);
                            printedLive = true;
                        }
                        Point location = worlds.get(currentWorld).get(i).get(j).getLocation();
                        if (location.x > menuWidth - itemSize && location.x < screenWidth && location.y > -itemSize && location.y < screenHeight) {
                            if (currentLevel == i) {
                                drawItem(worlds.get(currentWorld).get(i).get(j));
                            } else {
                                drawFaddedItem(worlds.get(currentWorld).get(i).get(j));
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
        memoryGraphics.fillRect(0, screenHeight - bottomMenuHeight, screenWidth, screenHeight - bottomMenuHeight);
        memoryGraphics.setColor(Color.white);
        memoryGraphics.drawLine(menuWidth, 0, menuWidth, screenHeight);
        memoryGraphics.drawLine(menuWidth, screenHeight - bottomMenuHeight, screenWidth, screenHeight - bottomMenuHeight);
        for (Item item : menuItems) {
            drawItem(item);
        }
        memoryGraphics.setColor(Color.black);
        memoryGraphics.fillRect(menuWidth + 1, 0, screenWidth - menuWidth, tabHeight);
        double tabWidth = (screenWidth - menuWidth) / worlds.size();
        for (int i = 0; i < worlds.size(); i++) {
            if (currentWorld == i) {
                memoryGraphics.setColor(Color.lightGray);
            } else {
                memoryGraphics.setColor(Color.gray);
            }
            memoryGraphics.fillRect(menuWidth + (int) (i * tabWidth) + 1, 0, (int) tabWidth - 1, tabHeight);
            Rectangle2D stringSize = memoryGraphics.getFontMetrics().getStringBounds(worlds.get(i).getName(), memoryGraphics);
            memoryGraphics.setColor(Color.black);
            memoryGraphics.drawString(worlds.get(i).getName(), menuWidth + ((int) (i * tabWidth)) + 1 + (int) ((tabWidth - stringSize.getWidth()) / 2), (tabHeight / 2) + (int) (stringSize.getHeight() / 2));
        }
        memoryGraphics.setColor(Color.black);
        memoryGraphics.drawLine(0, screenHeight - bottomMenuHeight, menuWidth - 1, screenHeight - bottomMenuHeight);
        memoryGraphics.drawString(worlds.get(currentWorld).getName(), iconPadding * 2, screenHeight - (iconPadding * 2));
        memoryGraphics.drawString("Level: " + String.valueOf(currentLevel), screenWidth - 60, screenHeight - (iconPadding * 2));
        memoryGraphics.drawImage(iconImages[worlds.get(currentWorld).get(currentLevel).isVisible() ? 0 : 1], menuWidth + iconPadding, screenHeight - iconSize - iconPadding, this);
        memoryGraphics.drawImage(iconImages[painting ? 2 : 3], menuWidth + iconSize + (iconPadding * 2), screenHeight - iconSize - iconPadding, this);
        getGraphics().drawImage(memoryImage, getInsets().left, getInsets().top, this);
        getGraphics().dispose();
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
        if (worlds.get(currentWorld).get(level).addItem(item)) {
            drawGame();
            if (setUndo) {
                worlds.get(currentWorld).addUndo(new ItemBackup('a', level, item.getType(), (Point) item.getLocation().clone()));
                if (wipeRedoCache) {
                    worlds.get(currentWorld).clearRedo();
                }
            }
        }
    }

    public final void removeFromLevelChecked(int level, Item item, boolean setUndo, boolean wipeRedoCache) {
        Item removedItem = worlds.get(currentWorld).get(level).removeItem(item);
        if (removedItem != null) {
            drawGame();
            if (setUndo) {
                worlds.get(currentWorld).addUndo(new ItemBackup('r', level, removedItem.getType(), (Point) removedItem.getLocation().clone()));
                if (wipeRedoCache) {
                    worlds.get(currentWorld).clearRedo();
                }
            }
        }
    }

    public final Item getFromLevelChecked(int level, Item item) {
        Item removedItem = worlds.get(currentWorld).get(level).removeItem(item);
        if (removedItem != null) {
            worlds.get(currentWorld).addUndo(new ItemBackup('r', level, removedItem.getType(), (Point) removedItem.getLocation().clone()));
            worlds.get(currentWorld).clearRedo();
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
        worlds.get(currentWorld).shiftItems(x * itemSize, y * itemSize / 2);
    }

    public final void findPlaceToBe(ItemBackup backup) {
        while (backup.level < currentLevel) {
            chengleLevel('d');
        }
        while (backup.level > currentLevel) {
            chengleLevel('u');
        }
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
    }

    public final void undo() {
        ItemBackup backup = worlds.get(currentWorld).peekUndo();
        if (backup == null) {
            return;
        } else if (backup.backupType == 'a' || backup.backupType == 'r') {
            findPlaceToBe(backup);
            backup = worlds.get(currentWorld).peekUndo();
            worlds.get(currentWorld).addRedo(new ItemBackup(backup.backupType, backup.level, backup.type, backup.location));
            if (backup.backupType == 'r') {
                addToLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false);
            } else {
                removeFromLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false);
            }
        } else if (backup.backupType == 'h') {
            hideCurrentLevel();
            worlds.get(currentWorld).addRedo(backup);
        }
        worlds.get(currentWorld).popUndo();
    }

    public final void redo() {
        ItemBackup backup = worlds.get(currentWorld).peekRedo();
        if (backup == null) {
            return;
        } else if (backup.backupType == 'a' || backup.backupType == 'r') {
            findPlaceToBe(backup);
            backup = worlds.get(currentWorld).peekRedo();
            worlds.get(currentWorld).addUndo(new ItemBackup(backup.backupType, backup.level, backup.type, backup.location));
            if (backup.backupType == 'a') {
                addToLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false);
            } else {
                removeFromLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false);
            }
        } else if (backup.backupType == 'h') {
            hideCurrentLevel();
            worlds.get(currentWorld).addUndo(backup);
        }
        worlds.get(currentWorld).popRedo();
    }

    private void chengleLevel(char direction) {
        boolean moved = true;
        if (direction == 'u') {
            if (currentLevel + 1 == worlds.get(currentWorld).size()) {
                worlds.get(currentWorld).add(new Level());
            }
            currentLevel++;
        } else {
            if (currentLevel > 0) {
                if (worlds.get(currentWorld).get(currentLevel).size() == 0) {
                    worlds.get(currentWorld).remove(currentLevel);
                }
                currentLevel--;
            } else {
                moved = false;
            }
        }
        if (moved) {
            drawGame();
        }
    }

    private void hideCurrentLevel() {
        worlds.get(currentWorld).get(currentLevel).switchVisibility();
        drawGame();
    }

    private void switchWorld(int toGo) {
        currentWorld = (toGo > worlds.size() - 1) ? 0 : (toGo < 0 ? worlds.size() - 1 : toGo);
        currentLevel = 0;
        drawGame();
    }

    private void addWorld() {
        String name = null;
        boolean bad;
        do {
            bad = false;
            try {
                name = JOptionPane.showInputDialog(this, "Name the new world", "New", JOptionPane.QUESTION_MESSAGE).replaceAll(" ", "_");
                if (name.equals("")) {
                    bad = true;
                }
                for (World world : worlds) {
                    if (world.getName().equals(name)) {
                        bad = true;
                        break;
                    }
                }
            } catch (NullPointerException ex) {
                bad = true;
                break;
            }
        } while (bad);
        if (!bad) {
            worlds.add(new World(name));
            worlds.get(worlds.size() - 1).add(new Level());
            currentWorld = worlds.size() - 1;
            drawGame();
        }
    }

    private void removeWorld() {
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this World", "Remove?", JOptionPane.YES_NO_OPTION) == 0) {
            boolean move = worlds.size() == 1;
            while (worlds.size() == 1) {
                addWorld();
            }
            if (move) {
                currentWorld--;
            }
            worlds.remove(currentWorld);
            if (currentWorld >= worlds.size()) {
                currentWorld--;
            }
            drawGame();
        }
    }

    private void exit() {
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to Quit?", "Quit?", JOptionPane.YES_NO_OPTION) == 0) {
            if (JOptionPane.showConfirmDialog(null, "Would you like to save your game?", "Save Game?", JOptionPane.YES_NO_OPTION) == 0) {
                save();
            }
            System.exit(0);
        }
        System.exit(0);
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        Point actualPoint = me.getPoint();
        actualPoint.translate(-getInsets().left, -getInsets().top);
        Point snapedPoint = snapToLocation(actualPoint);
        if (painting && actualPoint.x > menuWidth && actualPoint.y < screenHeight - bottomMenuHeight && actualPoint.y > tabHeight) {
            if (SwingUtilities.isRightMouseButton(me)) {
                removeFromLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true, true);
            } else if (SwingUtilities.isLeftMouseButton(me)) {
                addToLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true, true);
            }
        } else {
            if (currentLevelObject != null && SwingUtilities.isLeftMouseButton(me)) {
                currentLevelObject.setLocationAndFix(snapedPoint);
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
        Point actualPoint = me.getPoint();
        actualPoint.translate(-getInsets().left, -getInsets().top);
        Point snapedPoint = snapToLocation(actualPoint);
        if (actualPoint.x > menuWidth && actualPoint.y < screenHeight - bottomMenuHeight && actualPoint.y > tabHeight) {
            if (SwingUtilities.isRightMouseButton(me)) {
                removeFromLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true, true);
            } else if (SwingUtilities.isLeftMouseButton(me)) {
                if (painting) {
                    addToLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true, true);
                } else {
                    Item item = getFromLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType));
                    if (item != null) {
                        currentItemType = item.getType();
                        currentLevelObject = item;
                    } else {
                        currentLevelObject = new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType);
                    }
                }
            }
        } else {
            int itemType = getSelectedMenuItemType(actualPoint);
            if (itemType >= 0) {
                currentItemType = itemType;
                currentLevelObject = new Item(snapedPoint.x, snapedPoint.y, itemSize, itemType);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        Point actualPoint = me.getPoint();
        actualPoint.translate(-getInsets().left, -getInsets().top);
        if (currentLevelObject != null && !painting) {
            if (actualPoint.x > menuWidth && actualPoint.y < screenHeight - bottomMenuHeight && actualPoint.y > tabHeight) {
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
            worlds.get(currentWorld).addUndo(new ItemBackup('h'));
            worlds.get(currentWorld).clearRedo();
        } else if (key == KeyEvent.VK_Z && ke.isControlDown()) {
            undo();
        } else if (key == KeyEvent.VK_Y && ke.isControlDown()) {
            redo();
        } else if (key == KeyEvent.VK_S && ke.isControlDown()) {
            saveKeyIsDown = true;
        } else if (key == KeyEvent.VK_TAB && ke.isControlDown()) {
            switchWorld(currentWorld + (ke.isShiftDown() ? -1 : 1));
        } else if (key == KeyEvent.VK_N && ke.isControlDown()) {
            addWorld();
        } else if (key == KeyEvent.VK_F4 && ke.isControlDown()) {
            removeWorld();
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        int key = ke.getKeyCode();
        if (key == KeyEvent.VK_PAGE_UP && levelUpKeyIsDown) {
            levelUpKeyIsDown = false;
            chengleLevel('u');
        } else if (key == KeyEvent.VK_PAGE_DOWN && levelDownKeyIsDown) {
            levelDownKeyIsDown = false;
            chengleLevel('d');
        } else if (key == KeyEvent.VK_S && ke.isControlDown() && saveKeyIsDown == true) {
            saveKeyIsDown = false;
            save();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        if (mwe.getX() < menuWidth) {
            int amount = mwe.getWheelRotation() * 15;
            if ((menuItems[0].getY() - amount > 0 || menuItems[menuItems.length - 1].getY() - amount > screenHeight - bottomMenuHeight) && (menuItems[menuItems.length - 1].getY() - amount + itemSize < screenHeight - bottomMenuHeight || menuItems[0].getY() - amount < 0)) {
                for (Item item : menuItems) {
                    item.shiftLocation(0, -amount);
                }
                drawGame();
            }
        }
    }

    @Override
    public void windowOpened(WindowEvent we) {
    }

    @Override
    public void windowClosing(WindowEvent we) {
        exit();
    }

    @Override
    public void windowClosed(WindowEvent we) {
    }

    @Override
    public void windowIconified(WindowEvent we) {
    }

    @Override
    public void windowDeiconified(WindowEvent we) {
    }

    @Override
    public void windowActivated(WindowEvent we) {
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
    }

    @Override
    public void componentResized(ComponentEvent ce) {
        screenWidth = getContentPane().getWidth();
        screenHeight = getContentPane().getHeight();
    }

    @Override
    public void componentMoved(ComponentEvent ce) {
    }

    @Override
    public void componentShown(ComponentEvent ce) {
    }

    @Override
    public void componentHidden(ComponentEvent ce) {
    }
}
