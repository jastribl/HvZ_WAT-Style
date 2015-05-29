package UI;

import Structures.Item;
import java.awt.*;
import javax.swing.*;
import static leveleditor.Globals.*;

public class MainMenu {

    private final Item[] menuItems = new Item[numberOfMenuItems];

    public MainMenu() {
        MediaTracker imageTracker = new MediaTracker(new JFrame());
        for (int item = 0; item < itemImages.length; item++) {
            menuItems[item] = new Item(item, itemSize * ((item % 3) + 1), itemSize * ((item / 3) + 1), true);
            try {
                itemImages[item] = new ImageIcon(getClass().getResource("/media/block" + item + ".png")).getImage().getScaledInstance(itemSize, itemSize, Image.SCALE_SMOOTH);
                imageTracker.addImage(itemImages[item], 0);
            } catch (Exception e) {
            }
        }
        try {
            imageTracker.waitForID(0);
        } catch (InterruptedException e) {
        }
    }

    public final int getItemAt(Point point) {
        for (Item menuItem : menuItems) {
            Rectangle rectangle = new Rectangle(menuItem.getX(), menuItem.getY(), itemSize, itemSize);
            if (rectangle.contains(point)) {
                return menuItem.getType();
            }
        }
        return -1;
    }

    public final void scroll(int amount) {
        if ((menuItems[0].getY() - amount > 0 || menuItems[menuItems.length - 1].getY() - amount > screenHeight) && (menuItems[menuItems.length - 1].getY() - amount + itemSize < screenHeight || menuItems[0].getY() - amount < 0)) {
            for (Item item : menuItems) {
                item.shiftLocation(0, -amount);
            }
        }
    }

    public final void draw() {
        memoryGraphics.setColor(Color.gray);
        memoryGraphics.fillRect(0, 0, menuWidth, screenHeight);
        memoryGraphics.setColor(Color.white);
        memoryGraphics.drawLine(menuWidth, 0, menuWidth, screenHeight);
        for (Item item : menuItems) {
            item.draw();
        }
    }
}
