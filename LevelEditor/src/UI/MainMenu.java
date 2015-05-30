package UI;

import Structures.Item;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import static leveleditor.Globals.*;

public class MainMenu {

    public static final int numberOfGroups = 2;
    private static final int numberOfBlocks = 9, numberOfSpecials = 4;
    public static ArrayList<Image[]> itemImages = new ArrayList();
    private final ArrayList<Item[]> menuItems = new ArrayList();

    public MainMenu() {
        MediaTracker imageTracker = new MediaTracker(new JFrame());
        menuItems.add(new Item[numberOfBlocks]);
        itemImages.add(new Image[numberOfBlocks]);
        for (int item = 0; item < numberOfBlocks; item++) {
            menuItems.get(0)[item] = new Item(0, item, menuTabHeight + (itemSize * ((item % 3) + 1)), itemSize * ((item / 3) + 1), true);
            try {
                itemImages.get(0)[item] = new ImageIcon(getClass().getResource("/media/block" + item + ".png")).getImage().getScaledInstance(itemSize, itemSize, Image.SCALE_SMOOTH);
                imageTracker.addImage(itemImages.get(0)[item], 0);
            } catch (Exception e) {
            }
        }
        menuItems.add(new Item[numberOfSpecials]);
        itemImages.add(new Image[numberOfSpecials]);
        for (int special = 0; special < numberOfSpecials; special++) {
            menuItems.get(1)[special] = new Item(1, special, menuTabHeight + (itemSize * ((special % 3) + 1)), itemSize * ((special / 3) + 1), true);
            try {
                itemImages.get(1)[special] = new ImageIcon(getClass().getResource("/media/special" + special + ".png")).getImage().getScaledInstance(itemSize, itemSize, Image.SCALE_SMOOTH);
                imageTracker.addImage(itemImages.get(1)[special], 0);
            } catch (Exception e) {
            }
        }
        try {
            imageTracker.waitForID(0);
        } catch (InterruptedException e) {
        }
    }

    public final int getItemAt(Point point) {
        for (Item menuItem : menuItems.get(currentItemGroup)) {
            Rectangle rectangle = new Rectangle(menuItem.getX(), menuItem.getY(), itemSize, itemSize);
            if (rectangle.contains(point)) {
                return menuItem.getType();
            }
        }
        return -1;
    }

    public final void scroll(int amount) {
        if ((menuItems.get(currentItemGroup)[0].getY() - amount > 0 || menuItems.get(currentItemGroup)[menuItems.get(currentItemGroup).length - 1].getY() - amount > screenHeight) && (menuItems.get(currentItemGroup)[menuItems.get(currentItemGroup).length - 1].getY() - amount + itemSize < screenHeight || menuItems.get(currentItemGroup)[0].getY() - amount < 0)) {
            for (Item item : menuItems.get(currentItemGroup)) {
                item.shiftLocation(0, -amount);
            }
        }
    }

    public void selectMenuTabAt(Point point) {
        currentItemGroup = point.y / menuTabWidth;
        currentItemType = 0;
    }

    public final void draw() {
        memoryGraphics.setColor(Color.gray);
        memoryGraphics.fillRect(0, 0, menuWidth, screenHeight);
        memoryGraphics.setColor(Color.white);
        memoryGraphics.drawLine(menuWidth, 0, menuWidth, screenHeight);
        for (Item item : menuItems.get(currentItemGroup)) {
            item.draw();
        }
        for (int i = 0; i < numberOfGroups; i++) {
            if (currentItemGroup == i) {
                memoryGraphics.setColor(Color.darkGray);
            } else {
                memoryGraphics.setColor(Color.lightGray);
            }
            memoryGraphics.fillRoundRect(0, i * menuTabWidth, menuTabHeight, menuTabWidth - 1, 20, 20);
            memoryGraphics.fillRect(menuTabHeight / 2, i * menuTabWidth, (menuTabHeight / 2) + 1, menuTabWidth - 1);
//            memoryGraphics.setColor(Color.black);
//            Rectangle2D stringSize = memoryGraphics.getFontMetrics().getStringBounds(worlds.get(i).getName(), memoryGraphics);
//            memoryGraphics.drawString(worlds.get(i).getName(), menuWidth + 1 + (i * worldTabWidth) + (int) ((worldTabWidth - stringSize.getWidth()) / 2), (worldTabHeight / 3) + (int) (stringSize.getHeight() / 2));
        }
    }
}
