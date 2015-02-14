package leveleditor;

import java.awt.Point;

public class Item implements Comparable, Cloneable {

    private Point location;
    private final int type, width, height;

    public Item(int x, int y, int widthGiven, int heightGiven, int typeGiven) {
        type = typeGiven;
        width = widthGiven;
        height = heightGiven;
        location = new Point(x, y);
        fixLocation();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void fixLocation() {
        location.x -= width / 2;
        location.y -= height / 2;
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getType() {
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
