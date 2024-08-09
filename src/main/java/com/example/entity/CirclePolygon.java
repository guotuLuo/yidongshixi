package com.example.entity;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.*;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.TransformException;

public class CirclePolygon {
    public static Polygon getCirclePolygon(double circleLat, double circleLon, double radiusInMeters) {
        try {
            // 创建GeometryFactory
            GeometryFactory geometryFactory = new GeometryFactory();

            // 定义WGS84地理坐标系
            CoordinateReferenceSystem geographicCRS = DefaultGeographicCRS.WGS84;

            // 定义投影坐标系（EPSG:3857，Web Mercator投影）
            CoordinateReferenceSystem projectedCRS = CRS.decode("EPSG:3857");

            // 创建从地理坐标系到投影坐标系的转换工具
            MathTransform transformToProjected = CRS.findMathTransform( geographicCRS, projectedCRS);


            // 将圆心转换为投影坐标系
            Point centerPoint = geometryFactory.createPoint(new Coordinate(circleLon, circleLat));
            Point projectedCenterPoint = (Point) JTS.transform(centerPoint, transformToProjected);

            // 在投影坐标系中生成圆形缓冲区
            Polygon bufferPolygon = (Polygon) projectedCenterPoint.buffer(radiusInMeters);

            // 将缓冲区转换回地理坐标系（WGS84）
            MathTransform transformToGeographic = CRS.findMathTransform(projectedCRS,  geographicCRS);
            Polygon geographicBufferPolygon = (Polygon) JTS.transform(bufferPolygon,  transformToGeographic);

            // 输出结果
            System.out.println("Circle Polygon in WGS84: " + geographicBufferPolygon);
            return geographicBufferPolygon;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
