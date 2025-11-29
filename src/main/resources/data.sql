-- 清除现有数据并插入用户（密码都是123456）
DELETE FROM users;
DELETE FROM products;

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

-- 插入服装商品数据（简化版，只保留主图片，全部改为服装类别）
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES
  (
    '纯棉圆领T恤 - 基础款',
    '100%纯棉材质，舒适透气，简约设计，适合日常穿着',
    89.99,
    25,
    0.00,
    60,
    true,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"category": "服装", "brand": "基础款"}, "extended_attributes": {"颜色": ["白色", "黑色", "灰色", "蓝色"], "尺寸": ["S", "M", "L", "XL", "XXL"], "材质": ["纯棉"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
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
    true,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"category": "服装", "brand": "时尚女装"}, "extended_attributes": {"颜色": ["碎花蓝色", "碎花粉色", "纯白色"], "尺寸": ["S", "M", "L", "XL"], "材质": ["雪纺", "棉质"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
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
    false,
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"category": "服装", "brand": "商务系列"}, "extended_attributes": {"颜色": ["深黑色", "藏青色", "灰色"], "尺寸": ["28", "30", "32", "34", "36"], "材质": ["弹力棉", "涤纶混纺"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
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
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"category": "服装", "brand": "运动系列"}, "extended_attributes": {"颜色": ["黑色", "灰色", "白色", "蓝色"], "尺寸": ["S", "M", "L", "XL", "XXL"], "材质": ["棉绒", "抓绒"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
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
    (SELECT id FROM users WHERE username = 'admin'),
    '{"specifications": {"category": "服装", "brand": "经典系列"}, "extended_attributes": {"颜色": ["深蓝色", "浅蓝色", "黑色"], "尺寸": ["S", "M", "L", "XL"], "材质": ["牛仔布", "棉质"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
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
    '{"specifications": {"category": "服装", "brand": "配饰系列"}, "extended_attributes": {"颜色": ["红色", "蓝色", "紫色", "粉色"], "尺寸": ["均码"], "材质": ["真丝"]}, "image_data": {"main_image": "/images/placeholder-product.png"}}'::jsonb,
    NOW(),
    NOW()
  );