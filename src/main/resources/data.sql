-- 插入用户（如果需要）密码都是123456
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

-- 插入商品数据
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
SELECT * FROM (
  VALUES
    (
      'Nike Air Max 270',
      '时尚舒适的运动鞋，适合日常穿着和轻度运动',
      899.00,
      0,
      10.00,
      50,
      true,
      (SELECT id FROM users WHERE username = 'admin'),
      '{"specifications": {"category": "鞋类", "brand": "Nike"}, "extended_attributes": {"颜色": ["黑色", "白色", "蓝色"], "尺寸": ["36", "37", "38", "39", "40", "41", "42", "43", "44"], "材质": ["合成材料", "透气网面"]}, "image_data": {"main_image": "/images/products/nike-air-max-270-main.jpg", "gallery": ["/images/products/nike-air-max-270-1.jpg", "/images/products/nike-air-max-270-2.jpg"]}}'::jsonb,
      NOW(),
      NOW()
    ),
    (
      'Uniqlo U 纯棉圆领T恤',
      '100%纯棉材质，舒适透气，简约设计适合多种场合',
      99.00,
      0,
      0.00,
      100,
      true,
      (SELECT id FROM users WHERE username = 'admin'),
      '{"specifications": {"category": "服装", "brand": "Uniqlo"}, "extended_attributes": {"颜色": ["白色", "黑色", "灰色", "蓝色"], "尺寸": ["S", "M", "L", "XL", "XXL"], "材质": ["纯棉"]}, "image_data": {"main_image": "/images/products/uniqlo-tshirt-main.jpg", "gallery": ["/images/products/uniqlo-tshirt-1.jpg", "/images/products/uniqlo-tshirt-2.jpg"]}}'::jsonb,
      NOW(),
      NOW()
    ),
    (
      'Samsonite商务双肩包',
      '大容量设计，防水面料，适合商务和旅行使用',
      599.00,
      0,
      5.00,
      30,
      true,
      (SELECT id FROM users WHERE username = 'admin'),
      '{"specifications": {"category": "箱包", "brand": "Samsonite"}, "extended_attributes": {"颜色": ["黑色", "灰色", "蓝色"], "尺寸": ["40L", "50L"], "材质": ["防水尼龙", "涤纶"]}, "image_data": {"main_image": "/images/products/samsonite-backpack-main.jpg", "gallery": ["/images/products/samsonite-backpack-1.jpg", "/images/products/samsonite-backpack-2.jpg"]}}'::jsonb,
      NOW(),
      NOW()
    ),
    (
      'Apple Watch Series 8',
      '多功能运动监测，心率监测，消息提醒，长续航',
      2999.00,
      0,
      0.00,
      20,
      true,
      (SELECT id FROM users WHERE username = 'admin'),
      '{"specifications": {"category": "配饰", "brand": "Apple"}, "extended_attributes": {"颜色": ["黑色", "银色", "金色"], "表带材质": ["硅胶", "金属", "皮革"], "屏幕尺寸": ["1.3英寸", "1.5英寸"]}, "image_data": {"main_image": "/images/products/apple-watch-main.jpg", "gallery": ["/images/products/apple-watch-1.jpg", "/images/products/apple-watch-2.jpg"]}}'::jsonb,
      NOW(),
      NOW()
    ),
    (
      'Zara 优雅连衣裙',
      '优雅设计，舒适面料，适合多种场合穿着',
      299.00,
      0,
      15.00,
      40,
      true,
      (SELECT id FROM users WHERE username = 'admin'),
      '{"specifications": {"category": "服装", "brand": "Zara"}, "extended_attributes": {"颜色": ["红色", "黑色", "蓝色", "白色"], "尺寸": ["S", "M", "L", "XL"], "材质": ["雪纺", "棉质"]}, "image_data": {"main_image": "/images/products/zara-dress-main.jpg", "gallery": ["/images/products/zara-dress-1.jpg", "/images/products/zara-dress-2.jpg"]}}'::jsonb,
      NOW(),
      NOW()
    ),
    (
      'Sony WH-1000XM4 无线耳机',
      '降噪功能，蓝牙5.0连接，长续航时间',
      1999.00,
      0,
      20.00,
      25,
      true,
      (SELECT id FROM users WHERE username = 'admin'),
      '{"specifications": {"category": "配饰", "brand": "Sony"}, "extended_attributes": {"颜色": ["黑色", "白色"], "连接方式": ["蓝牙5.0"], "续航时间": ["20小时", "30小时"]}, "image_data": {"main_image": "/images/products/sony-headphones-main.jpg", "gallery": ["/images/products/sony-headphones-1.jpg", "/images/products/sony-headphones-2.jpg"]}}'::jsonb,
      NOW(),
      NOW()
    )
) AS new_products(product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = new_products.product_name);