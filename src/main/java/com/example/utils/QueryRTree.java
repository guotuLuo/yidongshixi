package com.example.utils;

import com.example.entity.Device;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import rx.Observable;

import java.util.List;
import java.util.stream.Collectors;

public class QueryRTree {
    public static List<Device> queryRTreePolygon(RTree<Device, Point> rtree, Polygon polygon, int maxWorkers) throws Exception {
        // 使用GeometryFactory缓存，避免重复创建
        GeometryFactory geometryFactory = new GeometryFactory();

        // Step 1: 粗筛选 - 根据最小外接矩形查找
        Envelope envelope = polygon.getEnvelopeInternal();
        Observable<Entry<Device, Point>> observable = rtree.search(Geometries.rectangle(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY()));

        // Step 2: 并行处理和过滤
        List<Device> result = observable.toList()
                .toBlocking()
                .single()
                .parallelStream()  // 使用并行流
                .map(entry -> entry.value())
                .filter(device -> polygon.covers(geometryFactory.createPoint(new Coordinate(device.longitude, device.latitude))))
                .collect(Collectors.toList());

        return result;
    }
}
