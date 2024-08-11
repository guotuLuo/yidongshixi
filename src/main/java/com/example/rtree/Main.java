package com.example.rtree;

import com.example.entity.Device;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Point;
import org.locationtech.jts.geom.*;

import java.util.List;

import static com.example.utils.CreateRTree.createRTree;
import static com.example.utils.LoadData.loadInfoData;
import static com.example.utils.QueryRTree.queryRTreePolygon;

public class Main {

    // 读取 CSV 文件中的数据


    // 创建 R 树索引


    // 查询在多边形范围内的设备
//    public static List<Device> queryRTreePolygon(RTree<Device, Point> rtree, Polygon polygon, int maxWorkers) throws Exception {
//        ExecutorService executor = Executors.newFixedThreadPool(maxWorkers);
//
//        // Step 1: 粗筛选 - 根据最小外接矩形查找
//        Envelope envelope = polygon.getEnvelopeInternal();
//        Observable<Device> observable = rtree.search(Geometries.rectangle(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY()))
//                .map(entry -> entry.value());
//
//        // Step 2: 精细过滤 - 确定点是否在多边形内部
//        List<Callable<Device>> tasks = Lists.newArrayList();
//        observable.forEach(entry -> tasks.add(() -> {
//            if (polygon.covers(new GeometryFactory().createPoint(new Coordinate(entry.longitude, entry.latitude)))) {
//                return entry;
//            }
//            return null;
//        }));
//
//        // 收集结果
//        List<Device> result = Lists.newArrayList();
//        List <Future<Device>> futures = executor.invokeAll(tasks);
//        for (Future<Device> future : futures) {
//            Device device = future.get();
//            if (device != null) {
//                result.add(device);
//            }
//        }
//
//        executor.shutdown();
//        return result;
//    }

//    public static List<Device> queryRTreePolygon(RTree<Device, Point> rtree, Polygon polygon, int maxWorkers) throws Exception {
//        ExecutorService executor = Executors.newFixedThreadPool(maxWorkers);
//
//        // 使用GeometryFactory缓存，避免重复创建
//        GeometryFactory geometryFactory = new GeometryFactory();
//
//        // Step 1: 粗筛选 - 根据最小外接矩形查找
//        Envelope envelope = polygon.getEnvelopeInternal();
//        Observable<Device> observable = rtree.search(Geometries.rectangle(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY()))
//                .map(entry -> entry.value());
//
//        // Step 2: 精细过滤 - 确定点是否在多边形内部
//        List<Device> result = new ArrayList<>();
//        List<Callable<Device>> tasks = observable.map(device -> (Callable<Device>) () -> {
//            if (polygon.covers(geometryFactory.createPoint(new Coordinate(device.longitude, device.latitude)))) {
//                return device;
//            }
//            return null;
//        }).toList().toBlocking().single();
//
//        // 收集结果
//        List<Future<Device>> futures = executor.invokeAll(tasks);
//        for (Future<Device> future : futures) {
//            Device device = future.get();
//            if (device != null) {
//                result.add(device);
//            }
//        }
//
//        executor.shutdown();
//        return result;
//    }


    // 惰性求值  最垃圾的
//    public static List<Device> queryRTreePolygon(RTree<Device, Point> rtree, Polygon polygon, int maxWorkers) throws Exception {
//        // 使用GeometryFactory缓存，避免重复创建
//        GeometryFactory geometryFactory = new GeometryFactory();
//
//        // Step 1: 粗筛选 - 根据最小外接矩形查找
//        Envelope envelope = polygon.getEnvelopeInternal();
//        Observable<Device> observable = rtree.search(Geometries.rectangle(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY()))
//                .map(entry -> entry.value())
//                .filter(device -> polygon.covers(geometryFactory.createPoint(new Coordinate(device.longitude, device.latitude))));
//
//        // Step 2: 并行执行并收集结果
//        List<Device> result = observable.toList().toBlocking().single();
//        return result;
//    }


    // 主程序
    public static void main(String[] args) throws Exception {
        // 读取 300 万设备的随机数据，建立 R 树
        List<Device> info = loadInfoData("src/main/resources/device_info.csv", 3000000);
        long start = System.currentTimeMillis();

        // 创建 R 树并插入数据
        RTree<Device, Point> rtree = createRTree(info);
        long buildTreeTime = System.currentTimeMillis();
        System.out.println("Build R Tree time: " + (buildTreeTime - start) + "ms");

        // 以杭州为中心，半径 700 公里的六边形顶点
//        Coordinate[] coordinates = new Coordinate[]{
//                new Coordinate(112.941677, 30.247739),
//                new Coordinate(116.582514, 35.693445),
//                new Coordinate(123.86412, 35.69343),
//                new Coordinate(127.504904, 30.247693),
//                new Coordinate(123.864103, 24.801945),
//                new Coordinate(116.582511, 24.801968),
//                new Coordinate(112.941677, 30.247739)
//        };
        // 以西安为中心，半径 700 公里的六边形顶点
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(105.160707, 28.725077),
                new Coordinate(112.719301, 28.879113),
                new Coordinate(116.586489, 34.230691),
                new Coordinate(112.807192, 39.635978),
                new Coordinate(112.807192, 39.635978),
                new Coordinate(101.381411, 34.194350),
                new Coordinate(105.160707, 28.725077)
        };

        //  全国范围内
//                Coordinate[] coordinates = new Coordinate[]{
//                new Coordinate(75.842135, 53.808502),
//                new Coordinate(135.256197, 54.732298),
//                new Coordinate(136.750338, 18.492219),
//                new Coordinate(71.711276, 13.765639),
//                new Coordinate(75.842135, 53.808502)
//        };
        Polygon polygon = new GeometryFactory().createPolygon(coordinates);

        List<Device> devicesInPolygon = queryRTreePolygon(rtree, polygon, 16);
        long end = System.currentTimeMillis();

        devicesInPolygon = queryRTreePolygon(rtree, polygon, 16);
        long end1 = System.currentTimeMillis();
        System.out.println("Query time: " + (end1 - end) + "ms");
        System.out.println("Found " + devicesInPolygon.size() + " devices within the polygon.");
    }
}

// 考虑当前传入多边形和那些省的边界相交，利用当前相交的边界重新查询建树，那么
// 34 个省内的geocode分别建树， 共有34个树多线程查询
// 先查出有那些省是有可能的，然后在这些省内的R树内部进行查询
// 后面这些咋整呢
