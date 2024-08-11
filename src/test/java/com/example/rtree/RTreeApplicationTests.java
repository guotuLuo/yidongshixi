package com.example.rtree;

import com.example.utils.PolygonCircleUnion;
import com.example.utils.XMLParser;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.entity.Device;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.example.utils.CreateRTree.createRTree;
import static com.example.utils.LoadData.loadInfoData;
import static com.example.utils.QueryRTree.queryRTreePolygon;

@SpringBootTest
class RTreeApplicationTests {
//    private String xmlData = "<area>"
//                           + "<areaDesc>浙江省杭州市、宁波市、绍兴市</areaDesc>"
//                           + "<polygon>39.87132449822707,116.43805764744252 39.87128923401099,116.4382911465195 39.8712502877112,116.43788996409587 39.87112784527392,116.43772789392722 39.87104020541204,116.4375042478286 39.87132449822707,116.43805764744252</polygon>"
//                           + "<circle>39.85799384124283,116.40441202579439 100000</circle>"
//                           + "<geocode>330100000000,330200000000,330600000000</geocode>"
//                           + "<geocode>330400000000,330500000000,110100000000</geocode>"
//                           + "</area>";

    private String xmlData = "<area>"
            + "<areaDesc>浙江省杭州市、宁波市、绍兴市</areaDesc>"
            + "<polygon>28.725077,105.160707 28.879113,112.719301 34.230691,116.586489 39.635978,112.807192 39.635978,112.807192, 34.194350,101.381411 28.725077,105.160707</polygon>"
            + "<circle>39.85799384124283,116.40441202579439 100000</circle>"
            + "<geocode>610100000000,330200000000,330600000000</geocode>"
            + "<geocode>330400000000,330500000000,110100000000</geocode>"
            + "</area>";

    @Test
    void contextLoads() throws Exception {
        // 加载设备信息
        List<Device> info = loadInfoData("src/main/resources/device_info.csv", 3000000);
        long init = System.currentTimeMillis();

        // 创建 R 树并插入数据 建树
        RTree<Device, Point> rtree = createRTree(info);

        long buildTreeTime = System.currentTimeMillis();
        System.out.println("Build R Tree time: " + (buildTreeTime - init) + "ms");

        Map<String, String> map = XMLParser.ParsertoNodeList(xmlData);
        // 根据传入的圆形和多边形构建并集

        List<Polygon> list = PolygonCircleUnion.getUnionPolygon(map.get("polygon"), map.get("circle"));
        List<String> targetGeocodes = Arrays.stream(map.get("geocode").split(",")).toList();
        // 根据当前多边形并集，搜索所有设备
        List<Device> devicesInPolygon = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (Polygon polygon : list) {
            long end1 = System.currentTimeMillis();
            devicesInPolygon.addAll(queryRTreePolygon(rtree, polygon, 16));
            long end2 = System.currentTimeMillis();
            System.out.println("Query time: " + (end2 - end1) + "ms");
            System.out.println("Found " + devicesInPolygon.size() + " devices within the UnionPolygon.");
        }
        // 打印出来都是京✌
        // 110100000000
        // 传入geocode列表 #TODO geocode作为外置条件搜索吧

        List<Device> matchingDevices = devicesInPolygon.stream()
                .filter(device -> targetGeocodes.contains(device.getGeocode()))
                .toList();
        long end = System.currentTimeMillis();
        System.out.println(matchingDevices.size());
        System.out.println("Query time: " + (end - start) + "ms");
    }
}
