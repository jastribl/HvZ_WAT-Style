package Structures;

import java.util.*;

public class Cache {

    private final Stack<BackupObject> backup = new Stack();

    public final void add(BackupObject object) {
        backup.add(object);
    }

    public final Stack<BackupObject> getCache() {
        return backup;
    }

    public final BackupObject peek() {
        return (backup.isEmpty() ? null : backup.peek());
    }

    public final BackupObject pop() {
        return (backup.isEmpty() ? null : backup.pop());
    }

    public final int getBackupSize() {
        return backup.size();
    }

    public final void clear() {
        backup.clear();
    }
}
