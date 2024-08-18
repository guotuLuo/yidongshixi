package com.example.utils;
import com.example.entity.Device;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChinaMapPlotter extends JPanel {

    private List<List<Point>> polygons = new ArrayList<>();
    private List<Point> markers = new ArrayList<>();

    public ChinaMapPlotter(Set<Device> devices) {
        try {
            loadGeoJson("src/main/resources/china_boundary_all.json"); // JSON文件路径
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 添加要标注的点，示例：北京的经纬度
        //markers.add(convertGeoToPixel(116.404, 39.915));
        for (Device device : devices) {
            markers.add(convertGeoToPixel(device.getLongitude(), device.getLatitude()));
        }
    }

    private void loadGeoJson(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filePath));

        JsonNode features = root.get("features");
        for (JsonNode feature : features) {
            JsonNode geometry = feature.get("geometry");
            JsonNode coordinates = geometry.get("coordinates");

            for (JsonNode polygon : coordinates) {
                List<Point> points = new ArrayList<>();
                for (JsonNode coord : polygon.get(0)) {  // 处理 MultiPolygon 的第一个多边形
                    double lon = coord.get(0).asDouble();
                    double lat = coord.get(1).asDouble();
                    points.add(convertGeoToPixel(lon, lat));
                }
                polygons.add(points);
            }
        }
    }

    private Point convertGeoToPixel(double lon, double lat) {
        // 这里需要根据你的地图比例和尺寸进行计算
        int mapWidth = 1200;
        int mapHeight = 900;

        // 假设中国的经纬度范围（根据实际情况调整）
        double minLon = 73.0;
        double maxLon = 135.0;
        double minLat = 18.0;
        double maxLat = 54.0;

        int x = (int) ((lon - minLon) / (maxLon - minLon) * mapWidth);
        int y = (int) ((maxLat - lat) / (maxLat - minLat) * mapHeight); // y 方向和纬度相反

        return new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 绘制中国地图边界
        g.setColor(Color.BLACK);
        for (List<Point> polygon : polygons) {
            int[] xPoints = polygon.stream().mapToInt(p -> p.x).toArray();
            int[] yPoints = polygon.stream().mapToInt(p -> p.y).toArray();
            g.drawPolygon(xPoints, yPoints, polygon.size());
        }

        // 绘制标注点
        g.setColor(Color.RED);
        for (Point marker : markers) {
            g.fillOval(marker.x - 1, marker.y - 1, 2, 2);  // 画一个半径为5的红色圆点
        }
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame();
//        ChinaMapPlotter mapPlotter = new ChinaMapPlotter();
//        frame.add(mapPlotter);
//        frame.setSize(1600, 1200);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//    }
}
