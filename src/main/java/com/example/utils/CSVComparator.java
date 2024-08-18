package com.example.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CSVComparator {
    public static Set<String> readCSV(String filePath) throws IOException {
        Set<String> records = new HashSet<>();
        BufferedReader csvReader = new BufferedReader(new FileReader(filePath));
        String row;

        // Skip header
        csvReader.readLine();

        while ((row = csvReader.readLine()) != null) {
            records.add(row.trim());
        }
        csvReader.close();
        return records;
    }

    public static void compareCSVFiles(String filePath1, String filePath2) throws IOException {
        Set<String> file1Records = readCSV(filePath1);
        Set<String> file2Records = readCSV(filePath2);

        Set<String> difference = new HashSet<>(file1Records);
        difference.removeAll(file2Records);

        System.out.println("Differences between the files:");
        System.out.println(difference.size());
    }
}
