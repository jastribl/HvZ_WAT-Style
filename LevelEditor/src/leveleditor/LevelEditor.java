package leveleditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static leveleditor.Globals.*;

public final class LevelEditor extends JFrame implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener, WindowListener, ComponentListener {

    private final Image memoryImage;
    private final int numberOfPaintingTools = 3;
    private int currentItemType = 0;
    private boolean canDraw = false;
    private Point rectangleStart = null, rectangleEnd = null;
    private final OpenWindow openWindow = new OpenWindow(this);
    private final Menu menu = new Menu();

    LevelEditor() {
        setTitle("LevelUpGame - 2015 - Justin Stribling");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(999999999, 999999999);
        getContentPane().setSize(99999999, 99999999);
        addMouseMotionListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);
        addWindowListener(this);
        addComponentListener(this);
        addMouseListener(this);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        memoryImage = createImage(getContentPane().getWidth(), getContentPane().getHeight());
        memoryGraphics = memoryImage.getGraphics();
        loadAll();
        canDraw = true;
        new Timer(20, (ActionEvent ae) -> {
            draw();
        }).start();
    }

    public static void main(String[] args) {
        LevelEditor levelEditor = new LevelEditor();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (canDraw) {
            draw();
        }
    }

    public final void draw() {
        memoryGraphics.setColor(Color.black);
        memoryGraphics.fillRect(0, 0, screenWidth, screenHeight);
        if (worlds.size() > 0) {
            worlds.get(currentWorld).draw();
            menu.drawTabs();
            menu.drawBottomMenu();
            menu.drawSideManu();
            tabWidth = (screenWidth - menuWidth) / worlds.size();
        } else {
            tabWidth = 0;
        }
        getContentPane().getGraphics().drawImage(memoryImage, 0, 0, null);
        getContentPane().getGraphics().dispose();
    }

    private void addToLevelChecked(int level, Item item, boolean setUndo) {
        if (worlds.get(currentWorld).get(level).addItemChecked(item)) {
            if (setUndo) {
                worlds.get(currentWorld).addUndo(new ItemBackup(ADD, level, item.getType(), (Point) item.getLocation().clone()));
                worlds.get(currentWorld).clearRedo();
            }
        }
    }

    private void removeFromLevelChecked(int level, Item item, boolean setUndo) {
        Item removedItem = worlds.get(currentWorld).get(level).removeItem(item);
        if (removedItem != null) {
            if (setUndo) {
                worlds.get(currentWorld).addUndo(new ItemBackup(REMOVE, level, removedItem.getType(), (Point) removedItem.getLocation().clone()));
                worlds.get(currentWorld).clearRedo();
            }
        }
    }

    private Item getFromCurrentLevel(Item item) {
        Item removedItem = worlds.get(currentWorld).get(currentLevel).removeItem(item);
        if (removedItem != null) {
            worlds.get(currentWorld).addUndo(new ItemBackup(REMOVE, currentLevel, removedItem.getType(), (Point) removedItem.getLocation().clone()));
            worlds.get(currentWorld).clearRedo();
            try {
                return (Item) removedItem.clone();
            } catch (CloneNotSupportedException ex) {
            }
        }
        return null;
    }

    private void moveItems(int x, int y) {
        worlds.get(currentWorld).shiftItems(x * itemSize, y * halfItemSize);
        if (paintingMode == RECTANGLE && rectangleStart != null) {
            rectangleStart.translate(x * itemSize, y * itemSize);
            populateRectangle();
        }
    }

    private void findPlaceToBe(ItemBackup backup) {
        while (backup.level < currentLevel) {
            chengleLevel(DOWN);
        }
        while (backup.level > currentLevel) {
            chengleLevel(UP);
        }
        worlds.get(currentWorld).get(currentLevel).setVisible(true);
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

    private void undo() {
        ItemBackup backup = worlds.get(currentWorld).peekUndo();
        if (backup == null) {
            return;
        } else if (backup.backupType == ADD || backup.backupType == REMOVE) {
            findPlaceToBe(backup);
            backup = worlds.get(currentWorld).peekUndo();
            worlds.get(currentWorld).addRedo(new ItemBackup(backup.backupType, backup.level, backup.type, backup.location));
            if (backup.backupType == REMOVE) {
                addToLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false);
            } else {
                removeFromLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false);
            }
        }
        worlds.get(currentWorld).popUndo();
        if (worlds.get(currentWorld).undoSize() == 0) {
            worlds.get(currentWorld).setChages(false);
        }
    }

    private void redo() {
        ItemBackup backup = worlds.get(currentWorld).peekRedo();
        if (backup == null) {
            return;
        } else if (backup.backupType == ADD || backup.backupType == REMOVE) {
            findPlaceToBe(backup);
            backup = worlds.get(currentWorld).peekRedo();
            worlds.get(currentWorld).addUndo(new ItemBackup(backup.backupType, backup.level, backup.type, backup.location));
            if (backup.backupType == ADD) {
                addToLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false);
            } else {
                removeFromLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false);
            }
        }
        worlds.get(currentWorld).popRedo();
    }

    private void chengleLevel(int direction) {
        if (direction == UP) {
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
            }
        }
    }

    private void hideCurrentLevel() {
        if (worlds.size() > 0) {
            worlds.get(currentWorld).get(currentLevel).switchVisibility();
        }
    }

    private void changePaintingMode() {
        if (worlds.size() > 0) {
            paintingMode = (paintingMode + 1) % (numberOfPaintingTools);
        }
    }

    private void switchWorld(int direction) {
        if (worlds.size() > 0) {
            if (direction == UP) {
                if (currentWorld == worlds.size() - 1) {
                    currentWorld = 0;
                } else {
                    currentWorld++;
                }
            } else if (direction == DOWN) {
                if (currentWorld == 0) {
                    currentWorld = worlds.size() - 1;
                } else {
                    currentWorld--;
                }
            }
        }
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
        }
    }

    private void addRectangleToLevel() {
        for (Item item : rectangleItems.getLevel()) {
            addToLevelChecked(currentLevel, item, true);
        }
        rectangleItems.clear();
        rectangleStart = null;
        rectangleEnd = null;
    }

    private void removeRectangleFromLevel() {
        for (Item item : rectangleItems.getLevel()) {
            removeFromLevelChecked(currentLevel, item, true);
        }
        rectangleItems.clear();
        rectangleStart = null;
        rectangleEnd = null;
    }

    private void populateRectangle() {
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
                    rectangleItems.addItemUnchecked(new Item(rectangleStart.x + (itemSize * j) + xShift, y, itemSize, currentItemType));
                }
            }
        } catch (NullPointerException e) {
        }
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

