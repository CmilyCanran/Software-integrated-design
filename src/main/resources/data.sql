
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
-- 插入基于图片示例的17个商品数据
-- 1. T恤衫 - 基础款纯棉T恤
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '纯棉圆领T恤 - 经典款',
  '100%纯棉材质，舒适透气，简约设计，适合日常穿着。经典圆领设计，多种颜色可选，是衣橱必备的基础单品。',
  89.99,
  45,
  10.00,
  120,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["白色", "黑色", "灰色", "蓝色", "粉色"], "尺寸": ["S", "M", "L", "XL", "XXL"], "材质": ["纯棉"], "分类": ["服装"], "品牌": ["基础款"], "适合季节": ["春夏", "四季"], "领型": ["圆领"], "袖长": ["短袖"], "适合场合": ["日常", "休闲", "运动"]}, "image_data": {"main_image": "/uploads/images/products/1image20251203120000.jpg"}}'::jsonb,
  NOW(),
  NOW()
);

-- 2. 高跟鞋 - 优雅女士高跟鞋
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '优雅细跟高跟鞋 - 职场必备',
  '精致细跟设计，提升女性气质。优质PU皮材质，舒适内垫，适合商务会议、晚宴等正式场合。多种经典色可选。',
  299.99,
  28,
  15.00,
  85,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["黑色", "裸色", "红色", "银色"], "尺寸": ["35", "36", "37", "38", "39", "40"], "材质": ["PU皮", "真皮"], "分类": ["鞋类"], "品牌": ["优雅坊"], "适合季节": ["四季"], "鞋跟高度": ["8cm", "10cm", "12cm"], "适合场合": ["商务", "晚宴", "约会"]}, "image_data": {"main_image": "/uploads/images/products/2image20251203120001.jpg"}}'::jsonb,
  NOW(),
  NOW()
);

-- 3. 牛仔裤 - 经典修身牛仔裤
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '经典修身牛仔裤 - 百搭款',
  '优质牛仔面料，修身剪裁，完美修饰腿型。经典五袋设计，适合各种休闲场合。耐穿耐洗，性价比高。',
  199.99,
  67,
  20.00,
  95,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["深蓝色", "浅蓝色", "黑色", "灰色"], "尺寸": ["28", "29", "30", "31", "32", "33", "34"], "材质": ["牛仔布", "弹力牛仔"], "分类": ["服装"], "品牌": ["牛仔经典"], "适合季节": ["春秋", "四季"], "裤型": ["修身", "直筒"], "适合场合": ["日常", "休闲", "聚会"]}, "image_data": {"main_image": "/uploads/images/products/3image20251203120003.jpg"}}'::jsonb,
  NOW(),
  NOW()
);

-- 4. 休闲鞋 - 舒适休闲运动鞋
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '轻便休闲运动鞋 - 舒适款',
  '轻量化设计，透气网面材质，缓震鞋底，适合日常散步、轻度运动。简约时尚，百搭各种休闲装。',
  159.99,
  89,
  0.00,
  150,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["白色", "黑色", "灰色", "蓝色"], "尺寸": ["39", "40", "41", "42", "43", "44", "45"], "材质": ["网面", "合成革", "橡胶底"], "分类": ["鞋类"], "品牌": ["舒适步"], "适合季节": ["春夏", "四季"], "鞋底类型": ["缓震底", "防滑底"], "适合场合": ["休闲", "运动", "日常"]}, "image_data": {"main_image": "/uploads/images/products/4image20251203120004.jpg"}}'::jsonb,
  NOW(),
  NOW()
);

-- 5. 长袄羽绒服 - 加长保暖羽绒服
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '加长保暖羽绒服 - 极寒款',
  '90%白鸭绒填充，加长设计保暖性强，防风防水面料，适合严寒冬季。简约大方，实用性强。',
  599.99,
  34,
  25.00,
  60,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["黑色", "深蓝色", "咖啡色"], "尺寸": ["S", "M", "L", "XL", "XXL"], "材质": ["羽绒服", "防水面料"], "分类": ["服装"], "品牌": ["暖冬"], "适合季节": ["冬季"], "充绒量": ["200g", "250g", "300g"], "长度": ["长款", "超长款"], "适合场合": ["日常", "户外", "旅行"]}, "image_data": {"main_image": "/uploads/images/products/5image20251203120005.jpg"}}'::jsonb,
  NOW(),
  NOW()
);

-- 6. 冬季五件套 - 冬季保暖套装
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '冬季保暖五件套 - 全能防护',
  '包含帽子、围巾、手套、袜子、护膝五件套，全面保暖。柔软毛绒材质，适合冬季户外活动。',
  129.99,
  156,
  30.00,
  200,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["黑色", "灰色", "米色", "粉色"], "尺寸": ["均码"], "材质": ["毛绒", "羊毛混纺"], "分类": ["配饰"], "品牌": ["温暖家"], "适合季节": ["冬季"], "套装内容": ["帽子", "围巾", "手套", "袜子", "护膝"], "适合场合": ["户外", "日常", "运动"]}, "image_data": {"main_image": "/uploads/images/products/6image20251203120006.jpg"}}'::jsonb,
  NOW(),
  NOW()
);

