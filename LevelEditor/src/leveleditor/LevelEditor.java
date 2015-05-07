package leveleditor;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import javax.swing.*;
import static leveleditor.Globals.*;

public final class LevelEditor extends JFrame implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener, WindowListener, ComponentListener {

    private final Image memoryImage;
    private Point rectangleStart = null, rectangleEnd = null;
    private Level rectangleItems = new Level();
    private boolean addingRectangle = true;

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
        ActionListener tabsRightClickListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String command = ae.getActionCommand();
                if (command.equals("Close")) {
                    closeTab(currentWorld);
                } else if (command.equals("Close All")) {
                    saveAll();
                    while (worlds.size() > 0) {
                        worlds.remove(0);
                    }
                    currentWorld = 0;
                    drawGame();
                } else if (command.equals("Rename")) {
                    renameWorld(currentWorld);
                } else if (command.equals("Save")) {
                    worlds.get(currentWorld).save();
                    drawGame();
                } else if (command.equals("Save All")) {
                    saveAll();
                    drawGame();
                } else if (command.equals("Delete")) {
                    removeWorld(currentWorld);
                } else {
                    return;
                }
                ((JMenuItem) ae.getSource()).getParent().setVisible(false);
            }
        };
        for (int i = 0; i < tabsRightClickMenuItems.length; i++) {
            tabsRightClickMenuItems[i] = new JMenuItem(tabsRightClickText[i]);
            tabsRightClickMenuItems[i].setActionCommand(tabsRightClickText[i]);
            tabsRightClickMenuItems[i].addActionListener(tabsRightClickListener);
            tabsRightClickMenu.add(tabsRightClickMenuItems[i]);
        }
        setVisible(true);
        memoryImage = createImage(getContentPane().getWidth(), getContentPane().getHeight());
        memoryGraphics = memoryImage.getGraphics();
        loadAll();
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
        if (worlds.size() > 0) {
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
                                if (addingRectangle) {
                                    levelToDraw.addItemChecked(rectangleItems.get(j));
                                } else {
                                    levelToDraw.removeItem(rectangleItems.get(j));
                                }
                            }
                        }
                    }
                    for (int j = 0; j < levelToDraw.size(); j++) {
                        if (i == currentLevel) {
                            levelToDraw.get(j).draw();
                        } else {
                            levelToDraw.get(j).drawFaded();
                        }
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
        if (worlds.size() > 0) {
            for (Item item : menuItems) {
                item.draw();
            }
            memoryGraphics.setColor(Color.black);
            memoryGraphics.fillRect(menuWidth + 1, 0, screenWidth - menuWidth, tabHeight);
            tabWidth = (screenWidth - menuWidth) / worlds.size();
        } else {
            tabWidth = 0;
        }
        int count = 0;
        for (int i = 0; i < worlds.size(); i++) {
            if (currentWorld == i) {
                memoryGraphics.setColor(Color.lightGray);
            } else {
                memoryGraphics.setColor(Color.gray);
            }
            memoryGraphics.fillRoundRect(menuWidth + (int) (count * tabWidth) + 1, 0, (int) tabWidth - 1, tabHeight, 10, 20);
            memoryGraphics.fillRect(menuWidth + (int) (count * tabWidth) + 1, tabHeight / 2, (int) tabWidth - 1, (tabHeight / 2) + 1);
            memoryGraphics.setColor(Color.black);
            if (worlds.get(i).hasChanges()) {
                memoryGraphics.setFont(new Font(defaultFont.getFontName(), Font.BOLD, defaultFont.getSize() + 5));
            }
            Rectangle2D stringSize = memoryGraphics.getFontMetrics().getStringBounds(worlds.get(i).getName(), memoryGraphics);
            memoryGraphics.drawString(worlds.get(i).getName(), menuWidth + 1 + (count * tabWidth) + (int) ((tabWidth - stringSize.getWidth()) / 2), (tabHeight / 3) + (int) (stringSize.getHeight() / 2));
            memoryGraphics.setFont(defaultFont);
            count++;
        }
        if (worlds.size() > 0) {
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
            if (setUndo) {
                worlds.get(currentWorld).addUndo(new ItemBackup('a', level, item.getType(), (Point) item.getLocation().clone()));
                if (wipeRedoCache) {
                    worlds.get(currentWorld).clearRedo();
                }
            }
            if (draw) {
                drawGame();
            }
        }
    }

    public final void removeFromLevelChecked(int level, Item item, boolean setUndo, boolean wipeRedoCache, boolean draw) {
        Item removedItem = worlds.get(currentWorld).get(level).removeItem(item);
        if (removedItem != null) {
            if (setUndo) {
                worlds.get(currentWorld).addUndo(new ItemBackup('r', level, removedItem.getType(), (Point) removedItem.getLocation().clone()));
                if (wipeRedoCache) {
                    worlds.get(currentWorld).clearRedo();
                }
            }
            if (draw) {
                drawGame();
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
        if (paintingMode == 2 && rectangleStart != null) {
            rectangleStart.x += x * itemSize;
            populateRectangle();
        }
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
        while (backup.location.y < tabHeight + itemSize) {
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
                removeFromLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false, true);
            }
        } else if (backup.backupType == 'h') {
            hideCurrentLevel();
            worlds.get(currentWorld).addRedo(backup);
        }
        worlds.get(currentWorld).popUndo();
        if (worlds.get(currentWorld).undoSize() == 0) {
            worlds.get(currentWorld).setChages(false);
            drawGame();
        }
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
                removeFromLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false, true);
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

    private void switchWorld(char direction) {
        if (worlds.size() > 0) {
            if (direction == 'u') {
                if (currentWorld == worlds.size() - 1) {
                    currentWorld = 0;
                } else {
                    currentWorld++;
                }
            } else if (direction == 'd') {
                if (currentWorld == 0) {
                    currentWorld = worlds.size() - 1;
                } else {
                    currentWorld--;
                }
            }
            drawGame();
        }
    }

    private String getNewWorldName() {
        String name;
        boolean bad;
        do {
            bad = false;
            try {
                name = JOptionPane.showInputDialog(this, "Name the new world", "New", JOptionPane.QUESTION_MESSAGE).replaceAll(" ", "_");
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

    private void addWorld() {
        String newWorldName = getNewWorldName();
        if (newWorldName != null) {
            allWorlds.add(newWorldName);
            World world = new World(newWorldName);
            world.addLevelUnchecked(new Level());
            worlds.add(world);
            currentWorld = worlds.size() - 1;
            saveAll();
            drawGame();
        }
    }

    private void removeWorld(int index) {
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this World", "Remove?", JOptionPane.YES_NO_OPTION) == 0) {
            saveAll();
            new File(worlds.get(index).getName() + ".txt").delete();
            allWorlds.remove(worlds.get(index).getName());
            worlds.remove(index);
            if (currentWorld == worlds.size()) {
                currentWorld--;
            }
            saveAll();
            drawGame();
        }
    }

    private void renameWorld(int index) {
        String newWorldName = getNewWorldName();
        if (newWorldName != null) {
            String oldWorldName = worlds.get(index).getName();
            worlds.get(index).setName(newWorldName);
            allWorlds.set(allWorlds.indexOf(oldWorldName), newWorldName);
            new File(oldWorldName + ".txt").delete();
            saveAll();
            drawGame();
        }
    }

    private void selectTab(Point point) {
        if (!worlds.isEmpty()) {
            currentWorld = (point.x - menuWidth) / tabWidth;
        } else {
            currentWorld = 0;
        }
        drawGame();
    }

    private void closeTab(int index) {
        saveAll();
        worlds.remove(index);
        if (currentWorld == worlds.size()) {
            currentWorld--;
        }
        drawGame();
    }

    private void addRectangleToLevel() {
        for (Item item : rectangleItems.getLevel()) {
            addToLevelChecked(currentLevel, item, true, true, false);
        }
        rectangleItems.clear();
        rectangleStart = null;
        rectangleEnd = null;
        drawGame();
    }

    public void removeRectangleFromLevel() {
        for (Item item : rectangleItems.getLevel()) {
            removeFromLevelChecked(currentLevel, item, true, true, false);
        }
        rectangleItems.clear();
        rectangleStart = null;
        rectangleEnd = null;
        drawGame();
    }

    private void populateRectangle() {
        rectangleItems.clear();
        for (int i = rectangleStart.x; i != rectangleEnd.x; i += (rectangleEnd.x > rectangleStart.x ? 1 : -1) * (itemSize / 4)) {
            for (int j = rectangleStart.y; j != rectangleEnd.y; j += (rectangleEnd.y > rectangleStart.y ? 1 : -1) * (itemSize / 4)) {
                Point p = snapToGrid(new Point(i, j));
                rectangleItems.addItemChecked(new Item(p.x, p.y, itemSize, currentItemType));
            }
        }
        drawGame();
    }

    private void exit() {
//        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to Quit?", "Quit?", JOptionPane.YES_NO_OPTION) == 0) {
//            if (JOptionPane.showConfirmDialog(null, "Would you like to save your game?", "Save Game?", JOptionPane.YES_NO_OPTION) == 0) {
//                saveAll();
//            }
//            System.exit(0);
//        }
        System.exit(0);
    }

    private boolean mouseIsInMain(Point point) {
        if (point != null) {
            return (point.x > menuWidth && point.x < screenWidth && point.y > tabHeight && point.y < screenHeight - bottomMenuHeight);
        }
        return false;
    }

    private boolean mouseIsInSideMenu(Point point) {
        if (point != null) {
            return (point.x > 0 && point.x < menuWidth && point.y > 0 && point.y < screenHeight);
        }
        return false;
    }

    private boolean mouseIsInBottomMenu(Point point) {
        if (point != null) {
            return (point.x > menuWidth && point.x < screenWidth && point.y > screenHeight - bottomMenuHeight && point.y < screenHeight);
        }
        return false;
    }

    private boolean mouseIsInTabs(Point point) {
        if (point != null) {
            return (point.x > menuWidth && point.x < screenWidth && point.y > 0 && point.y < tabHeight);
        }
        return false;
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (worlds.size() > 0) {
            Point actualPoint = ((JFrame) me.getSource()).getContentPane().getMousePosition();
            if (worlds.get(currentWorld).get(currentLevel).isVisible() && actualPoint != null) {
                Point snapedPoint = snapToGrid(actualPoint);
                if (mouseIsInMain(actualPoint)) {
                    if (paintingMode == 0) {
                        if (SwingUtilities.isRightMouseButton(me)) {
                            removeFromLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true, true, true);
                        } else if (SwingUtilities.isLeftMouseButton(me)) {
                            addToLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true, true, true);
                        }
                    } else if (paintingMode == 1) {
                        if (currentLevelObject != null && SwingUtilities.isLeftMouseButton(me)) {
                            currentLevelObject.setLocationAndFix(snapedPoint);
                            drawGame();
                        }
                    } else if (paintingMode == 2) {
                        if (rectangleStart != null && (SwingUtilities.isLeftMouseButton(me) || SwingUtilities.isRightMouseButton(me))) {
                            rectangleEnd = snapedPoint;
                            populateRectangle();
                        }
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
        if (worlds.size() > 0) {
            if (worlds.get(currentWorld).get(currentLevel).isVisible()) {
                Point actualPoint = ((JFrame) me.getSource()).getContentPane().getMousePosition();
                Point snapedPoint = snapToGrid(actualPoint);
                if (mouseIsInMain(actualPoint)) {
                    if (SwingUtilities.isRightMouseButton(me)) {
                        if (paintingMode == 0 || paintingMode == 1) {
                            removeFromLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true, true, true);
                        } else if (paintingMode == 2) {
                            rectangleStart = snapedPoint;
                            addingRectangle = false;
                        }
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
                            rectangleStart = snapedPoint;
                            addingRectangle = true;
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
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (worlds.size() > 0) {
            if (worlds.get(currentWorld).get(currentLevel).isVisible()) {
                Point actualPoint = ((JFrame) me.getSource()).getContentPane().getMousePosition();
                if (actualPoint != null) {
                    if (mouseIsInTabs(actualPoint) && worlds.size() > 0) {
                        if (me.isPopupTrigger()) {
                            tabsRightClickMenu.setLocation(me.getPoint());
                            tabsRightClickMenu.setVisible(true);
                        }
                    } else if (mouseIsInMain(actualPoint)) {
                        if (currentLevelObject != null && paintingMode == 1) {
                            addToLevelChecked(currentLevel, currentLevelObject, true, true, true);
                            currentLevelObject = null;
                        } else if (paintingMode == 2) {
                            if (addingRectangle) {
                                addRectangleToLevel();
                            } else {
                                removeRectangleFromLevel();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
        if (paintingMode == 2) {
            if (addingRectangle) {
                addRectangleToLevel();
            } else {
                removeRectangleFromLevel();
            }
        }
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
            closeTab(currentWorld);
        } else if (key == KeyEvent.VK_O && ke.isControlDown()) {
            openWindow.display(worlds);
        } else if (key == KeyEvent.VK_DELETE) {
            removeWorld(currentWorld);
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        Point actualPoint = ((JFrame) mwe.getSource()).getContentPane().getMousePosition();
        if (mouseIsInMain(actualPoint)) {
            int amount = -mwe.getWheelRotation();
            if (mwe.isControlDown()) {
                moveItems(amount, 0);
            } else {
                moveItems(0, amount);
            }
            drawGame();
        } else if (mouseIsInSideMenu(actualPoint)) {
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
}
