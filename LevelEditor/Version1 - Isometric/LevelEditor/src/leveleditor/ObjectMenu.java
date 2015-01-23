package leveleditor;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class ObjectMenu {

    private final int floors = 3, walls = 3;
    private final LevelObject[] floorObjects = new LevelObject[floors];
    private final LevelObject[] wallObjects = new LevelObject[walls];

    public ObjectMenu() {
        for (int i = 0; i < floors; i++) {
            floorObjects[i] = new LevelObject(65 * (i + 1), 70, 0, i);
        }
        for (int i = 0; i < walls; i++) {
            wallObjects[i] = new LevelObject(65 * (i + 1), 200, 1, i);
        }
    }

    public LevelObject getSelectedMenuItem(Point testLocation) throws CloneNotSupportedException {
        LevelObject testObject;
        for (int i = 0; i < floors; i++) {
            testObject = floorObjects[i];
            Rectangle rectangle = new Rectangle(testObject.getX(), testObject.getY(), testObject.getWidth(), testObject.getHeight());
            if (rectangle.contains(testLocation)) {
                return (LevelObject) testObject.clone();
            }
        }
        for (int i = 0; i < walls; i++) {
            testObject = wallObjects[i];
            Rectangle rectangle = new Rectangle(testObject.getX(), testObject.getY(), testObject.getWidth(), testObject.getHeight());
            if (rectangle.contains(testLocation)) {
                return (LevelObject) testObject.clone();
            }
        }
        return null;
    }

    public final void draw(Graphics g) {
        g.drawString("Floors", 10, 20);
        for (int i = 0; i < floors; i++) {
            floorObjects[i].draw(g);
        }
        g.drawString("Walls", 10, 150);
        for (int i = 0; i < walls; i++) {
            wallObjects[i].draw(g);
        }
    }
}
