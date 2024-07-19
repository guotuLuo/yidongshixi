import time
import geopandas as gpd
from shapely.geometry import Point
import random
import pandas as pd
from concurrent.futures import ThreadPoolExecutor, as_completed


final_geo_df = pd.read_csv('final_geo.csv')
def reverse_geocode(longitude, latitude):
    # 计算距离的平方
    final_geo_df['distance'] = (
                (final_geo_df['latitude'] - latitude) ** 2 + (final_geo_df['longitude'] - longitude) ** 2)

    # 找到距离最小的行
    nearest = final_geo_df.loc[final_geo_df['distance'].idxmin()]

    # 返回城市、省份和地理编码
    return nearest['city'], nearest['province'], nearest['geocode']

def generate_random_point(polygon):
    minx, miny, maxx, maxy = polygon.bounds
    while True:
        point = Point(random.uniform(minx, maxx), random.uniform(miny, maxy))
        if polygon.contains(point):
            return point

def process_point(i, point):
    loc = reverse_geocode(point.x, point.y)
    longitude, latitude = point.x, point.y
    geocode = loc[2]
    id = str(i).zfill(22)
    return [id, longitude, latitude, geocode]


def generate_random_points_within_polygon(polygon, num_points):
    data = []
    start = time.time()

    with ThreadPoolExecutor() as executor:
        future_to_point = {executor.submit(generate_random_point, polygon): i for i in range(num_points)}

        for future in as_completed(future_to_point):
            i = future_to_point[future]
            point = future.result()
            data.append(process_point(i, point))

    end = time.time()
    print(f"运行时间: {end - start:.6f} 秒")
    df = pd.DataFrame(data, columns=['id', 'longitude', 'latitude', 'geocode'])
    df.to_csv('device_points.csv', index=False)


# 示例用法
china_gdf = gpd.read_file("country/gadm41_CHN_1.json")
china_polygon = china_gdf.union_all()
generate_random_points_within_polygon(china_polygon, 3000000)
