package leveleditor;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class ObjectMenu {

    private final int floors = 1;
    private final LevelObject[] objects = new LevelObject[floors];

    public ObjectMenu(int unitG, int angleG) {
        for (int i = 0; i < floors; i++) {
            objects[i] = new LevelObject(unitG, angleG);
            objects[i].setLocation(new Point(65 * (i + 1), 70));
        }
    }

    public final void zoomTo(int angleG) {
        for (LevelObject object : objects) {
            object.zoomTo(angleG);
        }
    }

    public LevelObject getSelectedMenuItem(Point testLocation) throws CloneNotSupportedException {
        LevelObject testObject;
        for (int i = 0; i < floors; i++) {
            testObject = objects[i];
            Rectangle rectangle = new Rectangle(testObject.getX(), testObject.getY(), testObject.getUnit(), testObject.getUnit());
            if (rectangle.contains(testLocation)) {
                return (LevelObject) testObject.clone();
            }
        }
        return null;
    }

    public final void draw(Graphics g) {
        for (int i = 0; i < floors; i++) {
            objects[i].draw(g, true);
        }
    }
}
