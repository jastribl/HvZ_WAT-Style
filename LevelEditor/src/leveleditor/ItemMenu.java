package leveleditor;

import java.awt.*;
import javax.swing.*;
import static leveleditor.Globals.*;

public class ItemMenu {

    private final Item[] menuItems = new Item[numberOfMenuItems];

    public ItemMenu() {
        MediaTracker imageTracker = new MediaTracker(new JFrame());
        for (int i = 0; i < itemImages.length; i++) {
            menuItems[i] = new Item(itemSize * ((i % 3) + 1), itemSize * ((i / 3) + 1), itemSize, i);
            try {
                itemImages[i] = new ImageIcon(getClass().getResource("/media/o" + i + ".png")).getImage().getScaledInstance(itemSize, itemSize, Image.SCALE_SMOOTH);
                imageTracker.addImage(itemImages[i], 0);
            } catch (Exception e) {
            }
        }
        try {
            imageTracker.waitForID(0);
        } catch (InterruptedException e) {
        }
    }

    public final int getSelectedItem(Point point) {
        for (Item menuItem : menuItems) {
            Rectangle rectangle = new Rectangle(menuItem.getX(), menuItem.getY(), itemSize, itemSize);
            if (rectangle.contains(point)) {
                return menuItem.getType();
            }
        }
        return -1;
    }

    public final void scroll(int amount) {
        if ((menuItems[0].getY() - amount > 0 || menuItems[menuItems.length - 1].getY() - amount > screenHeight - bottomMenuHeight) && (menuItems[menuItems.length - 1].getY() - amount + itemSize < screenHeight - bottomMenuHeight || menuItems[0].getY() - amount < 0)) {
            for (Item item : menuItems) {
                item.shiftLocation(0, -amount);
            }
        }
    }

    public final void draw() {
        for (Item item : menuItems) {
            item.draw();
        }
    }
}
