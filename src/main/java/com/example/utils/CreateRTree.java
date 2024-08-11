package com.example.utils;

import com.example.entity.Device;
import com.github.davidmoten.rtree.*;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Point;

import java.util.List;

public class CreateRTree {
    public static RTree<Device, Point> createRTree(List<Device> info) {
        // TODO 不同的RTRree 分割算法
        // 原始方法
//        RTree<Device, Point> rtree = RTree.create();
//        for (Device device : info) {
//            rtree = rtree.add(device, Geometries.pointGeographic(device.longitude, device.latitude));
//        }
//        return rtree;


//        // 设置四分法分割算法
        RTree<Device, Point> rtreeQuadratic = RTree
                .selector(new SelectorMinimalAreaIncrease())
                .minChildren(1)
                .maxChildren(4)
                .splitter(new SplitterQuadratic())
                .create();
        for (Device device : info) {
            rtreeQuadratic = rtreeQuadratic.add(device, Geometries.pointGeographic(device.longitude, device.latitude));
        }
        return rtreeQuadratic;

        // 设置R*-树分割算法
//        RTree<Device, Point> rtreeRStar = RTree
//                .selector(new SelectorMinimalAreaIncrease())
//                .minChildren(1)
//                .maxChildren(4)
//                .splitter(new SplitterRStar())
//                .create();
//        for (Device device : info) {
//            rtreeRStar = rtreeRStar.add(device, Geometries.pointGeographic(device.longitude, device.latitude));
//        }
//        return rtreeRStar;
    }
}
