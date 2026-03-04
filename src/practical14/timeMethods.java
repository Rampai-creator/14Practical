import java.text.*;
import java.util.*;

public class timeMethods {

    //Data generation
    static final int N = 1 << 20; 
    static String[] kvKeys = new String[N + 1]; //shuffled keys (1-indexed)
    static String[] kvValues = new String[N + 1];

    //Load factors to test and the corresponding number of entries to insert
    static final double[] ALPHAS = { 0.75, 0.80, 0.85, 0.90, 0.95 };
    static final int[] LOAD_SIZES = { 750_000, 800_000, 850_000, 900_000, 950_000 };

    static final int REPETITIONS = 30; //statistical requirement
    static final int LOOKUP_N = 500; //lookups per repetition

    public static void main(String[] args) {

        DecimalFormat fmt6 = new DecimalFormat("0.000000");
        DecimalFormat fmt4 = new DecimalFormat("0.0000");

        System.out.println("Generating and shuffling " + N + " key-value pairs...");
        generateData();
        System.out.println("Data ready.\n");

        //Results table header
        System.out.println("=".repeat(72));
        System.out.printf("%-22s %8s %12s %12s%n",
                "Method", "Alpha", "Open (s)", "Chained (s)");
        System.out.println("=".repeat(72));

        for (int idx = 0; idx < ALPHAS.length; idx++) {
            double alpha = ALPHAS[idx];
            int loadSize = LOAD_SIZES[idx];

            //choosing a prime m so that alpha = loadSize/m exactly
            //m >= loadSize / alpha; we want m >= loadSize
            int mRaw = (int) Math.ceil(loadSize / alpha) + 10;
            int mPrime = nextPrime(mRaw);

            //build and fill the two tables once 
            openHash oh = buildOpenHash(mPrime, loadSize);
            chainedHash ch = buildChainedHash(mPrime, loadSize);

            //Choose LOOKUP_N lookup keys spread across the inserted range
            String[] lookupKeys = new String[LOOKUP_N];
            Random rng = new Random(42L);
            for (int k = 0; k < LOOKUP_N; k++) {
                lookupKeys[k] = kvKeys[rng.nextInt(loadSize) + 1];
            }
            //time open hash lookups
            double openTotal = 0, openTotal2 = 0;
            for (int rep = 0; rep < REPETITIONS; rep++) {
                long start = System.currentTimeMillis();
                for (String key : lookupKeys) {
                    oh.lookup(key);
                }
                long finish = System.currentTimeMillis();
                double t = finish - start;
                openTotal += t;
                openTotal2 += t * t;
            }
            double openAvg = openTotal / REPETITIONS / 1000.0; //seconds

            //time chained hash lookups
            double chainTotal = 0, chainTotal2 = 0;
            for (int rep = 0; rep < REPETITIONS; rep++) {
                long start = System.currentTimeMillis();
                for (String key : lookupKeys) {
                    ch.lookup(key);
                }
                long finish = System.currentTimeMillis();
                double t = finish - start;
                chainTotal += t;
                chainTotal2 += t * t;
            }
            double chainAvg = chainTotal / REPETITIONS / 1000.0; //seconds

            //standard deviations (ms)
            double openSD = stdDev(openTotal, openTotal2, REPETITIONS);
            double chainSD = stdDev(chainTotal, chainTotal2, REPETITIONS);

            //print row (Displaying)
            System.out.printf("Hash \u03b1 = %2.0f%% N = %7d %8s %12s %12s%n",
                    alpha * 100, loadSize,
                    "",
                    fmt6.format(openAvg),
                    fmt6.format(chainAvg));

            //print detailed stats below each row
            System.out.printf(" 1/(1-\u03b1) = %5.1f open \u00b1%.4fms chained \u00b1%.4fms%n",
                    1.0 / (1.0 - alpha), openSD, chainSD);
        }

        System.out.println("=".repeat(72));
        System.out.println("\nAll times are averages over " + REPETITIONS +
                " repetitions, each doing " + LOOKUP_N + " lookups.");
    }

    //Data generation: produce N shuffled integer keys numbered 1..N,
    //then assign String values "1".."N" to the shuffled order.
   
    static void generateData() {
        //Create keys 1..N
        Integer[] keys = new Integer[N];
        for (int i = 0; i < N; i++) keys[i] = i + 1;

        //Shuffle 
        List<Integer> keyList = Arrays.asList(keys);
        Collections.shuffle(keyList, new Random(12345L));

        //Assign: shuffled key gets String value = position in shuffle (1-indexed)
        for (int pos = 0; pos < N; pos++) {
            kvKeys[pos + 1] = String.valueOf(keyList.get(pos)); //key
            kvValues[pos + 1] = String.valueOf(pos + 1); //value = shuffle rank
        }
    }
