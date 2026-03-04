package practical13;

import java.io.*;
import java.text.*;
import java.util.*;

public class timeMethods {
    public static int N = 32654; 
    public static Node[] records = new Node[N + 1];
    public static Node[] sortedRecords; 

    public static void main(String args[]) {
        DecimalFormat twoD = new DecimalFormat("0.00");
        DecimalFormat fourD = new DecimalFormat("0.0000");
        DecimalFormat fiveD = new DecimalFormat("0.00000");

        long start, finish;
        double runTime = 0, runTime2 = 0, time;
        double totalTime = 0.0;
        int n = 30; //Number of keys to search 
        int repetition, repetitions = 30;

        //Loading data from file
        loadData("ulysses.numbered");

        sortedRecords = records.clone();
        //Removing null entries and sort
        ArrayList<Node> validNodes = new ArrayList<Node>();
        for (int i = 1; i <= N; i++) {
            if (records[i] != null) {
                validNodes.add(records[i]);
            }
        }
        Collections.sort(validNodes);
        sortedRecords = validNodes.toArray(new Node[0]);

        //Generate 30 random keys for testing
        int[] testKeys = new int[30];
        Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            testKeys[i] = rand.nextInt(N) + 1; //Keys from 1 to N
        }
         runTime = 0;
        runTime2 = 0;

        for(repetition = 0; repetition < repetitions; repetition++) {
            start = System.currentTimeMillis();

            //Call both procedures to time them together 
            for (int i = 0; i < n; i++) {
                linearSearch(records, testKeys[i]);
                binarySearch(sortedRecords, testKeys[i]);
            }
            finish = System.currentTimeMillis();

            time = (double)(finish - start);
            runTime += time;
            runTime2 += (time * time);
        }

        double aveRuntime = runTime/repetitions;
        double stdDeviation = Math.sqrt((runTime2 - repetitions*aveRuntime*aveRuntime)/(repetitions-1));

        //Printing statistics 
        System.out.printf("\n\n\fStatistics\n");
        System.out.println("________________________________________________");
        System.out.println("Total time = " + runTime/1000 + "s.");
        System.out.println("Total time\u00b2 = " + runTime2);
        System.out.println("Average time = " + fiveD.format(aveRuntime/1000)
                + "s. " + '\u00B1' + " " + fourD.format(stdDeviation) + "ms.");
        System.out.println("Standard deviation = " + fourD.format(stdDeviation));
        System.out.println("n = " + n);
        System.out.println("Average time / run = " + fiveD.format(aveRuntime/n*1000)
                + '\u00B5' + "s. ");
        System.out.println("Repetitions = " + repetitions);
        System.out.println("________________________________________________");
        System.out.println();
        System.out.println();
    }

    //Method to load data from ulysses.numbered
    static void loadData(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                //Parse key and data
                String[] parts = line.split("\\s+", 2);
                if (parts.length >= 2) {
                    try {
                        int key = Integer.parseInt(parts[0]);
                        String data = parts[1];
                        if (key >= 1 && key <= N) {
                            records[key] = new Node(key, data);
                            count++;
                        }
                    } catch (NumberFormatException e) {
                        //Skiping lines that don't start with a number
                    }
                }
            }
            reader.close();
            System.out.println("Loaded " + count + " records from " + filename);

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename);
            System.err.println("Creating dummy data for testing...");
            //Creating dummy data if file not found for testing
            for (int i = 1; i <= 1000; i++) {
                records[i] = new Node(i, "Dummy data for key " + i);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    //Linear search implementation
    static String linearSearch(Node[] array, int key) {
        // Search through the array (starting from index 1 as per our storage)
        for (int i = 1; i < array.length; i++) {
            if (array[i] != null && array[i].key == key) {
                return array[i].data;
            }
        }
        return null; //Not found
    }

    //Binary search implementation 
    static String binarySearch(Node[] array, int key) {
        int left = 0;//assumes sorted array
        int right = array.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (array[mid].key == key) {
                return array[mid].data;
            }

            if (array[mid].key < key) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null; //Not found
    }
}
//Node class to store key data pairs
class Node implements Comparable<Node> {
    int key;
    String data;

    Node(int k, String d) {
        key = k;
        data = d;
    }

    public int compareTo(Node other) {
        return Integer.compare(this.key, other.key);
    }
}
