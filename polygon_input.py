import time

from shapely.geometry import Polygon, Point
from shapely.ops import triangulate
import matplotlib.pyplot as plt
from matplotlib.patches import Polygon as MplPolygon
import numpy as np
from generator_devices_latitude_longitude import *
from pylab import mpl
import pandas as pd
# 设置显示中文字体
mpl.rcParams["font.sans-serif"] = ["SimHei"]

# 生成20个随机点
# points = np.random.rand(20, 2)
points = np.array([
    [112.941677, 30.247739],
    [116.582514, 35.693445],
    [123.86412, 35.69343],
    [127.504904, 30.247693],
    [123.864103, 24.801945],
    [116.582511, 24.801968]
])
# 使用Shapely创建多边形
polygon = Polygon(points)

# 确保多边形是有效的简单多边形
if not polygon.is_valid:
    polygon = polygon.convex_hull

# 绘制多边形
fig, ax = plt.subplots()
patch = MplPolygon(np.array(polygon.exterior.coords), closed=True, edgecolor='k', facecolor='c', alpha=0.3)
ax.add_patch(patch)

# 绘制边界点
x, y = points[:,0], points[:,1]
ax.plot(x, y, 'o', color='red')

# 绘制杭州
x, y = 120.226901, 30.308497
ax.plot(x, y, 'o', color='yellow')
text_x, text_y = 120.226901, 30.308497
ax.text(text_x, text_y, '杭州', fontsize=20, color='black', ha='left', va='bottom')


randomPoints = getPointsInChina(3000000)
# 把生成的id、 longitude、 latitude 、 geocode存入能够快速读写的文件中
data = []
for i, point in enumerate(randomPoints):
    if polygon.contains(point):
        print(point)
        loc = reverse_geocode(point.x, point.y)
        new_x, new_y = point.x, point.y
        ax.plot(new_x, new_y, 'o', color='blue', label='New Points')
        ax.text(new_x, new_y, str(loc[0]), fontsize=14, color='black', ha='left', va='bottom')
    else:
        print(point)
        print("failed, dont have the point")
        print(reverse_geocode(point.x, point.y))

ax.set_title("以杭州为中心，半径700KM作六边形")
plt.show()



