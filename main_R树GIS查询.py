import json
import numpy as np
import pandas as pd
from rtree import index
from shapely.geometry import Point, Polygon, shape
from concurrent.futures import ThreadPoolExecutor, as_completed
import time
# 示例：生成300万设备的随机坐标数据
def load_info_data(file_path, num_records=1000):
    df = pd.read_csv(file_path)
    df = df[['latitude', 'longitude']].head(num_records)
    info = [(i, row['latitude'], row['longitude']) for i, row in df.iterrows()]
    return info

# 初始化R树索引
def create_rtree(info):
    rtree_idx = index.Index()
    for device in info:
        device_id, latitude, longitude = device
        rtree_idx.insert(device_id, (longitude, latitude, longitude, latitude), obj=device)
    return rtree_idx

# 查询指定半径范围内的设备
def filter_point(polygon, item):
    device_id, latitude, longitude = item.object
    device_point = Point(longitude, latitude)
    if polygon.contains(device_point):
        return item.object
    return None


# 查询在指定多边形范围内的设备
def query_rtree_polygon(rtree_idx, polygon, max_workers=4):
    # 获取多边形的边界框
    bounds = polygon.bounds

    # 查询R树，找到可能的匹配项
    possible_matches = list(rtree_idx.intersection(bounds, objects=True))

    # 多线程过滤结果，检查每个点是否在多边形内
    result = []
    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = [executor.submit(filter_point, polygon, item) for item in possible_matches]
        for future in as_completed(futures):
            res = future.result()
            if res is not None:
                result.append(res)

    return result

# 示例使用
if __name__ == "__main__":
    # 读取300万设备的随机数据， 建立R树
    info = load_info_data("device_info.csv", 300000)
    start = time.time()
    # 创建R树并插入数据
    rtree_idx = create_rtree(info)
    buildTreeTime = time.time()
    print("build R Tree time:{}", buildTreeTime - start)

    # 以杭州为中心，半径700公里的六边形顶点
    points = np.array([
        [112.941677, 30.247739],
        [116.582514, 35.693445],
        [123.86412, 35.69343],
        [127.504904, 30.247693],
        [123.864103, 24.801945],
        [116.582511, 24.801968],
        [112.941677, 30.247739]
    ])

    polygon = Polygon(points)
    device_in_polygon = query_rtree_polygon(rtree_idx, polygon)
    end = time.time()
    print("query time:{}", end - buildTreeTime)
    print(f"found {len(device_in_polygon)} devices within the polygon.")
    for device in device_in_polygon:
        print(f"device id:{ device[0]}")

    # 中国边界, 以区为单位，太碎了
    # with open('./省市区边界json/adcode-master/data/fences/100000.json', 'r') as file:
    #     china_boder = json.load(file)
    #
    # multipolygon = shape(china_boder)
    # for polygon in multipolygon.geoms:
    #     device_in_polygon = query_rtree_polygon(rtree_idx, polygon)
    #     end = time.time()
    #     print("query time:{}", end - buildTreeTime)
    #     print(f"found {len(device_in_polygon)} devices within the polygon.")
    #     print(f"Found {device_in_polygon} devices within the polygon.")

