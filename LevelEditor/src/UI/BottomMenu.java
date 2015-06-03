package UI;

import java.awt.*;
import javax.swing.*;
import static leveleditor.Globals.*;

public class BottomMenu {

    private final int numberOfPaintingTools = 4, numberOfVisibilityIcons = 2;
    private final int numberOfIcons = numberOfPaintingTools + numberOfVisibilityIcons;
    private final Image iconImages[] = new Image[numberOfIcons];

    public BottomMenu() {
        MediaTracker imageTracker = new MediaTracker(new JFrame());
        for (int icon = 0; icon < iconImages.length; icon++) {
            try {
                iconImages[icon] = new ImageIcon(getClass().getResource("/media/icon" + icon + ".png")).getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                imageTracker.addImage(iconImages[icon], 0);
            } catch (Exception e) {
            }
        }
        try {
            imageTracker.waitForID(0);
        } catch (InterruptedException e) {
        }
    }

    public final void changePaintingMode(int direction) {
        if (direction == UP) {
            currentDrawingMode = (currentDrawingMode + 1) % (numberOfPaintingTools);
        } else if (direction == DOWN) {
            currentDrawingMode += (currentDrawingMode == 0 ? numberOfPaintingTools - 1 : -1);
        }
        currentLevelObject = null;
    }

    public final void draw() {
        memoryGraphics.setColor(Color.gray);
        memoryGraphics.fillRect(menuWidth, screenHeight - bottomMenuHeight, screenWidth, screenHeight - bottomMenuHeight);
        memoryGraphics.setColor(Color.white);
        memoryGraphics.drawLine(menuWidth, screenHeight - bottomMenuHeight, screenWidth, screenHeight - bottomMenuHeight);
        memoryGraphics.setColor(Color.black);
        memoryGraphics.drawString("Level: " + String.valueOf(currentLevel), screenWidth - 60, screenHeight - (iconPadding * 2));
        memoryGraphics.drawImage(iconImages[worlds.get(currentWorld).get(currentLevel).isVisible() ? 0 : 1], menuWidth + iconPadding, screenHeight - iconSize - iconPadding, null);
        memoryGraphics.drawImage(iconImages[currentDrawingMode + 2], menuWidth + iconSize + (iconPadding * 2), screenHeight - iconSize - iconPadding, null);
    }
}
