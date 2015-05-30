package leveleditor;

import UI.*;
import Structures.*;
import static UI.MainMenu.numberOfGroups;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static leveleditor.Globals.*;

public final class LevelEditor extends JFrame implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener, WindowListener, ComponentListener {

    private final Image memoryImage;
    private boolean canDraw = false;
    private final OpenWindow openWindow = new OpenWindow(this);
    private final UiManager uiManager = new UiManager();

    LevelEditor() {
        setTitle("LevelUpGame - 2015 - Justin Stribling");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setFocusTraversalKeysEnabled(false);
        setSize(5000, 5000);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        addMouseMotionListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);
        addWindowListener(this);
        addComponentListener(this);
        addMouseListener(this);
        setVisible(true);
        memoryImage = createImage(getContentPane().getWidth(), getContentPane().getHeight());
        memoryGraphics = memoryImage.getGraphics();
        preloadLevelNames();
        new Timer(20, (ActionEvent ae) -> {
            draw();
        }).start();
        canDraw = true;
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
            uiManager.draw();
            worldTabWidth = (screenWidth - menuWidth) / worlds.size();
            menuTabWidth = (screenHeight) / numberOfGroups;
        } else {
            worldTabWidth = 0;
            menuTabWidth = 0;
        }
        getContentPane().getGraphics().drawImage(memoryImage, 0, 0, null);
        getContentPane().getGraphics().dispose();
    }

    private void switchWorld(int direction) {
        if (worlds.size() > 0) {
            if (direction == UP) {
                currentWorld = (currentWorld + 1) % (worlds.size());
            } else if (direction == DOWN) {
                currentWorld += (currentWorld == 0 ? worlds.size() - 1 : -1);
            }
        }
    }

    private void addWorld() {
        String newWorldName = getNewWorldName();
        if (newWorldName != null) {
            allWorlds.add(newWorldName);
            worlds.add(new World(newWorldName, false));
            currentWorld = worlds.size() - 1;
            saveLevelNames();
        }
    }

    private void exit() {
        closeAllTabs();
        System.exit(0);
    }

    private boolean mouseIsInMain(Point point) {
        return (point.x > menuWidth && point.x < screenWidth && point.y > worldTabHeight && point.y < screenHeight - bottomMenuHeight);
    }

    private boolean mouseIsInMainMenu(Point point) {
        return (point.x > 0 && point.x < menuWidth && point.y > 0 && point.y < screenHeight);
    }

    private boolean mouseIsInMenuTabs(Point point) {
        return (point.x < worldTabHeight);
    }

    private boolean mouseIsInMenuItems(Point point) {
        return (point.x > worldTabHeight);
    }

    private boolean mouseIsInBottomMenu(Point point) {
        return (point.x > menuWidth && point.x < screenWidth && point.y > screenHeight - bottomMenuHeight && point.y < screenHeight);
    }

    private boolean mouseIsInTabs(Point point) {
        return (point.x > menuWidth && point.x < screenWidth && point.y > 0 && point.y < worldTabHeight);
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (worlds.size() > 0) {
            Point actualPoint = getContentPane().getMousePosition();
            if (worlds.get(currentWorld).get(currentLevel).isVisible()) {
                Point snapedPoint;
                if (actualPoint == null) {
                    snapedPoint = null;
                } else {
                    snapedPoint = snapToGrid(actualPoint);
                }
                if (currentDrawingMode == PAINT && actualPoint != null && mouseIsInMain(actualPoint)) {
                    Item newItem = new Item(currentItemGroup, currentItemType, snapedPoint, true);
                    if (SwingUtilities.isRightMouseButton(me)) {
                        worlds.get(currentWorld).removeFromCurrentLevelChecked(newItem);
                    } else if (SwingUtilities.isLeftMouseButton(me)) {
                        worlds.get(currentWorld).addToCurrentLevelChecked(newItem);
                    }
                } else if (currentDrawingMode == POINT) {
                    if (currentLevelObject != null && SwingUtilities.isLeftMouseButton(me)) {
                        currentLevelObject.setLocationAndFix(snapedPoint);
                    }
                } else if (currentDrawingMode == RECTANGLE || currentDrawingMode == DIAMOND) {
                    if (gridStart != null) {
                        if (snapedPoint != null) {
                            gridEnd = snapedPoint;
                        }
                        populateGrid();
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
        uiManager.closeAllRightClickMenus();
        if (worlds.size() > 0 && worlds.get(currentWorld).get(currentLevel).isVisible()) {
            Point actualPoint = ((JFrame) me.getSource()).getContentPane().getMousePosition();
            if (actualPoint != null) {
                Point snapedPoint = snapToGrid(actualPoint);
                if (mouseIsInMain(actualPoint)) {
                    Item newItem = new Item(currentItemGroup, currentItemType, snapedPoint, true);
                    if (SwingUtilities.isRightMouseButton(me)) {
                        if (currentDrawingMode == PAINT || currentDrawingMode == POINT) {
                            worlds.get(currentWorld).removeFromCurrentLevelChecked(newItem);
                        } else if (currentDrawingMode == RECTANGLE || currentDrawingMode == DIAMOND) {
                            gridStart = snapedPoint;
                            drawingGrid = false;
                        }
                    } else if (SwingUtilities.isLeftMouseButton(me)) {
                        if (currentDrawingMode == PAINT) {
                            worlds.get(currentWorld).addToCurrentLevelChecked(newItem);
                        } else if (currentDrawingMode == POINT) {
                            Item item = worlds.get(currentWorld).getFromCurrentLevel(newItem);
                            if (item != null) {
                                currentItemType = item.getType();
                                currentLevelObject = item;
                            } else {
                                currentLevelObject = newItem;
                            }
                        } else if (currentDrawingMode == RECTANGLE || currentDrawingMode == DIAMOND) {
                            gridStart = snapedPoint;
                            drawingGrid = true;
                        }
                    }
                } else if (mouseIsInTabs(actualPoint)) {
                    uiManager.selectWorldTabAt(actualPoint);
                } else if (mouseIsInMainMenu(actualPoint)) {
                    if (mouseIsInMenuTabs(actualPoint)) {
                        uiManager.selectMenuTabAt(actualPoint);
                    } else if (mouseIsInMenuItems(actualPoint)) {
                        int itemType = uiManager.getSelectedGroupAndType(actualPoint);
                        if (itemType >= 0) {
                            currentItemType = itemType;
                            currentLevelObject = new Item(currentItemGroup, itemType, snapedPoint, true);
                        }
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
                if (mouseIsInTabs(actualPoint)) {
                    if (me.isPopupTrigger()) {
                        uiManager.showTabRightClickMenu(me.getLocationOnScreen());
                    }
                } else if (mouseIsInMain(actualPoint)) {
                    if (worlds.get(currentWorld).get(currentLevel).isVisible()) {
                        if (currentDrawingMode == POINT && currentLevelObject != null) {
                            worlds.get(currentWorld).addToCurrentLevelChecked(currentLevelObject);
                            currentLevelObject = null;
                        } else if (currentDrawingMode == RECTANGLE || currentDrawingMode == DIAMOND) {
                            worlds.get(currentWorld).applyGrid();
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
        if (currentDrawingMode == POINT) {
            currentLevelObject = null;
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int key = ke.getKeyCode();
        if (!worlds.isEmpty()) {
            if (key == KeyEvent.VK_PAGE_UP) {
                worlds.get(currentWorld).chengleLevel(UP);
            } else if (key == KeyEvent.VK_PAGE_DOWN) {
                worlds.get(currentWorld).chengleLevel(DOWN);
            } else if (key == KeyEvent.VK_UP) {
                worlds.get(currentWorld).moveItems(0, 1);
            } else if (key == KeyEvent.VK_RIGHT) {
                worlds.get(currentWorld).moveItems(-1, 0);
            } else if (key == KeyEvent.VK_DOWN) {
                worlds.get(currentWorld).moveItems(0, -1);
            } else if (key == KeyEvent.VK_LEFT) {
                worlds.get(currentWorld).moveItems(1, 0);
            } else if (key == KeyEvent.VK_P) {
                uiManager.changePaintingMode(ke.isControlDown() ? DOWN : UP);
            } else if (key == KeyEvent.VK_H) {
                worlds.get(currentWorld).get(currentLevel).switchVisibility();
            } else if (key == KeyEvent.VK_DELETE) {
                removeCurrentWorld();
            } else if (key == KeyEvent.VK_L) {
                worlds.get(currentWorld).findFirstItem();
            }
        }
        if (ke.isControlDown()) {
            if (!worlds.isEmpty()) {
                if (key == KeyEvent.VK_Z) {
                    worlds.get(currentWorld).Do(UNDO);
                } else if (key == KeyEvent.VK_Y) {
                    worlds.get(currentWorld).Do(REDO);
                } else if (key == KeyEvent.VK_S) {
                    if (ke.isShiftDown()) {
                        saveAll();
                    } else if (worlds.size() > 0) {
                        worlds.get(currentWorld).save();
                    }
                } else if (key == KeyEvent.VK_TAB) {
                    switchWorld(ke.isShiftDown() ? DOWN : UP);
                } else if (key == KeyEvent.VK_F4) {
                    closeTab(currentWorld);
                }
            }
            if (key == KeyEvent.VK_N) {
                addWorld();
            } else if (key == KeyEvent.VK_O) {
                openWindow.display(worlds, this);
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        Point actualPoint = ((JFrame) mwe.getSource()).getContentPane().getMousePosition();
        if (actualPoint != null) {
            if (mouseIsInMain(actualPoint)) {
                int amount = -mwe.getWheelRotation();
                if (mwe.isControlDown()) {
                    worlds.get(currentWorld).moveItems(amount, 0);
                } else {
                    worlds.get(currentWorld).moveItems(0, amount);
                }
            } else if (mouseIsInMainMenu(actualPoint)) {
                uiManager.scrollMainMenu(mwe.getWheelRotation() * 15);
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
        uiManager.closeAllRightClickMenus();
    }

    @Override
    public void componentResized(ComponentEvent ce) {
        uiManager.closeAllRightClickMenus();
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
