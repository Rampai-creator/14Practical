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
