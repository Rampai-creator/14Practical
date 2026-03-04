public class openHash {
    private String[] keys;
    private String[] values;
    private boolean[] deleted; //tombstone markers for removed entries
    private int m; //table size (should be prime and >= N)
    private int count; 
    private static final int PROBE_PRIME = 7; //prime used for linear probing step

    //Constructor: m should be a prime >= N
    public openHash(int m) {
        this.m = m;
        keys = new String[m + 1]; 
        values = new String[m + 1];
        deleted = new boolean[m + 1];
        count = 0;
    }

    //Hash function: maps a String key to an index in [1..m]
    //Uses Java's hashCode, made positive, then mod m, then +1 to land in [1..m]
    public int hash(String key) {
        int h = key.hashCode();
        if (h < 0) h = -h;
        return (h % m) + 1;
    }

    //Insert (key, value) pair into the open hash table.
    //Uses linear probing: next probe = (current + PROBE_PRIME) mod m + 1
    public void insert(String key, String value) {
        if (isFull()) {
            throw new RuntimeException("Hash table is full");
        }
        int i = hash(key);
        int firstDeleted = -1; 
        int probes = 0;

        while (probes < m) {
            if (keys[i] == null) {
                //Empty slot 
                int insertAt = (firstDeleted != -1) ? firstDeleted : i;
                keys[insertAt] = key;
                values[insertAt] = value;
                deleted[insertAt] = false;
                count++;
                return;
            } else if (deleted[i]) {
                //Tombstone slot 
                if (firstDeleted == -1) firstDeleted = i;
            } else if (keys[i].equals(key)) {
                //Key already present update value
                values[i] = value;
                return;
            }
            i = (i - 1 + PROBE_PRIME) % m + 1; //linear probe step
            probes++;
        }

        //If we only found tombstones and no empty slot, reuse first tombstone
        if (firstDeleted != -1) {
            keys[firstDeleted] = key;
            values[firstDeleted] = value;
            deleted[firstDeleted] = false;
            count++;
        }
    }

    //Lookup: return value for key, or null if not found
    public String lookup(String key) {
        int i = hash(key);
        int probes = 0;

        while (probes < m) {
            if (keys[i] == null && !deleted[i]) {
                return null; //emptyslot means key absent
            }
            if (!deleted[i] && keys[i] != null && keys[i].equals(key)) {
                return values[i];
            }
            i = (i - 1 + PROBE_PRIME) % m + 1;
            probes++;
        }
        return null;
    }

    //Remove: return value and delete the entry
    public String remove(String key) {
        int i = hash(key);
        int probes = 0;

        while (probes < m) {
            if (keys[i] == null && !deleted[i]) {
                return null; //not found
            }
            if (!deleted[i] && keys[i] != null && keys[i].equals(key)) {
                String val = values[i];
                keys[i] = null;
                values[i] = null;
                deleted[i] = true; //tombstone
                count--;
                return val;
            }
            i = (i - 1 + PROBE_PRIME) % m + 1;
            probes++;
        }
        return null;
    }

    public boolean isInTable(String key) {
        return lookup(key) != null;
    }

    public boolean isFull() {
        return count >= m;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    public int tableSize() {
        return m;
    }
}
