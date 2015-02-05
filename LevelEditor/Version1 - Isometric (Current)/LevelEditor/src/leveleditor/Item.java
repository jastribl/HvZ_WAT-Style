package leveleditor;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import javax.swing.ImageIcon;

public class Item implements Comparable, Cloneable {

    private Point location;
    private final int type, height, width;
    private final Image normalImage, faddedImage;

    public Item(int xG, int yG, int typeG) {
        type = typeG;
        normalImage = new ImageIcon(getClass().getResource("/media/" + type + ".png")).getImage();
        faddedImage = new ImageIcon(getClass().getResource("/media/" + type + "f.png")).getImage();
        height = normalImage.getHeight(null);
        width = normalImage.getWidth(null);
        location = new Point(xG, yG);
        fixLocation();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void fixLocation() {
        fixXLocation();
        fixYLocation();
    }

    private void fixXLocation() {
        location.x -= normalImage.getWidth(null) / 2;
    }

    private void fixYLocation() {
        location.y -= normalImage.getHeight(null) / 2;
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
    
    public int getType(){
        return type;
    }

    public final void setX(int xG) {
        location.x = xG;
    }

    public final void setY(int yG) {
        location.y = yG;
    }

    public final void setLocation(Point locationG) {
        location = locationG;
        fixLocation();
    }

    public final void draw(Graphics g) {
        g.drawImage(normalImage, getX(), getY(), null);
    }

    public final void drawFadded(Graphics g) {
        g.drawImage(faddedImage, getX(), getY(), null);

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
