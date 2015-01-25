package leveleditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JFrame;

public class LevelEditor extends JFrame implements MouseMotionListener, MouseListener, KeyListener, MouseWheelListener {

    private final Image memoryImage;
    private final Graphics memoryGraphics;
    private final int screenWidth, screenHeight;
    private Item currentObject;
    private int currentLayer = 0;
    private World world = new World();
    private int scale = 64, angle = 50, heighttt = 15;
    private final ObjectMenu menu = new ObjectMenu(scale, angle);
    private boolean canDraw = false;
    private boolean controlDown = false;

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
        currentObject = new Item(scale, angle);
        setVisible(true);
        memoryImage = createImage(screenWidth, screenHeight);
        memoryGraphics = memoryImage.getGraphics();
        world.get(0).clear();
        for (int j = 1; j < heighttt; j++) {
            for (int i = 2; i < 13; i++) {
                world.get(0).addItem(i, j, new Item(scale, angle));
            }
        }
        canDraw = true;
        drawGame();
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
        if (canDraw) {
            memoryGraphics.setColor(Color.black);
            memoryGraphics.fillRect(screenWidth / 5, 0, screenWidth - screenWidth / 5, screenHeight);
            for (Level level : world) {
                for (Item item : level) {
                    item.draw(memoryGraphics, true, screenWidth / 5, 0, scale, angle);
                }
            }
//        boolean printedLive = false;
//        for (int i = 0; i < levels.size(); i++) {
//            if (currentObject != null && !printedLive && levels.get(i).size() == 0) {
//                currentObject.draw(memoryGraphics, true);
//                printedLive = true;
//            } else {
//                for (int j = 0; j < levels.get(i).size(); j++) {
//                    if (currentObject != null && !printedLive && currentLayer == i && currentObject.getY() < levels.get(i).get(j).getY()) {
//                        currentObject.draw(memoryGraphics, true);
//                        printedLive = true;
//                    }
//                    levels.get(i).get(j).draw(memoryGraphics, currentLayer == i);
//                }
//            }
//            if (currentObject != null && !printedLive && currentLayer == i) {
//                currentObject.draw(memoryGraphics, true);
//                printedLive = true;
//            }
//        }
            memoryGraphics.setColor(Color.gray);
            memoryGraphics.fillRect(0, 0, screenWidth / 5, screenHeight);
            memoryGraphics.setColor(Color.white);
            memoryGraphics.drawLine(screenWidth / 5, 0, screenWidth / 5, screenHeight);
            memoryGraphics.drawString("Level: " + String.valueOf(currentLayer), screenWidth - 50, screenHeight - 10);
            menu.draw(memoryGraphics);
            memoryGraphics.setColor(Color.white);
            memoryGraphics.drawString("Angle: " + String.valueOf(angle), screenWidth - 70, 30);
            getGraphics().drawImage(memoryImage, 0, 0, this);
        }
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (currentObject != null) {
            Point location = me.getLocationOnScreen();
            int num = (int) (angle / 90.0 * scale);
            currentObject.setLocation((location.x - (screenWidth / 5)) / scale * scale + (screenWidth / 5), location.y / num * num);
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
//            for (int i = 0; i < levels.get(currentLayer).size(); i++) {
//                Item object = levels.get(currentLayer).get(i);
//                Rectangle rectangle = new Rectangle(object.getX(), object.getY(), object.getUnit(), object.getUnit());
//                if (rectangle.contains(location)) {
//                    currentObject = object;
//                    levels.get(currentLayer).removeObject(i);
//                    drawGame();
//                    foundOne = true;
//                    break;
//                }
//            }
//            if (!foundOne) {
//                currentObject = null;
//            }
        } else {
            try {
                Item temp = menu.getSelectedMenuItem(location);
                if (temp != null) {
                    currentObject = temp;
                    currentObject.setLocation(location.x, location.y);
                    drawGame();
                }
            } catch (CloneNotSupportedException ex) {
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
//        Point location = me.getLocationOnScreen();
//        if (currentObject != null) {
//            if (location.x > screenWidth / 5) {
//                levels.get(currentLayer).addObject(currentObject);
//                levels.get(currentLayer).sort();
//            }
//            currentObject = null;
//            drawGame();
//        }
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
        if (key == KeyEvent.VK_UP) {
            moveAll(0, -1);
        } else if (key == KeyEvent.VK_RIGHT) {
            moveAll(1, 0);
        } else if (key == KeyEvent.VK_DOWN) {
            moveAll(0, 1);
        } else if (key == KeyEvent.VK_LEFT) {
            moveAll(-1, 0);
        } else if (key == KeyEvent.VK_CONTROL) {
            controlDown = true;
        }
    }

    private void moveAll(int x, int y) {
        for (int i = 0; i < world.size(); i++) {
            for (int j = 0; j < world.get(i).size(); j++) {
                world.get(i).get(j).setLocation(world.get(i).get(j).getX() + x, world.get(i).get(j).getY() + y);
            }
        }
        drawGame();
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        int key = ke.getKeyCode();
        if (key == KeyEvent.VK_CONTROL && controlDown) {
            controlDown = false;
        }
//        if (key == 107) {
//            if (currentLayer + 1 == levels.size()) {
//                levels.add(new Level());
//            }
//            currentLayer++;
//            drawGame();
//        } else if (key == 109) {
//            if (currentLayer == 0) {
//                levels.add(0, new Level());
//            } else {
//                currentLayer--;
//            }
//            drawGame();
//        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        if (controlDown) {
            angle += (int) mwe.getPreciseWheelRotation();
            if (angle > 70) {
                angle = 70;
            } else if (angle < 30) {
                angle = 30;
            }
            drawGame();
        } else {
            scale += (int) mwe.getPreciseWheelRotation();
            if (scale > 128) {
                scale = 128;
            } else if (scale < 16) {
                scale = 16;
            }
            drawGame();
        }
    }
}
