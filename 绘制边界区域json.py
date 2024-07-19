import json
import matplotlib.pyplot as plt
from shapely.geometry import shape
from pylab import mpl
mpl.rcParams["font.sans-serif"] = ["SimHei"]
# 从文件夹中读取JSON数据
with open('./省市区边界json/adcode-master/data/fences/100000.json', 'r') as file:
    data = json.load(file)

with open('./省市区边界json/adcode-master/data/fences/110000.json', 'r') as beijing_file:
    beijing = json.load(beijing_file)

# 使用Shapely创建MultiPolygon对象
multipolygon = shape(data)
beijing_polygon = shape(beijing)
# 创建绘图
fig, ax = plt.subplots()
for polygon in multipolygon.geoms:
    x, y = polygon.exterior.xy
    ax.plot(x, y, color='blue')

x, y = beijing_polygon.exterior.xy
ax.plot(x, y, color='blue')

# 设置图形的比例
ax.set_aspect('equal')
ax.set_title("北京市")

# 显示图形
plt.show()