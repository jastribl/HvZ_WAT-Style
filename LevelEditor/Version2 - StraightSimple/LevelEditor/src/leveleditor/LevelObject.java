package leveleditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class LevelObject implements Cloneable {

    private Point location;
    private int grideWidth, topHeight;

    public LevelObject(int grideWidthG, int angleG) {
        location = new Point(0, 0);
        grideWidth = grideWidthG;
        topHeight = (int) (angleG / 90.0 * grideWidth);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public final int getX() {
        return location.x;
    }

    public final int getY() {
        return location.y;
    }

    public final int getUnit() {
        return grideWidth;
    }

    public final void setLocation(Point locationG) {
        location = locationG;
    }

    public final void zoomTo(int angleG) {
        topHeight = (int) (angleG / 90.0 * grideWidth);
    }

    public final void draw(Graphics g, boolean filled) {
        g.setColor(Color.white);
        g.drawRect(getX(), getY(), grideWidth, topHeight);
        g.drawRect(getX(), getY() + topHeight, grideWidth, grideWidth - topHeight);
        if (filled) {
            g.setColor(Color.green);
            g.fillRect(getX() + 1, getY() + 1, grideWidth - 1, topHeight - 1);
            g.setColor(Color.blue);
            g.fillRect(getX() + 1, getY() + topHeight + 1, grideWidth - 2, grideWidth - topHeight - 2);
        } else {
            g.setColor(Color.gray);
            g.fillRect(getX() + 1, getY() + 1, grideWidth - 1, topHeight - 1);
            g.fillRect(getX() + 1, getY() + topHeight + 1, grideWidth - 2, grideWidth - topHeight - 2);
        }
    }
}
