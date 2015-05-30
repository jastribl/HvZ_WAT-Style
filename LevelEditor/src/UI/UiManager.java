package UI;

import java.awt.*;
import static leveleditor.Globals.*;

public class UiManager {

    private final RightClickManager rightClickManager = new RightClickManager();
    private final BottomMenu bottomMenu = new BottomMenu();
    private final MainMenu mainMenu = new MainMenu();

    public final void changePaintingMode(int direction) {
        bottomMenu.changePaintingMode(direction);
    }

    public final int getSelectedGroupAndType(Point point) {
        return mainMenu.getItemAt(point);
    }

    public final void selectWorldTabAt(Point point) {
        currentWorld = (worlds.isEmpty() ? 0 : (point.x - menuWidth) / worldTabWidth);
    }

    public final void scrollMainMenu(int amount) {
        mainMenu.scroll(amount);
    }

    public final void closeAllRightClickMenus() {
        rightClickManager.closeAll();
    }

    public void selectMenuTabAt(Point point) {
        mainMenu.selectMenuTabAt(point);
    }

    public final void showTabRightClickMenu(Point point) {
        rightClickManager.showTabMenu(point);
    }

    public final void draw() {
        bottomMenu.draw();
        mainMenu.draw();
    }
}
