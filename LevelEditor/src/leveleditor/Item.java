package leveleditor;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import javax.swing.ImageIcon;

public class Item implements Comparable, Cloneable {

    private Point location;
    private final int type;
    private final int width, height;
    private final Image itemImage;

    public Item(int x, int y, int widthGiven, int heightGiven, int typeGiven) {
        type = typeGiven;
        width = widthGiven;
        height = heightGiven;
        itemImage = new ImageIcon(getClass().getResource("/media/" + type + ".png")).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
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

    public void shiftX(int xShift) {
        location.x += xShift;
    }

    public void shiftY(int yShift) {
        location.y += yShift;
    }

    public final void setLocationAndFix(Point locationG) {
        location = locationG;
        fixLocation();
    }

    public final void shiftLocation(int xShift, int yShift) {
        location.x += xShift;
        location.y += yShift;
    }

    public final void draw(Graphics g) {
        g.drawImage(itemImage, getX(), getY(), null);
    }

    public final void drawFadded(Graphics g) {
        Composite savedComposite = ((Graphics2D) g).getComposite();
        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        ((Graphics2D) g).drawImage(itemImage, getX(), getY(), null);
        ((Graphics2D) g).setComposite(savedComposite);
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