-- 7. 夹克羽绒服 - 轻便夹克羽绒服
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '轻便夹克羽绒服 - 时尚款',
  '轻薄设计，80%白鸭绒填充，时尚修身剪裁。防风透气，适合秋冬季节日常穿着。',
  399.99,
  78,
  20.00,
  110,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["黑色", "蓝色", "红色", "军绿色"], "尺寸": ["S", "M", "L", "XL", "XXL"], "材质": ["轻薄羽绒服", "尼龙面料"], "分类": ["服装"], "品牌": ["轻暖"], "适合季节": ["秋冬", "初春"], "充绒量": ["150g", "180g"], "版型": ["修身", "宽松"], "适合场合": ["日常", "休闲", "旅行"]}, "image_data": {"main_image": "/uploads/images/products/7image20251203120007.jpg"}}'::jsonb,
  NOW(),
  NOW()
);

-- 8. 牛仔帽 - 经典牛仔帽
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '经典牛仔帽 - 时尚款',
  '优质牛仔布材质，经典牛仔帽造型，时尚百搭。可调节围围，适合各种头型，彰显个性风格。',
  79.99,
  123,
  10.00,
  180,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["蓝色", "黑色", "卡其色"], "尺寸": ["均码", "可调节"], "材质": ["牛仔布", "棉"], "分类": ["配饰"], "品牌": ["牛仔风"], "适合季节": ["春夏", "四季"], "帽檐宽度": ["宽檐", "窄檐"], "适合场合": ["休闲", "户外", "时尚"]}, "image_data": {"main_image": "/uploads/images/products/8image20251203120008.jpg"}}'::jsonb,
  NOW(),
  NOW()
);

-- 9. 学生装 - 青春学生制服
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '青春学生制服套装 - 校园款',
  '经典学生装设计，包含上衣和裤子。舒适棉涤混纺面料，耐穿易打理，适合学生日常穿着。',
  159.99,
  201,
  15.00,
  140,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["白色上衣+蓝色裤子", "白色上衣+黑色裤子", "浅蓝色套装"], "尺寸": ["150cm", "160cm", "170cm", "180cm"], "材质": ["棉涤混纺"], "分类": ["服装"], "品牌": ["青春园"], "适合季节": ["春秋", "四季"], "套装类型": ["上衣+裤子"], "适合场合": ["校园", "日常", "活动"]}, "image_data": {"main_image": "/uploads/images/products/9image20251203120009.jpg"}}'::jsonb,
  NOW(),
  NOW()
);

-- 10. 短袄羽绒服 - 短款时尚羽绒服
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '短款时尚羽绒服 - 修身款',
  '短款设计，时尚修身，85%白鸭绒填充。轻便保暖，适合都市女性冬季穿着，搭配性强。',
  449.99,
  56,
  25.00,
  90,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["米白色", "粉色", "浅蓝色", "黑色"], "尺寸": ["S", "M", "L", "XL"], "材质": ["羽绒服", "防风面料"], "分类": ["服装"], "品牌": ["都市丽人"], "适合季节": ["冬季"], "充绒量": ["180g", "220g"], "衣长": ["短款"], "适合场合": ["日常", "购物", "聚会"]}, "image_data": {"main_image": "/uploads/images/products/10image202512031200010.jpg"}}'::jsonb,
  NOW(),
  NOW()
);

-- 11. 毛线帽 - 编织毛线帽
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '手工编织毛线帽 - 温暖款',
  '优质毛线手工编织，柔软舒适，保暖效果好。多种经典配色，适合秋冬季节佩戴。',
  49.99,
  267,
  20.00,
  250,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["米色", "灰色", "驼色", "酒红色", "深蓝色"], "尺寸": ["均码"], "材质": ["羊毛", "兔毛混纺"], "分类": ["配饰"], "品牌": ["温暖织"], "适合季节": ["秋冬", "初春"], "编织方式": ["手工编织", "机器编织"], "适合场合": ["日常", "户外", "休闲"]}, "image_data": {"main_image": "/uploads/images/products/11image202512031200011.jpg"}}'::jsonb,
  NOW(),
  NOW()
);

