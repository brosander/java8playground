package com.github.java8playground;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Created by bryan on 4/5/16.
 */
public class StreamDemo {
    @Test
    public void testFilterOdd() {
        List<Integer> integers = Arrays.asList(0, 1, 2, 3, 4, 5, 6);

        // Iterative
        List<Integer> evenIntegersIterative = new ArrayList<>();
        for (Integer integer : integers) {
            if (integer % 2 == 0) {
                evenIntegersIterative.add(integer);
            }
        }


        // Stream
        List<Integer> evenIntegersStream = integers.stream()
                .filter(integer -> integer % 2 == 0)
                .collect(Collectors.toList());

        assertEquals( evenIntegersIterative, evenIntegersStream);
    }

    @Test
    public void testMapParseIntegers() {
        List<String> strings = Arrays.asList("0", "1", "2", "3", "4", "5", "6");

        // Iterative
        List<Integer> integersIterative = new ArrayList<>();
        for (String string : strings) {
            integersIterative.add(Integer.parseInt(string));
        }

        // Stream
        List<Integer> integersStream = strings.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        assertEquals(integersIterative, integersStream);
    }
}
