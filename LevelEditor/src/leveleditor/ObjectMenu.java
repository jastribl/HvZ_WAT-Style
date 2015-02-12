package leveleditor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class ObjectMenu {

    private final Item[] menuItems = new Item[9];
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public ObjectMenu(int width, int height) {
        for (int i = 0; i < menuItems.length; i++) {
            menuItems[i] = new Item(width * ((i % 3) + 1), height * ((i / 3) + 1), width, height, i);
        }
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
            item.shiftY(-move);
        }
    }

    public final void draw(Graphics g) {
        for (Item item : menuItems) {
            item.draw(g);
        }
    }
}
