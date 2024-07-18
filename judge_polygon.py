from shapely.geometry import Polygon
from shapely.validation import explain_validity

# 定义WGS84坐标（从你的文件中提取的示例坐标）
coordinates = [
    (115.423411, 39.969825), (115.423472, 39.970432), (115.423941, 39.971456),
    # ... 添加所有的坐标点
    (116.002196, 40.584137)
]

# 创建多边形
polygon = Polygon(coordinates)

# 检查多边形是否有效
validity = explain_validity(polygon)
print(f"Polygon validity: {validity}")

# 如果多边形无效，可以尝试修复
if not polygon.is_valid:
    from shapely.ops import unary_union, polygonize

    # 使用unary_union合并可能的重叠部分
    fixed_polygon = unary_union(polygon)

    # 检查修复后的多边形是否有效
    fixed_validity = explain_validity(fixed_polygon)
    print(f"Fixed polygon validity: {fixed_validity}")

    # 将修复后的多边形保存
    polygon = fixed_polygon

# 输出多边形的WKT格式
print(polygon.wkt)
