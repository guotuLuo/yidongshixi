package com.example.entity;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.*;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.TransformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.StreamSupport;

@Component
public class CirclePolygon {
    // 将MathTransform对象缓存起来，避免重复创建
    @Autowired
    private  MathTransform transformToProjected;

    @Autowired
    private  MathTransform transformToGeographic;


    public Polygon getCirclePolygon(double circleLat, double circleLon, double radiusInMeters) {
        try {
            GeometryFactory geometryFactory = new GeometryFactory();
            // 将圆心转换为投影坐标系
            Point centerPoint = geometryFactory.createPoint(new Coordinate(circleLon, circleLat));
            Point projectedCenterPoint = (Point) JTS.transform(centerPoint, transformToProjected);
            // 在投影坐标系中生成圆形缓冲区
            Polygon bufferPolygon = (Polygon) projectedCenterPoint.buffer(radiusInMeters);
            // 将缓冲区转换回地理坐标系（WGS84）
            Polygon geographicBufferPolygon = (Polygon) JTS.transform(bufferPolygon, transformToGeographic);

            return geographicBufferPolygon;
        } catch (Exception e) {
            System.err.println("Error generating circle polygon: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}