package com.example.utils;

import com.example.entity.Device;
import com.github.davidmoten.guavamini.Lists;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class LoadData {
    public static List<Device> loadInfoData(String filePath, int numRecords) throws IOException {
        List<Device> info = Lists.newArrayList();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            int id = 0;
            while ((line = br.readLine()) != null && id < numRecords) {
                String[] values = line.split(",");
                String deviceId = values[0];
                double longitude = Double.parseDouble(values[1]);
                double latitude = Double.parseDouble(values[2]);
                BigDecimal bd = new BigDecimal(values[3]);
                String geocode = bd.toPlainString(); // 转换为字符串格式
                info.add(new Device(deviceId, latitude, longitude, geocode));
            }
        }
        return info;
    }
}
