package Structures;

import java.awt.*;
import static leveleditor.Globals.*;

public final class Item implements Comparable {

    private final int type;
    private Point location;

    public Item(int typeG, Point locationG, boolean fixLocation) {
        type = typeG;
        location = (Point) locationG.clone();
        if (fixLocation) {
            fixLocation();
        }
    }

    public Item(int typeGiven, int x, int y, boolean fixLocations) {
        this(typeGiven, new Point(x, y), fixLocations);
    }

    private void fixLocation() {
        location.x -= halfItemSize;
        location.y -= halfItemSize;
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
