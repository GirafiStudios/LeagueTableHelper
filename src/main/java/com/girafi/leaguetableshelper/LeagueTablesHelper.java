package com.girafi.leaguetableshelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LeagueTablesHelper {
    public static final int YEAR_START = 2023;
    public static final int YEAR_END = 2024;

    public static void main(String[] args) {
        try {
            System.out.println("Choose a type: (Format | Sort | Combine | Merge | JS | DS): ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("format") || input.equalsIgnoreCase("simple") || input.equalsIgnoreCase("ks") || input.equalsIgnoreCase("fs")) {
                simple();
            } else if (input.equalsIgnoreCase("sort") || input.equalsIgnoreCase("ss")) {
                sort(streamCSV("Input.txt").map(Entry::fromCSV).collect(Collectors.toList()), true);
            } else if (input.equalsIgnoreCase("combine")) {
                List<Entry> inputList = streamCSV("Input.txt").map(Entry::fromCSV).collect(Collectors.toList());

                int placing = 0;
                for (Entry entry : inputList) {
                    placing++;
                    entry.print(placing);
                }
            } else if (input.equalsIgnoreCase("merge") || input.equalsIgnoreCase("js")) {
                List<Entry> inputList = streamCSV("Input.txt").map(Entry::fromCSV).collect(Collectors.toList());
                merge(input, mergeLists(streamCSV("MergePromotion.txt").map(Entry::fromCSV).toList(), inputList), mergeLists(streamCSV("MergeRelegation.txt").map(Entry::fromCSV).toList(), inputList));
            } else if (input.equalsIgnoreCase("ds")) { //How to use: Put all teams in both groups for MergePromotion & Merge Relegation. Put the teams in the DS Pulje you need the league table for in Input
                List<Entry> inputList = streamCSV("Input.txt").map(Entry::fromCSV).collect(Collectors.toList());
                List<String> inputNames = inputList.stream().map(entry -> entry.name).toList();
                List<Entry> fullPromotion = mergeLists(streamCSV("MergePromotion.txt").map(Entry::fromCSV).toList(), new ArrayList<>());
                List<Entry> fullRelegation = mergeLists(streamCSV("MergeRelegation.txt").map(Entry::fromCSV).toList(), new ArrayList<>());

                fullPromotion.removeIf(fp -> !inputNames.contains(fp.name));
                fullRelegation.removeIf(fp -> !inputNames.contains(fp.name));

                merge(input, fullPromotion, fullRelegation);
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void merge(String input, List<Entry> fullPromotion, List<Entry> fullRelegation) throws IOException {
        sortList(fullPromotion);
        sortList(fullRelegation);

        String js1 = "\t\t" + "JS1";
        String js2 = "\t\t" + "JS2";
        String promotion = "\t\t" + "Promotion Stage";
        String relegation = "\t\t" + "Relegation Stage";
        int placing = 0;
        for (Entry entry : fullPromotion) {
            placing++;
            entry.print(placing, input.equalsIgnoreCase("js") ? js1 : promotion);
        }
        for (Entry entry : fullRelegation) {
            placing++;
            entry.print(placing, input.equalsIgnoreCase("js") ? js2 : relegation);
        }
    }

    private static List<Entry> sort(List<Entry> entries, boolean print) {
        entries = new ArrayList<>(entries);
        sortList(entries);

        if (print) {
            int placing = 0;
            for (Entry entry : entries) {
                placing++;
                entry.print(placing);
            }
        }
        return entries;
    }

    public static List<Entry> mergeLists(List<Entry> entries, List<Entry> otherEntries) {
        var otherEntriesByName = otherEntries.stream()
                .collect(Collectors.toMap(Entry::name, Function.identity()));
        return entries.stream().map(entry -> {
            var other = otherEntriesByName.get(entry.name());
            return other != null ? Entry.merge(entry, other) : entry;
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    private static List<Entry> sortList(List<Entry> list) {
        list.sort(
                Comparator.comparingInt(Entry::points)
                        .thenComparingInt(Entry::goalDifference)
                        .thenComparingInt(Entry::goalsFor)
                        .reversed()
        );
        return list;
    }

    private static Stream<String[]> streamCSV(String fileName) throws IOException {
        return Files.lines(Path.of(fileName), StandardCharsets.ISO_8859_1).map(line -> line.split("\\t|((?!\\d)-(?=\\d))"));
    }

    private record Entry(String name, int played, int wins, int draws, int losses, int goalsFor, int goalsAgainst, int points) {

        public int goalDifference() {
            return goalsFor - goalsAgainst;
        }

        public void print(int placing, String... additional) {
            String tab = "\t";
            System.out.println(name + tab + YEAR_START + tab + YEAR_END + tab + tab + placing + tab + played + tab + wins + tab + draws + tab + losses + tab + goalsFor + tab + goalsAgainst + tab + points + tab + 1 + tab + (additional.length > 0 ? additional[0].replace(tab, "") : ""));       }

        public static Entry fromCSV(String[] columns) {
            return new Entry(
                    columns[1],
                    Integer.parseInt(columns[2]),
                    Integer.parseInt(columns[3]),
                    Integer.parseInt(columns[4]),
                    Integer.parseInt(columns[5]),
                    Integer.parseInt(columns[6]),
                    Integer.parseInt(columns[7]),
                    Integer.parseInt(columns[8])
            );
        }

        public static Entry merge(Entry first, Entry second) {
            return new Entry(
                    first.name(),
                    first.played() + second.played(),
                    first.wins() + second.wins(),
                    first.draws() + second.draws(),
                    first.losses() + second.losses(),
                    first.goalsFor() + second.goalsFor(),
                    first.goalsAgainst() + second.goalsAgainst(),
                    first.points() + second.points()
            );
        }
    }

    public static void simple() {
        List<String> list = new ArrayList<>();

        List<String> readInputList = readInputFile("Input.txt");
        Collections.reverse(readInputList);
        for (String input : readInputList) {
            String[] data = input.split("\\t|((?!\\d)-(?=\\d))");

            list.add(0, "replaceWithNewLine" + data[1]); //Team
            list.add(1, String.valueOf(YEAR_START));
            list.add(2, String.valueOf(YEAR_END));
            list.add(3, ""); //Empty for Excel output purposes
            list.add(4, data[0]); // Placing
            list.add(5, data[2]); // Played
            list.add(6, data[3]); //Wins
            list.add(7, data[4]); //Draws
            list.add(8, data[5]); //Loses
            list.add(9, data[6]); //Goals For
            list.add(10, data[7]); //Goals Against
            list.add(11, data[8]); //Points
            list.add(12, "1"); //Overall
        }

        for (String s : list) {
            System.out.print("\t" + replaceUnwantedCharacters(s.replace("replaceWithNewLine", "\n")));
        }
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