package leveleditor;

import UI.*;
import Structures.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static leveleditor.Globals.*;

public final class LevelEditor extends JFrame implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener, WindowListener, ComponentListener {

    private final Image memoryImage;
    private boolean canDraw = false;
    private final OpenWindow openWindow = new OpenWindow(this);
    private final RightClickManager rightClickManager = new RightClickManager();
    private final MainMenu mainMenu = new MainMenu();

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
            bottomMenu.draw();
            mainMenu.draw();
            worldTabWidth = (screenWidth - menuWidth) / worlds.size();
            menuTabWidth = screenHeight / numberOfMenuGroups;
        } else {
            worldTabWidth = 0;
            menuTabWidth = 0;
        }
        getContentPane().getGraphics().drawImage(memoryImage, 0, 0, null);
        getContentPane().getGraphics().dispose();
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

    private boolean mouseIsInMenuObjects(Point point) {
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
                Point snapedPoint = snapToGrid(actualPoint);
                if (currentDrawingMode == PAINT && actualPoint != null && mouseIsInMain(actualPoint)) {
                    BaseObject newObject = null;
                    if (currentGroup == BLOCK) {
                        newObject = new Block(currentGroup, currentType, snapedPoint);
                    } else if (currentGroup == SPECIAL) {
                        if (currentType == PORTAL) {
                            newObject = new Portal(currentGroup, currentType, snapedPoint, "");
                        }
                    }
                    if (SwingUtilities.isRightMouseButton(me)) {
                        worlds.get(currentWorld).removeFromCurrentLevelChecked(newObject);
                    } else if (SwingUtilities.isLeftMouseButton(me)) {
                        worlds.get(currentWorld).addToCurrentLevelChecked(newObject);
                    }
                } else if (currentDrawingMode == POINT && currentLevelObject != null && SwingUtilities.isLeftMouseButton(me)) {
                    currentLevelObject.setLocation(snapedPoint);
                } else if ((currentDrawingMode == RECTANGLE || currentDrawingMode == DIAMOND) && gridStart != null) {
                    if (snapedPoint != null) {
                        gridEnd = snapedPoint;
                    }
                    populateGrid();
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
        rightClickManager.closeAll();
        if (worlds.size() > 0 && worlds.get(currentWorld).get(currentLevel).isVisible()) {
            Point actualPoint = ((JFrame) me.getSource()).getContentPane().getMousePosition();
            if (actualPoint != null) {
                Point snapedPoint = snapToGrid(actualPoint);
                if (mouseIsInMain(actualPoint)) {
                    BaseObject newObject = null;
                    if (currentGroup == BLOCK) {
                        newObject = new Block(currentGroup, currentType, snapedPoint);
                    } else if (currentGroup == SPECIAL) {
                        if (currentType == PORTAL) {
                            newObject = new Portal(currentGroup, currentType, snapedPoint, "");
                        }
                    }
                    if (SwingUtilities.isRightMouseButton(me)) {
                        if (currentDrawingMode == PAINT || currentDrawingMode == POINT) {
                            worlds.get(currentWorld).removeFromCurrentLevelChecked(newObject);
                        } else if (currentDrawingMode == RECTANGLE || currentDrawingMode == DIAMOND) {
                            gridStart = snapedPoint;
                            drawingGrid = false;
                        }
                    } else if (SwingUtilities.isLeftMouseButton(me)) {
                        if (currentDrawingMode == PAINT) {
                            worlds.get(currentWorld).addToCurrentLevelChecked(newObject);
                        } else if (currentDrawingMode == POINT) {
                            BaseObject object = worlds.get(currentWorld).getFromCurrentLevel(newObject);
                            if (object == null) {
                                currentLevelObject = newObject;
                            } else {
                                currentType = object.getType();
                                currentLevelObject = object.DeepCopy();
                            }
                        } else if (currentDrawingMode == RECTANGLE || currentDrawingMode == DIAMOND) {
                            gridStart = snapedPoint;
                            drawingGrid = true;
                        }
                    }
                } else if (mouseIsInTabs(actualPoint)) {
                    currentWorld = (worlds.isEmpty() ? 0 : (actualPoint.x - menuWidth) / worldTabWidth);
                } else if (mouseIsInMainMenu(actualPoint)) {
                    if (mouseIsInMenuTabs(actualPoint)) {
                        mainMenu.selectMenuTabAt(actualPoint);
                    } else if (mouseIsInMenuObjects(actualPoint)) {
                        mainMenu.setCurrentTypeToObjectAt(actualPoint);
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
                        rightClickManager.showTabMenu(me.getLocationOnScreen());
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
                worlds.get(currentWorld).moveAll(0, 1);
            } else if (key == KeyEvent.VK_RIGHT) {
                worlds.get(currentWorld).moveAll(-1, 0);
            } else if (key == KeyEvent.VK_DOWN) {
                worlds.get(currentWorld).moveAll(0, -1);
            } else if (key == KeyEvent.VK_LEFT) {
                worlds.get(currentWorld).moveAll(1, 0);
            } else if (key == KeyEvent.VK_P) {
                if (currentGroup == SPECIAL) {
                    bottomMenu.setDrawingMode(POINT);
                } else {
                    bottomMenu.changeDrawingMode(ke.isControlDown() ? DOWN : UP);
                }
            } else if (key == KeyEvent.VK_H) {
                worlds.get(currentWorld).get(currentLevel).switchVisibility();
            } else if (key == KeyEvent.VK_DELETE) {
                removeCurrentWorld();
            } else if (key == KeyEvent.VK_L) {
                worlds.get(currentWorld).findFirstObject();
            }
        }
        if (ke.isControlDown()) {
            if (!worlds.isEmpty()) {
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
                    if (worlds.size() > 0) {
                        if (!ke.isShiftDown()) {
                            currentWorld = (currentWorld + 1) % (worlds.size());
                        } else {
                            currentWorld += (currentWorld == 0 ? worlds.size() - 1 : -1);
                        }
                    }
                } else if (key == KeyEvent.VK_F4) {
                    closeTab(currentWorld);
                }
            }
            if (key == KeyEvent.VK_N) {
                addWorld();
            } else if (key == KeyEvent.VK_O) {
                openWindow.display(this);
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
                    worlds.get(currentWorld).moveAll(amount, 0);
                } else {
                    worlds.get(currentWorld).moveAll(0, amount);
                }
            } else if (mouseIsInMainMenu(actualPoint)) {
                mainMenu.scroll(mwe.getWheelRotation() * 15);
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
        rightClickManager.closeAll();
    }

    @Override
    public void componentResized(ComponentEvent ce) {
        rightClickManager.closeAll();
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
