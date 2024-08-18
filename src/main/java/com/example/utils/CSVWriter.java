package com.example.utils;

import com.example.entity.Device;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class CSVWriter {
    public static void writeDevicesToCSV(Set<Device> devices, String filePath) throws IOException {
        FileWriter csvWriter = new FileWriter(filePath);
        // Write device data
        for (Device device : devices) {
            csvWriter.append(device.getId());
            csvWriter.append("\n");
        }
        csvWriter.flush();
        csvWriter.close();
    }

}
