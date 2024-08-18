package com.example.rtree;

import com.example.utils.PolygonCircleUnion;
import com.example.utils.XMLParser;
import org.checkerframework.checker.units.qual.A;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.entity.Device;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Point;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.utils.CreateRTree.createRTree;
import static com.example.utils.LoadData.loadInfoData;
import static com.example.utils.QueryRTree.queryRTreePolygon;

@SpringBootTest
class RTreeApplicationTests {
    // geocode 对应的区域是杭州市、宁波市、绍兴市、嘉兴市、湖州市 1/960 * 1000000 = 1300+
    // polygon 对应的区域是以西安为中心的径长700km的正六边形  -- S = 3 * 1.73 / 2 * 700 ^ 2  / 9600000 * 1000000 = 130000+
    // circle  对应的区域是以北京市中心为中心的半径100km的圆 9000+
    private String xmlData = "<area>"
            + "<areaDesc>浙江省杭州市、宁波市、绍兴市</areaDesc>"
            + "<polygon>28.725077,105.160707 28.879113,112.719301 34.230691,116.586489 39.635978,112.807192 39.853848,105.159922 34.194350,101.381411 28.725077,105.160707</polygon>"
            + "<circle>39.85799384124283,116.40441202579439 100000</circle>"
            + "<geocode>610100000000,330200000000,330600000000</geocode>"
            + "<geocode>330400000000,330500000000,110100000000</geocode>"
            + "</area>";


    // 坐标系转换这里是比较花费时长的
    @Autowired
    private PolygonCircleUnion PolygonCircleUnion;

    // rtree 缓存
    private static RTree<Device, Point> rtree;
    // 设备信息缓存
    private static List<Device> info;
//    @Test
//    void BuildRTree() throws Exception {
//        // 加载设备信息
//        info = loadInfoData("src/main/resources/device_info.csv", 3000000);
////        long init = System.currentTimeMillis();
//        // 创建 R 树并插入数据 建树
//        rtree = createRTree(info);
////        long buildTreeTime = System.currentTimeMillis();
////        System.out.println("Build R Tree time: " + (buildTreeTime - init) + "ms");
//    }

//    @Test
//    void QueryRTree(String xmlData) throws Exception{
//        List<Device> devicesInPolygon = new ArrayList<>();
//        // 传入数据，开始运行程序
//        long start = System.currentTimeMillis();
//        Map<String, String> map = XMLParser.ParsertoNodeList(xmlData);
//        // 根据传入的圆形和多边形构建并集
//        List<Polygon> list = PolygonCircleUnion.getUnionPolygon(map.get("polygon"), map.get("circle"));
//        Set<String> targetGeocodes = Arrays.stream(map.get("geocode").split(",")).collect(Collectors.toSet());
//        // 根据当前多边形并集，搜索所有设备
////        long temp = System.currentTimeMillis();
////        System.out.println("Get the Union Area:" + (temp - start) + "ms");
//        for (Polygon polygon : list) {
////            long end1 = System.currentTimeMillis();
//            devicesInPolygon.addAll(queryRTreePolygon(rtree, polygon, 12));
////            long end2 = System.currentTimeMillis();
////            System.out.println("Query time: " + (end2 - end1) + "ms");
//            System.out.println("Find " + devicesInPolygon.size() + " devices within the UnionPolygon.");
//        }
//
//        // 传入geocode列表 #TODO geocode作为外置条件搜索
//        List<Device> filteredDevices = info.stream()
//                .filter(device -> targetGeocodes.contains(device.getGeocode()))
//                .toList();
//
//        // 将 filteredDevices 和 devicesInPolygon 合并，并集处理
//        Set<Device> resultSet = Stream.concat(filteredDevices.stream(), devicesInPolygon.stream())
//                .collect(Collectors.toSet());
//
//        long end = System.currentTimeMillis();
//        // 误差 （1444332 - 143337） / 144332 = 995 / 144332 = 0.006
//        System.out.println("Find " + resultSet.size() + " devices in the given area");
//        System.out.println("All Query time: " + (end - start) + "ms");
//    }

//    @Test
//    void TestRTree() throws Exception {
//        //TODO 建树
//        BuildRTree();
//
//        Scanner scanner = new Scanner(System.in);
//        StringBuilder xmlData = new StringBuilder();
//        String line;
//
//        System.out.println("请输入每组XML数据（输入为空行以结束每组输入，输入EOF或Ctrl+D结束所有输入）：");
//
//        // 读取每一行，直到遇到EOF或Ctrl+D
//        while (scanner.hasNextLine()) {
//            line = scanner.nextLine().trim();
//
//            // 如果输入为空行，表示一组XML结束
//            if (line.isEmpty()) {
//                if (!xmlData.isEmpty()) {
//                    // 处理当前组的XML数据
//                    QueryRTree(xmlData.toString());
//                    // 清空StringBuilder，准备读取下一组
//                    xmlData.setLength(0);
//                }
//            } else {
//                // 否则继续读取当前组的数据
//                xmlData.append(line).append("\n");
//            }
//        }
//
//        // 处理最后一组未添加的XML数据
//        if (xmlData.length() > 0) {
//            QueryRTree(xmlData.toString());
//        }
//    }




}
