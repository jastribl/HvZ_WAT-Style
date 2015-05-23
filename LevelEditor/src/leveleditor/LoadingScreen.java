package leveleditor;

import java.awt.*;
import javax.swing.*;
import static leveleditor.Globals.*;

public class LoadingScreen {

    private final Image loadingImages[] = new Image[12];
    private int loadingPoint = 0;

    public LoadingScreen() {
        MediaTracker imageTracker = new MediaTracker(new JFrame());
        for (int i = 0; i < loadingImages.length; i++) {
            try {
                loadingImages[i] = new ImageIcon(getClass().getResource("/media/loading" + i + ".png")).getImage().getScaledInstance(loadingSize, loadingSize, Image.SCALE_SMOOTH);
                imageTracker.addImage(loadingImages[i], 0);
            } catch (Exception e) {
            }
        }
        try {
            imageTracker.waitForID(0);
        } catch (InterruptedException e) {
        }
    }

    public final void next() {
        loadingPoint = (++loadingPoint % loadingImages.length);
    }

    public final void draw() {
        memoryGraphics.drawImage(loadingImages[loadingPoint], (screenWidth - loadingSize) / 2, (screenHeight - loadingSize) / 2, null);
    }
}
