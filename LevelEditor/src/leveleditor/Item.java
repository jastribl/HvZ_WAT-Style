package leveleditor;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import static leveleditor.Globals.itemImages;
import static leveleditor.Globals.memoryGraphics;

public final class Item implements Comparable, Cloneable {

    private Point location;
    final int type, size;

    public Item(int x, int y, int sizeGiven, int typeGiven) {
        type = typeGiven;
        size = sizeGiven;
        location = new Point(x, y);
        fixLocation();
    }

    //for use in undo/redo caches
    //doesn't shift the position to allign with the mouse click
    public Item(int x, int y, int typeGiven) {
        type = typeGiven;
        size = 0;
        location = new Point(x, y);
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

    public final void shiftLocation(int xShift, int yShift) {
        location.x += xShift;
        location.y += yShift;
    }

    //draws an item to the screen
    public final void draw() {
        memoryGraphics.drawImage(itemImages[type], location.x, location.y, null);
    }

    //draws an item to the screen (fadded)
    public final void drawFadded() {
        Composite savedComposite = ((Graphics2D) memoryGraphics).getComposite();
        ((Graphics2D) memoryGraphics).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        ((Graphics2D) memoryGraphics).drawImage(itemImages[type], location.x, location.y, null);
        ((Graphics2D) memoryGraphics).setComposite(savedComposite);
    }

    @Override
    public int compareTo(Object o) {
        Item o2 = (Item) o;
        if (getY() == o2.getY()) {
            if (getX() == o2.getX()) {
                return 0;
            } else {
                return getX() > o2.getX() ? 1 : -1;
            }
        } else {
            return getY() > o2.getY() ? 1 : -1;
        }
    }
}
