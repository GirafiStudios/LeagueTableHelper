package com.girafi.LeagueTablesHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LeagueTablesHelper {

    public static void main(String[] args) {
        System.out.println("Choose a type: (Format | Sort | Merge): ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("format") || input.equalsIgnoreCase("simple") || input.equalsIgnoreCase("ks") || input.equalsIgnoreCase("fs")) {
            simple();
        } else if (input.equalsIgnoreCase("sort") || input.equalsIgnoreCase("ss")) {
            sort("Input.txt", true);
        } else if (input.equalsIgnoreCase("merge") || input.equalsIgnoreCase("js")) {
            merge();
        }
        System.out.println();
    }

    public static void simple() {
        List<String> list = new ArrayList<>();
        for (String input : readInputFile("Input.txt")) {
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

    private static Set<Map.Entry<String, List<String>>> sort(String fileName, boolean printResult) {
        LinkedHashMap<String, List<String>> hashMap = new LinkedHashMap<>();

        for (String input : readInputFile(fileName)) {
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


        if (printResult) {
            int placing = 0;
            for (Map.Entry<String, List<String>> entry : entrySetSortedByValue) {
                String s = entry.getKey();
                placing++;
                System.out.print("\t" + replaceUnwantedCharacters(s.replace("replaceWithNewLine", "\n") + "\t\t\t\t" + placing + hashMap.get(s).stream().map(m -> "\t" + m).collect(Collectors.toList())));
            }
            System.out.println();
        }
        return entrySetSortedByValue;
    }

    public static void merge() {
        Set<Map.Entry<String, List<String>>> fallStage = sort("Input.txt", false);
        HashMap<String, List<Integer>> fallStageMap = new HashMap<>();
        Set<Map.Entry<String, List<String>>> promotionStage = sort("MergePromotion.txt", false);
        HashMap<String, List<Integer>> promotionStageMap = new HashMap<>();
        Set<Map.Entry<String, List<String>>> relegationStage = sort("MergeRelegation.txt", false);
        Set<Map.Entry<String, List<String>>> combined = new HashSet<>();

        for (Map.Entry<String, List<String>> fallEntry : fallStage) {
            fallStageMap.put(fallEntry.getKey(), fallEntry.getValue().stream().map(Integer::parseInt).collect(Collectors.toList()));
        }
        for (Map.Entry<String, List<String>> promotionEntry : promotionStage) {
            promotionStageMap.put(promotionEntry.getKey(), promotionEntry.getValue().stream().map(Integer::parseInt).collect(Collectors.toList()));
        }

        for (String fallString : fallStageMap.keySet()) {
            if (promotionStageMap.containsKey(fallString)) {
                for (String promotionString : promotionStageMap.keySet()) {
                    List<Integer> fallResults = fallStageMap.get(promotionString);
                    List<Integer> promotionResults = promotionStageMap.get(promotionString);
                    List<Integer> result = IntStream.range(0, fallStageMap.values().size())
                            .mapToObj(i -> fallResults.get(i) + promotionResults.get(i))
                            .collect(Collectors.toList());
                    System.out.println(result);
                }
            }
        }




        //TEMPORARY OUTPUTS JUST FOR REFERENCE
        int placing = 0;
        for (Map.Entry<String, List<String>> entry : fallStage) {
            String s = entry.getKey();
            placing++;
            System.out.print("\t" + replaceUnwantedCharacters(s.replace("replaceWithNewLine", "\n") + "\t\t\t\t" + placing + entry.getValue().stream().map(m -> "\t" + m).collect(Collectors.toList())));
        }
        System.out.println();

        int placing1 = 0;
        for (Map.Entry<String, List<String>> entry : promotionStage) {
            String s = entry.getKey();
            placing1++;
            System.out.print("\t" + replaceUnwantedCharacters(s.replace("replaceWithNewLine", "\n") + "\t\t\t\t" + placing1 + entry.getValue().stream().map(m -> "\t" + m).collect(Collectors.toList())));
        }
        System.out.println();

        int placing2 = 0;
        for (Map.Entry<String, List<String>> entry : relegationStage) {
            String s = entry.getKey();
            placing2++;
            System.out.print("\t" + replaceUnwantedCharacters(s.replace("replaceWithNewLine", "\n") + "\t\t\t\t" + placing2 + entry.getValue().stream().map(m -> "\t" + m).collect(Collectors.toList())));
        }
        System.out.println();
    }

    public static List<String> readInputFile(String fileName) {
        List<String> list = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
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