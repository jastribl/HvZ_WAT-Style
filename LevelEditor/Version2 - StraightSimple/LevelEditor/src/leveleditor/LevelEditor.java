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

public class LevelEditor extends JFrame implements MouseMotionListener, MouseListener, KeyListener, MouseWheelListener {

    private final Image memoryImage;
    private final Graphics memoryGraphics;
    private final int screenWidth, screenHeight;
    private final ArrayList<Level> levels;
    private LevelObject currentLevelObject;
    private int currentLevel = 0;
    private boolean levelUpKeyIsDown = false, levelDownKeyIsDown = false;
    private int grideWidth = 64, grideHeight, angle = 60;
    private final ObjectMenu menu = new ObjectMenu(grideWidth, angle);

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
        currentLevelObject = new LevelObject(grideWidth, angle);
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
                currentLevelObject.draw(memoryGraphics, true);
                printedLive = true;
            } else {
                for (int j = 0; j < levels.get(i).size(); j++) {
                    if (currentLevelObject != null && !printedLive && currentLevel == i && currentLevelObject.getY() < levels.get(i).get(j).getY()) {
                        currentLevelObject.draw(memoryGraphics, true);
                        printedLive = true;
                    }
                    levels.get(i).get(j).draw(memoryGraphics, currentLevel == i);
                }
            }
            if (currentLevelObject != null && !printedLive && currentLevel == i) {
                currentLevelObject.draw(memoryGraphics, true);
                printedLive = true;
            }
        }
        memoryGraphics.setColor(Color.gray);
        memoryGraphics.fillRect(0, 0, screenWidth / 5, screenHeight);
        memoryGraphics.setColor(Color.white);
        memoryGraphics.drawLine(screenWidth / 5, 0, screenWidth / 5, screenHeight);
        memoryGraphics.drawString("Level: " + String.valueOf(currentLevel), screenWidth - 50, screenHeight - 10);
        menu.draw(memoryGraphics);
        getGraphics().drawImage(memoryImage, 0, 0, this);
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (currentLevelObject != null) {
            Point location = me.getLocationOnScreen();
            int num = (int) (angle / 90.0 * grideWidth);
            currentLevelObject.setLocation(new Point(location.x / grideWidth * grideWidth, location.y / num * num));
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
                LevelObject object = levels.get(currentLevel).get(i);
                Rectangle rectangle = new Rectangle(object.getX(), object.getY(), object.getUnit(), object.getUnit());
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
                LevelObject temp = menu.getSelectedMenuItem(location);
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
            for (LevelObject object : level) {
                object.setLocation(new Point(object.getX() + (x * xShift), object.getY() + (y * yShift)));
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
            if (currentLevel == 0) {
                levels.add(0, new Level());
            } else {
                currentLevel--;
            }
            drawGame();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        angle += (int) mwe.getPreciseWheelRotation();
        if (angle > 80) {
            angle = 80;
        } else if (angle < 10) {
            angle = 10;
        }
        menu.zoomTo(angle);
        for (int i = 0; i < levels.size(); i++) {
            for (int j = 0; j < levels.get(i).size(); j++) {
                ((LevelObject) levels.get(i).get(j)).zoomTo(angle);
            }
        }
        drawGame();
    }
}
