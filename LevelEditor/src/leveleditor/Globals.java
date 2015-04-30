package leveleditor;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class Globals {

    public static Graphics memoryGraphics = null;
    public static int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    public static final int itemSize = 64, levelOffset = itemSize / 4, menuWidth = itemSize * 4, tabHeight = 25, iconSize = 40, iconPadding = 5, bottomMenuHeight = iconSize + (iconPadding * 2), numberOfItemsTypes = 9, numberOfIconImages = 4;
    public static int tabWidth = 0;
    public static Item currentLevelObject = null;
    public static int currentItemType = 6, currentLevel = 0, currentWorld = 0;
    public static boolean levelUpKeyIsDown = false, levelDownKeyIsDown = false, saveKeyIsDown = false, painting = true;
    public static ArrayList<World> worlds = new ArrayList();
    public static boolean canDraw = false;
    public static final Item[] menuItems = new Item[numberOfItemsTypes];
    public static final JPopupMenu mainAreaRightClickMenu = new JPopupMenu(), mainMenuRightClickMenu = new JPopupMenu(), bottomMenuRightClickMenu = new JPopupMenu(), tabsRightClickMenu = new JPopupMenu();
    public static final JMenuItem mainAreaRightClickMenuItems[] = new JMenuItem[1], mainMenuRightClickMenuItems[] = new JMenuItem[1], bottomMenuRightClickMenuItems[] = new JMenuItem[1], tabsRightClickMenuItems[] = new JMenuItem[5];
    public static final OpenWindow openWindow = new OpenWindow();
    public static int numberOfWorldsOpen = 0;
    public static Image itemImages[] = new Image[numberOfItemsTypes], iconImages[] = new Image[numberOfIconImages];

    //find the appropriate location to put the item baed on the mouse location
    public static Point snapToGrid(Point p) {
        int yy = p.y / levelOffset * levelOffset + (itemSize / 2);
        if ((yy / levelOffset) % 2 == 0) {
            return new Point(((p.x + (itemSize / 2)) / itemSize * itemSize), yy);
        } else {
            return new Point((p.x / itemSize * itemSize) + (itemSize / 2), yy);
        }
    }
}
