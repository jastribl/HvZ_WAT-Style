package leveleditor;

import java.util.EmptyStackException;
import java.util.Stack;

public class Backup {

    private final Stack<ItemBackup> backup = new Stack();

    public final void add(ItemBackup item) {
        backup.add(item);
    }

    public final Stack<ItemBackup> getBackup() {
        return backup;
    }

    public final ItemBackup peek() {
        try {
            return backup.peek();
        } catch (EmptyStackException e) {
            return null;
        }
    }

    public final ItemBackup pop() {

        try {
            return backup.pop();
        } catch (EmptyStackException e) {
            return null;
        }
    }

    public final void clear() {
        backup.clear();
    }
}
