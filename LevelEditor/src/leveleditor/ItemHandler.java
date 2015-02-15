//package leveleditor;
//
//import java.awt.AlphaComposite;
//import java.awt.Composite;
//import java.awt.Dimension;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.Image;
//import java.awt.Point;
//import java.awt.Rectangle;
//import java.awt.Toolkit;
//import java.util.ArrayList;
//import javax.swing.ImageIcon;
//
//public class ItemHandler {
//
//    public final World world;
//    private final Image[] images;
//    private final Item[] menuItems;
//    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//    private final int itemWidth, itemHeight;
//    ArrayList<ItemBackup> undoCache = new ArrayList(), redoCache = new ArrayList();
//
//    public ItemHandler(int itemWidthGiven, int itemHeightGiven, int numberOfItems) {
//        itemWidth = itemWidthGiven;
//        itemHeight = itemHeightGiven;
//        world = new World();
//        menuItems = new Item[numberOfItems];
//        images = new Image[numberOfItems];
//        for (int i = 0; i < numberOfItems; i++) {
//            menuItems[i] = new Item(itemWidthGiven * ((i % 3) + 1), itemHeightGiven * ((i / 3) + 1), itemWidthGiven, itemHeightGiven, i);
//            images[i] = new ImageIcon(getClass().getResource("/media/" + i + ".png")).getImage().getScaledInstance(itemWidth, itemHeight, Image.SCALE_SMOOTH);
//        }
//    }
//
//    public final void addItem(int level, Item item, boolean setUndo, boolean wipeRedoCache) {
//        if (world.get(level).addItem(item) && setUndo) {
//            undoCache.add(new ItemBackup('a', level, item));
//        }
//        if (wipeRedoCache) {
//            redoCache.clear();
//        }
//    }
//
//    public final void removeItem(int level, int i, boolean setUndo, boolean wipeRedoCache) {
//        if (world.get(level).removeItem(i) && setUndo) {
//            undoCache.add(new ItemBackup('r', level, world.get(level).get(i)));
//        }
//        if (wipeRedoCache) {
//            redoCache.clear();
//        }
//    }
//
//    public final void removeItem(int level, Item item, boolean setUndo, boolean wipeRedoCache) {
//        if (world.get(level).removeItem(item) && setUndo) {
//            undoCache.add(new ItemBackup('r', level, item));
//        }
//        if (wipeRedoCache) {
//            redoCache.clear();
//        }
//    }
//
//    public Item getSelectedMenuItem(Point testLocation) {
//        Item testObject;
//        for (Item menuItem : menuItems) {
//            testObject = menuItem;
//            Rectangle rectangle = new Rectangle(testObject.getX(), testObject.getY(), testObject.getWidth(), testObject.getHeight());
//            if (rectangle.contains(testLocation)) {
//                try {
//                    return (Item) testObject.clone();
//                } catch (CloneNotSupportedException ex) {
//                }
//            }
//        }
//        return null;
//    }
//
//    public final void scroll(int amount) {
//        int move = amount * 15;
//        if (menuItems[0].getY() - move < 0 && menuItems[menuItems.length - 1].getY() - move < screenSize.getHeight() || menuItems[menuItems.length - 1].getY() - move + menuItems[menuItems.length - 1].getHeight() > screenSize.getHeight() && menuItems[0].getY() - move > 0) {
//            return;
//        }
//        for (Item item : menuItems) {
//            item.shiftLocation(0, -move);
//        }
//    }
//
//    public void moveItems(int x, int y) {
//        for (Level level : world) {
//            for (Item item : level) {
//                item.shiftLocation(x * itemWidth, y * itemHeight / 2);
//            }
//        }
//        for (ItemBackup item : redoCache) {
//            item.item.shiftLocation(x * itemWidth, y * itemHeight / 2);
//        }
//    }
//
//    public final void drawMenu(Graphics g) {
//        for (Item item : menuItems) {
//            drawItem(g, item);
//        }
//    }
//
//    public final void drawItem(Graphics g, Item item) {
//        g.drawImage(images[item.getType()], item.getX(), item.getY(), null);
//    }
//
//    public final void drawFaddedItem(Graphics g, Item item) {
//        Composite savedComposite = ((Graphics2D) g).getComposite();
//        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
//        ((Graphics2D) g).drawImage(images[item.getType()], item.getX(), item.getY(), null);
//        ((Graphics2D) g).setComposite(savedComposite);
//    }
//
//    public final void undo() {
//        if (undoCache.size() > 0) {
//            ItemBackup backup = undoCache.get(undoCache.size() - 1);
//            redoCache.add(backup);
//            undoCache.remove(undoCache.size() - 1);
//            if (backup.type == 'r') {
//                addItem(backup.level, backup.item, false, false);
//            } else if (backup.type == 'a') {
//                removeItem(backup.level, backup.item, false, false);
//            }
//        }
//    }
//
//    public final void redo() {
//        if (redoCache.size() > 0) {
//            ItemBackup backup = redoCache.get(redoCache.size() - 1);
//            undoCache.add(backup);
//            redoCache.remove(backup);
//            if (backup.type == 'a') {
//                addItem(backup.level, backup.item, false, false);
//            } else if (backup.type == 'r') {
//                removeItem(backup.level, backup.item, false, false);
//            }
//        }
//    }
//
//    private final class ItemBackup {
//
//        public char type;
//        public int level;
//        public Item item;
//
//        public ItemBackup(char typeGiven, int levelGiven, Item itemGiven) {
//            type = typeGiven;
//            level = levelGiven;
//            item = itemGiven;
//        }
//    }
//}
