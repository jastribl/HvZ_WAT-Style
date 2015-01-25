package leveleditor;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.ImageIcon;

public class Item implements Comparable, Cloneable {

    private int x, y;

    public Item(int xG, int yG) {
        x = xG;
        y = yG;
    }

    @Override
    protected Item clone() throws CloneNotSupportedException {
        return (Item) super.clone();
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final void setLocation(int xG, int yG) {
        x = xG;
        y = yG;
    }

    public final void draw(Graphics g, boolean filled, int minX, int minY, int scale, int angle) {
        int height = (int) (angle / 90.0 * scale);
        int depth = scale - height;
        g.drawImage(new ImageIcon(getClass().getResource("/media/top.png")).getImage(), (x * scale) + minX, (y * height) + minY, scale, height, null);
        g.drawImage(new ImageIcon(getClass().getResource("/media/side.png")).getImage(), (x * scale) + minX, (y * height) + minY + height, scale, depth, null);

//        g.setColor(Color.black);
//        g.drawRect((x * scale) + minX, (y * height) + minY, scale - 1, height - 1);
//        g.drawRect((x * scale) + minX, (y * height) + minY + height, scale - 1, depth - 1);
//        if (filled) {
//            g.setColor(Color.gray);
//            g.fillRect((x * scale) + minX + 1, (y * height) + minY + 1, scale - 1 - 1, height - 1 - 1);
//            g.setColor(Color.blue);
//            g.fillRect((x * scale) + minX + 1, (y * height) + minY + height + 1, scale - 1 - 1, depth - 1 - 1);
//        } else {
//            g.setColor(Color.gray);
//            g.drawRect((x * scale) + minX + 1, (y * height) + minY + 1, scale - 1 - 1, height - 1 - 1);
//            g.drawRect((x * scale) + minX + 1, (y * height) + minY + height + 1, scale - 1 - 1, depth - 1 - 1);
//        }
    }

    @Override
    public int compareTo(Object o) {
        Item i2 = (Item) o;
        if (this.getX() == i2.getX()) {
            if (this.getY() == i2.getY()) {
                return 0;
            } else {
                return this.getY() > i2.getY() ? 1 : -1;
            }
        } else {
            return this.getX() > i2.getX() ? 1 : -1;
        }
    }

    public static BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        double scaleX = (double) width / imageWidth;
        double scaleY = (double) height / imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
        return bilinearScaleOp.filter(image, new BufferedImage(width, height, image.getType()));
    }
}
