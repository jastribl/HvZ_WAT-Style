package leveleditor;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class ObjectMenu {

    private final int items = 1;
    private final Item[] objects = new Item[items];
    private final int scale, angle;
    private int X0 = 0, y0 = 0;

    public ObjectMenu(int scaleG, int angleG) {
        scale = scaleG;
        angle = angleG;
        for (int i = 0; i < items; i++) {
            objects[i] = new Item(scale, angle);
            objects[i].setLocation(45, 45);
        }
    }

    public final void zoomTo(int angleG) {
//        for (Item object : objects) {
//            object.tiltTo(angleG);
//        }
    }

    public Item getSelectedMenuItem(Point testLocation) throws CloneNotSupportedException {
        Item object;
        for (int i = 0; i < items; i++) {
            object = objects[i];
            Rectangle rectangle = new Rectangle(object.getX(), object.getY(), 64, 64);
            if (rectangle.contains(testLocation)) {
                return (Item) object.clone();
            }
        }
        return null;
    }

    public final void draw(Graphics g) {
        for (int i = 0; i < items; i++) {
            objects[i].draw(g, true, 0, 0, scale, angle);
        }
    }
}
