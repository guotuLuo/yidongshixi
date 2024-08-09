package com.example.utils;
import com.example.entity.CirclePolygon;
import org.geotools.geometry.jts.GeometryBuilder;
import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.union.CascadedPolygonUnion;
import org.locationtech.jts.geom.util.PolygonExtracter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PolygonCircleUnion {
    public static List<Polygon> getUnionPolygon(String polygonData, String circleData) {
        List<Polygon> unionPolygons = null;
        try {
            // 解析polygon坐标
            String[] polygonPoints = polygonData.split(" ");
            Coordinate[] polygonCoordinates = new Coordinate[polygonPoints.length];
            for (int i = 0; i < polygonPoints.length; i++) {
                String[] latLon = polygonPoints[i].split(",");
                double lat = Double.parseDouble(latLon[0]);
                double lon = Double.parseDouble(latLon[1]);
                DecimalFormat df = new DecimalFormat("#.########");

                // 格式化double值
                lon = Double.parseDouble(df.format(lon));
                lat = Double.parseDouble(df.format(lat));

                polygonCoordinates[i] = new Coordinate(lon, lat);  // 注意顺序是lon, lat
            }

            // 创建GeometryFactory
            GeometryFactory geometryFactory = new GeometryFactory();
            GeometryBuilder geometryBuilder = new GeometryBuilder();
            // 创建polygon对象
            Polygon polygon = geometryFactory.createPolygon(polygonCoordinates);

            ConvexHull convexHull = new ConvexHull(polygon.getCoordinates(), geometryFactory);
            Geometry hullGeometry = convexHull.getConvexHull();

            // 如果生成的凸包是多边形，则直接使用；如果是其他几何类型，则进一步处理
            if (hullGeometry instanceof Polygon) {
                polygon = (Polygon) hullGeometry;
                System.out.println("Fixed Polygon: " + polygon);
            } else {
                System.out.println("Convex Hull generated a non-polygon geometry: " + hullGeometry);
            }


            // # TODO 创建圆形Polygon
            // 解析circle的圆心和半径
            String[] circleParts = circleData.split(" ");
            String[] circleCenterParts = circleParts[0].split(",");
            DecimalFormat df = new DecimalFormat("#.########");
            double circleLat = Double.parseDouble(circleCenterParts[0]);
            double circleLon = Double.parseDouble(circleCenterParts[1]);
            double radius = Double.parseDouble(circleParts[1]);

            circleLat = Double.parseDouble(df.format(circleLat));
            circleLon = Double.parseDouble(df.format(circleLon));


            Polygon circlePolygon = CirclePolygon.getCirclePolygon(circleLat, circleLon, radius);

            // 计算polygon和circle的并集
            Geometry unionGeometry = polygon.union(circlePolygon);

            // 提取并集后的polygon
            unionPolygons = new ArrayList<>();
            for (int i = 0; i < unionGeometry.getNumGeometries(); i++) {
                Geometry geom = unionGeometry.getGeometryN(i);
                if (geom instanceof Polygon) {
                    unionPolygons.add((Polygon) geom);
                }
            }

            // 输出结果
            for (Polygon unionPolygon : unionPolygons) {
                System.out.println("Union Polygon: " + unionPolygon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unionPolygons;
    }
}
