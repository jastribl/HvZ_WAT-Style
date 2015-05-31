package Structures;

import java.awt.Point;
import static leveleditor.Globals.*;

public class Portal extends Item {

    private String destination;

    public Portal(int groupG, int typeG, Point locationG, String destinationG) {
        super(groupG, typeG, locationG);
        destination = destinationG;
    }

    public Portal(int groupG, int typeGiven, int x, int y, String destination) {
        this(groupG, typeGiven, new Point(x, y), destination);
    }

    public final String getDestination() {
        return destination;
    }

    public final void setDestination(String newDestination) {
        destination = newDestination;
    }

    public final boolean validate() {
        for (World world : worlds) {
            if ((!world.getName().equals(worlds.get(currentWorld).getName())) && world.getName().equals(destination)) {
                return true;
            }
        }
        return false;
    }
}
