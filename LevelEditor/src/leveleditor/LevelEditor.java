package leveleditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static leveleditor.Globals.*;

public final class LevelEditor extends JFrame implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener, WindowListener, ComponentListener {

    private final Image memoryImage;
    private final int numberOfPaintingTools = 3;
    private boolean canDraw = false;
    private final OpenWindow openWindow = new OpenWindow(this);
    private final Menu menu = new Menu();

    LevelEditor() {
        setTitle("LevelUpGame - 2015 - Justin Stribling");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setFocusTraversalKeysEnabled(false);
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
        preloadLevelNames();
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
        return (point.x > menuWidth && point.x < screenWidth && point.y > tabHeight && point.y < screenHeight - bottomMenuHeight);
    }

    private boolean mouseIsInSideMenu(Point point) {
        return (point.x > 0 && point.x < menuWidth && point.y > 0 && point.y < screenHeight);
    }

    private boolean mouseIsInBottomMenu(Point point) {
        return (point.x > menuWidth && point.x < screenWidth && point.y > screenHeight - bottomMenuHeight && point.y < screenHeight);
    }

    private boolean mouseIsInTabs(Point point) {
        return (point.x > menuWidth && point.x < screenWidth && point.y > 0 && point.y < tabHeight);
    }

    private boolean mouseIsOnScreenNotInMenu(Point point) {
        return (point.x > menuWidth && point.x < screenWidth && point.y > 0 && point.y < screenHeight);
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (worlds.size() > 0) {
            Point actualPoint = ((JFrame) me.getSource()).getContentPane().getMousePosition();
            if (worlds.get(currentWorld).get(currentLevel).isVisible() && actualPoint != null) {
                Point snapedPoint = snapToGrid(actualPoint);
                if (mouseIsOnScreenNotInMenu(actualPoint)) {
                    if (paintingMode == PAINT) {
                        Item newItem = new Item(snapedPoint, currentItemType, true);
                        if (SwingUtilities.isRightMouseButton(me)) {
                            worlds.get(currentWorld).removeFromLevelChecked(currentLevel, newItem, true);
                        } else if (SwingUtilities.isLeftMouseButton(me)) {
                            worlds.get(currentWorld).addToLevelChecked(currentLevel, newItem, true);
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
        if (worlds.size() > 0 && worlds.get(currentWorld).get(currentLevel).isVisible()) {
            Point actualPoint = ((JFrame) me.getSource()).getContentPane().getMousePosition();
            if (actualPoint != null) {
                Point snapedPoint = snapToGrid(actualPoint);
                if (mouseIsInMain(actualPoint)) {
                    Item newItem = new Item(snapedPoint, currentItemType, true);
                    if (SwingUtilities.isRightMouseButton(me)) {
                        if (paintingMode == PAINT || paintingMode == POINT) {
                            worlds.get(currentWorld).removeFromLevelChecked(currentLevel, newItem, true);
                        } else if (paintingMode == RECTANGLE) {
                            rectangleStart = snapedPoint;
                            drawingRectangle = false;
                        }
                    } else if (SwingUtilities.isLeftMouseButton(me)) {
                        if (paintingMode == PAINT) {
                            worlds.get(currentWorld).addToLevelChecked(currentLevel, newItem, true);
                        } else if (paintingMode == POINT) {
                            Item item = worlds.get(currentWorld).getFromCurrentLevel(newItem);
                            if (item != null) {
                                currentItemType = item.getType();
                                currentLevelObject = item;
                            } else {
                                currentLevelObject = newItem;
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
                        currentLevelObject = new Item(snapedPoint, itemType, true);
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
                        menu.showTabRightClickMenu(me.getLocationOnScreen());
                    }
                } else if (mouseIsInMain(actualPoint)) {
                    if (worlds.get(currentWorld).get(currentLevel).isVisible()) {
                        if (paintingMode == POINT && currentLevelObject != null) {
                            worlds.get(currentWorld).addToLevelChecked(currentLevel, currentLevelObject, true);
                            currentLevelObject = null;
                        } else if (paintingMode == RECTANGLE) {
                            worlds.get(currentWorld).applyRectangle();
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
            worlds.get(currentWorld).applyRectangle();
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int key = ke.getKeyCode();
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
            changePaintingMode();
        } else if (key == KeyEvent.VK_H) {
            hideCurrentLevel();
        } else if (key == KeyEvent.VK_DELETE) {
            removeCurrentWorld();
        }
        if (ke.isControlDown()) {
            if (key == KeyEvent.VK_Z) {
                worlds.get(currentWorld).undo();
            } else if (key == KeyEvent.VK_Y) {
                worlds.get(currentWorld).redo();
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
            } else if (mouseIsInSideMenu(actualPoint)) {
                menu.scroll(mwe.getWheelRotation() * 15);
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
        menu.closeAllRightClickMenus();
    }

    @Override
    public void componentResized(ComponentEvent ce) {
        menu.closeAllRightClickMenus();
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
