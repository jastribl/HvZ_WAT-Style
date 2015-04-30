package leveleditor;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.*;
import javax.swing.*;
import static leveleditor.Globals.*;

public final class LevelEditor extends JFrame implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener, WindowListener, ComponentListener, ActionListener {

    private final Image memoryImage;

    LevelEditor() {
        //load images
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
                imageTracker.addImage(iconImages[i], 1);
            } catch (Exception e) {
            }
        }
        try {
            imageTracker.waitForID(0);
            imageTracker.waitForID(1);
        } catch (InterruptedException e) {
        }
        //set up window
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
        //right click menues
        tabsRightClickMenuItems[0] = new JMenuItem("Close");
        tabsRightClickMenuItems[0].setActionCommand("CloseTab");
        tabsRightClickMenuItems[1] = new JMenuItem("Close All");
        tabsRightClickMenuItems[1].setActionCommand("CloseAllTabs");
        tabsRightClickMenuItems[2] = new JMenuItem("Save");
        tabsRightClickMenuItems[2].setActionCommand("SaveTab");
        tabsRightClickMenuItems[3] = new JMenuItem("Save All");
        tabsRightClickMenuItems[3].setActionCommand("SaveAllTabs");
        tabsRightClickMenuItems[4] = new JMenuItem("Delete");
        tabsRightClickMenuItems[4].setActionCommand("DeleteWorld");
        for (JMenuItem item : tabsRightClickMenuItems) {
            item.addActionListener(this);
            tabsRightClickMenu.add(item);
        }
        //open window and set up graphics
        setVisible(true);
        memoryImage = createImage(getContentPane().getWidth(), getContentPane().getHeight());
        memoryGraphics = memoryImage.getGraphics();
        //load worlds from files and display
        load();
        canDraw = true;
        drawGame();
    }

    public static void main(String[] args) {
        LevelEditor levelEditor = new LevelEditor();
    }

    //load all worlds from files
    private void load() {
        Scanner worldReader;
        try {
            worldReader = new Scanner(new File("levels.txt"));
        } catch (IOException ex) {
            worlds.add(new World("World_1"));
            worlds.get(0).addLevelUnchecked(new Level());
            worlds.get(0).setOpen(false);
            return;
        }
        while (worldReader.hasNext()) {
            worlds.add(new World(worldReader.next()));
        }
        for (World world : worlds) {
            Scanner reader;
            try {
                reader = new Scanner(new File(world.getName() + ".txt"));
            } catch (IOException ex) {
                world.addLevelUnchecked(new Level());
                return;
            }
            int xShift = screenWidth / 2 + menuWidth - reader.nextInt() / 2;
            int yShift = screenHeight / 2 - reader.nextInt() / 2;
            int numberOfLevels = reader.nextInt(), numberOfBlocks, type;
            for (int i = 0; i < numberOfLevels; i++) {
                Level level = new Level();
                numberOfBlocks = reader.nextInt();
                for (int j = 0; j < numberOfBlocks; j++) {
                    type = reader.nextInt();
                    Point point = new Point((reader.nextInt() * (itemSize / 2)) + xShift, (reader.nextInt() * (levelOffset / 2)) + yShift);
                    point = snapToGrid(point);
                    reader.nextInt();
                    level.addItemUnchecked(new Item(point.x, point.y, itemSize, type));
                }
                world.addLevelUnchecked(level);
            }
            world.setOpen(false);
        }
    }

    //saves all worlds to files
    private void saveAll() {
        File worldFile = new File("levels.txt");
        try (BufferedWriter worldWriter = new BufferedWriter(new FileWriter(worldFile))) {
            for (World world : worlds) {
                worldWriter.write(world.getName() + "\n");
                saveOne(world);
            }
        } catch (IOException ex) {
        }
    }

    //saves one world to file
    private void saveOne(int i) {
        saveOne(worlds.get(i));
    }

    //saves one world to file
    private void saveOne(World world) {
        int minX = 999999, minY = 999999, maxX = -999999, maxY = -999999;
        for (Level level : world.getWorld()) {
            for (Item item : level.getLevel()) {
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
        String levelText = String.valueOf((maxX - minX) + itemSize) + " " + String.valueOf((maxY - minY) + itemSize) + "\n" + String.valueOf(world.size()) + "\n";
        for (Level level : world.getWorld()) {
            levelText += String.valueOf(level.size()) + "\n";
            for (Item item : level.getLevel()) {
                int trans = 0;
                levelText += String.valueOf(item.getType()) + " " + String.valueOf((item.getX() - minX) / (itemSize / 2)) + " " + String.valueOf((item.getY() - minY) / (levelOffset / 2)) + " " + String.valueOf(trans) + "\n";
            }
        }
        File file = new File(world.getName() + ".txt");
        try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
            output.write(levelText);
        } catch (IOException ex) {
        }
        world.setSaved();
    }

    //paint method called by default (calls drawGame)
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (canDraw) {
            drawGame();
        }
    }

    //draws all content to the screen
    public final void drawGame() {
        Font defaultFont = memoryGraphics.getFontMetrics().getFont();
        memoryGraphics.setColor(Color.black);
        memoryGraphics.fillRect(menuWidth, 0, screenWidth - menuWidth, screenHeight);
        boolean printedLive = null == currentLevelObject;
        for (int i = 0; i < worlds.get(currentWorld).size(); i++) {
            if (!printedLive && worlds.get(currentWorld).get(i).size() == 0) {
                currentLevelObject.draw();
                printedLive = true;
            } else {
                for (int j = 0; j < worlds.get(currentWorld).get(i).size(); j++) {
                    if (worlds.get(currentWorld).get(i).isVisible()) {
                        if (!printedLive && currentLevel == i && currentLevelObject.getY() < worlds.get(currentWorld).get(i).get(j).getY()) {
                            currentLevelObject.draw();
                            printedLive = true;
                        }
                        Point location = worlds.get(currentWorld).get(i).get(j).getLocation();
                        if (location.x > menuWidth - itemSize && location.x < screenWidth && location.y > -itemSize && location.y < screenHeight) {
                            if (currentLevel == i) {
                                worlds.get(currentWorld).get(i).get(j).draw();
                            } else {
                                worlds.get(currentWorld).get(i).get(j).drawFadded();
                            }
                        }
                    }
                }
            }
            if (!printedLive && currentLevel == i) {
                currentLevelObject.draw();
                printedLive = true;
            }
        }
        memoryGraphics.setColor(Color.gray);
        memoryGraphics.fillRect(0, 0, menuWidth, screenHeight);
        memoryGraphics.fillRect(0, screenHeight - bottomMenuHeight, screenWidth, screenHeight - bottomMenuHeight);
        memoryGraphics.setColor(Color.white);
        memoryGraphics.drawLine(menuWidth, 0, menuWidth, screenHeight);
        memoryGraphics.drawLine(menuWidth, screenHeight - bottomMenuHeight, screenWidth, screenHeight - bottomMenuHeight);
        for (Item item : menuItems) {
            item.draw();
        }
        memoryGraphics.setColor(Color.black);
        memoryGraphics.fillRect(menuWidth + 1, 0, screenWidth - menuWidth, tabHeight);
        if (numberOfWorldsOpen == 0) {
            tabWidth = 0;
        } else {
            tabWidth = (screenWidth - menuWidth) / numberOfWorldsOpen;
        }
        int count = 0;
        for (int i = 0; i < worlds.size(); i++) {
            if (worlds.get(i).isOpen()) {
                if (currentWorld == i) {
                    memoryGraphics.setColor(Color.lightGray);
                } else {
                    memoryGraphics.setColor(Color.gray);
                }
                memoryGraphics.fillRect(menuWidth + (int) (count * tabWidth) + 1, 0, (int) tabWidth - 1, tabHeight);
                memoryGraphics.setColor(Color.black);
                if (worlds.get(i).hasChanges()) {
                    memoryGraphics.setFont(new Font(defaultFont.getFontName(), Font.BOLD, defaultFont.getSize() + 5));
                }
                Rectangle2D stringSize = memoryGraphics.getFontMetrics().getStringBounds(worlds.get(i).getName(), memoryGraphics);
                memoryGraphics.drawString(worlds.get(i).getName(), menuWidth + 1 + (count * tabWidth) + (int) ((tabWidth - stringSize.getWidth()) / 2), (tabHeight / 2) + (int) (stringSize.getHeight() / 2));
                memoryGraphics.setFont(defaultFont);
                count++;
            }
        }
        memoryGraphics.setColor(Color.black);
        memoryGraphics.drawLine(0, screenHeight - bottomMenuHeight, menuWidth - 1, screenHeight - bottomMenuHeight);
        memoryGraphics.drawString(worlds.get(currentWorld).getName(), iconPadding * 2, screenHeight - (iconPadding * 2));
        memoryGraphics.drawString("Level: " + String.valueOf(currentLevel), screenWidth - 60, screenHeight - (iconPadding * 2));
        memoryGraphics.drawImage(iconImages[worlds.get(currentWorld).get(currentLevel).isVisible() ? 0 : 1], menuWidth + iconPadding, screenHeight - iconSize - iconPadding, this);
        memoryGraphics.drawImage(iconImages[painting ? 2 : 3], menuWidth + iconSize + (iconPadding * 2), screenHeight - iconSize - iconPadding, this);
        getContentPane().getGraphics().drawImage(memoryImage, 0, 0, this);
        getContentPane().getGraphics().dispose();
    }

    //performs a check and if passes, add the item to the current level
    public final void addToLevelChecked(int level, Item item, boolean setUndo, boolean wipeRedoCache) {
        if (worlds.get(currentWorld).get(level).addItemChecked(item)) {
            drawGame();
            if (setUndo) {
                worlds.get(currentWorld).addUndo(new ItemBackup('a', level, item.getType(), (Point) item.getLocation().clone()));
                if (wipeRedoCache) {
                    worlds.get(currentWorld).clearRedo();
                }
            }
        }
    }

    //performs a check and if passes, remove the item to the current level
    public final void removeFromLevelChecked(int level, Item item, boolean setUndo, boolean wipeRedoCache) {
        Item removedItem = worlds.get(currentWorld).get(level).removeItem(item);
        if (removedItem != null) {
            drawGame();
            if (setUndo) {
                worlds.get(currentWorld).addUndo(new ItemBackup('r', level, removedItem.getType(), (Point) removedItem.getLocation().clone()));
                if (wipeRedoCache) {
                    worlds.get(currentWorld).clearRedo();
                }
            }
        }
    }

    //determines if there is an items under the mouse, and if so, returns the items
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

    //determines if the mouse is on a menu item, and if so, returns its id
    public final int getSelectedMenuItemType(Point point) {
        for (Item menuItem : menuItems) {
            Rectangle rectangle = new Rectangle(menuItem.getX(), menuItem.getY(), itemSize, itemSize);
            if (rectangle.contains(point)) {
                return menuItem.getType();
            }
        }
        return -1;
    }

    //shifts all items in the current world the desired amount
    public final void moveItems(int x, int y) {
        worlds.get(currentWorld).shiftItems(x * itemSize, y * itemSize / 2);
    }

    //moves to the correct level and location to show a backup item
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
        while (backup.location.y < itemSize) {
            moveItems(0, 1);
        }
        while (backup.location.y > screenHeight - itemSize) {
            moveItems(0, -1);
        }
    }

    //restores the last action
    public final void undo() {
        ItemBackup backup = worlds.get(currentWorld).peekUndo();
        if (backup == null) {
            return;
        } else if (backup.backupType == 'a' || backup.backupType == 'r') {
            findPlaceToBe(backup);
            backup = worlds.get(currentWorld).peekUndo();
            worlds.get(currentWorld).addRedo(new ItemBackup(backup.backupType, backup.level, backup.type, backup.location));
            if (backup.backupType == 'r') {
                addToLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false);
            } else {
                removeFromLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false);
            }
        } else if (backup.backupType == 'h') {
            hideCurrentLevel();
            worlds.get(currentWorld).addRedo(backup);
        }
        worlds.get(currentWorld).popUndo();
    }

    //redos the last undo
    public final void redo() {
        ItemBackup backup = worlds.get(currentWorld).peekRedo();
        if (backup == null) {
            return;
        } else if (backup.backupType == 'a' || backup.backupType == 'r') {
            findPlaceToBe(backup);
            backup = worlds.get(currentWorld).peekRedo();
            worlds.get(currentWorld).addUndo(new ItemBackup(backup.backupType, backup.level, backup.type, backup.location));
            if (backup.backupType == 'a') {
                addToLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false);
            } else {
                removeFromLevelChecked(backup.level, new Item(backup.location.x, backup.location.y, backup.type), false, false);
            }
        } else if (backup.backupType == 'h') {
            hideCurrentLevel();
            worlds.get(currentWorld).addUndo(backup);
        }
        worlds.get(currentWorld).popRedo();
    }

    //chenges levels and maintains the world object
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

    //hides the current level in the currrent world
    private void hideCurrentLevel() {
        worlds.get(currentWorld).get(currentLevel).switchVisibility();
        drawGame();
    }

    //chenges to the next available open level
    private void switchWorld(char dir) {
        if (numberOfWorldsOpen > 0) {
            if (dir == 'u') {
                boolean found = false;
                for (int i = currentWorld + 1; i < worlds.size(); i++) {
                    if (worlds.get(i).isOpen()) {
                        currentWorld = i;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    for (int i = 0; i < currentWorld; i++) {
                        if (worlds.get(i).isOpen()) {
                            currentWorld = i;
                            break;
                        }
                    }
                }
            } else if (dir == 'd') {
                boolean found = false;
                for (int i = currentWorld - 1; i >= 0; i--) {
                    if (worlds.get(i).isOpen()) {
                        currentWorld = i;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    for (int i = worlds.size() - 1; i > currentWorld; i--) {
                        if (worlds.get(i).isOpen()) {
                            currentWorld = i;
                            break;
                        }
                    }
                }
            }
            drawGame();
        }
    }

    //adds a world
    private void addWorld() {
        String name = null;
        boolean bad;
        do {
            bad = false;
            try {
                name = JOptionPane.showInputDialog(this, "Name the new world", "New", JOptionPane.QUESTION_MESSAGE).replaceAll(" ", "_");
                if (name.equals("")) {
                    bad = true;
                }
                for (World world : worlds) {
                    if (world.getName().equals(name)) {
                        bad = true;
                        break;
                    }
                }
            } catch (NullPointerException ex) {
                bad = true;
                break;
            }
        } while (bad);
        if (!bad) {
            worlds.add(new World(name));
            worlds.get(worlds.size() - 1).addLevelUnchecked(new Level());
            currentWorld = worlds.size() - 1;
            numberOfWorldsOpen++;
            drawGame();
        }
    }

    //removes a world
    private void removeWorld() {
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this World", "Remove?", JOptionPane.YES_NO_OPTION) == 0) {
            boolean move = worlds.size() == 1;
            while (worlds.size() == 1) {
                addWorld();
            }
            if (move) {
                currentWorld--;
            }
            worlds.remove(currentWorld);
            if (currentWorld >= worlds.size()) {
                currentWorld--;
            }
            numberOfWorldsOpen--;
            drawGame();
        }
    }

    //selects the tab that is under the mouse
    private void selectTab(Point point) {
        int tabToGo;
        if (numberOfWorldsOpen == 0) {
            tabToGo = 0;
        } else {
            tabToGo = (point.x - menuWidth) / tabWidth;
        }
        int testingTab = 0;
        for (int i = 0; i < worlds.size(); i++) {
            if (worlds.get(i).isOpen()) {
                if (testingTab == tabToGo) {
                    currentWorld = i;
                    break;
                }
                testingTab++;
            }
        }
        drawGame();
    }

    //closes the curent worlds open tab
    private void closeCurrentTab() {
        saveAll();
        worlds.get(currentWorld).setOpen(false);
        numberOfWorldsOpen--;
        switchWorld('u');
        drawGame();
    }

    //exits the program (with confermations)
    private void exit() {
//        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to Quit?", "Quit?", JOptionPane.YES_NO_OPTION) == 0) {
//            if (JOptionPane.showConfirmDialog(null, "Would you like to save your game?", "Save Game?", JOptionPane.YES_NO_OPTION) == 0) {
//                save();
//            }
//            System.exit(0);
//        }
        System.exit(0);
    }

    //determines if the mouse is in the main section of the window
    private boolean mouseIsInMain(Point point) {
        return (point.x > menuWidth && point.y < screenHeight - bottomMenuHeight && point.y > tabHeight);
    }

    //determines if the mouse is in the main side menu section of the window
    private boolean mouseIsInSideMenu(Point point) {
        return (point.x < menuWidth && point.y < screenHeight - bottomMenuHeight);
    }

    //determines if the mouse is in the bottom menu section of the window
    private boolean mouseIsInBottomMenu(Point point) {
        return (point.x > menuWidth && point.y > screenHeight - bottomMenuHeight);
    }

    //determines if the mouse is in the tabs section of the window
    private boolean mouseIsInTabs(Point point) {
        return (point.x > menuWidth && point.y < tabHeight);
    }

    //triggered when the mouse is moved
    @Override
    public void mouseDragged(MouseEvent me) {
        Point actualPoint = ((JFrame) me.getSource()).getContentPane().getMousePosition();
        Point snapedPoint = snapToGrid(actualPoint);
        if (painting && mouseIsInMain(actualPoint)) {
            if (SwingUtilities.isRightMouseButton(me)) {
                removeFromLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true, true);
            } else if (SwingUtilities.isLeftMouseButton(me)) {
                addToLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true, true);
            }
        } else {
            if (currentLevelObject != null && SwingUtilities.isLeftMouseButton(me)) {
                currentLevelObject.setLocationAndFix(snapedPoint);
                drawGame();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    //triggered when the mouse is pressed
    @Override
    public void mousePressed(MouseEvent me) {
        tabsRightClickMenu.setVisible(false);
        mainAreaRightClickMenu.setVisible(false);
        mainMenuRightClickMenu.setVisible(false);
        bottomMenuRightClickMenu.setVisible(false);
        Point actualPoint = ((JFrame) me.getSource()).getContentPane().getMousePosition();
        Point snapedPoint = snapToGrid(actualPoint);
        if (mouseIsInMain(actualPoint)) {
            if (SwingUtilities.isRightMouseButton(me)) {
                removeFromLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true, true);
            } else if (SwingUtilities.isLeftMouseButton(me)) {
                if (painting) {
                    addToLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType), true, true);
                } else {
                    Item item = getFromLevelChecked(currentLevel, new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType));
                    if (item != null) {
                        currentItemType = item.getType();
                        currentLevelObject = item;
                    } else {
                        currentLevelObject = new Item(snapedPoint.x, snapedPoint.y, itemSize, currentItemType);
                    }
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

    //triggerd when the mouse is released
    @Override
    public void mouseReleased(MouseEvent me) {
        if (me.isPopupTrigger()) {
            Point point = ((JFrame) me.getSource()).getContentPane().getMousePosition();
            if (mouseIsInMain(point)) {
                mainAreaRightClickMenu.setLocation(me.getPoint());
                mainAreaRightClickMenu.setVisible(true);
            } else if (mouseIsInSideMenu(point)) {
                mainMenuRightClickMenu.setLocation(me.getPoint());
                mainMenuRightClickMenu.setVisible(true);
            } else if (mouseIsInBottomMenu(point)) {
                bottomMenuRightClickMenu.setLocation(me.getPoint());
                bottomMenuRightClickMenu.setVisible(true);
            } else if (mouseIsInTabs(point) && numberOfWorldsOpen > 0) {
                tabsRightClickMenu.setLocation(me.getPoint());
                tabsRightClickMenu.setVisible(true);
            }
        } else {
            Point actualPoint = ((JFrame) me.getSource()).getContentPane().getMousePosition();
            if (currentLevelObject != null && !painting) {
                if (mouseIsInMain(actualPoint)) {
                    addToLevelChecked(currentLevel, currentLevelObject, true, true);
                }
                currentLevelObject = null;
            }
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

    //triggered when a key is pressed
    @Override
    public void keyPressed(KeyEvent ke) {
        int key = ke.getKeyCode();
        if (key == KeyEvent.VK_PAGE_UP) {
            levelUpKeyIsDown = true;
        } else if (key == KeyEvent.VK_PAGE_DOWN) {
            levelDownKeyIsDown = true;
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
            painting = !painting;
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
            saveKeyIsDown = true;
        } else if (key == KeyEvent.VK_TAB && ke.isControlDown()) {
            switchWorld(ke.isShiftDown() ? 'd' : 'u');
        } else if (key == KeyEvent.VK_N && ke.isControlDown()) {
            addWorld();
        } else if (key == KeyEvent.VK_F4 && ke.isControlDown()) {
            closeCurrentTab();
        } else if (key == KeyEvent.VK_O && ke.isControlDown()) {
            openWindow.display(worlds);
        }
    }

    //triggered when a key is released
    @Override
    public void keyReleased(KeyEvent ke) {
        int key = ke.getKeyCode();
        if (key == KeyEvent.VK_PAGE_UP && levelUpKeyIsDown) {
            levelUpKeyIsDown = false;
            chengleLevel('u');
        } else if (key == KeyEvent.VK_PAGE_DOWN && levelDownKeyIsDown) {
            levelDownKeyIsDown = false;
            chengleLevel('d');
        } else if (key == KeyEvent.VK_S && ke.isControlDown() && saveKeyIsDown == true) {
            saveKeyIsDown = false;
            saveAll();
        }
    }

    //triggered when the mouse wheel is moved
    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        if (mwe.getX() < menuWidth) {
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
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
    }

    //triggered when there is a major chages to the window
    @Override
    public void componentResized(ComponentEvent ce) {
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

    //triggered when a right click menu item is clicked
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("CloseTab")) {
            closeCurrentTab();
        } else if (ae.getActionCommand().equals("CloseAllTabs")) {
            saveAll();
            for (World world : worlds) {
                world.setOpen(false);
                numberOfWorldsOpen--;
            }
            currentWorld = 0;
            drawGame();
        } else if (ae.getActionCommand().equals("SaveTab")) {
            saveOne(currentWorld);
        } else if (ae.getActionCommand().equals("SaveAllTabs")) {
            saveAll();
        } else if (ae.getActionCommand().equals("DeleteWorld")) {
            removeWorld();
        } else {
            return;
        }
        ((JMenuItem) ae.getSource()).getParent().setVisible(false);
    }
}