-- 12. 皮鞋 - 正装皮鞋
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '商务正装皮鞋 - 经典款',
  '优质头层牛皮，手工缝制，经典德比鞋型。内里柔软透气，适合商务正式场合穿着。',
  399.99,
  89,
  15.00,
  120,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["黑色", "深棕色"], "尺寸": ["39", "40", "41", "42", "43", "44"], "材质": ["头层牛皮", "猪皮内里"], "分类": ["鞋类"], "品牌": ["商务名品"], "适合季节": ["四季"], "鞋底材质": ["牛皮底", "橡胶底"], "适合场合": ["商务", "正式", "会议"]}, "image_data": {"main_image": "/uploads/images/products/12image202512031200012.jpg"}}'::jsonb,
  NOW(),
  NOW()
);

-- 13. 鸭舌帽 - 休闲鸭舌帽
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '休闲鸭舌帽 - 运动款',
  '经典鸭舌帽设计，纯棉面料，吸汗透气。可调节后围，适合运动户外活动。',
  59.99,
  178,
  10.00,
  220,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["黑色", "白色", "海军蓝", "红色", "绿色"], "尺寸": ["均码", "可调节"], "材质": ["纯棉", "帆布"], "分类": ["配饰"], "品牌": ["运动风"], "适合季节": ["春夏", "四季"], "帽檐样式": ["平檐", "弯檐"], "适合场合": ["运动", "户外", "休闲"]}, "image_data": {"main_image": "/uploads/images/products/13image202512031200013.jpg"}}'::jsonb,
  NOW(),
  NOW()
);

-- 14. 短袖裙 - 夏季短袖连衣裙
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '夏季碎花短袖连衣裙 - 清新款',
  '轻盈雪纺面料，清新碎花图案，A字版型显瘦。适合夏季各种休闲场合，展现女性魅力。',
  189.99,
  145,
  30.00,
  100,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["粉色碎花", "蓝色碎花", "黄色碎花", "紫色碎花"], "尺寸": ["S", "M", "L", "XL"], "材质": ["雪纺", "涤纶"], "分类": ["服装"], "品牌": ["花语"], "适合季节": ["夏季"], "裙长": ["及膝", "过膝"], "袖长": ["短袖"], "适合场合": ["约会", "聚会", "度假"]}, "image_data": {"main_image": "/uploads/images/products/14image202512031200014.jpg"}}'::jsonb,
  NOW(),
  NOW()
);

-- 15. 毛衣 - 保暖针织毛衣
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '保暖针织毛衣 - 经典款',
  '优质羊毛混纺，保暖舒适，经典针织纹理。简约大方，适合秋冬季节日常穿着。',
  249.99,
  98,
  20.00,
  130,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["米白色", "驼色", "燕麦色", "深灰色"], "尺寸": ["S", "M", "L", "XL", "XXL"], "材质": ["羊毛混纺", "羊绒"], "分类": ["服装"], "品牌": ["暖意"], "适合季节": ["秋冬", "初春"], "领型": ["圆领", "V领"], "袖长": ["长袖"], "适合场合": ["日常", "办公", "休闲"]}, "image_data": {"main_image": "/uploads/images/products/15image202512031200015.jpg"}}'::jsonb,
  NOW(),
  NOW()
);

-- 16. 围巾 - 装饰围巾
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '时尚装饰围巾 - 百搭款',
  '轻薄透气，多种时尚图案，装饰性强。适合春秋季搭配，提升整体造型感。',
  69.99,
  234,
  25.00,
  280,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["格子图案", "条纹图案", "纯色", "波点图案"], "尺寸": ["180cm长", "200cm长"], "材质": ["涤纶", "丝巾", "棉"], "分类": ["配饰"], "品牌": ["时尚配"], "适合季节": ["春秋", "四季"], "图案类型": ["几何", "花卉", "抽象"], "适合场合": ["日常", "办公", "聚会"]}, "image_data": {"main_image": "/uploads/images/products/16image202512031200016.jpg"}}'::jsonb,
  NOW(),
  NOW()
);

-- 17. 运动鞋 - 专业运动鞋
INSERT INTO products (product_name, description, price, sales_count, discount, stock_quantity, is_available, creator_id, product_data, created_at, updated_at)
VALUES (
  '专业缓震运动鞋 - 训练款',
  '专业运动鞋设计，高弹缓震科技，透气网面。适合跑步、健身等运动，保护脚部健康。',
  349.99,
  167,
  15.00,
  160,
  true,
  (SELECT id FROM users WHERE username = 'shoper'),
  '{"specifications": {"颜色": ["黑红配色", "蓝白配色", "灰橙配色", "全黑配色"], "尺寸": ["38", "39", "40", "41", "42", "43", "44", "45"], "材质": ["网面", "TPU", "橡胶底"], "分类": ["鞋类"], "品牌": ["极速"], "适合季节": ["四季"], "鞋底科技": ["缓震科技", "防滑科技"], "运动类型": ["跑步", "训练", "综合"], "适合场合": ["运动", "健身", "户外"]}, "image_data": {"main_image": "/uploads/images/products/17image202512031200017.jpg"}}'::jsonb,
  NOW(),
  NOW()
);