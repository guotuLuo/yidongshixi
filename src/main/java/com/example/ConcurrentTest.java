package com.example;
import com.example.configuration.Config;
import com.example.utils.PolygonCircleUnion;
import com.example.utils.XMLParser;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Point;
import org.locationtech.jts.geom.Polygon;
import org.openjdk.jmh.annotations.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.entity.Device;

import static com.example.utils.CreateRTree.createRTree;
import static com.example.utils.LoadData.loadInfoData;
import static com.example.utils.QueryRTree.queryRTreePolygon;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
public class ConcurrentTest {
    private static List<String> list;
    private static String xmlData1 = "<area>"
            + "<polygon>28.725077,105.160707 28.879113,112.719301 34.230691,116.586489 39.635978,112.807192 39.853848,105.159922 34.194350,101.381411 28.725077,105.160707</polygon>"
            + "<circle>39.85799384124283,116.40441202579439 100000</circle>"
            + "<geocode>610100000000,330200000000,330600000000</geocode>"
            + "<geocode>330400000000,330500000000,110100000000</geocode>"
            + "</area>";

    private static String xmlData2 = "<area>"
            + "<polygon>28.725077,105.160707 28.879113,112.719301 34.230691,116.586489 39.635978,112.807192 39.853848,105.159922 34.194350,101.381411 28.725077,105.160707</polygon>"
            + "<circle>30.85799384124283,112.40441202579439 100000</circle>"
            + "<geocode>330200000000,330600000000</geocode>"
            + "</area>";


    private static String xmlData3 = "<area>"
            + "<polygon>30.247739,112.941677 35.693445,116.582514 35.693430,123.86412 30.247693,127.504904 24.801945,123.864103 24.801968,116.582511 30.247739,112.941677</polygon>"
            + "<circle>39.85799384124283,116.40441202579439 100000</circle>"
            + "<geocode>610100000000,110100000000</geocode>"
            + "</area>";
    private static List<Device> info;

    private  static PolygonCircleUnion polygonCircleUnion = new PolygonCircleUnion();
    // rtree 缓存
    private static RTree<Device, Point> rtree;
    public void Build() throws Exception {
        // 加载设备信息
        info = loadInfoData("src/main/resources/device_info.csv", 1200000);
        long init = System.currentTimeMillis();


        // 创建 R 树并插入数据 建树
        rtree = createRTree(info);
        // rtree.visualize(2048, 2048).save("src/main/resources/rtree.png","png");
        // 翻转图片
        // FlipImage.flip("src/main/resources/rtree.png");
        long buildTreeTime = System.currentTimeMillis();
        System.out.println("Build R Tree time: " + (buildTreeTime - init) + "ms");
    }

    Set<Device> QueryRTree(String xmlData) throws Exception{
        List<Device> devicesInPolygon = new ArrayList<>();
        // 传入数据，开始运行程序
        long start = System.currentTimeMillis();
        Map<String, String> map = XMLParser.ParsertoNodeList(xmlData);
        // 根据传入的圆形和多边形构建并集
        List<Polygon> list = polygonCircleUnion.getUnionPolygon(map.get("polygon"), map.get("circle"));
        Set<String> targetGeocodes = Arrays.stream(map.get("geocode").split(",")).collect(Collectors.toSet());
        // 根据当前多边形并集，搜索所有设备
        long temp = System.currentTimeMillis();
        //System.out.println("Get the Union Area:" + (temp - start) + "ms");
        for (Polygon polygon : list) {
            devicesInPolygon.addAll(queryRTreePolygon(rtree, polygon, 12));
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
//        System.err.println("All Query time: " + (end - start) + "ms");
        try (FileWriter writer = new FileWriter("C:/Users/guotuluo/Desktop/Set/moveBrick/RTree/src/main/resources/output.txt", true)) {
            writer.write("QueryRTree execution time: " + (end - start) + "ms\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    @Setup(Level.Trial)
    public  void setUp() throws Exception {
        // TODO: 建树
        Build();
        list = new ArrayList<>();
        list.add(xmlData1);
        list.add(xmlData2);
        list.add(xmlData3);
    }
    private static int count = 0;

    @Benchmark
    public  Set<Device> testQueryRTree() throws Exception {
        // 执行查询
        count = (count + 1) % 3;
        Set<Device> devices = QueryRTree(list.get(count));
        return devices;
    }

}
