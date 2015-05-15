package leveleditor;

import java.awt.*;
import static leveleditor.Globals.*;

public final class Item implements Comparable, Cloneable {

    private Point location;
    final int type, size;

    public Item(Point p, int sizeGiven, int typeGiven) {
        location = p;
        fixLocation();
        size = sizeGiven;
        type = typeGiven;
    }

    public Item(int x, int y, int sizeGiven, int typeGiven) {
        location = new Point(x, y);
        fixLocation();
        size = sizeGiven;
        type = typeGiven;
    }

//    for use in undo/redo caches
//    doesn't shift the position to allign with the mouse click
    public Item(Point p, int typeGiven) {
        location = p;
        type = typeGiven;
        size = 0;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void fixLocation() {
        location.x -= size / 2;
        location.y -= size / 2;
    }

    public final Point getLocation() {
        return location;
    }

    public final int getX() {
        return location.x;
    }

    public final int getY() {
        return location.y;
    }

    public final int getType() {
        return type;
    }

    public final void setLocationAndFix(Point locationG) {
        location = locationG;
        fixLocation();
    }

    public final void snap() {
        location = snapToGrid(location);
    }

    public final void shiftLocation(int xShift, int yShift) {
        location.x += xShift;
        location.y += yShift;
    }

    public final void draw() {
        memoryGraphics.drawImage(itemImages[type], location.x, location.y, null);
    }

    public final void drawFaded() {
        Composite savedComposite = ((Graphics2D) memoryGraphics).getComposite();
        ((Graphics2D) memoryGraphics).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        draw();
        ((Graphics2D) memoryGraphics).setComposite(savedComposite);
    }

    @Override
    public int compareTo(Object o) {
        Item o2 = (Item) o;
        if (location.y == o2.getY()) {
            if (location.x == o2.getX()) {
                return 0;
            } else {
                return location.x > o2.getX() ? 1 : -1;
            }
        } else {
            return location.y > o2.getY() ? 1 : -1;
        }
    }
}
