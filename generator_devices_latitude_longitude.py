import geopandas as gpd
from shapely.geometry import Point, Polygon
import numpy as np
import random
import geopy
import mysql.connector

db = mysql.connector.connect(
    host="localhost",  # MySQL服务器地址
    user="root",  # 用户名
    password="061966",  # 密码
    database="china_boder"  # 数据库名称
)

cursor = db.cursor()

def generate_random_points_within_polygon(polygon, num_points):
    points = []
    minx, miny, maxx, maxy = polygon.bounds
    while len(points) < num_points:
        random_point = Point(random.uniform(minx, maxx), random.uniform(miny, maxy))
        if polygon.contains(random_point):
            points.append(random_point)
    return points


def reverse_geocode(longotude, latitude):
    cursor.execute('''
        SELECT city, province FROM final_geo
        ORDER BY ((latitude - %s)*(latitude - %s) + (longitude - %s)*(longitude - %s)) ASC
        LIMIT 1
    ''', (latitude, latitude, longotude, longotude))
    return cursor.fetchone()

def getPointsInChina():
    # 下载和加载中国边界数据（这里假设你已经下载了中国的GeoJSON边界文件）
    china_gdf = gpd.read_file("country/gadm41_CHN_1.json")
    # 合并所有几何形状到一个多边形
    china_polygon = china_gdf.unary_union
    # 生成30个中国边界内的随机点
    random_points = generate_random_points_within_polygon(china_polygon, 50)
    # 转换为WGS84坐标
   #  wgs84_points = [(point.x, point.y) for point in random_points]
    return random_points


if __name__ == "__main__":
    wgs84_points = getPointsInChina()
    coordinates = []
    # 输出生成的坐标
    for i, point in enumerate(wgs84_points):
        coordinates.append(point)
        print(f"Point {i+1}: {point}")
    # # 示例WGS84坐标
    for coord in coordinates:
        location = reverse_geocode(coord[1], coord[0])
        print(f"Coordinates: {coord} -> Location: {location}")

