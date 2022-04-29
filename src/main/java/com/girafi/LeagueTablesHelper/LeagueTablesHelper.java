package com.girafi.LeagueTablesHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LeagueTablesHelper {

    public static void main(String[] args) {
        System.out.println("Choose a type: (Format | Sort | Merge): ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("format") || input.equalsIgnoreCase("simple")) {
            simple();
        } else if (input.equalsIgnoreCase("sort")) {
            sort();
        } else if (input.equalsIgnoreCase("merge")) {
            //TODO
            System.out.println("NOT IMPLEMENTED YET");
        }
        System.out.println();
    }

    public static void simple() {
        List<String> list = new ArrayList<>();
        for (String input : readInputFile()) {
            String[] data = input.split("\\t|((?!\\d)-(?=\\d))");

            list.add(0, "replaceWithNewLine" + data[1]); //Team
            list.add(1, ""); //Empty for Excel output purposes
            list.add(2, ""); //Empty for Excel output purposes
            list.add(3, ""); //Empty for Excel output purposes
            list.add(4, data[0]); // Placing
            list.add(5, data[2]); // Played
            list.add(6, data[3]); //Wins
            list.add(7, data[4]); //Draws
            list.add(8, data[5]); //Loses
            list.add(9, data[6]); //Goals For
            list.add(10, data[7]); //Goals Against
            list.add(11, data[8]); //Points
        }

        for (String s : list) {
            System.out.print("\t" + replaceUnwantedCharacters(s.replace("replaceWithNewLine", "\n")));
        }
    }

    private static void sort() {
        LinkedHashMap<String, List<String>> hashMap = new LinkedHashMap<>();

        for (String input : readInputFile()) {
            String[] data = input.split("\\t|((?!\\d)-(?=\\d))");

            List<String> leagueEntries = new ArrayList<>();
            leagueEntries.add(0, data[2]); //Played
            leagueEntries.add(1, data[3]); //Wins
            leagueEntries.add(2, data[4]); //Draws
            leagueEntries.add(3, data[5]); //Loses
            leagueEntries.add(4, data[6]); //Goals For
            leagueEntries.add(5, data[7]); //Goals Against
            leagueEntries.add(6, data[8]); //Points
            hashMap.put("replaceWithNewLine" + data[1], leagueEntries); //Team name, used as HashMap identifier
        }

        Set<Map.Entry<String, List<String>>> entriesToBeSorted = hashMap.entrySet();

        Comparator<Map.Entry<String, List<String>>> comparator = (o1, o2) -> {
            int points = Integer.parseInt(o1.getValue().get(6));
            int points2 = Integer.parseInt(o2.getValue().get(6));

            if (points == points2) {
                int goalFor = Integer.parseInt(o1.getValue().get(4));
                int goalAgainst = Integer.parseInt(o1.getValue().get(5));
                int goalFor2 = Integer.parseInt(o2.getValue().get(4));
                int goalAgainst2 = Integer.parseInt(o2.getValue().get(5));
                int goalDifference = goalFor - goalAgainst;
                int goalDifference2 = goalFor2 - goalAgainst2;
                if (goalDifference == goalDifference2) {
                    return Integer.compare(goalFor2, goalFor);
                } else {
                    return Integer.compare(goalDifference2, goalDifference);
                }
            } else {
                return Integer.compare(points2, points);
            }
        };

        List<Map.Entry<String, List<String>>> entries = new ArrayList<>(entriesToBeSorted);
        entries.sort(comparator);

        LinkedHashMap<String, List<String>> sortedByValue = new LinkedHashMap<>(entries.size());
        for (Map.Entry<String, List<String>> entry : entries) {
            sortedByValue.put(entry.getKey(), entry.getValue());
        }
        Set<Map.Entry<String, List<String>>> entrySetSortedByValue = sortedByValue.entrySet();


        int placing = 0;
        for (Map.Entry<String, List<String>> entry : entrySetSortedByValue) {
            String s = entry.getKey();
            placing++;
            System.out.print("\t" + replaceUnwantedCharacters(s.replace("replaceWithNewLine", "\n") + "\t\t\t\t" + placing +  hashMap.get(s).stream().map(m -> "\t" + m).collect(Collectors.toList())));
        }
        System.out.println();
    }

    public static List<String> readInputFile() {
        List<String> list = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("Input.txt"));
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String replaceUnwantedCharacters(String input) {
        return input.replace(",", "").replace("[", "").replace("]", "");
    }
}