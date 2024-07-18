import time
from generator_devices_latitude_longitude import *
import pandas as pd
def getPointsInChina(num_points):
    # 下载和加载中国边界数据（这里假设你已经下载了中国的GeoJSON边界文件）
    china_gdf = gpd.read_file("country/gadm41_CHN_1.json")
    # 合并所有几何形状到一个多边形
    china_polygon = china_gdf.unary_union
    # 生成30个中国边界内的随机点
    random_points = generate_random_points_within_polygon(china_polygon, num_points)
    # 转换为WGS84坐标
   #  wgs84_points = [(point.x, point.y) for point in random_points]
    return random_points

def convert_id_to_binary_with_padding(id, length=22):
    # 将ID转换为二进制，并去掉前面的 '0b'
    binary_str = bin(id)[2:]
    # 计算需要补的0的数量
    padding_length = length - len(binary_str)
    # 用0填充到指定长度
    padded_binary_str = '0' * padding_length + binary_str
    return padded_binary_str


randomPoints = getPointsInChina(10)
# 把生成的id、 longitude、 latitude 、 geocode存入能够快速读写的文件中
data = []
for i, point in enumerate(randomPoints):
    print(time.time())
    loc = reverse_geocode(point.x, point.y)
    longitude, latitude = point.x, point.y
    geocode = loc[2]
    id = convert_id_to_binary_with_padding(i)
    data.append([id, longitude, latitude, geocode])
df = pd.DataFrame(data, columns=['id', 'longitude', 'latitude', 'geocode'])
# 将 DataFrame 写入 CSV 文件
df.to_csv('device_points.csv', index=False)