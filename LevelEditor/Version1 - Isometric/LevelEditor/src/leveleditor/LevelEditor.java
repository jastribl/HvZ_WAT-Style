package leveleditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import javax.swing.JFrame;

public class LevelEditor extends JFrame implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {

    private final Image memoryImage;
    private final Graphics memoryGraphics;
    private final int screenWidth, screenHeight;
    private final ArrayList<Level> levels;
    private Item currentLevelObject;
    private final ObjectMenu menu = new ObjectMenu();
    private int currentLevel = 0;
    private boolean levelUpKeyIsDown = false, levelDownKeyIsDown = false;

    LevelEditor() {
        setTitle("LevelUpGame - 2015 - Justin Stribling");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
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
        currentLevelObject = new Item(0, 0, 0);
        levels = new ArrayList();
        levels.add(new Level());
        setVisible(true);
        memoryImage = createImage(screenWidth, screenHeight);
        memoryGraphics = memoryImage.getGraphics();
    }

    public static void main(String[] args) {
        LevelEditor levelEditor = new LevelEditor();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawGame();
    }

    public final void drawGame() {
        memoryGraphics.setColor(Color.black);
        memoryGraphics.fillRect(screenWidth / 5, 0, screenWidth - screenWidth / 5, screenHeight);
        boolean printedLive = false;
        for (int i = 0; i < levels.size(); i++) {
            if (currentLevelObject != null && !printedLive && levels.get(i).size() == 0) {
                currentLevelObject.draw(memoryGraphics);
                printedLive = true;
            } else {
                for (int j = 0; j < levels.get(i).size(); j++) {
                    if (currentLevelObject != null && !printedLive && currentLevel == i && currentLevelObject.getY() < levels.get(i).get(j).getY()) {
                        currentLevelObject.draw(memoryGraphics);
                        printedLive = true;
                    }
                    if (currentLevel == i) {
                        levels.get(i).get(j).draw(memoryGraphics);
                    } else {
                        levels.get(i).get(j).drawFadded(memoryGraphics);
                    }
                }
            }
            if (currentLevelObject != null && !printedLive && currentLevel == i) {
                currentLevelObject.draw(memoryGraphics);
                printedLive = true;
            }
        }
        memoryGraphics.setColor(Color.gray);
        memoryGraphics.fillRect(0, 0, screenWidth / 5, screenHeight);
        memoryGraphics.setColor(Color.white);
        memoryGraphics.drawLine(screenWidth / 5, 0, screenWidth / 5, screenHeight);
        menu.draw(memoryGraphics);
        memoryGraphics.drawString("Level: " + String.valueOf(currentLevel), screenWidth - 60, screenHeight - 10);
        getGraphics().drawImage(memoryImage, 0, 0, this);
        getGraphics().dispose();
    }

    private Point fixLocation(int a, int b, int mod) {
        int xx, yy;
        xx = Math.round(b / mod - a / mod);
        yy = Math.round(b / mod + a / mod);
        return new Point((yy - xx) / 2 * mod, (yy + xx) / 2 * mod);
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        //32 needs to become item width or height /2
//        System.out.println((me.getX() / 32 + me.getY() / 32) / 2);
//        System.out.println(me.getY() / 32 - (me.getX() / 32) / 32);
        if (currentLevelObject != null) {
            Point location = me.getLocationOnScreen();
            currentLevelObject.setLocation(fixLocation(location.x, location.y, 15));
            drawGame();
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
        boolean foundOne = false;
        Point location = me.getLocationOnScreen();
        if (location.x > screenWidth / 5) {
            for (int i = 0; i < levels.get(currentLevel).size(); i++) {
                Item object = levels.get(currentLevel).get(i);
                Rectangle rectangle = new Rectangle(object.getX(), object.getY(), object.getWidth(), object.getHeight());
                if (rectangle.contains(location)) {
                    currentLevelObject = object;
                    levels.get(currentLevel).removeObject(i);
                    drawGame();
                    foundOne = true;
                    break;
                }
            }
            if (!foundOne) {
                currentLevelObject = null;
            }
        } else {
            try {
                Item temp = menu.getSelectedMenuItem(location);
                if (temp != null) {
                    currentLevelObject = temp;
                    currentLevelObject.setLocation(location);
                    drawGame();
                }
            } catch (CloneNotSupportedException ex) {
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        Point location = me.getLocationOnScreen();
        if (currentLevelObject != null) {
            if (location.x > screenWidth / 5) {
                levels.get(currentLevel).addObject(currentLevelObject);
                levels.get(currentLevel).sort();
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
            moveAll(0, -1);
        } else if (key == KeyEvent.VK_RIGHT) {
            moveAll(1, 0);
        } else if (key == KeyEvent.VK_DOWN) {
            moveAll(0, 1);
        } else if (key == KeyEvent.VK_LEFT) {
            moveAll(-1, 0);
        }
    }

    private void moveAll(int x, int y) {
        int xShift = 60, yShift = 30;
        for (Level level : levels) {
            for (Item object : level) {
                object.setX(object.getX() + (x * xShift));
                object.setY(object.getY() + (y * yShift));
            }
        }
        drawGame();
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        int key = ke.getKeyCode();
        if (key == 107 && levelUpKeyIsDown) {
            levelUpKeyIsDown = false;
            if (currentLevel + 1 == levels.size()) {
                levels.add(new Level());
            }
            currentLevel++;
            drawGame();
        } else if (key == 109 && levelDownKeyIsDown) {
            levelDownKeyIsDown = false;
            if (currentLevel > 0) {
//                if (levels.get(currentLevel).size() == 0) {
//                    levels.remove(currentLevel);
//                    System.out.println("deleated");
//                }
                currentLevel--;
            }
            drawGame();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        if (mwe.getX() < screenWidth / 5) {
            menu.scroll(mwe.getWheelRotation());
            drawGame();
        }
    }
}
