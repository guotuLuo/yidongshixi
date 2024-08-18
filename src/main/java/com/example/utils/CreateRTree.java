package com.example.utils;

import com.example.entity.Device;
import com.github.davidmoten.rtree.*;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CreateRTree {
    public static RTree<Device, Point> createRTree(List<Device> info) {
        // TODO 不同的RTRree 分割算法
        // 设置二次分裂算法
//        RTree<Device, Point> rtreeQuadratic = RTree
//                .selector(new SelectorMinimalAreaIncrease())
//                .minChildren(5)
//                .maxChildren(10)
//                .splitter(new SplitterQuadratic())
//                .create();
//        for (Device device : info) {
//            rtreeQuadratic = rtreeQuadratic.add(device, Geometries.pointGeographic(device.longitude, device.latitude));
//        }
//        return rtreeQuadratic;


//         设置R*树分割算法
        RTree<Device, Point> rtreeRStar = RTree
                .selector(new SelectorMinimalAreaIncrease())
                .minChildren(5)
                .maxChildren(10)
                .splitter(new SplitterRStar())
                .create();
        for (Device device : info) {
            rtreeRStar = rtreeRStar.add(device, Geometries.pointGeographic(device.longitude, device.latitude));
        }
        return rtreeRStar;
//
//        info.sort(Comparator.comparingDouble(Device::getLatitude));
//        // 使用STRtree 批量建树
//        long s = System.currentTimeMillis();
//        List<Entry<Device, Point>> entries = info.stream()
//                .map(device -> Entries.entry(device, Geometries.point(device.getLongitude(), device.getLatitude())))
//                .toList();
//        long r = System.currentTimeMillis();
//        System.out.println(r - s);
//        // 使用STR批量加载方法构建R树
//        RTree<Device, Point> STrtree = RTree.star()
//                .minChildren(50)
//                .maxChildren(100)
//                .splitter(new SplitterRStar())
//                .create();
//        long t = System.currentTimeMillis();
//        System.out.println(t - r);
//        STrtree = STrtree.add(entries);
//        long p = System.currentTimeMillis();
//        System.out.println(p - t);
//        return STrtree;
    }
}
