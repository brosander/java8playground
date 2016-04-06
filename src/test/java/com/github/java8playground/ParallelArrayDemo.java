package com.github.java8playground;

import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by bryan on 4/5/16.
 */
public class ParallelArrayDemo {
    private Comparator<Integer> makeComparator(int expensiveRuns) {
        return (o1, o2) -> {
            String myString = "";
            for (int i = 0; i < expensiveRuns; i++) {
                myString += "expensive concatenation ";
            }
            return Integer.compare(o1, o2);
        };
    }

    @Test
    public void testNoComparator() {
        benchmark(1_000_000, 10, 25, Arrays::sort, Arrays::parallelSort);
    }

    @Test
    public void testExpensiveComparator() {
        Comparator<Integer> expensiveComparator = makeComparator(5);
        benchmark(100_000, 5, 10, integers -> Arrays.sort(integers, expensiveComparator), integers -> Arrays.parallelSort(integers, expensiveComparator));
    }

    @Test
    public void testCheapComparator() {
        Comparator<Integer> expensiveComparator = makeComparator(0);
        benchmark(100_000, 5, 10, integers -> Arrays.sort(integers, expensiveComparator), integers -> Arrays.parallelSort(integers, expensiveComparator));
    }

    public void benchmark(int arraySize, int warmupRuns, int benchmarkRuns, Consumer<Integer[]> baseline, Consumer<Integer[]> test) {
        Random random = new Random();
        int numOccurrences = (warmupRuns + benchmarkRuns) * 2;

        System.out.println("Array size: " + arraySize);
        System.out.println("Warmup number: " + warmupRuns);
        System.out.println("Run number: " + benchmarkRuns);

        System.out.println("Generating " + (1 + warmupRuns + benchmarkRuns) + " arrays of size " + arraySize);
        long beforeArrays = System.currentTimeMillis();
        Integer[] original = Stream.generate(random::nextInt).limit(arraySize).toArray(Integer[]::new);
        Integer[][] copies = Stream.generate(() -> Arrays.copyOf(original, original.length)).limit(numOccurrences).toArray(value -> new Integer[numOccurrences][]);
        System.out.println("Finished generating arrays after " + (System.currentTimeMillis() - beforeArrays) / 1000.0 + " seconds");

        System.out.println("Starting " + warmupRuns + " warmup sorts of each type");
        long beforeWarmup = System.currentTimeMillis();
        int warmupMax = warmupRuns * 2;
        for (int i = 0; i < warmupMax; i += 2) {
            baseline.accept(copies[i]);
            test.accept(copies[warmupRuns + i]);
        }
        System.out.println("Finished warmup after " + (System.currentTimeMillis() - beforeWarmup) / 1000.0 + " seconds");

        System.out.println("Starting " + benchmarkRuns + " benchmark runs of each type");
        long beforeBenchmark = System.currentTimeMillis();
        long normalTime = 0;
        long parallelTime = 0;
        int runMax = benchmarkRuns * 2;
        for (int i = 0; i < runMax; i += 2) {
            int normalIndex = warmupMax + i;
            int parallelIndex = normalIndex + 1;

            long before = System.currentTimeMillis();
            baseline.accept(copies[normalIndex]);
            normalTime += (System.currentTimeMillis() - before);

            before = System.currentTimeMillis();
            test.accept(copies[parallelIndex]);
            parallelTime += (System.currentTimeMillis() - before);
        }
        System.out.println("Finished benchmark after " + (System.currentTimeMillis() - beforeBenchmark) / 1000.0 + " seconds");
        System.out.println(benchmarkRuns + " baseline runs took a total of " + normalTime / 1000.0 + " seconds for an average of " + (normalTime / (benchmarkRuns * 1000.0)) + " seconds per sort");
        System.out.println(benchmarkRuns + " test runs took a total of " + parallelTime / 1000.0 + " seconds for an average of " + (parallelTime / (benchmarkRuns * 1000.0)) + " seconds per sort");
    }
}
