package com.ral.young.basic.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author renyunhui
 * @description 这是一个SteamExample类
 * @date 2024-12-26 14-04-43
 * @since 1.0.0
 */
public class SteamExample {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        Integer reduce = numbers.stream().reduce(0, Integer::sum);
        System.out.println(reduce);

        List<String> list = Arrays.asList("apple", "banana", "cherry", "date", "elderberry", "fig", "grape");

        // 1. Filter: Select elements that start with 'a'
        List<String> filteredList = list.stream()
                .filter(s -> s.startsWith("a"))
                .collect(Collectors.toList());
        System.out.println("Filtered List: " + filteredList);

        // 2. Map: Convert each string to its length
        List<Integer> lengths = list.stream()
                .map(String::length)
                .collect(Collectors.toList());
        System.out.println("Lengths: " + lengths);

        // 3. Sorted: Sort the list alphabetically
        List<String> sortedList = list.stream()
                .sorted()
                .collect(Collectors.toList());
        System.out.println("Sorted List: " + sortedList);

        // 4. Distinct: Remove duplicates
        List<String> distinctList = list.stream()
                .distinct()
                .collect(Collectors.toList());
        System.out.println("Distinct List: " + distinctList);

        // 5. Collect: Group by the first letter
        Map<Character, List<String>> groupedByFirstLetter = list.stream()
                .collect(Collectors.groupingBy(s -> s.charAt(0)));
        System.out.println("Grouped by First Letter: " + groupedByFirstLetter);

        // 6. Reduce: Concatenate all strings
        String concatenated = list.stream()
                .reduce("", (a, b) -> a + b);
        System.out.println("Concatenated String: " + concatenated);

        // 7. AnyMatch: Check if any string starts with 'b'
        boolean anyStartsWithB = list.stream()
                .anyMatch(s -> s.startsWith("b"));
        System.out.println("Any starts with 'b': " + anyStartsWithB);

        // 8. AllMatch: Check if all strings have length greater than 3
        boolean allLengthGreaterThan3 = list.stream()
                .allMatch(s -> s.length() > 3);
        System.out.println("All length greater than 3: " + allLengthGreaterThan3);

        // 9. NoneMatch: Check if no string starts with 'z'
        boolean noneStartsWithZ = list.stream()
                .noneMatch(s -> s.startsWith("z"));
        System.out.println("None starts with 'z': " + noneStartsWithZ);

        // 10. FlatMap: Flatten a list of lists
        List<List<String>> listOfLists = Arrays.asList(
                Arrays.asList("a", "b", "c"),
                Arrays.asList("d", "e", "f"),
                Arrays.asList("g", "h", "i")
        );

        // flatMap 方法在 Java 的 Stream API 中用于将流中的每个元素转换为另一个流，
        // 然后将这些流合并成一个单一的流。这在处理集合的集合时特别有用，可以将所有元素处理成一个单一的流。
        List<String> flatList = listOfLists.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        System.out.println(flatList);
    }
}
