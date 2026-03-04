public class chainedHash {

    //Internal linked-list node
    private static class ChainNode {
        String key;
        String value;
        ChainNode next;

        ChainNode(String k, String v) {
            key = k;
            value = v;
            next = null;
        }
    }

    private ChainNode[] table; //table[1..m]; table[0] unused
    private int m;
    private int count;

    //Constructor
    public chainedHash(int m) {
        this.m = m;
        table = new ChainNode[m + 1]; 
        count = 0;
    }

    //Same hash function as openHash
    public int hash(String key) {
        int h = key.hashCode();
        if (h < 0) h = -h;
        return (h % m) + 1;
    }
  
    public void insert(String key, String value) {
        int i = hash(key);
        ChainNode current = table[i];

        //Walk the chain looking for the key
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value; // update existing
                return;
            }
            current = current.next;
        }

        //Key not found
        ChainNode newNode = new ChainNode(key, value);
        newNode.next = table[i];
        table[i] = newNode;
        count++;
    }
    //Lookup: return value for key, or null if absent
    public String lookup(String key) {
        int i = hash(key);
        ChainNode current = table[i];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    //Remove: return value and delete the node; null if not found
    public String remove(String key) {
        int i = hash(key);
        ChainNode current = table[i];
        ChainNode prev = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (prev == null) {
                    table[i] = current.next;
                } else {
                    prev.next = current.next;
                }
                count--;
                return current.value;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }

    public boolean isInTable(String key) {
        return lookup(key) != null;
    }

    //Chained hashing is never "full" in the traditional sense
    public boolean isFull() {
        return false;
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


    
