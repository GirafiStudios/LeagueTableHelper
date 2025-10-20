package com.girafi.leaguetableshelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FormatterHelper {
    public static void main(String[] args) {
        Scanner sc = null;
        try {
            sc = new Scanner(new File("zFormatThis.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<String> tokens = new ArrayList<>();

        // Read all tokens, but skip any stray single-letter words
        while (sc.hasNext()) {
            String t = sc.next();
            if (t.matches("[A-Za-z]")) {
                continue;
            }
            tokens.add(t);
        }
        sc.close();

        int i = 0;
        // Process one record at a time
        while (i < tokens.size()) {
            // 1) position
            String pos = tokens.get(i++);

            // 2) team name: collect until we hit the first stat token
            StringBuilder nameBuf = new StringBuilder();
            while (i < tokens.size() &&
                    !(tokens.get(i).matches("\\d+") || tokens.get(i).matches("\\d+-\\d+"))) {
                nameBuf.append(tokens.get(i++)).append(" ");
            }
            String team = nameBuf.toString().trim();

            // 3) exactly 6 stat fields: GP, W, D, L, GF-GA, Pts
            List<String> stats = new ArrayList<>();
            for (int j = 0; j < 6 && i < tokens.size(); j++) {
                stats.add(tokens.get(i++));
            }

            // 4) output as one tab-delimited line
            System.out.print(pos + "\t" + team);
            for (String s : stats) {
                System.out.print("\t" + s);
            }
            System.out.println();
        }
    }
}