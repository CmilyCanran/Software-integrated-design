-- 清理数据表（按外键依赖顺序）
DELETE FROM products;
DELETE FROM users;

-- 插入测试用户数据（密码都是123456）

INSERT INTO public.users
(id, created_at, email, enabled, "password", "role", updated_at, username)
VALUES(1, '2025-11-20 11:29:24.570', '123@123.com', true, '$2a$10$NNZy/f1yOvevIxg0gDQoaO1PhtIqdxOD3/lkmqhhSfsX46XNTuDee', 'USER', '2025-11-20 11:29:24.570', 'user');
INSERT INTO public.users
(id, created_at, email, enabled, "password", "role", updated_at, username)
VALUES(2, '2025-11-20 11:39:54.488', '123@122.com', true, '$2a$10$Uvo6.s5cj8YHpXcQYLsfIu4KQ1nD9jyvu3XmwvhZQP3iK9zNMrsES', 'ADMIN', '2025-11-20 11:39:54.488', 'admin');
INSERT INTO public.users
(id, created_at, email, enabled, "password", "role", updated_at, username)
VALUES(3, '2025-11-20 11:40:56.570', '122@122.com', true, '$2a$10$4E0.QEKYUAMr8IzADOQEKez/ElAPr3VBOWSjNDXmBfasLF/c/NMMO', 'SHOPER', '2025-11-20 11:40:56.570', 'shoper');
--用户密码都是123456

