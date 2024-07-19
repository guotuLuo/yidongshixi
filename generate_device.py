import time
from generator_devices_latitude_longitude import *
import pandas as pd
from concurrent.futures import ThreadPoolExecutor, as_completed

china_gdf = gpd.read_file("country/gadm41_CHN_1.json")
# 合并所有几何形状到一个多边形
china_polygon = china_gdf.unary_union

def generate_random_points_within_polygon(polygon, num_points):
    data = []
    minx, miny, maxx, maxy = polygon.bounds
    start = time.time()
    for i in range(num_points):
        point = Point(random.uniform(minx, maxx), random.uniform(miny, maxy))
        if polygon.contains(point):
            loc = reverse_geocode(point.x, point.y)
            longitude, latitude = point.x, point.y
            geocode = loc[2]
            id = str(i)
            data.append([id, longitude, latitude, geocode])
    end = time.time()
    print(end - start)
    df = pd.DataFrame(data, columns=['id', 'longitude', 'latitude', 'geocode'])
    df.to_csv('device_points.csv', index=False)

randomPoints = generate_random_points_within_polygon(china_polygon, 1000)
end = time.time()


