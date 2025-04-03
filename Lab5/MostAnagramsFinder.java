import java.io.*;
import java.util.*;

public class MostAnagramsFinder {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java MostAnagramsFinder <dictionary file>");
            System.exit(1);
        }

        String filename = args[0];
        Map<String, List<String>> anagramMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String word;
            while ((word = reader.readLine()) != null) {
                word = word.trim();
                if (!word.isEmpty()) {
                    String sortedKey = getSortedKey(word);
                    anagramMap.computeIfAbsent(sortedKey, k -> new ArrayList<>()).add(word);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Cannot open file '" + filename + "' for input.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error: An I/O error occurred reading '" + filename + "'.");
            System.exit(1);
        }

        List<List<String>> maxAnagramGroups = new ArrayList<>();
        int maxSize = 0;

        for (List<String> group : anagramMap.values()) {
            if (group.size() > 1) {
                if (group.size() > maxSize) {
                    maxSize = group.size();
                    maxAnagramGroups.clear();
                }
                if (group.size() == maxSize) {
                    Collections.sort(group);
                    maxAnagramGroups.add(group);
                }
            }
        }

        if (maxAnagramGroups.isEmpty()) {
            System.out.println("No anagrams found.");
        } else {
            System.out.println("Groups: " + maxAnagramGroups.size() + ", Anagram count: " + maxSize);
            maxAnagramGroups.sort(Comparator.comparing(a -> a.get(0)));
            for (List<String> group : maxAnagramGroups) {
                System.out.println(group.toString().replaceAll(",", ""));
            }
        }
    }

    private static String getSortedKey(String word) {
        char[] chars = word.toLowerCase().toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}
