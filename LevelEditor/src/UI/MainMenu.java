package UI;

import Structures.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import static leveleditor.Globals.*;

public class MainMenu {

    private final String[] groupNames = new String[]{"Blocks", "Specials"};
    public static ArrayList<Image[]> ObjectImages = new ArrayList();
    private final ArrayList<BaseObject[]> menuObjects = new ArrayList();

    public MainMenu() {
        MediaTracker imageTracker = new MediaTracker(new JFrame());
        menuObjects.add(new BaseObject[numberOfBlocks]);
        ObjectImages.add(new Image[numberOfBlocks]);
        for (int i = 0; i < numberOfBlocks; i++) {
            menuObjects.get(0)[i] = new Block(0, i, menuTabHeight + (objectSize * ((i % 3) + 1)), objectSize * ((i / 3) + 1));
            try {
                ObjectImages.get(0)[i] = new ImageIcon(getClass().getResource("/media/block" + i + ".png")).getImage().getScaledInstance(objectSize, objectSize, Image.SCALE_SMOOTH);
                imageTracker.addImage(ObjectImages.get(0)[i], 0);
            } catch (Exception e) {
            }
        }
        menuObjects.add(new BaseObject[numberOfSpecials]);
        ObjectImages.add(new Image[numberOfSpecials]);
        menuObjects.get(1)[0] = new Portal(1, 0, menuTabHeight + (objectSize * ((0 % 3) + 1)), objectSize * ((0 / 3) + 1), "");
        try {
            ObjectImages.get(1)[0] = new ImageIcon(getClass().getResource("/media/special0.png")).getImage().getScaledInstance(objectSize, objectSize, Image.SCALE_SMOOTH);
            imageTracker.addImage(ObjectImages.get(1)[0], 0);
        } catch (Exception e) {
        }
        try {
            imageTracker.waitForID(0);
        } catch (InterruptedException e) {
        }
    }

    public final void setCurrentTypeToObjectAt(Point point) {
        for (BaseObject menuObject : menuObjects.get(currentGroup)) {
            Rectangle rectangle = new Rectangle(menuObject.getX() - halfObjectSize, menuObject.getY() - halfObjectSize, objectSize, objectSize);
            if (rectangle.contains(point)) {
                currentType = menuObject.getType();
                return;
            }
        }
    }

    public final void scroll(int amount) {
        if ((menuObjects.get(currentGroup)[0].getY() - amount > 0 || menuObjects.get(currentGroup)[menuObjects.get(currentGroup).length - 1].getY() - amount > screenHeight) && (menuObjects.get(currentGroup)[menuObjects.get(currentGroup).length - 1].getY() - amount + objectSize < screenHeight || menuObjects.get(currentGroup)[0].getY() - amount < 0)) {
            for (BaseObject menuObject : menuObjects.get(currentGroup)) {
                menuObject.shiftLocation(0, -amount);
            }
        }
    }

    public void selectMenuTabAt(Point point) {
        currentGroup = point.y / menuTabWidth;
        if (currentGroup == BLOCK) {
            bottomMenu.setDrawingMode(PAINT);
        } else if (currentGroup == SPECIAL) {
            bottomMenu.setDrawingMode(POINT);
        }
        currentType = 0;
    }

    public final void draw() {
        memoryGraphics.setColor(Color.gray);
        memoryGraphics.fillRect(0, 0, menuWidth, screenHeight);
        memoryGraphics.setColor(Color.white);
        memoryGraphics.drawLine(menuWidth, 0, menuWidth, screenHeight);
        for (BaseObject object : menuObjects.get(currentGroup)) {
            object.draw();
        }
        for (int i = 0; i < numberOfMenuGroups; i++) {
            if (currentGroup == i) {
                memoryGraphics.setColor(Color.darkGray);
            } else {
                memoryGraphics.setColor(Color.lightGray);
            }
            memoryGraphics.fillRoundRect(0, i * menuTabWidth, menuTabHeight, menuTabWidth - 1, 20, 20);
            memoryGraphics.fillRect(menuTabHeight / 2, i * menuTabWidth, (menuTabHeight / 2) + 1, menuTabWidth - 1);
        }
    }
}
