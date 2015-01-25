package leveleditor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

public class ObjectMenu {

    private final Item[] items = new Item[6];
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public ObjectMenu() {
        int maxWidth = 0, maxHeight = 0;
        ImageIcon image;
        for (int i = 0; i < items.length; i++) {
            image = new ImageIcon(getClass().getResource("/media/" + i + ".png"));
            if (image.getIconWidth() > maxWidth) {
                maxWidth = image.getIconWidth();
            }
            if (image.getIconHeight() > maxHeight) {
                maxHeight = image.getIconHeight();
            }
        }
        for (int i = 0; i < items.length; i++) {
            items[i] = new Item(maxWidth * ((i % 3) + 1), maxHeight * ((i / 3) + 1), i);
        }
    }

    public Item getSelectedMenuItem(Point testLocation) throws CloneNotSupportedException {
        Item testObject;
        for (int i = 0; i < items.length; i++) {
            testObject = items[i];
            Rectangle rectangle = new Rectangle(testObject.getX(), testObject.getY(), testObject.getWidth(), testObject.getHeight());
            if (rectangle.contains(testLocation)) {
                return (Item) testObject.clone();
            }
        }
        return null;
    }

    public final void scroll(int amount) {
        int move = amount * 15;
        if (items[0].getY() - move < 0 && items[items.length - 1].getY() - move < screenSize.getHeight() || items[items.length - 1].getY() - move + items[items.length - 1].getHeight() > screenSize.getHeight() && items[0].getY() - move > 0) {
            return;
        }
        for (Item item : items) {
            item.setY(item.getY() - move);
        }
    }

    public final void draw(Graphics g) {
        for (Item item : items) {
            item.draw(g);
        }
    }
}
