package com.example.rtree;

import com.example.utils.PolygonCircleUnion;
import com.example.utils.XMLParser;
import com.github.davidmoten.rtree.Entry;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.entity.Device;
import com.github.davidmoten.guavamini.Lists;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import org.locationtech.jts.geom.*;
import rx.Observable;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.example.rtree.Main.*;
import static com.example.utils.getDeviceByGeocode.getDeviceByGeocode;
import static java.util.stream.Collectors.toList;

@SpringBootTest
class RTreeApplicationTests {
    private String xmlData = "<area>"
                           + "<areaDesc>浙江省杭州市、宁波市、绍兴市</areaDesc>"
                           + "<polygon>39.87132449822707,116.43805764744252 39.87128923401099,116.4382911465195 39.8712502877112,116.43788996409587 39.87112784527392,116.43772789392722 39.87104020541204,116.4375042478286 39.87132449822707,116.43805764744252</polygon>"
                           + "<circle>39.85799384124283,116.40441202579439 100000</circle>"
                           + "<geocode>330100000000,330200000000,330600000000</geocode>"
                           + "<geocode>330400000000,330500000000,110100000000</geocode>"
                           + "</area>";
//                           + "<area>"
//                           + "<areaDesc>浙江省嘉兴市、湖州市</areaDesc>"
//                           + "<geocode>330400000000,330500000000, 110100000000</geocode>"
//                           + "</area>";
    @Test
    void contextLoads() throws Exception {
        // 加载设备信息
        List<Device> info = loadInfoData("src/main/resources/device_info.csv", 3000000);
        long init = System.currentTimeMillis();

        // 创建 R 树并插入数据
        RTree<Device, Point> rtree = createRTree(info);

        long buildTreeTime = System.currentTimeMillis();
        System.out.println("Build R Tree time: " + (buildTreeTime - init) + "ms");

        Map<String, String> map = XMLParser.ParsertoNodeList(xmlData);
        // 根据传入的圆形和多边形构建并集
        long start = System.currentTimeMillis();
        List<Polygon> list = PolygonCircleUnion.getUnionPolygon(map.get("polygon"), map.get("circle"));

        // 根据当前多边形并集，搜索所有设备
        List<Device> devicesInPolygon = new ArrayList<>();
        for (Polygon polygon : list) {
            long end1 = System.currentTimeMillis();
            devicesInPolygon.addAll(queryRTreePolygon(rtree, polygon, 16));
            long end2 = System.currentTimeMillis();
            System.out.println("Query time: " + (end2 - end1) + "ms");
            System.out.println("Found " + devicesInPolygon.size() + " devices within the UnionPolygon.");
        }
        // 打印出来都是京✌

        // 传入geocode列表
        List<String> targetGeocodes = Arrays.stream(map.get("geocode").split(",")).toList();
        List<Device> matchingDevices = devicesInPolygon.stream()
                .filter(device -> targetGeocodes.contains(device.getGeocode().trim()))
                .toList();
        long end = System.currentTimeMillis();
        System.out.println(matchingDevices.size());
        System.out.println("Query time: " + (end - start) + "ms");
    }
}
