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
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class LevelEditor extends JFrame implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {
    
    private final Image memoryImage;
    private final Graphics memoryGraphics;
    private final int screenWidth, screenHeight;
    private final ArrayList<Level> levels;
    private final int imageWidth = 64, imageHeight = imageWidth, imageOffset = imageWidth / 4, menuWidth = imageWidth * 4;
    private final ObjectMenu menu = new ObjectMenu(imageWidth, imageHeight);
    private Item currentLevelObject;
    private int currentItemType = 0, currentLevel = 0;
    private boolean levelUpKeyIsDown = false, levelDownKeyIsDown = false;
    private boolean shiftKeyIsDown = false, dragging = false;
    private Point startDragLocation, endDragLocation;
    private boolean saveIsDown = false;
    private boolean showingAll = true;
    
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
        levels = new ArrayList();
        setVisible(true);
        memoryImage = createImage(screenWidth, screenHeight);
        memoryGraphics = memoryImage.getGraphics();
        openLevel();
    }
    
    public static void main(String[] args) {
        LevelEditor levelEditor = new LevelEditor();
    }
    
    private void saveLevel() {
        int minX = 999999, minY = 999999, maxX = -999999, maxY = -999999;
        for (Level layer : levels) {
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
        String levelText = String.valueOf(levels.size()) + "\n";
        Image saveImage = createImage((maxX - minX) + imageWidth, (maxY - minY) + imageHeight);
        final Image itemImage = new ImageIcon(getClass().getResource("/media/" + "boxBounding" + ".png")).getImage();//.getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
        Graphics saveGraphics = saveImage.getGraphics();
        saveGraphics.setColor(Color.white);
        saveGraphics.fillRect(0, 0, saveImage.getWidth(null), saveImage.getHeight(null));
        for (int i = 0; i < levels.size(); i++) {
            levelText += String.valueOf(levels.get(i).size()) + "\n";
            for (Item item : levels.get(i)) {
                int trans = 1;
                if (i == 0) {
                    trans = 0;
                }
                levelText += String.valueOf(item.getType()) + " " + String.valueOf(item.getX() - minX) + " " + String.valueOf(item.getY() - minY) + " " + String.valueOf(trans) + "\n";
                if (i == 1) {
                    saveGraphics.drawImage(itemImage, item.getX() - minX, item.getY() - minY, null);
                }
            }
        }
        File file = new File("level.txt");
        try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
            output.write(levelText);
        } catch (IOException ex) {
        }
        try {
            ImageIO.write((RenderedImage) saveImage, "png", new File("test.png"));
        } catch (IOException e) {
            System.out.println("error");
        }
    }
    
    private void openLevel() {
        try {
            Scanner reader = new Scanner(Paths.get("level.txt"));
            int numberOfLevels = reader.nextInt();
            for (int i = 0; i < numberOfLevels; i++) {
                levels.add(new Level());
                int numberOfBlocks = reader.nextInt();
                for (int j = 0; j < numberOfBlocks; j++) {
                    int type = reader.nextInt();
                    Point point = fixLocation(new Point(reader.nextInt(), reader.nextInt()));
                    int trans = reader.nextInt();
                    levels.get(levels.size() - 1).addObject(new Item(point.x + menuWidth, point.y, imageWidth, imageHeight, type));
                }
            }
        } catch (IOException ex) {
            levels.add(new Level());
        }
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawGame();
    }
    
    public final void drawGame() {
        memoryGraphics.setColor(Color.black);
        memoryGraphics.fillRect(menuWidth, 0, screenWidth - menuWidth, screenHeight);
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
            memoryGraphics.setColor(Color.red);
            if (shiftKeyIsDown && dragging) {
                memoryGraphics.fillOval(startDragLocation.x - 5, startDragLocation.y - 5, 10, 10);
                memoryGraphics.fillOval(endDragLocation.x - 5, endDragLocation.y - 5, 10, 10);
            }
        }
        memoryGraphics.setColor(Color.gray);
        memoryGraphics.fillRect(0, 0, menuWidth, screenHeight);
        memoryGraphics.setColor(Color.white);
        memoryGraphics.drawLine(menuWidth, 0, menuWidth, screenHeight);
        menu.draw(memoryGraphics);
        memoryGraphics.drawString("Level: " + String.valueOf(currentLevel), screenWidth - 60, screenHeight - 10);
        getGraphics().drawImage(memoryImage, 0, 0, this);
        getGraphics().dispose();
    }
    
    private Point fixLocation(Point p) {
        int yy = p.y / imageOffset * imageOffset + imageWidth / (imageWidth / 2);
        if ((p.y / imageOffset) % 2 == 0) {
            return new Point(((p.x + (imageWidth / 2)) / imageWidth * imageWidth), yy);
        } else {
            return new Point((p.x / imageWidth * imageWidth) + (imageWidth / 2), yy);
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent me) {
        if (currentLevelObject != null) {
            Point location = me.getLocationOnScreen();
            currentLevelObject.setLocationAndFix(fixLocation(location));
            drawGame();
            if (shiftKeyIsDown) {
                endDragLocation = fixLocation(location);
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
        dragging = true;
        boolean foundOne = false;
        Point location = me.getLocationOnScreen();
        if (location.x > menuWidth) {
            if (shiftKeyIsDown) {
                startDragLocation = fixLocation(location);
                endDragLocation = fixLocation(location);
            }
            for (int i = levels.get(currentLevel).size() - 1; i >= 0; i--) {
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
                Point temp = fixLocation(location);
                currentLevelObject = new Item(temp.x, temp.y, imageWidth, imageHeight, currentItemType);
            }
        } else {
            int type = menu.getSelectedMenuType(location);
            if (type >= 0) {
                currentItemType = type;
                Item item = new Item(location.x, location.y, imageWidth, imageHeight, type);
                currentLevelObject = item;
                currentLevelObject.setLocationAndFix(location);
                drawGame();
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent me) {
        dragging = false;
        if (currentLevelObject != null) {
            if (me.getLocationOnScreen().x > menuWidth) {
                levels.get(currentLevel).addObject(currentLevelObject);
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
        } else if (key == KeyEvent.VK_SHIFT && !dragging) {
            shiftKeyIsDown = true;
        } else if (key == KeyEvent.VK_S && ke.isControlDown()) {
            saveIsDown = true;
        }
        drawGame();
    }
    
    private void moveAll(int x, int y) {
        for (Level level : levels) {
            for (Item object : level) {
                object.shiftLocation(x * imageWidth, y * imageHeight / 2);
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
                if (levels.get(currentLevel).size() == 0) {
                    levels.remove(currentLevel);
                }
                currentLevel--;
            }
            drawGame();
        } else if (key == KeyEvent.VK_SHIFT) {
            shiftKeyIsDown = false;
        } else if (key == KeyEvent.VK_S && ke.isControlDown() && saveIsDown == true) {
            saveIsDown = false;
            saveLevel();
        }
        drawGame();
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        if (mwe.getX() < menuWidth) {
            menu.scroll(mwe.getWheelRotation());
            drawGame();
        }
    }
}
