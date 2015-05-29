package UI;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import static leveleditor.Globals.*;

public class UiManager {

    private final RightClickManager rightClickManager = new RightClickManager();
    private final BottomMenu bottomMenu = new BottomMenu();
    private final MainMenu mainMenu = new MainMenu();

    public final void changePaintingMode(int direction) {
        bottomMenu.changePaintingMode(direction);
    }

    public final int getSelectedMenuItem(Point point) {
        return mainMenu.getItemAt(point);
    }

    public final void selectWorldTabAt(Point point) {
        currentWorld = (worlds.isEmpty() ? 0 : (point.x - menuWidth) / tabWidth);
    }

    public final void scrollMainMenu(int amount) {
        mainMenu.scroll(amount);
    }

    public final void closeAllRightClickMenus() {
        rightClickManager.closeAll();
    }

    public final void showTabRightClickMenu(Point point) {
        rightClickManager.showTabMenu(point);
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
        bottomMenu.draw();
    }

    public final void drawMainManu() {
        mainMenu.draw();
    }
}
