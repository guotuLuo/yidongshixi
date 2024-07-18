from shapely.geometry import Polygon, Point
from shapely.ops import triangulate
import matplotlib.pyplot as plt
from matplotlib.patches import Polygon as MplPolygon
import numpy as np
# 生成20个随机点
points = np.random.rand(1000000, 2)

# 使用Shapely创建多边形
polygon = Polygon(points)

# 确保多边形是有效的简单多边形
if not polygon.is_valid:
    polygon = polygon.convex_hull

# 绘制多边形
fig, ax = plt.subplots()
patch = MplPolygon(np.array(polygon.exterior.coords), closed=True, edgecolor='k', facecolor='c', alpha=0.3)
ax.add_patch(patch)

# 绘制点
x, y = points[:,0], points[:,1]
ax.plot(x, y, 'o', color='red')

plt.show()
