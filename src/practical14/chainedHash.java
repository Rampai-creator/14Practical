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

    
