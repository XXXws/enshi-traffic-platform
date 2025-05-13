/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3308
 Source Server Type    : MySQL
 Source Server Version : 80100 (8.1.0)
 Source Host           : localhost:3308
 Source Schema         : enshistraffic

 Target Server Type    : MySQL
 Target Server Version : 80100 (8.1.0)
 File Encoding         : 65001

 Date: 13/05/2025 14:14:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for monitoring_points
-- ----------------------------
DROP TABLE IF EXISTS `monitoring_points`;
CREATE TABLE `monitoring_points`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '监控点ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '监控点名称',
  `type` enum('CAMERA','SENSOR','SPEED_DETECTOR','WEATHER_STATION','COMBINED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '监控设备类型',
  `status` enum('ACTIVE','INACTIVE','MAINTENANCE','OFFLINE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '设备状态',
  `longitude` decimal(10, 7) NOT NULL COMMENT '经度',
  `latitude` decimal(10, 7) NOT NULL COMMENT '纬度',
  `road_id` int NULL DEFAULT NULL COMMENT '关联道路ID',
  `road_section_id` int NULL DEFAULT NULL COMMENT '关联路段ID',
  `installation_date` date NULL DEFAULT NULL COMMENT '安装日期',
  `last_maintenance_date` date NULL DEFAULT NULL COMMENT '最后维护日期',
  `data_frequency` int NULL DEFAULT NULL COMMENT '数据采集频率(秒)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '设备描述',
  `capabilities` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '设备功能描述',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_monitor_road`(`road_id` ASC) USING BTREE,
  INDEX `idx_monitor_section`(`road_section_id` ASC) USING BTREE,
  INDEX `idx_monitor_type`(`type` ASC) USING BTREE,
  INDEX `idx_monitor_status`(`status` ASC) USING BTREE,
  INDEX `idx_monitor_location`(`longitude` ASC, `latitude` ASC) USING BTREE,
  CONSTRAINT `monitoring_points_ibfk_1` FOREIGN KEY (`road_id`) REFERENCES `roads` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `monitoring_points_ibfk_2` FOREIGN KEY (`road_section_id`) REFERENCES `road_sections` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '交通监控点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of monitoring_points
-- ----------------------------
INSERT INTO `monitoring_points` VALUES (1, '航空路中段监控点', 'CAMERA', 'ACTIVE', 109.4812340, 30.3101230, 1, 2, '2023-08-15', '2025-01-10', 60, '监控航空路中段路口交通情况', NULL, '2025-05-13 14:13:58', '2025-05-13 14:13:58');
INSERT INTO `monitoring_points` VALUES (2, '舞阳商圈监控点', 'COMBINED', 'ACTIVE', 109.4874560, 30.2856780, 2, 5, '2023-09-20', '2025-01-15', 30, '监控舞阳商圈交通流量，包含摄像头和流量传感器', NULL, '2025-05-13 14:13:58', '2025-05-13 14:13:58');
INSERT INTO `monitoring_points` VALUES (3, '清江大桥监控点', 'CAMERA', 'ACTIVE', 109.4687650, 30.2901230, 3, 7, '2023-07-10', '2024-12-05', 60, '监控清江大桥通行情况', NULL, '2025-05-13 14:13:58', '2025-05-13 14:13:58');
INSERT INTO `monitoring_points` VALUES (4, 'G209恩施北入口检测点', 'SPEED_DETECTOR', 'ACTIVE', 109.4301230, 30.3456780, 4, 8, '2023-10-05', '2025-02-20', 120, '监测国道车辆进入恩施的速度和流量', NULL, '2025-05-13 14:13:58', '2025-05-13 14:13:58');
INSERT INTO `monitoring_points` VALUES (5, '沿江路隧道监控点', 'COMBINED', 'ACTIVE', 109.4578900, 30.2853450, 5, 11, '2023-11-15', '2025-03-01', 30, '监控隧道内通行情况和空气质量', NULL, '2025-05-13 14:13:58', '2025-05-13 14:13:58');

-- ----------------------------
-- Table structure for peak_period_rules
-- ----------------------------
DROP TABLE IF EXISTS `peak_period_rules`;
CREATE TABLE `peak_period_rules`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则名称',
  `day_type` enum('WORKDAY','WEEKEND','HOLIDAY','ALL') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '适用日期类型',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '规则描述',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_peak_day_type`(`day_type` ASC) USING BTREE,
  INDEX `idx_peak_active`(`is_active` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '高峰期规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of peak_period_rules
-- ----------------------------
INSERT INTO `peak_period_rules` VALUES (1, '工作日早高峰', 'WORKDAY', '07:30:00', '09:30:00', 1, '工作日早上的交通高峰期', '2025-05-13 14:14:09', '2025-05-13 14:14:09');
INSERT INTO `peak_period_rules` VALUES (2, '工作日晚高峰', 'WORKDAY', '17:00:00', '19:30:00', 1, '工作日晚上的交通高峰期', '2025-05-13 14:14:09', '2025-05-13 14:14:09');
INSERT INTO `peak_period_rules` VALUES (3, '周末商圈高峰', 'WEEKEND', '14:00:00', '18:00:00', 1, '周末商业区的交通高峰期', '2025-05-13 14:14:09', '2025-05-13 14:14:09');
INSERT INTO `peak_period_rules` VALUES (4, '节假日景区高峰', 'HOLIDAY', '09:00:00', '17:00:00', 1, '节假日景区周边的交通高峰期', '2025-05-13 14:14:09', '2025-05-13 14:14:09');
INSERT INTO `peak_period_rules` VALUES (5, '工作日中午次高峰', 'WORKDAY', '11:30:00', '13:30:00', 1, '工作日午间的交通次高峰', '2025-05-13 14:14:09', '2025-05-13 14:14:09');

-- ----------------------------
-- Table structure for regions
-- ----------------------------
DROP TABLE IF EXISTS `regions`;
CREATE TABLE `regions`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '区域ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区域名称',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '区域编码',
  `type` enum('ADMINISTRATIVE','FUNCTIONAL','COMMERCIAL','RESIDENTIAL','INDUSTRIAL','SCENIC') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区域类型：行政区、功能区、商业区、居住区、工业区、景区',
  `parent_id` int NULL DEFAULT NULL COMMENT '父级区域ID，用于层级关系',
  `level` int NOT NULL DEFAULT 1 COMMENT '区域层级，1为市级，2为区县级，3为乡镇级，4为功能区',
  `center_longitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '中心点经度',
  `center_latitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '中心点纬度',
  `boundary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '区域边界坐标集合，GeoJSON格式',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '区域描述',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE,
  INDEX `idx_region_type`(`type` ASC) USING BTREE,
  INDEX `idx_region_parent`(`parent_id` ASC) USING BTREE,
  CONSTRAINT `regions_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `regions` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '区域信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of regions
-- ----------------------------
INSERT INTO `regions` VALUES (1, '恩施市', 'ES01', 'ADMINISTRATIVE', NULL, 1, 109.4799960, 30.2951140, NULL, '恩施土家族苗族自治州首府', '2025-05-13 14:13:48', '2025-05-13 14:13:48');
INSERT INTO `regions` VALUES (2, '舞阳坝片区', 'ES0101', 'COMMERCIAL', 1, 2, 109.4869900, 30.2834070, NULL, '恩施市中心商业区，是集购物、餐饮、休闲于一体的商圈', '2025-05-13 14:13:48', '2025-05-13 14:13:48');
INSERT INTO `regions` VALUES (3, '学院片区', 'ES0102', 'FUNCTIONAL', 1, 2, 109.4953620, 30.2685190, NULL, '以恩施职业技术学院为中心的教育区域', '2025-05-13 14:13:48', '2025-05-13 14:13:48');
INSERT INTO `regions` VALUES (4, '航空路片区', 'ES0103', 'RESIDENTIAL', 1, 2, 109.4756310, 30.3024120, NULL, '恩施市主要居住区之一，靠近州政府', '2025-05-13 14:13:48', '2025-05-13 14:13:48');
INSERT INTO `regions` VALUES (5, '龙凤镇片区', 'ES0104', 'RESIDENTIAL', 1, 2, 109.4191250, 30.2988570, NULL, '恩施西部居住区，靠近腾龙洞景区', '2025-05-13 14:13:48', '2025-05-13 14:13:48');

-- ----------------------------
-- Table structure for road_sections
-- ----------------------------
DROP TABLE IF EXISTS `road_sections`;
CREATE TABLE `road_sections`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '路段ID',
  `road_id` int NOT NULL COMMENT '所属道路ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路段名称',
  `sequence` int NOT NULL COMMENT '在道路中的序号，从起点往终点方向递增',
  `start_longitude` decimal(10, 7) NOT NULL COMMENT '起点经度',
  `start_latitude` decimal(10, 7) NOT NULL COMMENT '起点纬度',
  `end_longitude` decimal(10, 7) NOT NULL COMMENT '终点经度',
  `end_latitude` decimal(10, 7) NOT NULL COMMENT '终点纬度',
  `length` decimal(8, 3) NOT NULL COMMENT '路段长度(公里)',
  `width` decimal(5, 2) NULL DEFAULT NULL COMMENT '路段宽度(米)',
  `lanes` tinyint NULL DEFAULT NULL COMMENT '车道数',
  `section_type` enum('NORMAL','TUNNEL','BRIDGE','INTERSECTION') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NORMAL' COMMENT '路段类型',
  `is_key_section` tinyint(1) NULL DEFAULT 0 COMMENT '是否为关键路段(易拥堵点)',
  `slope` decimal(5, 2) NULL DEFAULT NULL COMMENT '坡度(百分比)',
  `curve_radius` decimal(7, 2) NULL DEFAULT NULL COMMENT '弯道半径(米)，0表示直线',
  `curve_direction` enum('NONE','LEFT','RIGHT','S_SHAPE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'NONE' COMMENT '弯道方向',
  `elevation_start` int NULL DEFAULT NULL COMMENT '起点海拔(米)',
  `elevation_end` int NULL DEFAULT NULL COMMENT '终点海拔(米)',
  `visibility` enum('EXCELLENT','GOOD','FAIR','POOR') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '能见度条件',
  `blind_spot_risk` enum('NONE','LOW','MEDIUM','HIGH') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'NONE' COMMENT '视线盲区风险',
  `speed_limit` int NULL DEFAULT NULL COMMENT '路段限速(公里/小时)',
  `geometry` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '路段几何形状，GeoJSON LineString格式',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '路段描述',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `start_point` point NOT NULL,
  `end_point` point NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_section_road`(`road_id` ASC) USING BTREE,
  INDEX `idx_section_key`(`is_key_section` ASC) USING BTREE,
  INDEX `idx_section_type`(`section_type` ASC) USING BTREE,
  INDEX `idx_section_coordinates`(`start_longitude` ASC, `start_latitude` ASC, `end_longitude` ASC, `end_latitude` ASC) USING BTREE,
  INDEX `idx_section_slope_curve`(`slope` ASC, `curve_radius` ASC) USING BTREE,
  INDEX `idx_section_key_type`(`is_key_section` ASC, `section_type` ASC) USING BTREE,
  SPATIAL INDEX `idx_section_start_geo`(`start_point`),
  SPATIAL INDEX `idx_section_end_geo`(`end_point`),
  CONSTRAINT `road_sections_ibfk_1` FOREIGN KEY (`road_id`) REFERENCES `roads` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '道路段信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of road_sections
-- ----------------------------
INSERT INTO `road_sections` VALUES (1, 1, '航空路北段', 1, 109.4773560, 30.2956320, 109.4807430, 30.3079250, 1.500, 16.00, 4, 'NORMAL', 0, 1.50, 0.00, 'NONE', 430, 445, 'EXCELLENT', 'NONE', 50, NULL, '航空路北段，较为平直的城市道路', '2025-05-13 14:13:55', '2025-05-13 14:13:55', ST_GeomFromText('POINT(109.477356 30.295632)'), ST_GeomFromText('POINT(109.480743 30.307925)'));
INSERT INTO `road_sections` VALUES (2, 1, '航空路中段', 2, 109.4807430, 30.3079250, 109.4831680, 30.3172890, 1.200, 16.00, 4, 'NORMAL', 1, 2.00, 0.00, 'NONE', 445, 460, 'GOOD', 'NONE', 50, NULL, '航空路中段，交通流量较大', '2025-05-13 14:13:55', '2025-05-13 14:13:55', ST_GeomFromText('POINT(109.480743 30.307925)'), ST_GeomFromText('POINT(109.483168 30.317289)'));
INSERT INTO `road_sections` VALUES (3, 1, '航空路南段', 3, 109.4831680, 30.3172890, 109.4856790, 30.3263520, 1.500, 16.00, 4, 'NORMAL', 0, 4.00, 200.00, 'RIGHT', 460, 480, 'GOOD', 'LOW', 40, NULL, '航空路南段，有一定坡度和弯道', '2025-05-13 14:13:55', '2025-05-13 14:13:55', ST_GeomFromText('POINT(109.483168 30.317289)'), ST_GeomFromText('POINT(109.485679 30.326352)'));
INSERT INTO `road_sections` VALUES (4, 2, '舞阳大道北段', 1, 109.4869900, 30.2701230, 109.4871230, 30.2834070, 2.800, 24.00, 6, 'NORMAL', 0, 0.80, 0.00, 'NONE', 410, 425, 'EXCELLENT', 'NONE', 60, NULL, '舞阳大道北段，平直宽阔', '2025-05-13 14:13:55', '2025-05-13 14:13:55', ST_GeomFromText('POINT(109.48699 30.270123)'), ST_GeomFromText('POINT(109.487123 30.283407)'));
INSERT INTO `road_sections` VALUES (5, 2, '舞阳大道中央商业段', 2, 109.4871230, 30.2834070, 109.4890120, 30.2965240, 3.200, 24.00, 6, 'NORMAL', 1, 0.50, 0.00, 'NONE', 425, 430, 'EXCELLENT', 'NONE', 50, NULL, '舞阳大道商业区段，交通流量大，经常拥堵', '2025-05-13 14:13:55', '2025-05-13 14:13:55', ST_GeomFromText('POINT(109.487123 30.283407)'), ST_GeomFromText('POINT(109.489012 30.296524)'));
INSERT INTO `road_sections` VALUES (6, 2, '舞阳大道南段', 3, 109.4890120, 30.2965240, 109.4921540, 30.3124310, 2.600, 24.00, 6, 'NORMAL', 0, 2.10, 300.00, 'LEFT', 430, 450, 'GOOD', 'LOW', 60, NULL, '舞阳大道南段，有缓坡和弯道', '2025-05-13 14:13:55', '2025-05-13 14:13:55', ST_GeomFromText('POINT(109.489012 30.296524)'), ST_GeomFromText('POINT(109.492154 30.312431)'));
INSERT INTO `road_sections` VALUES (7, 3, '清江大桥段', 1, 109.4652340, 30.2879560, 109.4723450, 30.2923450, 0.850, 18.00, 4, 'BRIDGE', 1, 7.00, 0.00, 'NONE', 400, 420, 'GOOD', 'NONE', 40, NULL, '横跨清江的桥梁，重要的交通节点', '2025-05-13 14:13:55', '2025-05-13 14:13:55', ST_GeomFromText('POINT(109.465234 30.287956)'), ST_GeomFromText('POINT(109.472345 30.292345)'));
INSERT INTO `road_sections` VALUES (8, 4, 'G209恩施北山段', 1, 109.4267890, 30.3512340, 109.4367890, 30.3321560, 5.100, 18.00, 4, 'NORMAL', 0, 6.50, 150.00, 'S_SHAPE', 500, 650, 'FAIR', 'HIGH', 60, NULL, 'G209国道北部山区段，坡陡弯急，易起雾', '2025-05-13 14:13:55', '2025-05-13 14:13:55', ST_GeomFromText('POINT(109.426789 30.351234)'), ST_GeomFromText('POINT(109.436789 30.332156)'));
INSERT INTO `road_sections` VALUES (9, 4, 'G209恩施城区段', 2, 109.4367890, 30.3321560, 109.4832140, 30.2832140, 4.500, 18.00, 4, 'NORMAL', 1, 2.50, 200.00, 'RIGHT', 420, 500, 'GOOD', 'LOW', 70, NULL, 'G209国道穿过恩施城区段，交通流量大', '2025-05-13 14:13:55', '2025-05-13 14:13:55', ST_GeomFromText('POINT(109.436789 30.332156)'), ST_GeomFromText('POINT(109.483214 30.283214)'));
INSERT INTO `road_sections` VALUES (10, 4, 'G209恩施南山段', 3, 109.4832140, 30.2832140, 109.5001230, 30.2432100, 5.700, 18.00, 4, 'NORMAL', 0, 7.00, 180.00, 'S_SHAPE', 380, 420, 'FAIR', 'MEDIUM', 60, NULL, 'G209国道南部山区段，坡度大，有多处连续弯道', '2025-05-13 14:13:55', '2025-05-13 14:13:55', ST_GeomFromText('POINT(109.483214 30.283214)'), ST_GeomFromText('POINT(109.500123 30.24321)'));
INSERT INTO `road_sections` VALUES (11, 5, '沿江路隧道段', 1, 109.4623450, 30.2856780, 109.4523450, 30.2851230, 1.200, 10.00, 2, 'TUNNEL', 1, 2.00, 0.00, 'NONE', 400, 410, 'FAIR', 'LOW', 40, NULL, '穿过山体的城市隧道，视线受限', '2025-05-13 14:13:55', '2025-05-13 14:13:55', ST_GeomFromText('POINT(109.462345 30.285678)'), ST_GeomFromText('POINT(109.452345 30.285123)'));

-- ----------------------------
-- Table structure for roads
-- ----------------------------
DROP TABLE IF EXISTS `roads`;
CREATE TABLE `roads`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '道路ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '道路名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '道路编码',
  `type` enum('NATIONAL','PROVINCIAL','COUNTY','TOWNSHIP','URBAN','HIGHWAY') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '道路类型：国道、省道、县道、乡道、城市道路、高速公路',
  `level` tinyint NOT NULL COMMENT '道路等级：1-8，数字越小等级越高',
  `start_point` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '起点描述',
  `end_point` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '终点描述',
  `length` decimal(10, 2) NULL DEFAULT NULL COMMENT '道路长度(公里)',
  `lanes` tinyint NULL DEFAULT NULL COMMENT '车道数',
  `speed_limit` int NULL DEFAULT NULL COMMENT '限速(公里/小时)',
  `is_two_way` tinyint(1) NULL DEFAULT 1 COMMENT '是否双向通行',
  `is_tunnel` tinyint(1) NULL DEFAULT 0 COMMENT '是否为隧道',
  `is_bridge` tinyint(1) NULL DEFAULT 0 COMMENT '是否为桥梁',
  `is_monitored` tinyint(1) NULL DEFAULT 0 COMMENT '是否有实时监控',
  `avg_slope` decimal(5, 2) NULL DEFAULT NULL COMMENT '平均坡度(百分比)',
  `max_slope` decimal(5, 2) NULL DEFAULT NULL COMMENT '最大坡度(百分比)',
  `curve_density` int NULL DEFAULT NULL COMMENT '弯道密度(每公里弯道数)',
  `elevation_min` int NULL DEFAULT NULL COMMENT '最低海拔(米)',
  `elevation_max` int NULL DEFAULT NULL COMMENT '最高海拔(米)',
  `landslide_risk` enum('LOW','MEDIUM','HIGH','UNKNOWN') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'UNKNOWN' COMMENT '滑坡风险等级',
  `fog_frequency` enum('RARE','OCCASIONAL','FREQUENT','UNKNOWN') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'UNKNOWN' COMMENT '雾气频率',
  `road_condition` enum('EXCELLENT','GOOD','FAIR','POOR') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '道路状况',
  `geometry` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '道路几何形状，GeoJSON LineString格式',
  `region_id` int NULL DEFAULT NULL COMMENT '所属区域ID',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '道路描述',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE,
  INDEX `idx_road_type`(`type` ASC) USING BTREE,
  INDEX `idx_road_level`(`level` ASC) USING BTREE,
  INDEX `idx_road_region`(`region_id` ASC) USING BTREE,
  INDEX `idx_road_monitored`(`is_monitored` ASC) USING BTREE,
  INDEX `idx_road_terrain`(`is_tunnel` ASC, `is_bridge` ASC) USING BTREE,
  CONSTRAINT `roads_ibfk_1` FOREIGN KEY (`region_id`) REFERENCES `regions` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '道路基本信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roads
-- ----------------------------
INSERT INTO `roads` VALUES (1, '航空路', 'RD001', 'URBAN', 3, '舞阳大道交叉口', '州政府', 4.20, 4, 50, 1, 0, 0, 1, 2.50, 6.00, 2, 420, 480, 'LOW', 'OCCASIONAL', 'GOOD', NULL, 1, '恩施市主要城市干道，沿线分布有行政中心、居民区和商业区', '2025-05-13 14:13:51', '2025-05-13 14:13:51');
INSERT INTO `roads` VALUES (2, '舞阳大道', 'RD002', 'URBAN', 2, '恩施大峡谷方向', '清江南路交叉口', 8.60, 6, 60, 1, 0, 1, 1, 1.20, 3.00, 1, 410, 450, 'LOW', 'RARE', 'EXCELLENT', NULL, 2, '恩施市最主要的城市主干道，贯穿城区南北', '2025-05-13 14:13:51', '2025-05-13 14:13:51');
INSERT INTO `roads` VALUES (3, '清江大桥', 'RD003', 'URBAN', 3, '清江北路', '清江南路', 0.85, 4, 40, 1, 0, 1, 1, 5.00, 7.00, 0, 400, 420, 'LOW', 'FREQUENT', 'GOOD', NULL, 1, '横跨清江的重要桥梁，连接城区南北两侧', '2025-05-13 14:13:51', '2025-05-13 14:13:51');
INSERT INTO `roads` VALUES (4, 'G209国道（恩施段）', 'RD004', 'NATIONAL', 1, '恩施北入口', '恩施南出口', 15.30, 4, 70, 1, 0, 0, 1, 3.50, 8.00, 4, 380, 650, 'MEDIUM', 'FREQUENT', 'GOOD', NULL, 1, '穿过恩施市的国道，是恩施连接外界的重要通道', '2025-05-13 14:13:51', '2025-05-13 14:13:51');
INSERT INTO `roads` VALUES (5, '沿江路隧道', 'RD005', 'URBAN', 3, '沿江路东段', '沿江路西段', 1.20, 2, 40, 1, 1, 0, 1, 1.00, 2.00, 0, 400, 410, 'LOW', 'RARE', 'GOOD', NULL, 2, '沿江路上的隧道，缓解交通压力的重要设施', '2025-05-13 14:13:51', '2025-05-13 14:13:51');

-- ----------------------------
-- Table structure for traffic_events
-- ----------------------------
DROP TABLE IF EXISTS `traffic_events`;
CREATE TABLE `traffic_events`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '事件ID',
  `event_type` enum('ACCIDENT','CONSTRUCTION','CLOSURE','CONGESTION','WEATHER_WARNING','LANDSLIDE','OTHER') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '事件类型',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '事件标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '事件描述',
  `severity` enum('LOW','MEDIUM','HIGH','CRITICAL') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '严重程度',
  `status` enum('ACTIVE','RESOLVED','PLANNED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '事件状态',
  `road_id` int NULL DEFAULT NULL COMMENT '关联道路ID',
  `road_section_id` int NULL DEFAULT NULL COMMENT '关联路段ID',
  `longitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '事件位置经度',
  `latitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '事件位置纬度',
  `radius` int NULL DEFAULT NULL COMMENT '影响半径(米)',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `expected_duration` int NULL DEFAULT NULL COMMENT '预计持续时间(分钟)',
  `impact_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '影响描述',
  `detour_suggestion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '绕行建议',
  `reported_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '报告来源',
  `verified` tinyint(1) NULL DEFAULT 0 COMMENT '是否已验证',
  `report_time` datetime NULL DEFAULT NULL COMMENT '报告时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_event_type`(`event_type` ASC) USING BTREE,
  INDEX `idx_event_status`(`status` ASC) USING BTREE,
  INDEX `idx_event_road`(`road_id` ASC) USING BTREE,
  INDEX `idx_event_section`(`road_section_id` ASC) USING BTREE,
  INDEX `idx_event_time`(`start_time` ASC, `end_time` ASC) USING BTREE,
  INDEX `idx_event_severity`(`severity` ASC) USING BTREE,
  INDEX `idx_event_location`(`longitude` ASC, `latitude` ASC) USING BTREE,
  INDEX `idx_event_active_time`(`status` ASC, `start_time` ASC, `end_time` ASC) USING BTREE,
  INDEX `idx_event_type_severity`(`event_type` ASC, `severity` ASC) USING BTREE,
  CONSTRAINT `traffic_events_ibfk_1` FOREIGN KEY (`road_id`) REFERENCES `roads` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `traffic_events_ibfk_2` FOREIGN KEY (`road_section_id`) REFERENCES `road_sections` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '交通事件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of traffic_events
-- ----------------------------
INSERT INTO `traffic_events` VALUES (1, 'CONSTRUCTION', '舞阳大道道路维修', '舞阳大道中央商业段进行道路沥青修补工程', 'MEDIUM', 'ACTIVE', 2, 5, 109.4878900, 30.2876540, 500, '2025-05-12 08:00:00', '2025-05-16 18:00:00', 5760, '占用道路一侧车道，可能导致交通缓行', '建议绕行航空路或清江路', '市政工程处', 1, '2025-05-10 09:30:00', '2025-05-13 14:14:01', '2025-05-13 14:14:01');
INSERT INTO `traffic_events` VALUES (2, 'ACCIDENT', '清江大桥交通事故', '两车相撞，造成轻微拥堵', 'MEDIUM', 'RESOLVED', 3, 7, 109.4678900, 30.2897650, 200, '2025-05-12 09:15:00', '2025-05-12 10:30:00', 75, '占用桥面一个车道，造成桥面车辆缓行', '建议绕行沿江路', '交警大队', 1, '2025-05-12 09:20:00', '2025-05-13 14:14:01', '2025-05-13 14:14:01');
INSERT INTO `traffic_events` VALUES (3, 'WEATHER_WARNING', 'G209北段大雾预警', 'G209国道恩施北山段能见度低于100米', 'HIGH', 'ACTIVE', 4, 8, 109.4300000, 30.3450000, 3000, '2025-05-13 05:30:00', '2025-05-13 10:00:00', 270, '大雾严重影响行车视线，车辆需减速慢行', '建议推迟出行或选择城区道路', '市气象局', 1, '2025-05-13 05:00:00', '2025-05-13 14:14:01', '2025-05-13 14:14:01');
INSERT INTO `traffic_events` VALUES (4, 'CONGESTION', '舞阳商圈交通拥堵', '舞阳商圈周边道路车流量大，交通拥堵', 'LOW', 'ACTIVE', 2, 5, 109.4880000, 30.2860000, 800, '2025-05-13 17:30:00', NULL, 120, '晚高峰期间商圈周边道路行驶缓慢', '建议绕行或延后出行', '交通实时监控系统', 1, '2025-05-13 17:35:00', '2025-05-13 14:14:01', '2025-05-13 14:14:01');
INSERT INTO `traffic_events` VALUES (5, 'LANDSLIDE', 'G209南段小型滑坡', 'G209国道恩施南山段发生小型滑坡，部分碎石落入路面', 'HIGH', 'ACTIVE', 4, 10, 109.4900000, 30.2600000, 100, '2025-05-13 14:20:00', '2025-05-13 20:00:00', 340, '道路部分阻断，清理工作正在进行中', '建议绕行S230省道', '公路养护中心', 1, '2025-05-13 14:30:00', '2025-05-13 14:14:01', '2025-05-13 14:14:01');

-- ----------------------------
-- Table structure for traffic_flow_records
-- ----------------------------
DROP TABLE IF EXISTS `traffic_flow_records`;
CREATE TABLE `traffic_flow_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '流量记录ID',
  `road_section_id` int NOT NULL COMMENT '路段ID',
  `record_time` datetime NOT NULL COMMENT '记录时间',
  `day_type` enum('WORKDAY','WEEKEND','HOLIDAY') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日期类型',
  `time_period` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '时间段描述（如：早高峰、晚高峰）',
  `traffic_volume` int NULL DEFAULT NULL COMMENT '交通流量(辆/小时)',
  `avg_speed` decimal(5, 2) NULL DEFAULT NULL COMMENT '平均车速(公里/小时)',
  `congestion_level` enum('SMOOTH','SLOW','CONGESTED','HEAVILY_CONGESTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '拥堵等级',
  `congestion_index` decimal(4, 2) NULL DEFAULT NULL COMMENT '拥堵指数(0-10)',
  `travel_time_minutes` decimal(6, 2) NULL DEFAULT NULL COMMENT '通过时间(分钟)',
  `occupancy_rate` decimal(5, 2) NULL DEFAULT NULL COMMENT '占有率(%)',
  `car_count` int NULL DEFAULT NULL COMMENT '小型车数量',
  `bus_count` int NULL DEFAULT NULL COMMENT '公交车数量',
  `truck_count` int NULL DEFAULT NULL COMMENT '货车数量',
  `motorcycle_count` int NULL DEFAULT NULL COMMENT '摩托车数量',
  `weather_condition` enum('SUNNY','CLOUDY','RAINY','FOGGY','SNOWY','ICY') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '天气状况',
  `temperature` decimal(4, 1) NULL DEFAULT NULL COMMENT '温度(摄氏度)',
  `visibility_meters` int NULL DEFAULT NULL COMMENT '能见度(米)',
  `data_source` enum('SENSOR','CAMERA','MANUAL','API','SIMULATION') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据来源',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_flow_section_time`(`road_section_id` ASC, `record_time` ASC) USING BTREE,
  INDEX `idx_flow_congestion`(`congestion_level` ASC) USING BTREE,
  INDEX `idx_flow_weather`(`weather_condition` ASC) USING BTREE,
  INDEX `idx_flow_day_type`(`day_type` ASC) USING BTREE,
  INDEX `idx_flow_section_time_congestion`(`road_section_id` ASC, `record_time` ASC, `congestion_level` ASC) USING BTREE,
  INDEX `idx_flow_weather_time`(`weather_condition` ASC, `record_time` ASC) USING BTREE,
  INDEX `idx_flow_time_range`(`record_time` ASC) USING BTREE,
  INDEX `idx_flow_date`((cast(`record_time` as date)) ASC) USING BTREE,
  CONSTRAINT `traffic_flow_records_ibfk_1` FOREIGN KEY (`road_section_id`) REFERENCES `road_sections` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '交通流量记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of traffic_flow_records
-- ----------------------------
INSERT INTO `traffic_flow_records` VALUES (1, 5, '2025-05-13 08:00:00', 'WORKDAY', '早高峰', 1850, 25.30, 'SLOW', 6.80, 12.50, 75.50, 1600, 80, 120, 50, 'SUNNY', 22.5, 10000, 'SENSOR', '2025-05-13 14:14:04');
INSERT INTO `traffic_flow_records` VALUES (2, 5, '2025-05-13 12:00:00', 'WORKDAY', '午间', 1200, 38.60, 'SMOOTH', 3.50, 8.20, 45.20, 1000, 60, 110, 30, 'SUNNY', 26.8, 10000, 'SENSOR', '2025-05-13 14:14:04');
INSERT INTO `traffic_flow_records` VALUES (3, 5, '2025-05-13 18:00:00', 'WORKDAY', '晚高峰', 2100, 18.40, 'CONGESTED', 7.90, 16.80, 85.30, 1850, 90, 100, 60, 'CLOUDY', 24.2, 8000, 'SENSOR', '2025-05-13 14:14:04');
INSERT INTO `traffic_flow_records` VALUES (4, 7, '2025-05-13 08:30:00', 'WORKDAY', '早高峰', 1600, 22.70, 'SLOW', 6.20, 5.50, 80.10, 1450, 70, 50, 30, 'SUNNY', 22.0, 10000, 'CAMERA', '2025-05-13 14:14:04');
INSERT INTO `traffic_flow_records` VALUES (5, 7, '2025-05-13 18:30:00', 'WORKDAY', '晚高峰', 1750, 20.10, 'CONGESTED', 7.50, 6.80, 87.50, 1580, 75, 65, 30, 'CLOUDY', 23.5, 8000, 'CAMERA', '2025-05-13 14:14:04');
INSERT INTO `traffic_flow_records` VALUES (6, 8, '2025-05-13 07:00:00', 'WORKDAY', '早间', 850, 45.20, 'SMOOTH', 2.50, 8.50, 35.20, 650, 20, 170, 10, 'FOGGY', 18.5, 100, 'CAMERA', '2025-05-13 14:14:04');
INSERT INTO `traffic_flow_records` VALUES (7, 8, '2025-05-13 09:00:00', 'WORKDAY', '上午', 650, 50.60, 'SMOOTH', 2.10, 7.60, 28.40, 500, 15, 130, 5, 'FOGGY', 19.8, 300, 'CAMERA', '2025-05-13 14:14:04');
INSERT INTO `traffic_flow_records` VALUES (8, 11, '2025-05-13 08:15:00', 'WORKDAY', '早高峰', 780, 32.40, 'SLOW', 5.40, 3.80, 65.30, 730, 10, 30, 10, 'SUNNY', 21.0, 10000, 'SENSOR', '2025-05-13 14:14:04');
INSERT INTO `traffic_flow_records` VALUES (9, 11, '2025-05-13 18:15:00', 'WORKDAY', '晚高峰', 850, 28.60, 'SLOW', 5.80, 4.20, 72.50, 790, 15, 35, 10, 'CLOUDY', 23.0, 8000, 'SENSOR', '2025-05-13 14:14:04');
INSERT INTO `traffic_flow_records` VALUES (10, 2, '2025-05-13 17:00:00', 'WORKDAY', '晚高峰', 1450, 35.20, 'SLOW', 4.70, 4.10, 62.30, 1350, 40, 30, 30, 'CLOUDY', 24.0, 8000, 'SENSOR', '2025-05-13 14:14:04');

-- ----------------------------
-- Table structure for traffic_statistics
-- ----------------------------
DROP TABLE IF EXISTS `traffic_statistics`;
CREATE TABLE `traffic_statistics`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `road_id` int NULL DEFAULT NULL COMMENT '道路ID',
  `road_section_id` int NULL DEFAULT NULL COMMENT '路段ID',
  `region_id` int NULL DEFAULT NULL COMMENT '区域ID',
  `statistic_type` enum('HOURLY','DAILY','WEEKLY','MONTHLY') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计类型',
  `start_time` datetime NOT NULL COMMENT '统计开始时间',
  `end_time` datetime NOT NULL COMMENT '统计结束时间',
  `day_type` enum('WORKDAY','WEEKEND','HOLIDAY','ALL') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日期类型',
  `avg_traffic_volume` int NULL DEFAULT NULL COMMENT '平均交通流量(辆/小时)',
  `max_traffic_volume` int NULL DEFAULT NULL COMMENT '最大交通流量(辆/小时)',
  `min_traffic_volume` int NULL DEFAULT NULL COMMENT '最小交通流量(辆/小时)',
  `avg_speed` decimal(5, 2) NULL DEFAULT NULL COMMENT '平均速度(公里/小时)',
  `avg_congestion_index` decimal(4, 2) NULL DEFAULT NULL COMMENT '平均拥堵指数',
  `congestion_hours` decimal(5, 2) NULL DEFAULT NULL COMMENT '拥堵累计小时数',
  `congestion_percentage` decimal(5, 2) NULL DEFAULT NULL COMMENT '拥堵时间百分比',
  `peak_hours` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '高峰时段JSON',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_stats_road`(`road_id` ASC) USING BTREE,
  INDEX `idx_stats_section`(`road_section_id` ASC) USING BTREE,
  INDEX `idx_stats_region`(`region_id` ASC) USING BTREE,
  INDEX `idx_stats_type_time`(`statistic_type` ASC, `start_time` ASC, `end_time` ASC) USING BTREE,
  INDEX `idx_stats_day_type`(`day_type` ASC) USING BTREE,
  INDEX `idx_stats_region_period`(`region_id` ASC, `start_time` ASC, `end_time` ASC) USING BTREE,
  CONSTRAINT `traffic_statistics_ibfk_1` FOREIGN KEY (`road_id`) REFERENCES `roads` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `traffic_statistics_ibfk_2` FOREIGN KEY (`road_section_id`) REFERENCES `road_sections` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `traffic_statistics_ibfk_3` FOREIGN KEY (`region_id`) REFERENCES `regions` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '交通流量统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of traffic_statistics
-- ----------------------------
INSERT INTO `traffic_statistics` VALUES (1, 2, 5, 2, 'DAILY', '2025-05-12 00:00:00', '2025-05-12 23:59:59', 'WORKDAY', 1450, 2200, 320, 28.50, 5.80, 4.50, 18.75, '[{\"start\":\"07:30\",\"end\":\"09:30\"},{\"start\":\"17:00\",\"end\":\"19:00\"}]', '2025-05-13 14:14:12');
INSERT INTO `traffic_statistics` VALUES (2, 3, 7, 1, 'DAILY', '2025-05-12 00:00:00', '2025-05-12 23:59:59', 'WORKDAY', 1350, 1800, 280, 26.80, 5.50, 3.80, 15.83, '[{\"start\":\"07:45\",\"end\":\"09:15\"},{\"start\":\"17:30\",\"end\":\"19:15\"}]', '2025-05-13 14:14:12');
INSERT INTO `traffic_statistics` VALUES (3, 4, 8, 1, 'DAILY', '2025-05-12 00:00:00', '2025-05-12 23:59:59', 'WORKDAY', 680, 950, 150, 48.20, 2.30, 0.50, 2.08, '[{\"start\":\"07:00\",\"end\":\"08:00\"}]', '2025-05-13 14:14:12');
INSERT INTO `traffic_statistics` VALUES (4, 2, 5, 2, 'WEEKLY', '2025-05-06 00:00:00', '2025-05-12 23:59:59', 'ALL', 1380, 2200, 280, 30.40, 5.20, 28.50, 16.96, '[{\"start\":\"07:30\",\"end\":\"09:30\"},{\"start\":\"17:00\",\"end\":\"19:00\"}]', '2025-05-13 14:14:12');
INSERT INTO `traffic_statistics` VALUES (5, NULL, NULL, 1, 'MONTHLY', '2025-04-01 00:00:00', '2025-04-30 23:59:59', 'ALL', 1250, 2350, 120, 35.60, 4.80, 120.50, 16.74, '[{\"start\":\"07:30\",\"end\":\"09:30\"},{\"start\":\"17:00\",\"end\":\"19:00\"}]', '2025-05-13 14:14:12');

-- ----------------------------
-- Table structure for weather_records
-- ----------------------------
DROP TABLE IF EXISTS `weather_records`;
CREATE TABLE `weather_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '天气记录ID',
  `region_id` int NULL DEFAULT NULL COMMENT '区域ID',
  `record_time` datetime NOT NULL COMMENT '记录时间',
  `weather_type` enum('SUNNY','CLOUDY','RAINY','HEAVY_RAIN','FOGGY','HEAVY_FOG','SNOWY','ICY','WINDY','THUNDERSTORM') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '天气类型',
  `temperature` decimal(4, 1) NULL DEFAULT NULL COMMENT '温度(摄氏度)',
  `humidity` decimal(5, 2) NULL DEFAULT NULL COMMENT '湿度(%)',
  `wind_speed` decimal(5, 2) NULL DEFAULT NULL COMMENT '风速(米/秒)',
  `wind_direction` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '风向',
  `precipitation` decimal(5, 2) NULL DEFAULT NULL COMMENT '降水量(毫米)',
  `visibility_meters` int NULL DEFAULT NULL COMMENT '能见度(米)',
  `pressure` decimal(6, 2) NULL DEFAULT NULL COMMENT '气压(百帕)',
  `road_condition` enum('DRY','WET','FLOODED','SNOWY','ICY','MUDDY') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '道路状况',
  `warning_level` enum('NONE','BLUE','YELLOW','ORANGE','RED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'NONE' COMMENT '预警等级',
  `warning_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '预警类型',
  `warning_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '预警描述',
  `data_source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据来源',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_weather_region_time`(`region_id` ASC, `record_time` ASC) USING BTREE,
  INDEX `idx_weather_type`(`weather_type` ASC) USING BTREE,
  INDEX `idx_weather_visibility`(`visibility_meters` ASC) USING BTREE,
  INDEX `idx_weather_warning`(`warning_level` ASC) USING BTREE,
  INDEX `idx_weather_region_type_time`(`region_id` ASC, `weather_type` ASC, `record_time` ASC) USING BTREE,
  INDEX `idx_weather_warning_active`(`warning_level` ASC, `record_time` ASC) USING BTREE,
  CONSTRAINT `weather_records_ibfk_1` FOREIGN KEY (`region_id`) REFERENCES `regions` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '天气记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of weather_records
-- ----------------------------
INSERT INTO `weather_records` VALUES (1, 1, '2025-05-13 06:00:00', 'SUNNY', 20.5, 65.00, 2.50, 'E', 0.00, 10000, 1013.25, 'DRY', 'NONE', NULL, NULL, '恩施市气象站', '2025-05-13 14:14:06');
INSERT INTO `weather_records` VALUES (2, 1, '2025-05-13 12:00:00', 'SUNNY', 26.8, 55.00, 3.20, 'SE', 0.00, 10000, 1012.80, 'DRY', 'NONE', NULL, NULL, '恩施市气象站', '2025-05-13 14:14:06');
INSERT INTO `weather_records` VALUES (3, 1, '2025-05-13 18:00:00', 'CLOUDY', 24.0, 68.00, 2.80, 'S', 0.00, 8000, 1011.50, 'DRY', 'NONE', NULL, NULL, '恩施市气象站', '2025-05-13 14:14:06');
INSERT INTO `weather_records` VALUES (4, 1, '2025-05-13 05:00:00', 'HEAVY_FOG', 18.0, 92.00, 1.20, 'N', 0.00, 100, 1014.20, 'WET', 'YELLOW', '大雾预警', '恩施北部山区能见度低于100米，请减速慢行', '恩施市气象站', '2025-05-13 14:14:06');
INSERT INTO `weather_records` VALUES (5, 1, '2025-05-13 14:00:00', 'RAINY', 22.5, 85.00, 4.50, 'SW', 15.20, 2000, 1009.80, 'WET', 'BLUE', '降雨预警', '恩施南部有中到大雨，请注意交通安全', '恩施市气象站', '2025-05-13 14:14:06');

-- ----------------------------
-- Triggers structure for table road_sections
-- ----------------------------
DROP TRIGGER IF EXISTS `road_sections_before_insert`;
delimiter ;;
CREATE TRIGGER `road_sections_before_insert` BEFORE INSERT ON `road_sections` FOR EACH ROW BEGIN
    SET NEW.start_point = POINT(NEW.start_longitude, NEW.start_latitude);
    SET NEW.end_point = POINT(NEW.end_longitude, NEW.end_latitude);
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table road_sections
-- ----------------------------
DROP TRIGGER IF EXISTS `road_sections_before_update`;
delimiter ;;
CREATE TRIGGER `road_sections_before_update` BEFORE UPDATE ON `road_sections` FOR EACH ROW BEGIN
    SET NEW.start_point = POINT(NEW.start_longitude, NEW.start_latitude);
    SET NEW.end_point = POINT(NEW.end_longitude, NEW.end_latitude);
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
