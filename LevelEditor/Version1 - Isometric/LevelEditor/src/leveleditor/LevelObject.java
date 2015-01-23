package leveleditor;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import javax.swing.ImageIcon;

public class LevelObject implements Cloneable {

    private Point location;
    private final int category, type, height, width;
    private final Image image, faddedImage;

    public LevelObject(int xG, int yG, int categoryG, int typeG) {
        category = categoryG;
        type = typeG;
        image = new ImageIcon(getClass().getResource("/media/LevelObjects/" + category + "/" + type + ".png")).getImage();
        faddedImage = new ImageIcon(getClass().getResource("/media/LevelObjects/" + category + "/" + type + "f.png")).getImage();
        height = image.getHeight(null);
        width = image.getWidth(null);
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
        location.x -= image.getWidth(null) / 2;
    }

    private void fixYLocation() {
        location.y -= image.getHeight(null) / 2;
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

    public final int getCategory() {
        return category;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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
        g.drawImage(image, getX(), getY(), null);
    }

    public final void drawFadded(Graphics g) {
        g.drawImage(faddedImage, getX(), getY(), null);
    }
}
