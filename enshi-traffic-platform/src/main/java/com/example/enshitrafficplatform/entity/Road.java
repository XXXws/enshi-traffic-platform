package com.example.enshitrafficplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 道路实体类
 * 表示恩施市的各类道路，包括国道、省道、县道、乡道和城市道路等
 */
@Entity
@Table(name = "roads", indexes = {
    @Index(name = "idx_road_code", columnList = "code"),
    @Index(name = "idx_road_name", columnList = "name"),
    @Index(name = "idx_road_level", columnList = "level"),
    @Index(name = "idx_road_type", columnList = "type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Road {

    /**
     * 道路ID，主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 道路名称，例如：G209、S334、恩宣大道等
     */
    @NotBlank(message = "道路名称不能为空")
    @Size(max = 100, message = "道路名称长度不能超过100个字符")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 道路编号，例如：G209、S334等
     */
    @Size(max = 50, message = "道路编号长度不能超过50个字符")
    @Column(name = "code", length = 50)
    private String code;

    /**
     * 道路类型：国道、省道、县道、乡道、城市道路等
     */
    @NotBlank(message = "道路类型不能为空")
    @Size(max = 50, message = "道路类型长度不能超过50个字符")
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    /**
     * 道路等级：高速公路、一级公路、二级公路、三级公路、四级公路、城市主干道、城市次干道等
     */
    @Size(max = 50, message = "道路等级长度不能超过50个字符")
    @Column(name = "level", length = 50)
    private String level;

    /**
     * 道路总长度，单位：公里
     */
    @Column(name = "length")
    private Double length;

    /**
     * 道路宽度，单位：米
     */
    @Column(name = "width")
    private Double width;

    /**
     * 车道数量
     */
    @Column(name = "lane_count")
    private Integer laneCount;

    /**
     * 设计限速，单位：公里/小时
     */
    @Column(name = "speed_limit")
    private Integer speedLimit;

    /**
     * 路面材质：沥青、水泥、砂石等
     */
    @Size(max = 50, message = "路面材质长度不能超过50个字符")
    @Column(name = "surface_material", length = 50)
    private String surfaceMaterial;

    /**
     * 修建或改造年份
     */
    @Column(name = "construction_year")
    private Integer constructionYear;

    /**
     * 是否为单行道
     */
    @Column(name = "is_one_way", nullable = false)
    private Boolean isOneWay = false;

    /**
     * 道路状态：正常、维修中、关闭等
     */
    @NotBlank(message = "道路状态不能为空")
    @Size(max = 50, message = "道路状态长度不能超过50个字符")
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    /**
     * 道路的平均坡度（%），恩施地区山地较多，道路坡度变化大
     */
    @Column(name = "average_slope")
    private Double averageSlope;

    /**
     * 道路的平均曲率，反映道路弯曲程度，恩施地区山路较多
     */
    @Column(name = "average_curvature")
    private Double averageCurvature;

    /**
     * 道路的起点高程（米），反映海拔高度
     */
    @Column(name = "start_elevation")
    private Double startElevation;

    /**
     * 道路的终点高程（米），反映海拔高度
     */
    @Column(name = "end_elevation")
    private Double endElevation;

    /**
     * 道路的地理坐标线，GeoJSON格式的LineString
     */
    @Column(name = "geo_line", columnDefinition = "TEXT")
    private String geoLine;

    /**
     * 道路所属行政区域
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    /**
     * 道路的路段列表
     */
    @OneToMany(mappedBy = "road", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RoadSection> roadSections = new HashSet<>();

    /**
     * 添加路段
     * @param roadSection 路段实体
     */
    public void addRoadSection(RoadSection roadSection) {
        roadSections.add(roadSection);
        roadSection.setRoad(this);
    }

    /**
     * 移除路段
     * @param roadSection 路段实体
     */
    public void removeRoadSection(RoadSection roadSection) {
        roadSections.remove(roadSection);
        roadSection.setRoad(null);
    }

    /**
     * 计算道路的高程差
     * @return 高程差（米）
     */
    public Double calculateElevationDifference() {
        if (startElevation != null && endElevation != null) {
            return Math.abs(endElevation - startElevation);
        }
        return null;
    }

    /**
     * 判断道路是否为山区道路
     * 根据恩施地区的地形特点，坡度大于5%或曲率大于0.05的道路可视为山区道路
     * @return 是否为山区道路
     */
    public boolean isMountainRoad() {
        return (averageSlope != null && averageSlope > 5.0) || 
               (averageCurvature != null && averageCurvature > 0.05);
    }
    
    /**
     * 获取道路的所有监测点
     * @return 监测点集合
     */
    public Set<MonitoringPoint> getAllMonitoringPoints() {
        Set<MonitoringPoint> allPoints = new HashSet<>();
        for (RoadSection section : roadSections) {
            allPoints.addAll(section.getMonitoringPoints());
        }
        return allPoints;
    }
    
    /**
     * 获取道路的总监测点数量
     * @return
     */
    public int getMonitoringPointCount() {
        return getAllMonitoringPoints().size();
    }
    
    /**
     * 计算道路的平均坡度风险等级
     * 根据恩施市山区公路标准进行评估
     * @return 风险等级：低、中、高、极高
     */
    public String calculateSlopeRiskLevel() {
        if (averageSlope == null) {
            return "未知";
        }
        
        if (averageSlope > 10.0) {
            return "极高";
        } else if (averageSlope > 8.0) {
            return "高";
        } else if (averageSlope > 5.0) {
            return "中";
        } else {
            return "低";
        }
    }
    
    /**
     * 计算道路的平均曲率风险等级
     * 根据恩施市山区公路标准进行评估
     * @return 风险等级：低、中、高、极高
     */
    public String calculateCurvatureRiskLevel() {
        if (averageCurvature == null) {
            return "未知";
        }
        
        if (averageCurvature > 0.15) {
            return "极高";
        } else if (averageCurvature > 0.1) {
            return "高";
        } else if (averageCurvature > 0.05) {
            return "中";
        } else {
            return "低";
        }
    }
    
    /**
     * 获取道路的综合风险指数
     * 综合考虑坡度、曲率、路面状况等因素
     * @return 风险指数（0-10）
     */
    public Double calculateRiskIndex() {
        if (averageSlope == null || averageCurvature == null) {
            return null;
        }
        
        // 基础风险分
        double riskIndex = 0.0;
        
        // 坡度风险（占40%）
        if (averageSlope > 10.0) {
            riskIndex += 4.0;
        } else if (averageSlope > 8.0) {
            riskIndex += 3.0;
        } else if (averageSlope > 5.0) {
            riskIndex += 2.0;
        } else if (averageSlope > 3.0) {
            riskIndex += 1.0;
        }
        
        // 曲率风险（占40%）
        if (averageCurvature > 0.15) {
            riskIndex += 4.0;
        } else if (averageCurvature > 0.1) {
            riskIndex += 3.0;
        } else if (averageCurvature > 0.05) {
            riskIndex += 2.0;
        } else if (averageCurvature > 0.02) {
            riskIndex += 1.0;
        }
        
        // 路面状况风险（占20%）
        if ("较差".equals(surfaceMaterial)) {
            riskIndex += 2.0;
        } else if ("一般".equals(surfaceMaterial)) {
            riskIndex += 1.0;
        }
        
        return riskIndex;
    }
    
    /**
     * 获取在雨雾天气下的建议限速
     * 根据恩施地区多雨多雾的特点，计算恶劣天气下的安全车速
     * @return 建议限速（公里/小时）
     */
    public Integer getWeatherAdjustedSpeedLimit(String weatherCondition) {
        if (speedLimit == null) {
            return null;
        }
        
        int adjustedLimit = speedLimit;
        
        // 根据天气状况调整限速
        switch (weatherCondition) {
            case "大雨":
                adjustedLimit = (int)(speedLimit * 0.6);
                break;
            case "中雨":
                adjustedLimit = (int)(speedLimit * 0.7);
                break;
            case "小雨":
                adjustedLimit = (int)(speedLimit * 0.8);
                break;
            case "大雾":
                adjustedLimit = (int)(speedLimit * 0.5);
                break;
            case "雾":
                adjustedLimit = (int)(speedLimit * 0.7);
                break;
            case "雪":
            case "冰雪":
                adjustedLimit = (int)(speedLimit * 0.5);
                break;
        }
        
        // 山区道路在恶劣天气下进一步降低限速
        if (isMountainRoad()) {
            adjustedLimit = (int)(adjustedLimit * 0.8);
        }
        
        return adjustedLimit;
    }
    
    /**
     * 获取道路的当前拥堵路段列表
     * @return 拥堵路段列表
     */
    public List<RoadSection> getCongestedSections() {
        if (roadSections == null || roadSections.isEmpty()) {
            return List.of();
        }
        
        return roadSections.stream()
                .filter(section -> {
                    Double congestionIndex = section.getCurrentCongestionIndex();
                    return congestionIndex != null && congestionIndex > 6.0;
                })
                .collect(Collectors.toList());
    }
} 