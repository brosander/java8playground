package com.github.java8playground;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by bryan on 4/5/16.
 */
public class ParallelArrayDemo {
    @Test
    public void testParallelArraySort() {
        Random random = new Random();
        int size = 1_000_000;
        int warmupNumber = 10;
        int runNumber = 50;
        int numOccurrences = (warmupNumber + runNumber) * 2;

        System.out.println("Array size: " + size);
        System.out.println("Warmup number: " + warmupNumber);
        System.out.println("Run number: " + runNumber);

        System.out.println("Generating " + (1 + warmupNumber + runNumber) + " arrays of size " + size);
        long beforeArrays = System.currentTimeMillis();
        int[] original = IntStream.generate(random::nextInt).limit(size).toArray();
        int[][] copies = Stream.generate(() -> Arrays.copyOf(original, original.length)).limit(numOccurrences).toArray(value -> new int[numOccurrences][]);
        System.out.println("Finished generating arrays after " + (System.currentTimeMillis() - beforeArrays) / 1000.0 + " seconds");

        System.out.println("Starting " + warmupNumber + " warmup sorts of each type");
        long beforeWarmup = System.currentTimeMillis();
        int warmupMax = warmupNumber * 2;
        for (int i = 0; i < warmupMax; i += 2) {
            Arrays.sort(copies[i]);
            Arrays.parallelSort(copies[warmupNumber + i]);
        }
        System.out.println("Finished warmup after " + (System.currentTimeMillis() - beforeWarmup) / 1000.0 + " seconds");

        System.out.println("Starting " + runNumber + " benchmark runs of each type");
        long beforeBenchmark = System.currentTimeMillis();
        long normalTime = 0;
        long parallelTime = 0;
        int runMax = runNumber * 2;
        for (int i = 0; i < runMax; i += 2) {
            int normalIndex = warmupMax + i;
            int parallelIndex = normalIndex + 1;

            long before = System.currentTimeMillis();
            Arrays.sort(copies[normalIndex]);
            normalTime += (System.currentTimeMillis() - before);

            before = System.currentTimeMillis();
            Arrays.parallelSort(copies[parallelIndex]);
            parallelTime += (System.currentTimeMillis() - before);
        }
        System.out.println("Finished benchmark after " + (System.currentTimeMillis() - beforeBenchmark) / 1000.0 + " seconds");
        System.out.println(runNumber + " normal sorts took a total of " + normalTime / 1000.0 + " seconds for an average of " + (normalTime / (runNumber * 1000.0)) + " seconds per sort");
        System.out.println(runNumber + " parallel sorts took a total of " + parallelTime / 1000.0 + " seconds for an average of " + (parallelTime / (runNumber * 1000.0)) + " seconds per sort");
    }
}
