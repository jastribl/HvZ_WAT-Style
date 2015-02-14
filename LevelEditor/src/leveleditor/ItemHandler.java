package leveleditor;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

public class ItemHandler {

    private final World world;
    private final Image[] images;
    private final Item[] menuItems;
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public ItemHandler(int itemWidth, int itemHeight, int numberOfItems) {
        world = new World();
        menuItems = new Item[numberOfItems];
        images = new Image[numberOfItems];
        for (int i = 0; i < numberOfItems; i++) {
            menuItems[i] = new Item(itemWidth * ((i % 3) + 1), itemHeight * ((i / 3) + 1), itemWidth, itemHeight, i);
            images[i] = new ImageIcon(getClass().getResource("/media/" + i + ".png")).getImage().getScaledInstance(itemWidth, itemHeight, Image.SCALE_SMOOTH);
        }
    }

    public World getWorld() {
        return world;
    }

    public Item getSelectedMenuItem(Point testLocation) throws CloneNotSupportedException {
        Item testObject;
        for (Item menuItem : menuItems) {
            testObject = menuItem;
            Rectangle rectangle = new Rectangle(testObject.getX(), testObject.getY(), testObject.getWidth(), testObject.getHeight());
            if (rectangle.contains(testLocation)) {
                return (Item) testObject.clone();
            }
        }
        return null;
    }

    public int getSelectedMenuType(Point testLocation) {
        Item testObject;
        for (Item menuItem : menuItems) {
            testObject = menuItem;
            Rectangle rectangle = new Rectangle(testObject.getX(), testObject.getY(), testObject.getWidth(), testObject.getHeight());
            if (rectangle.contains(testLocation)) {
                return testObject.getType();
            }
        }
        return -1;
    }

    public final void scroll(int amount) {
        int move = amount * 15;
        if (menuItems[0].getY() - move < 0 && menuItems[menuItems.length - 1].getY() - move < screenSize.getHeight() || menuItems[menuItems.length - 1].getY() - move + menuItems[menuItems.length - 1].getHeight() > screenSize.getHeight() && menuItems[0].getY() - move > 0) {
            return;
        }
        for (Item item : menuItems) {
            item.shiftLocation(0, -move);
        }
    }

    public final void drawMenu(Graphics g) {
        for (Item item : menuItems) {
            drawItem(g, item);
        }
    }

    public final void drawItem(Graphics g, Item item) {
        g.drawImage(images[item.getType()], item.getX(), item.getY(), null);
    }

    public final void drawFaddedItem(Graphics g, Item item) {
        Composite savedComposite = ((Graphics2D) g).getComposite();
        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        ((Graphics2D) g).drawImage(images[item.getType()], item.getX(), item.getY(), null);
        ((Graphics2D) g).setComposite(savedComposite);
    }
}
