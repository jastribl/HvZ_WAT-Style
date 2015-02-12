package leveleditor;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class ItemDrawer {

    private final Graphics g;
    private final Image[] images;

    public ItemDrawer(Graphics gGiven, int numberOfItems, int itemWidth, int itemHeight) {
        g = gGiven;
        images = new Image[numberOfItems];
        for (int i = 0; i < numberOfItems; i++) {
            images[i] = new ImageIcon(getClass().getResource("/media/" + i + ".png")).getImage().getScaledInstance(itemWidth, itemHeight, Image.SCALE_SMOOTH);
        }
    }

    public final void draw(Item item) {
        g.drawImage(images[item.getType()], item.getX(), item.getY(), null);
    }

    public final void drawFadded(Item item) {
        Composite savedComposite = ((Graphics2D) g).getComposite();
        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        ((Graphics2D) g).drawImage(images[item.getType()], item.getX(), item.getY(), null);
        ((Graphics2D) g).setComposite(savedComposite);
    }
}
