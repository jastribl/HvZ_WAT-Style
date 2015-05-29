package UI;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import Structures.Item;
import static leveleditor.Globals.*;

public class UiManager implements ActionListener {

    private final int numberOfPaintingTools = 4, numberOfVisibilityIcons = 2;
    private final int numberOfIcons = numberOfPaintingTools + numberOfVisibilityIcons;
    private final Image iconImages[] = new Image[numberOfIcons];
    private final Item[] menuItems = new Item[numberOfMenuItems];
    private final String[] tabsRightClickText = {"Close", "Close All", "Rename", "Save", "Save All", "Delete"};
    private final JMenuItem tabsRightClickMenuItems[] = new JMenuItem[tabsRightClickText.length];
    private final JPopupMenu tabsRightClickMenu = new JPopupMenu();

    public UiManager() {
        MediaTracker imageTracker = new MediaTracker(new JFrame());
        for (int item = 0; item < itemImages.length; item++) {
            menuItems[item] = new Item(item, itemSize * ((item % 3) + 1), itemSize * ((item / 3) + 1), true);
            try {
                itemImages[item] = new ImageIcon(getClass().getResource("/media/block" + item + ".png")).getImage().getScaledInstance(itemSize, itemSize, Image.SCALE_SMOOTH);
                imageTracker.addImage(itemImages[item], 0);
            } catch (Exception e) {
            }
        }
        for (int icon = 0; icon < iconImages.length; icon++) {
            try {
                iconImages[icon] = new ImageIcon(getClass().getResource("/media/icon" + icon + ".png")).getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                imageTracker.addImage(iconImages[icon], 0);
            } catch (Exception e) {
            }
        }
        try {
            imageTracker.waitForID(0);
        } catch (InterruptedException e) {
        }
        for (int i = 0; i < tabsRightClickMenuItems.length; i++) {
            tabsRightClickMenuItems[i] = new JMenuItem(tabsRightClickText[i]);
            tabsRightClickMenuItems[i].setActionCommand(tabsRightClickText[i]);
            tabsRightClickMenuItems[i].addActionListener(this);
            tabsRightClickMenu.add(tabsRightClickMenuItems[i]);
        }
    }

    public final void changePaintingMode(int direction) {
        if (direction == UP) {
            currentDrawingMode = (currentDrawingMode + 1) % (numberOfPaintingTools);
        } else if (direction == DOWN) {
            currentDrawingMode += (currentDrawingMode == 0 ? numberOfPaintingTools - 1 : -1);
        }
        currentLevelObject = null;
    }

    public final int getSelectedMenuItem(Point point) {
        for (Item menuItem : menuItems) {
            Rectangle rectangle = new Rectangle(menuItem.getX(), menuItem.getY(), itemSize, itemSize);
            if (rectangle.contains(point)) {
                return menuItem.getType();
            }
        }
        return -1;
    }

    public final void selectTabAt(Point point) {
        currentWorld = (worlds.isEmpty() ? 0 : (point.x - menuWidth) / tabWidth);
    }

    public final void scroll(int amount) {
        if ((menuItems[0].getY() - amount > 0 || menuItems[menuItems.length - 1].getY() - amount > screenHeight) && (menuItems[menuItems.length - 1].getY() - amount + itemSize < screenHeight || menuItems[0].getY() - amount < 0)) {
            for (Item item : menuItems) {
                item.shiftLocation(0, -amount);
            }
        }
    }

    public final void closeAllRightClickMenus() {
        tabsRightClickMenu.setVisible(false);
    }

    public final void showTabRightClickMenu(Point point) {
        tabsRightClickMenu.setLocation(point);
        tabsRightClickMenu.setVisible(true);
    }

    public final void drawTabs() {
        Font defaultFont = memoryGraphics.getFontMetrics().getFont();
        int count = 0;
        for (int i = 0; i < worlds.size(); i++) {
            if (currentWorld == i) {
                memoryGraphics.setColor(Color.lightGray);
            } else {
                memoryGraphics.setColor(Color.gray);
            }
            memoryGraphics.fillRoundRect(menuWidth + (count * tabWidth) + 1, 0, tabWidth - 1, tabHeight, 10, 20);
            memoryGraphics.fillRect(menuWidth + (count * tabWidth) + 1, tabHeight / 2, tabWidth - 1, (tabHeight / 2) + 1);
            memoryGraphics.setColor(Color.black);
            if (worlds.get(i).hasChanges()) {
                memoryGraphics.setFont(new Font("default", Font.BOLD, defaultFont.getSize()));
            }
            Rectangle2D stringSize = memoryGraphics.getFontMetrics().getStringBounds(worlds.get(i).getName(), memoryGraphics);
            memoryGraphics.drawString(worlds.get(i).getName(), menuWidth + 1 + (count * tabWidth) + (int) ((tabWidth - stringSize.getWidth()) / 2), (tabHeight / 3) + (int) (stringSize.getHeight() / 2));
            memoryGraphics.setFont(defaultFont);
            count++;
        }
    }

    public final void drawBottomMenu() {
        memoryGraphics.setColor(Color.gray);
        memoryGraphics.fillRect(menuWidth, screenHeight - bottomMenuHeight, screenWidth, screenHeight - bottomMenuHeight);
        memoryGraphics.setColor(Color.white);
        memoryGraphics.drawLine(menuWidth, screenHeight - bottomMenuHeight, screenWidth, screenHeight - bottomMenuHeight);
        memoryGraphics.setColor(Color.black);
        memoryGraphics.drawString("Level: " + String.valueOf(currentLevel), screenWidth - 60, screenHeight - (iconPadding * 2));
        memoryGraphics.drawImage(iconImages[worlds.get(currentWorld).get(currentLevel).isVisible() ? 0 : 1], menuWidth + iconPadding, screenHeight - iconSize - iconPadding, null);
        memoryGraphics.drawImage(iconImages[currentDrawingMode + 2], menuWidth + iconSize + (iconPadding * 2), screenHeight - iconSize - iconPadding, null);
    }

    public final void drawSideManu() {
        memoryGraphics.setColor(Color.gray);
        memoryGraphics.fillRect(0, 0, menuWidth, screenHeight);
        memoryGraphics.setColor(Color.white);
        memoryGraphics.drawLine(menuWidth, 0, menuWidth, screenHeight);
        for (Item item : menuItems) {
            item.draw();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case "Close":
                closeTab(currentWorld);
                break;
            case "Close All":
                closeAllTabs();
                break;
            case "Rename":
                worlds.get(currentWorld).rename();
                break;
            case "Save":
                worlds.get(currentWorld).save();
                break;
            case "Save All":
                saveAll();
                break;
            case "Delete":
                removeCurrentWorld();
                break;
            default:
                return;
        }
        ((JMenuItem) ae.getSource()).getParent().setVisible(false);
    }
}
