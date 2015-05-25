package leveleditor;

import java.util.*;

public class Backup {

    private final Stack<ItemBackup> backup = new Stack();

    public final void add(ItemBackup item) {
        backup.add(item);
    }

    public final Stack<ItemBackup> getBackup() {
        return backup;
    }

    public final ItemBackup peek() {
        return (backup.isEmpty() ? null : backup.peek());
    }

    public final ItemBackup pop() {
        return (backup.isEmpty() ? null : backup.pop());
    }

    public final int getBackupSize() {
        return backup.size();
    }

    public final void clear() {
        backup.clear();
    }
}
