package com.github.java8playground;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

        assertEquals(evenIntegersIterative, evenIntegersStream);
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

    @Test
    public void testParseUserAndSort() {
        List<String> names = Arrays.asList("Tiffany ReportAuthor", "Suzy PowerUser", "Pat BusinessAnalyst", "Joe Admin");

        // Iterative
        List<User> usersIterative = new ArrayList<>(names.size());
        for (String name : names) {
            usersIterative.add(User.create(name));
        }
        Collections.sort(usersIterative);

        // Streaming
        List<User> usersStreaming = names.stream().map(User::create).sorted().collect(Collectors.toList());

        assertEquals(usersIterative, usersStreaming);
    }

    private Stream<String> readIlliad() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("Illiad.txt")));
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<String>() {
            String next = bufferedReader.readLine();

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public String next() {
                if (next == null) {
                    throw new NoSuchElementException();
                }
                String result = next;
                try {
                    next = bufferedReader.readLine();
                } catch (IOException e) {
                    next = null;
                    e.printStackTrace();
                }
                return result;
            }
        }, Spliterator.ORDERED), false).onClose(() -> {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                // Ignore
            }
        });
    }

    @Test
    public void testWordCount() throws IOException {
        // Iterative
        Map<String, Integer> iterativeResult = new HashMap<>();
        for (String line : readIlliad().collect(Collectors.toList())) {
            String[] words = line.split("\\s+");
            for (String word : words) {
                String trimmedWord = word.trim().toLowerCase();
                if (trimmedWord.length() > 0) {
                    Integer original = iterativeResult.get(trimmedWord.toLowerCase());
                    if (original == null) {
                        iterativeResult.put(trimmedWord.toLowerCase(), 1);
                    } else {
                        iterativeResult.put(trimmedWord.toLowerCase(), original + 1);
                    }
                }
            }
        }

        // Stream
        Map<String, Integer> streamResult = readIlliad()
                .parallel()
                .flatMap(s -> Arrays.stream(s.trim().split("\\s+")))
                .filter(s -> s.trim().length() > 0)
                .map(s -> Collections.singletonMap(s.trim().toLowerCase(), 1))
                .sequential()
                .reduce(new HashMap<>(), (partialResult, nextElement) -> {
                    nextElement.entrySet().forEach(nextElementEntry -> partialResult.merge(nextElementEntry.getKey(), nextElementEntry.getValue(), Integer::sum));
                    return partialResult;
                });

        // Print results
        streamResult.entrySet().stream().sorted((o1, o2) -> {
            int i = o2.getValue() - o1.getValue();
            if (i != 0) {
                return i;
            }
            return o1.getKey().compareTo(o2.getKey());
        }).limit(50).forEachOrdered(stringIntegerEntry -> System.out.println(stringIntegerEntry.getKey() + " -> " + stringIntegerEntry.getValue()));

        // Make sure they're the same
        assertEquals(iterativeResult, streamResult);
    }

    public static class ImmutableEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private final V value;

        public ImmutableEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException("Immutable entry");
        }
    }

    public static class User implements Comparable<User> {
        private final String firstName;
        private final String lastName;

        public User(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public static User create(String fullName) {
            String[] split = fullName.split(" ");
            return new User(split[0], split[1]);
        }

        @Override
        public int compareTo(User o) {
            int last = lastName.compareTo(o.lastName);
            if (last != 0) {
                return last;
            }
            return firstName.compareTo(o.firstName);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            User user = (User) o;

            if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
            return lastName != null ? lastName.equals(user.lastName) : user.lastName == null;

        }

        @Override
        public int hashCode() {
            int result = firstName != null ? firstName.hashCode() : 0;
            result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "User{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    '}';
        }
    }
}
