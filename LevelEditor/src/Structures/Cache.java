package Structures;

import java.util.*;

public class Cache {

    private final Stack<BackupItem> backup = new Stack();

    public final void add(BackupItem item) {
        backup.add(item);
    }

    public final Stack<BackupItem> getCache() {
        return backup;
    }

    public final BackupItem peek() {
        return (backup.isEmpty() ? null : backup.peek());
    }

    public final BackupItem pop() {
        return (backup.isEmpty() ? null : backup.pop());
    }

    public final int getBackupSize() {
        return backup.size();
    }

    public final void clear() {
        backup.clear();
    }
}
