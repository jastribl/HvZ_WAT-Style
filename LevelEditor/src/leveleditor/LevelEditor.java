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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.awt.Graphics2D;
import javax.swing.JFrame;

public class LevelEditor extends JFrame implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {

    private final Image memoryImage;
    private final Graphics memoryGraphics;
    private final int numberIfItems = 9, screenWidth, screenHeight, itemWidth = 64, itemHeight = itemWidth, levelOffset = itemWidth / 4, menuWidth = itemWidth * 4;
    private final ItemHandler itemHandler = new ItemHandler(itemWidth, itemHeight, numberIfItems);
    private Item currentLevelObject;
    private int currentItemType = 0, currentLevel = 0;
    private boolean levelUpKeyIsDown = false, levelDownKeyIsDown = false, saveIsDown = false;

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
        currentLevelObject = null;
        setVisible(true);
        memoryImage = createImage(screenWidth, screenHeight);
        memoryGraphics = (Graphics2D) memoryImage.getGraphics();
        openLevel();
    }

    public static void main(String[] args) {
        LevelEditor levelEditor = new LevelEditor();
    }

    private void saveLevel() {
        int minX = 999999, minY = 999999, maxX = -999999, maxY = -999999;
        for (Level layer : itemHandler.getWorld()) {
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
        String levelText = String.valueOf((maxX - minX) + itemWidth) + " " + String.valueOf((maxY - minY) + itemHeight) + "\n" + String.valueOf(itemHandler.getWorld().size()) + "\n";
        for (Level level : itemHandler.getWorld()) {
            levelText += String.valueOf(level.size()) + "\n";
            for (Item item : level) {
//                int trans = 1;
//                if (i == 0) {
//                    trans = 0;
//                }
                int trans = 0;
                levelText += String.valueOf(item.getType()) + " " + String.valueOf(item.getX() - minX) + " " + String.valueOf(item.getY() - minY) + " " + String.valueOf(trans) + "\n";
            }
        }
        File file = new File("level.txt");
        try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
            output.write(levelText);
        } catch (IOException ex) {
        }
    }

    private void openLevel() {
        try {
            Scanner reader = new Scanner(Paths.get("level.txt"));
            reader.nextInt();
            reader.nextInt();
            int numberOfLevels = reader.nextInt();
            for (int i = 0; i < numberOfLevels; i++) {
                itemHandler.getWorld().add(new Level());
                int numberOfBlocks = reader.nextInt();
                for (int j = 0; j < numberOfBlocks; j++) {
                    int type = reader.nextInt();
                    Point point = snapToLocation(new Point(reader.nextInt(), reader.nextInt()));
                    reader.nextInt();
                    itemHandler.getWorld().get(itemHandler.getWorld().size() - 1).addItem(new Item(point.x + menuWidth, point.y, itemWidth, itemHeight, type));
                }
            }
        } catch (IOException ex) {
            itemHandler.getWorld().add(new Level());
        }
        drawGame();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawGame();
    }

    public final void drawGame() {
        memoryGraphics.setColor(Color.black);
        memoryGraphics.fillRect(menuWidth, 0, screenWidth - menuWidth, screenHeight);
        boolean printedLive = null == currentLevelObject;
        for (int i = 0; i < itemHandler.getWorld().size(); i++) {
            if (!printedLive && itemHandler.getWorld().get(i).size() == 0) {
                itemHandler.drawItem(memoryGraphics, currentLevelObject);
                printedLive = true;
            } else {
                for (int j = 0; j < itemHandler.getWorld().get(i).size(); j++) {
                    if (!printedLive && currentLevel == i && currentLevelObject.getY() < itemHandler.getWorld().get(i).get(j).getY()) {
                        itemHandler.drawItem(memoryGraphics, currentLevelObject);
                        printedLive = true;
                    }
                    if (currentLevel == i) {
                        itemHandler.drawItem(memoryGraphics, itemHandler.getWorld().get(i).get(j));
                    } else {
                        itemHandler.drawFaddedItem(memoryGraphics, itemHandler.getWorld().get(i).get(j));
                    }
                }
            }
            if (!printedLive && currentLevel == i) {
                itemHandler.drawItem(memoryGraphics, currentLevelObject);
                printedLive = true;
            }
            memoryGraphics.setColor(Color.red);
        }
        memoryGraphics.setColor(Color.gray);
        memoryGraphics.fillRect(0, 0, menuWidth, screenHeight);
        memoryGraphics.setColor(Color.white);
        memoryGraphics.drawLine(menuWidth, 0, menuWidth, screenHeight);
        itemHandler.drawMenu(memoryGraphics);
        memoryGraphics.drawString("Level: " + String.valueOf(currentLevel), screenWidth - 60, screenHeight - 10);
        getGraphics().drawImage(memoryImage, 0, 0, this);
        getGraphics().dispose();
    }

    private Point snapToLocation(Point p) {
        int yy = p.y / levelOffset * levelOffset + (itemHeight / 2);
        if ((yy / levelOffset) % 2 == 0) {
            return new Point(((p.x + (itemWidth / 2)) / itemWidth * itemWidth), yy);
        } else {
            return new Point((p.x / itemWidth * itemWidth) + (itemWidth / 2), yy);
        }
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        Point point = snapToLocation(me.getLocationOnScreen());;
        if (point.x > menuWidth) {
            if (me.isShiftDown()) {
                itemHandler.getWorld().get(currentLevel).addItem(new Item(point.x, point.y, itemWidth, itemHeight, currentItemType));
            } else {
                if (currentLevelObject != null) {
                    currentLevelObject.setLocationAndFix(point);
                } else {
                    return;
                }
            }
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
        Point point = me.getLocationOnScreen();
        if (point.x > menuWidth) {
            for (int i = itemHandler.getWorld().get(currentLevel).size() - 1; i >= 0; i--) {
                Item object = itemHandler.getWorld().get(currentLevel).get(i);
                Rectangle rectangle = new Rectangle(object.getX(), object.getY(), object.getWidth(), object.getHeight());
                if (rectangle.contains(point)) {
                    currentLevelObject = object;
                    itemHandler.getWorld().get(currentLevel).removeItem(i);
                    foundOne = true;
                    break;
                }
            }
            if (!foundOne) {
                point = snapToLocation(point);
                currentLevelObject = new Item(point.x, point.y, itemWidth, itemHeight, currentItemType);
            }
        } else {
            int type = itemHandler.getSelectedMenuType(point);
            if (type >= 0) {
                currentItemType = type;
                Item item = new Item(point.x, point.y, itemWidth, itemHeight, type);
                currentLevelObject = item;
                currentLevelObject.setLocationAndFix(point);
            }
        }
        drawGame();
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (currentLevelObject != null) {
            if (me.getLocationOnScreen().x > menuWidth) {
                itemHandler.getWorld().get(currentLevel).addItem(currentLevelObject);
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
            moveAll(0, 1);
        } else if (key == KeyEvent.VK_RIGHT) {
            moveAll(-1, 0);
        } else if (key == KeyEvent.VK_DOWN) {
            moveAll(0, -1);
        } else if (key == KeyEvent.VK_LEFT) {
            moveAll(1, 0);
        } else if (key == KeyEvent.VK_S && ke.isControlDown()) {
            saveIsDown = true;
        } else {
            return;
        }
        drawGame();
    }

    private void moveAll(int x, int y) {
        for (Level level : itemHandler.getWorld()) {
            for (Item object : level) {
                object.shiftLocation(x * itemWidth, y * itemHeight / 2);
            }
        }
        drawGame();
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        int key = ke.getKeyCode();
        if (key == 107 && levelUpKeyIsDown) {
            levelUpKeyIsDown = false;
            if (currentLevel + 1 == itemHandler.getWorld().size()) {
                itemHandler.getWorld().add(new Level());
            }
            currentLevel++;
            drawGame();
        } else if (key == 109 && levelDownKeyIsDown) {
            levelDownKeyIsDown = false;
            if (currentLevel > 0) {
                if (itemHandler.getWorld().get(currentLevel).size() == 0) {
                    itemHandler.getWorld().remove(currentLevel);
                }
                currentLevel--;
            }
            drawGame();
        } else if (key == KeyEvent.VK_S && ke.isControlDown() && saveIsDown == true) {
            saveIsDown = false;
            saveLevel();
        } else {
            return;
        }
        drawGame();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        if (mwe.getX() < menuWidth) {
            itemHandler.scroll(mwe.getWheelRotation());
            drawGame();
        }
    }
}