-- 插入服装商品数据（简化版，只保留主图片，全部改为服装类别，支持分页测试）
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES
  -- 原有的6个商品
  (
    '纯棉圆领T恤 - 基础款',
    '100%纯棉材质，舒适透气，简约设计，适合日常穿着',
    89.99,
    25,
    0.00,
    60,
    true,
    (SELECT id FROM users WHERE username = 'shoper'),
    '{"specifications": {"颜色": ["白色", "黑色", "灰色"], "尺寸": ["S", "M", "L"], "材质": ["纯棉"], "分类": ["服装"], "品牌": ["基础款"], "适合季节": ["春夏", "四季"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '女士碎花连衣裙',
    '优雅碎花图案，雪纺面料，适合春夏季节穿着，展现女性魅力',
    259.99,
    18,
    15.00,
    35,
    false,
    (SELECT id FROM users WHERE username = 'shoper'),
    '{"specifications": {"颜色": ["碎花蓝色", "碎花粉色"], "尺寸": ["S", "M"], "材质": ["雪纺"], "分类": ["服装"], "品牌": ["时尚女装"], "适合场合": ["约会", "聚会"], "裙长": ["及膝", "长款"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '商务休闲西裤',
    '修身剪裁，弹力面料，适合商务场合和日常休闲穿着',
    199.99,
    12,
    10.00,
    0,
    true,
    (SELECT id FROM users WHERE username = 'shoper'),
    '{"specifications": {"颜色": ["深黑色", "藏青色", "灰色"], "尺寸": ["28", "30", "32", "34", "36"], "材质": ["弹力棉", "涤纶混纺"], "分类": ["服装"], "品牌": ["商务系列"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '运动连帽卫衣',
    '加绒保暖，宽松版型，适合运动健身和休闲外出',
    159.99,
    30,
    0.00,
    45,
    true,
    (SELECT id FROM users WHERE username = 'shoper'),
    '{"specifications": {"颜色": ["黑色", "灰色", "白色", "蓝色"], "尺寸": ["S", "M", "L", "XL", "XXL"], "材质": ["棉绒", "抓绒"], "分类": ["服装"], "品牌": ["运动系列"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '牛仔夹克外套',
    '经典牛仔面料，耐磨耐穿，四季百搭单品',
    299.00,
    22,
    20.00,
    25,
    true,
    (SELECT id FROM users WHERE username = 'shoper'),
    '{"specifications": {"颜色": ["深蓝色", "浅蓝色", "黑色"], "尺寸": ["S", "M", "L", "XL"], "材质": ["牛仔布", "棉质"], "分类": ["服装"], "品牌": ["经典系列"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '真丝围巾配饰',
    '100%真丝材质，手感柔软，多种系法，为服装增添亮点',
    129.99,
    8,
    5.00,
    50,
    true,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"颜色": ["红色", "蓝色", "紫色", "粉色"], "尺寸": ["均码"], "材质": ["真丝"], "分类": ["服装"], "品牌": ["配饰系列"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  -- 新增15个商品用于分页测试
  (
    'V领羊毛衫',
    '精选羊毛材质，柔软保暖，V领设计修饰颈部线条',
    189.99,
    15,
    10.00,
    30,
    true,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"颜色": ["米白色", "驼色"], "尺寸": ["M", "L"], "材质": ["羊毛"], "分类": ["服装"], "品牌": ["针织系列"], "领型": ["V领"], "厚度": ["中等"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '工装牛仔裤',
    '经典工装风格，耐磨面料，多口袋设计实用性强',
    249.99,
    20,
    0.00,
    40,
    true,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"颜色": ["深蓝色", "黑色"], "尺寸": ["30", "32", "34"], "材质": ["牛仔布"], "分类": ["服装"], "品牌": ["牛仔系列"], "裤型": ["直筒", "宽松"], "功能": ["多口袋"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '修身长袖衬衫',
    '商务休闲两相宜，免烫面料，易打理',
    139.99,
    10,
    5.00,
    25,
    true,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"颜色": ["白色", "浅蓝色", "条纹"], "尺寸": ["S", "M", "L", "XL", "XXL"], "材质": ["棉涤混纺"], "分类": ["服装"], "品牌": ["正装系列"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '运动短裤套装',
    '速干面料，透气舒适，适合运动健身',
    79.99,
    35,
    0.00,
    55,
    true,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"颜色": ["黑色", "灰色", "蓝色"], "尺寸": ["S", "M", "L", "XL"], "材质": ["聚酯纤维"], "分类": ["服装"], "品牌": ["运动系列"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '针织开衫外套',
    '轻薄针织材质，春秋季节百搭单品',
    169.99,
    12,
    15.00,
    20,
    true,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"颜色": ["粉色", "米色", "浅灰色"], "尺寸": ["S", "M", "L", "XL"], "材质": ["棉线"], "分类": ["服装"], "品牌": ["针织系列"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '高腰阔腿裤',
    '高腰设计拉长腿部线条，阔腿版型舒适显瘦',
    219.99,
    8,
    20.00,
    15,
    true,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"颜色": ["黑色", "卡其色", "深蓝色"], "尺寸": ["S", "M", "L", "XL"], "材质": ["西装面料"], "分类": ["服装"], "品牌": ["女装系列"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '棒球帽配饰',
    '经典棒球帽款式，棉质面料，调节后围设计',
    59.99,
    45,
    0.00,
    80,
    true,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"颜色": ["黑色", "白色", "红色", "蓝色"], "尺寸": ["均码"], "材质": ["棉"], "分类": ["服装"], "品牌": ["配饰系列"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '羽绒背心',
    '轻薄保暖，无袖设计方便活动，春秋过渡季节必备',
    179.99,
    18,
    25.00,
    0,
    false,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"颜色": ["黑色", "蓝色", "粉色"], "尺寸": ["S", "M", "L", "XL"], "材质": ["羽绒", "尼龙"], "分类": ["服装"], "品牌": ["羽绒系列"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '条纹长袖T恤',
    '经典条纹设计，棉质面料舒适透气',
    99.99,
    28,
    0.00,
    35,
    true,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"颜色": ["蓝白条纹", "黑白条纹"], "尺寸": ["S", "M", "L", "XL", "XXL"], "材质": ["纯棉"], "分类": ["服装"], "品牌": ["休闲系列"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '瑜伽运动服套装',
    '高弹力面料，吸湿排汗，适合瑜伽健身',
    159.99,
    22,
    10.00,
    28,
    true,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"颜色": ["紫色", "黑色", "灰色"], "尺寸": ["S", "M", "L", "XL"], "材质": ["氨纶混纺"], "分类": ["服装"], "品牌": ["运动系列"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '风衣外套',
    '经典风衣款式，防风面料，春秋季节理想选择',
    389.99,
    6,
    30.00,
    12,
    true,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"颜色": ["卡其色", "黑色", "藏青色"], "尺寸": ["S", "M", "L", "XL"], "材质": ["聚酯纤维", "棉"], "分类": ["服装"], "品牌": ["外套系列"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '休闲短裤',
    '纯棉面料，宽松版型，夏季居家休闲首选',
    69.99,
    32,
    0.00,
    48,
    true,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"颜色": ["灰色", "蓝色", "黑色"], "尺寸": ["S", "M", "L", "XL", "XXL"], "材质": ["纯棉"], "分类": ["服装"], "品牌": ["休闲系列"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  ),
  (
    '针织毛衣',
    '厚实针织面料，保暖舒适，冬季必备单品',
    229.99,
    14,
    15.00,
    18,
    true,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"颜色": ["红色", "绿色", "米白色"], "尺寸": ["S", "M", "L", "XL"], "材质": ["羊毛混纺"], "分类": ["服装"], "品牌": ["针织系列"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  );