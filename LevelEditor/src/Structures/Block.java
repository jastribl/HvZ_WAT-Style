package Structures;

import java.awt.Point;

public class Block extends BaseObject {

    public Block(int groupG, int typeG, Point locationG) {
        super(groupG, typeG, locationG);
    }

    public Block(int groupG, int typeGiven, int x, int y) {
        this(groupG, typeGiven, new Point(x, y));
    }

    Block(Block p) {
        super(p);
    }

    @Override
    public BaseObject DeepCopy() {
        return new Block(this);
    }
}