//    private boolean mouseIsInBottomMenu(Point point) {
//        if (point != null) {
//            return (point.x > menuWidth && point.x < screenWidth && point.y > screenHeight - bottomMenuHeight && point.y < screenHeight);
//        }
//        return false;
//    }
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
                    if (paintingMode == PAINT) {
                        if (SwingUtilities.isRightMouseButton(me)) {
                            removeFromLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true);
                        } else if (SwingUtilities.isLeftMouseButton(me)) {
                            addToLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true);
                        }
                    } else if (paintingMode == POINT) {
                        if (currentLevelObject != null && SwingUtilities.isLeftMouseButton(me)) {
                            currentLevelObject.setLocationAndFix(snapedPoint);
                        }
                    } else if (paintingMode == RECTANGLE) {
                        if (rectangleStart != null) {
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
        menu.closeAllRightClickMenus();
        if (worlds.size() > 0) {
            if (worlds.get(currentWorld).get(currentLevel).isVisible()) {
                Point actualPoint = ((JFrame) me.getSource()).getContentPane().getMousePosition();
                Point snapedPoint = snapToGrid(actualPoint);
                if (mouseIsInMain(actualPoint)) {
                    if (SwingUtilities.isRightMouseButton(me)) {
                        if (paintingMode == PAINT || paintingMode == POINT) {
                            removeFromLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true);
                        } else if (paintingMode == RECTANGLE) {
                            rectangleStart = snapedPoint;
                            drawingRectangle = false;
                        }
                    } else if (SwingUtilities.isLeftMouseButton(me)) {
                        if (paintingMode == PAINT) {
                            addToLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true);
                        } else if (paintingMode == POINT) {
                            Item item = getFromCurrentLevel(new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType));
                            if (item != null) {
                                currentItemType = item.getType();
                                currentLevelObject = item;
                            } else {
                                currentLevelObject = new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType);
                            }
                        } else if (paintingMode == RECTANGLE) {
                            rectangleStart = snapedPoint;
                            drawingRectangle = true;
                        }
                    }
                } else if (mouseIsInTabs(actualPoint)) {
                    menu.selectTabAt(actualPoint);
                } else if (mouseIsInSideMenu(actualPoint)) {
                    int itemType = menu.getSelectedItem(actualPoint);
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
            Point actualPoint = ((JFrame) me.getSource()).getContentPane().getMousePosition();
            if (actualPoint != null) {
                if (me.isPopupTrigger()) {
                    if (mouseIsInTabs(actualPoint)) {
//                        menu.showTabRightClickMenu(actualPoint);
                        menu.showTabRightClickMenu(me.getLocationOnScreen());
                    }
                } else if (mouseIsInMain(actualPoint)) {
                    if (worlds.get(currentWorld).get(currentLevel).isVisible()) {
                        if (currentLevelObject != null && paintingMode == POINT) {
                            addToLevelChecked(currentLevel, currentLevelObject, true);
                            currentLevelObject = null;
                        } else if (paintingMode == RECTANGLE) {
                            if (drawingRectangle) {
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
        if (paintingMode == POINT) {
            currentLevelObject = null;
        } else if (paintingMode == RECTANGLE) {
            if (drawingRectangle) {
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
            chengleLevel(UP);
        } else if (key == KeyEvent.VK_PAGE_DOWN) {
            chengleLevel(DOWN);
        } else if (key == KeyEvent.VK_UP) {
            moveItems(0, 1);
        } else if (key == KeyEvent.VK_RIGHT) {
            moveItems(-1, 0);
        } else if (key == KeyEvent.VK_DOWN) {
            moveItems(0, -1);
        } else if (key == KeyEvent.VK_LEFT) {
            moveItems(1, 0);
        } else if (key == KeyEvent.VK_P) {
            changePaintingMode();
        } else if (key == KeyEvent.VK_H) {
            hideCurrentLevel();
        }
        if (ke.isControlDown()) {
            if (key == KeyEvent.VK_Z) {
                undo();
            } else if (key == KeyEvent.VK_Y) {
                redo();
            } else if (key == KeyEvent.VK_S) {
                if (ke.isShiftDown()) {
                    saveAll();
                } else if (worlds.size() > 0) {
                    worlds.get(currentWorld).save();
                }
            } else if (key == KeyEvent.VK_TAB) {
                switchWorld(ke.isShiftDown() ? DOWN : UP);
            } else if (key == KeyEvent.VK_N) {
                addWorld();
            } else if (key == KeyEvent.VK_F4) {
                closeTab(currentWorld);
            } else if (key == KeyEvent.VK_O) {
                openWindow.display(worlds, this);
            } else if (key == KeyEvent.VK_DELETE) {
                removeWorld(currentWorld);
            }
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
        } else if (mouseIsInSideMenu(actualPoint)) {
            menu.scroll(mwe.getWheelRotation() * 15);
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
//        menu.closeAllRightClickMenus();
    }

    @Override
    public void componentResized(ComponentEvent ce) {
//        menu.closeAllRightClickMenus();
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
