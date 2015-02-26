package leveleditor;

import java.util.EmptyStackException;
import java.util.Stack;

public class Backup extends Stack<ItemBackup> {

    @Override
    public final ItemBackup peek() {
        try {
            return super.peek();
        } catch (EmptyStackException e) {
            return null;
        }
    }

    @Override
    public final ItemBackup pop() {

        try {
            return super.pop();
        } catch (EmptyStackException e) {
            return null;
        }
    }
}
