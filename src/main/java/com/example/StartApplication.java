package com.example;

import com.example.configuration.Config;
import com.example.utils.PolygonCircleUnion;
import com.example.utils.XMLParser;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.entity.Device;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Point;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.utils.CreateRTree.createRTree;
import static com.example.utils.LoadData.loadInfoData;
import static com.example.utils.QueryRTree.queryRTreePolygon;

@SpringBootApplication
public class StartApplication {
    // geocode 对应的区域是杭州市、宁波市、绍兴市、嘉兴市、湖州市 1/960 * 1000000 = 1300+
    // polygon 对应的区域是以西安为中心的径长700km的正六边形  -- S = 3 * 1.73 / 2 * 700 ^ 2  / 9600000 * 1000000 = 130000+
    // circle  对应的区域是以北京市中心为中心的半径100km的圆 9000+
    private String xmlData1 = "<area>"
            + "<areaDesc>浙江省杭州市、宁波市、绍兴市</areaDesc>"
            + "<polygon>28.725077,105.160707 28.879113,112.719301 34.230691,116.586489 39.635978,112.807192 39.853848,105.159922 34.194350,101.381411 28.725077,105.160707</polygon>"
            + "<circle>39.85799384124283,116.40441202579439 100000</circle>"
            + "<geocode>610100000000,330200000000,330600000000</geocode>"
            + "<geocode>330400000000,330500000000,110100000000</geocode>"
            + "</area>";

    private String xmlData2 = "<area>"
            + "<areaDesc>浙江省嘉兴市、湖州市</areaDesc>"
            + "<polygon>28.725077,105.160707 28.879113,112.719301 34.230691,116.586489 39.635978,112.807192 39.853848,105.159922 34.194350,101.381411 28.725077,105.160707</polygon>"
            + "<circle>30.85799384124283,112.40441202579439 100000</circle>"
            + "<geocode>330200000000,330600000000</geocode>"
            + "</area>";


    private String xmlData3 = "<area>"
            + "<areaDesc>北京市、西安市</areaDesc>"
            + "<polygon>30.247739,112.941677 35.693445,116.582514 35.693430,123.86412 30.247693,127.504904 24.801945,123.864103 24.801968,116.582511 30.247739,112.941677</polygon>"
            + "<circle>39.85799384124283,116.40441202579439 100000</circle>"
            + "<geocode>610100000000,330600000000</geocode>"
            + "<geocode>110100000000</geocode>"
            + "</area>";


    // 坐标系转换这里是比较花费时长的
    @Autowired
    private PolygonCircleUnion PolygonCircleUnion;

    // rtree 缓存
    private static RTree<Device, Point> rtree;
    // 设备信息缓存
    private static List<Device> info;

    void Build() throws Exception {
        // 加载设备信息
        info = loadInfoData("src/main/resources/device_info.csv", 3000000);
        long init = System.currentTimeMillis();
        // 创建 R 树并插入数据 建树
        rtree = createRTree(info);
        long buildTreeTime = System.currentTimeMillis();
        System.out.println("Build R Tree time: " + (buildTreeTime - init) + "ms");
    }


    void QueryRTree(String xmlData) throws Exception{
        List<Device> devicesInPolygon = new ArrayList<>();
        // 传入数据，开始运行程序
        long start = System.currentTimeMillis();
        Map<String, String> map = XMLParser.ParsertoNodeList(xmlData);
        // 根据传入的圆形和多边形构建并集
        List<Polygon> list = PolygonCircleUnion.getUnionPolygon(map.get("polygon"), map.get("circle"));
        Set<String> targetGeocodes = Arrays.stream(map.get("geocode").split(",")).collect(Collectors.toSet());
        // 根据当前多边形并集，搜索所有设备
        long temp = System.currentTimeMillis();
        //System.out.println("Get the Union Area:" + (temp - start) + "ms");
        for (Polygon polygon : list) {
            long end1 = System.currentTimeMillis();
            devicesInPolygon.addAll(queryRTreePolygon(rtree, polygon, 12));
            long end2 = System.currentTimeMillis();
            //System.out.println("Query time: " + (end2 - end1) + "ms");
            System.out.println("Find " + devicesInPolygon.size() + " devices within the UnionPolygon.");
        }

        // 传入geocode列表 #TODO geocode作为外置条件搜索
        List<Device> filteredDevices = info.stream()
                .filter(device -> targetGeocodes.contains(device.getGeocode()))
                .toList();

        // 将 filteredDevices 和 devicesInPolygon 合并，并集处理
        Set<Device> resultSet = Stream.concat(filteredDevices.stream(), devicesInPolygon.stream())
                .collect(Collectors.toSet());

        long end = System.currentTimeMillis();
        // 误差 （1444332 - 143337） / 144332 = 995 / 144332 = 0.006
        System.out.println("Find " + resultSet.size() + " devices in the given area");
        System.out.println("All Query time: " + (end - start) + "ms");
        System.out.println();
    }


    public static void main(String[] args) throws Exception {
        // 创建ApplicationContext
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        // 获取StartApplication的Bean实例
        StartApplication app = context.getBean(StartApplication.class);

        //TODO 建树
        app.Build();
        List<String> list = new ArrayList<>();
        list.add(app.xmlData1);
        list.add(app.xmlData2);
        list.add(app.xmlData3);
        for (int i = 0; i < 3; i++) {
            app.QueryRTree(list.get(i));
        }
    }
}

