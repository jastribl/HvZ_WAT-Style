package leveleditor;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import static leveleditor.Globals.*;

public final class LevelEditor extends JFrame implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener, WindowListener, ComponentListener, ActionListener {

    private final Image memoryImage;
    private Point paintingRectangleAncherPoint = null, paintingRectangleEndPoint = null;
    private Level rectangleItems = new Level();

    LevelEditor() {
        MediaTracker imageTracker = new MediaTracker(this);
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
                imageTracker.addImage(iconImages[i], 0);
            } catch (Exception e) {
            }
        }
        try {
            imageTracker.waitForID(0);
        } catch (InterruptedException e) {
        }
        setTitle("LevelUpGame - 2015 - Justin Stribling");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(999999999, 999999999);
        getContentPane().setSize(99999999, 99999999);
        setBackground(Color.black);
        setFocusable(true);
        addMouseMotionListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);
        addWindowListener(this);
        addComponentListener(this);
        addMouseListener(this);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setFocusTraversalKeysEnabled(false);
        tabsRightClickMenuItems[0] = new JMenuItem("Close");
        tabsRightClickMenuItems[0].setActionCommand("CloseTab");
        tabsRightClickMenuItems[1] = new JMenuItem("Close All");
        tabsRightClickMenuItems[1].setActionCommand("CloseAllTabs");
        tabsRightClickMenuItems[2] = new JMenuItem("Save");
        tabsRightClickMenuItems[2].setActionCommand("SaveTab");
        tabsRightClickMenuItems[3] = new JMenuItem("Save All");
        tabsRightClickMenuItems[3].setActionCommand("SaveAllTabs");
        tabsRightClickMenuItems[4] = new JMenuItem("Delete");
        tabsRightClickMenuItems[4].setActionCommand("DeleteWorld");
        for (JMenuItem item : tabsRightClickMenuItems) {
            item.addActionListener(this);
            tabsRightClickMenu.add(item);
        }
        setVisible(true);
        memoryImage = createImage(getContentPane().getWidth(), getContentPane().getHeight());
        memoryGraphics = memoryImage.getGraphics();
        load();
        canDraw = true;
        drawGame();
    }

    public static void main(String[] args) {
        LevelEditor levelEditor = new LevelEditor();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (canDraw) {
            drawGame();
        }
    }

    public final void drawGame() {
        Font defaultFont = memoryGraphics.getFontMetrics().getFont();
        memoryGraphics.setColor(Color.black);
        memoryGraphics.fillRect(menuWidth, 0, screenWidth - menuWidth, screenHeight);
        if (numberOfWorldsOpen > 0) {
            World world = worlds.get(currentWorld);
            for (int i = 0; i < world.size(); i++) {
                Level level = world.get(i);
                if (level.isVisible()) {
                    Level levelToDraw = new Level();
                    for (int j = 0; j < level.size(); j++) {
                        Item item = level.get(j);
                        Point p = item.getLocation();
                        if (p.x > menuWidth - itemSize && p.x < screenWidth && p.y > tabHeight - itemSize && p.y < screenHeight) {
                            levelToDraw.addItemUnchecked(item);
                        }
                    }
                    if (currentLevel == i) {
                        if ((paintingMode == 0 || paintingMode == 1) && currentLevelObject != null) {
                            levelToDraw.addItemChecked(currentLevelObject);
                        } else if (paintingMode == 2) {
                            for (int j = 0; j < rectangleItems.size(); j++) {
                                levelToDraw.addItemChecked(rectangleItems.get(j));
                            }
                        }
                    }
                    for (int j = 0; j < levelToDraw.size(); j++) {
                        levelToDraw.get(j).draw();
                    }
                }
            }
        }
        memoryGraphics.setColor(Color.gray);
        memoryGraphics.fillRect(0, 0, menuWidth, screenHeight);
        memoryGraphics.fillRect(0, screenHeight - bottomMenuHeight, screenWidth, screenHeight - bottomMenuHeight);
        memoryGraphics.setColor(Color.white);
        memoryGraphics.drawLine(menuWidth, 0, menuWidth, screenHeight);
        memoryGraphics.drawLine(0, screenHeight - bottomMenuHeight, screenWidth, screenHeight - bottomMenuHeight);
        if (numberOfWorldsOpen > 0) {
            for (Item item : menuItems) {
                item.draw();
            }
            memoryGraphics.setColor(Color.black);
            memoryGraphics.fillRect(menuWidth + 1, 0, screenWidth - menuWidth, tabHeight);
            tabWidth = (screenWidth - menuWidth) / numberOfWorldsOpen;
        } else {
            tabWidth = 0;
        }
        int count = 0;
        for (int i = 0; i < worlds.size(); i++) {
            if (worlds.get(i).isOpen()) {
                if (currentWorld == i) {
                    memoryGraphics.setColor(Color.lightGray);
                } else {
                    memoryGraphics.setColor(Color.gray);
                }
                memoryGraphics.fillRect(menuWidth + (int) (count * tabWidth) + 1, 0, (int) tabWidth - 1, tabHeight);
                memoryGraphics.setColor(Color.black);
                if (worlds.get(i).hasChanges()) {
                    memoryGraphics.setFont(new Font(defaultFont.getFontName(), Font.BOLD, defaultFont.getSize() + 5));
                }
                Rectangle2D stringSize = memoryGraphics.getFontMetrics().getStringBounds(worlds.get(i).getName(), memoryGraphics);
                memoryGraphics.drawString(worlds.get(i).getName(), menuWidth + 1 + (count * tabWidth) + (int) ((tabWidth - stringSize.getWidth()) / 2), (tabHeight / 2) + (int) (stringSize.getHeight() / 2));
                memoryGraphics.setFont(defaultFont);
                count++;
            }
        }
        if (numberOfWorldsOpen > 0) {
            memoryGraphics.setColor(Color.black);
            memoryGraphics.drawString("Level: " + String.valueOf(currentLevel), screenWidth - 60, screenHeight - (iconPadding * 2));
            memoryGraphics.drawImage(iconImages[worlds.get(currentWorld).get(currentLevel).isVisible() ? 0 : 1], menuWidth + iconPadding, screenHeight - iconSize - iconPadding, this);
            memoryGraphics.drawImage(iconImages[paintingMode + 2], menuWidth + iconSize + (iconPadding * 2), screenHeight - iconSize - iconPadding, this);
        }
        getContentPane().getGraphics().drawImage(memoryImage, 0, 0, this);
        getContentPane().getGraphics().dispose();
    }

    public final void addToLevelChecked(int level, Item item, boolean setUndo, boolean wipeRedoCache, boolean draw) {
        if (worlds.get(currentWorld).get(level).addItemChecked(item)) {
            if (draw) {
                drawGame();
            }
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
        while (backup.location.y < itemSize + tabHeight) {
            moveItems(0, 1);
        }
        while (backup.location.y > screenHeight - itemSize - bottomMenuHeight) {
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
                addToLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false, true);
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
                addToLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false, true);
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
                worlds.get(currentWorld).addLevelUnchecked(new Level());
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

    private void switchWorld(char dir) {
        if (numberOfWorldsOpen > 0) {
            if (dir == 'u') {
                boolean found = false;
                for (int i = currentWorld + 1; i < worlds.size(); i++) {
                    if (worlds.get(i).isOpen()) {
                        currentWorld = i;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    for (int i = 0; i < currentWorld; i++) {
                        if (worlds.get(i).isOpen()) {
                            currentWorld = i;
                            break;
                        }
                    }
                }
            } else if (dir == 'd') {
                boolean found = false;
                for (int i = currentWorld - 1; i >= 0; i--) {
                    if (worlds.get(i).isOpen()) {
                        currentWorld = i;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    for (int i = worlds.size() - 1; i > currentWorld; i--) {
                        if (worlds.get(i).isOpen()) {
                            currentWorld = i;
                            break;
                        }
                    }
                }
            }
            drawGame();
        }
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
            World world = new World(name);
            world.addLevelUnchecked(new Level());
            world.setOpen(true);
            worlds.add(world);
            currentWorld = worlds.size() - 1;
            numberOfWorldsOpen++;
            saveAll();
            drawGame();
        }
    }

    private void removeWorld(int index) {
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this World", "Remove?", JOptionPane.YES_NO_OPTION) == 0) {
            worlds.remove(index);
            chengleLevel('d');
            numberOfWorldsOpen--;
            saveAll();
            drawGame();
        }
    }

    private void selectTab(Point point) {
        int tabToGo;
        if (numberOfWorldsOpen == 0) {
            tabToGo = 0;
        } else {
            tabToGo = (point.x - menuWidth) / tabWidth;
        }
        int testingTab = 0;
        for (int i = 0; i < worlds.size(); i++) {
            if (worlds.get(i).isOpen()) {
                if (testingTab == tabToGo) {
                    currentWorld = i;
                    break;
                }
                testingTab++;
            }
        }
        drawGame();
    }

    private void closeCurrentTab() {
        saveAll();
        worlds.get(currentWorld).setOpen(false);
        numberOfWorldsOpen--;
        switchWorld('u');
        drawGame();
    }

    private void exit() {
//        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to Quit?", "Quit?", JOptionPane.YES_NO_OPTION) == 0) {
//            if (JOptionPane.showConfirmDialog(null, "Would you like to save your game?", "Save Game?", JOptionPane.YES_NO_OPTION) == 0) {
//                save();
//            }
//            System.exit(0);
//        }
        System.exit(0);
    }

    private boolean mouseIsInMain(Point point) {
        return (point.x > menuWidth && point.y < screenHeight - bottomMenuHeight && point.y > tabHeight);
    }

    private boolean mouseIsInSideMenu(Point point) {
        return (point.x < menuWidth);
    }

    private boolean mouseIsInBottomMenu(Point point) {
        return (point.x > menuWidth && point.y > screenHeight - bottomMenuHeight);
    }

    private boolean mouseIsInTabs(Point point) {
        return (point.x > menuWidth && point.y < tabHeight);
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        Point actualPoint = ((JFrame) me.getSource()).getContentPane().getMousePosition();
        if (actualPoint != null) {
            Point snapedPoint = snapToGrid(actualPoint);
            if (mouseIsInMain(actualPoint)) {
                if (paintingMode == 0) {
                    if (SwingUtilities.isRightMouseButton(me)) {
                        removeFromLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true, true);
                    } else if (SwingUtilities.isLeftMouseButton(me)) {
                        addToLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true, true, true);
                    }
                } else if (paintingMode == 1) {
                    if (currentLevelObject != null && SwingUtilities.isLeftMouseButton(me)) {
                        currentLevelObject.setLocationAndFix(snapedPoint);
                        drawGame();
                    }
                } else if (paintingMode == 2) {
                    if (SwingUtilities.isLeftMouseButton(me)) {
                        paintingRectangleEndPoint = snapedPoint;
                        rectangleItems.clear();
                        for (int i = paintingRectangleAncherPoint.x; i < paintingRectangleEndPoint.x; i += (itemSize / 4)) {
                            for (int j = paintingRectangleAncherPoint.y; j < paintingRectangleEndPoint.y; j += (itemSize / 4)) {
                                Point p = snapToGrid(new Point(i, j));
                                rectangleItems.addItemChecked(new Item(p.x, p.y, itemSize, currentItemType));
                            }
                        }
                        System.out.println(rectangleItems.size());
                        for (int i = 0; i < (paintingRectangleEndPoint.x - paintingRectangleAncherPoint.x) / itemSize; i++) {
                            for (int j = 0; j < (paintingRectangleEndPoint.y - paintingRectangleAncherPoint.y) / (itemSize / 4); j++) {
                                rectangleItems.addItemUnchecked(new Item(paintingRectangleAncherPoint.x + (i * itemSize) + (j % 2 == 1 ? itemSize / 2 : 0), paintingRectangleAncherPoint.y + ((j * itemSize) / 4), itemSize, currentItemType));
                            }
                        }
                        drawGame();
                    }
                }
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
        closeAllRightClickMenus();
        Point actualPoint = ((JFrame) me.getSource()).getContentPane().getMousePosition();
        Point snapedPoint = snapToGrid(actualPoint);
        if (mouseIsInMain(actualPoint)) {
            if (SwingUtilities.isRightMouseButton(me)) {
                removeFromLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true, true);
            } else if (SwingUtilities.isLeftMouseButton(me)) {
                if (paintingMode == 0) {
                    addToLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true, true, true);
                } else if (paintingMode == 1) {
                    Item item = getFromLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType));
                    if (item != null) {
                        currentItemType = item.getType();
                        currentLevelObject = item;
                    } else {
                        currentLevelObject = new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType);
                    }
                } else if (paintingMode == 2) {
                    paintingRectangleAncherPoint = snapedPoint;
                    drawGame();
                }
            }
        } else if (mouseIsInTabs(actualPoint)) {
            selectTab(actualPoint);
        } else if (mouseIsInSideMenu(actualPoint)) {
            int itemType = getSelectedMenuItemType(actualPoint);
            if (itemType >= 0) {
                currentItemType = itemType;
                currentLevelObject = new Item(snapedPoint.x, snapedPoint.y, itemSize, itemType);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        Point actualPoint = ((JFrame) me.getSource()).getContentPane().getMousePosition();
        if (actualPoint != null) {
            if (me.isPopupTrigger()) {
                if (mouseIsInMain(actualPoint)) {
                    mainAreaRightClickMenu.setLocation(me.getPoint());
                    mainAreaRightClickMenu.setVisible(true);
                } else if (mouseIsInSideMenu(actualPoint)) {
                    mainMenuRightClickMenu.setLocation(me.getPoint());
                    mainMenuRightClickMenu.setVisible(true);
                } else if (mouseIsInBottomMenu(actualPoint)) {
                    bottomMenuRightClickMenu.setLocation(me.getPoint());
                    bottomMenuRightClickMenu.setVisible(true);
                } else if (mouseIsInTabs(actualPoint) && numberOfWorldsOpen > 0) {
                    tabsRightClickMenu.setLocation(me.getPoint());
                    tabsRightClickMenu.setVisible(true);
                }
            } else {
                if (currentLevelObject != null && paintingMode == 1) {
                    if (mouseIsInMain(actualPoint)) {
                        addToLevelChecked(currentLevel, currentLevelObject, true, true, true);
                    }
                    currentLevelObject = null;
                } else if (paintingMode == 2) {
                    for (Item item : rectangleItems.getLevel()) {
                        addToLevelChecked(currentLevel, item, true, true, false);
                    }
                    rectangleItems.clear();
                    drawGame();
                }
            }
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
            chengleLevel('u');
        } else if (key == KeyEvent.VK_PAGE_DOWN) {
            chengleLevel('d');
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
            paintingMode = (paintingMode + 1) % (numberOfPaintingTools);
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
            if (ke.isShiftDown()) {
                saveAll();
            } else {
                worlds.get(currentWorld).save();
            }
            drawGame();
        } else if (key == KeyEvent.VK_TAB && ke.isControlDown()) {
            switchWorld(ke.isShiftDown() ? 'd' : 'u');
        } else if (key == KeyEvent.VK_N && ke.isControlDown()) {
            addWorld();
        } else if (key == KeyEvent.VK_F4 && ke.isControlDown()) {
            closeCurrentTab();
        } else if (key == KeyEvent.VK_O && ke.isControlDown()) {
            openWindow.display(worlds);
        } else if (openWindow.isVisible() && key == KeyEvent.VK_ENTER) {
            drawGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
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
        closeAllRightClickMenus();
        if (drawOpen) {
            drawGame();
            drawOpen = false;
        }
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
        closeAllRightClickMenus();
    }

    @Override
    public void componentResized(ComponentEvent ce) {
        closeAllRightClickMenus();
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

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("CloseTab")) {
            closeCurrentTab();
        } else if (ae.getActionCommand().equals("CloseAllTabs")) {
            saveAll();
            for (World world : worlds) {
                world.setOpen(false);
                numberOfWorldsOpen--;
            }
            currentWorld = 0;
            drawGame();
        } else if (ae.getActionCommand().equals("SaveTab")) {
            worlds.get(currentWorld).save();
            drawGame();
        } else if (ae.getActionCommand().equals("SaveAllTabs")) {
            saveAll();
            drawGame();
        } else if (ae.getActionCommand().equals("DeleteWorld")) {
            removeWorld(currentWorld);
        } else {
            return;
        }
        ((JMenuItem) ae.getSource()).getParent().setVisible(false);
    }
}
