package com.github.java8playground;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
