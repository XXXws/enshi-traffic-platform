package com.example.enshitrafficplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 道路路段实体类
 * 表示道路的具体路段，一条道路可以分为多个路段
 */
@Entity
@Table(name = "road_sections", indexes = {
    @Index(name = "idx_road_section_name", columnList = "name"),
    @Index(name = "idx_road_section_type", columnList = "type"),
    @Index(name = "idx_road_section_road", columnList = "road_id"),
    @Index(name = "idx_road_section_risk", columnList = "risk_level")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoadSection {

    /**
     * 路段ID，主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 路段名称，例如：G209某某段、城区段等
     */
    @NotBlank(message = "路段名称不能为空")
    @Size(max = 100, message = "路段名称长度不能超过100个字符")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 路段长度，单位：公里
     */
    @NotNull(message = "路段长度不能为空")
    @Positive(message = "路段长度必须为正数")
    @Column(name = "length", nullable = false)
    private Double length;

    /**
     * 起点名称，例如：某某路口、某某桥等
     */
    @Size(max = 100, message = "起点名称长度不能超过100个字符")
    @Column(name = "start_point_name", length = 100)
    private String startPointName;

    /**
     * 终点名称，例如：某某路口、某某桥等
     */
    @Size(max = 100, message = "终点名称长度不能超过100个字符")
    @Column(name = "end_point_name", length = 100)
    private String endPointName;

    /**
     * 起点经度
     */
    @Column(name = "start_longitude")
    private Double startLongitude;

    /**
     * 起点纬度
     */
    @Column(name = "start_latitude")
    private Double startLatitude;

    /**
     * 终点经度
     */
    @Column(name = "end_longitude")
    private Double endLongitude;

    /**
     * 终点纬度
     */
    @Column(name = "end_latitude")
    private Double endLatitude;

    /**
     * 路段的地理坐标线，GeoJSON格式的LineString
     */
    @Column(name = "geo_line", columnDefinition = "TEXT")
    private String geoLine;

    /**
     * 路段类型：桥梁段、隧道段、普通路段等
     */
    @Size(max = 50, message = "路段类型长度不能超过50个字符")
    @Column(name = "type", length = 50)
    private String type;

    /**
     * 路面状况：良好、一般、较差等
     */
    @Size(max = 50, message = "路面状况长度不能超过50个字符")
    @Column(name = "surface_condition", length = 50)
    private String surfaceCondition;

    /**
     * 路段的平均坡度（%）
     */
    @Column(name = "average_slope")
    private Double averageSlope;

    /**
     * 路段的平均曲率
     */
    @Column(name = "average_curvature")
    private Double averageCurvature;

    /**
     * 路段的最大坡度（%）
     */
    @Column(name = "max_slope")
    private Double maxSlope;

    /**
     * 路段的起点高程（米）
     */
    @Column(name = "start_elevation")
    private Double startElevation;

    /**
     * 路段的终点高程（米）
     */
    @Column(name = "end_elevation")
    private Double endElevation;

    /**
     * 路段的设计通行能力（辆/小时）
     */
    @Column(name = "design_capacity")
    private Integer designCapacity;

    /**
     * 路段风险等级：低风险、中风险、高风险
     * 根据恩施地区的特点，考虑坡度、曲率、地质条件等因素评估
     */
    @Size(max = 50, message = "风险等级长度不能超过50个字符")
    @Column(name = "risk_level", length = 50)
    private String riskLevel;

    /**
     * 地质风险描述，例如：滑坡风险、泥石流风险等
     */
    @Column(name = "geological_risk", columnDefinition = "TEXT")
    private String geologicalRisk;

    /**
     * 所属道路
     */
    @NotNull(message = "所属道路不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "road_id", nullable = false)
    private Road road;

    /**
     * 该路段上的监测点
     */
    @OneToMany(mappedBy = "roadSection", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MonitoringPoint> monitoringPoints = new HashSet<>();

    /**
     * 该路段的交通事件记录
     */
    @OneToMany(mappedBy = "roadSection", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TrafficEvent> trafficEvents = new HashSet<>();

    /**
     * 该路段的交通统计数据
     */
    @OneToMany(mappedBy = "roadSection", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TrafficStatistic> trafficStatistics = new HashSet<>();
    
    /**
     * 该路段的交通流量记录
     */
    @OneToMany(mappedBy = "roadSection", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TrafficFlowRecord> trafficFlowRecords = new HashSet<>();

    /**
     * 路段的高峰期规则
     */
    @OneToMany(mappedBy = "roadSection", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PeakPeriodRule> peakPeriodRules = new HashSet<>();

    /**
     * 添加监测点
     * @param monitoringPoint 监测点实体
     */
    public void addMonitoringPoint(MonitoringPoint monitoringPoint) {
        monitoringPoints.add(monitoringPoint);
        monitoringPoint.setRoadSection(this);
    }

    /**
     * 移除监测点
     * @param monitoringPoint 监测点实体
     */
    public void removeMonitoringPoint(MonitoringPoint monitoringPoint) {
        monitoringPoints.remove(monitoringPoint);
        monitoringPoint.setRoadSection(null);
    }

    /**
     * 添加交通事件
     * @param trafficEvent 交通事件实体
     */
    public void addTrafficEvent(TrafficEvent trafficEvent) {
        trafficEvents.add(trafficEvent);
        trafficEvent.setRoadSection(this);
    }

    /**
     * 移除交通事件
     * @param trafficEvent 交通事件实体
     */
    public void removeTrafficEvent(TrafficEvent trafficEvent) {
        trafficEvents.remove(trafficEvent);
        trafficEvent.setRoadSection(null);
    }

    /**
     * 添加交通统计数据
     * @param trafficStatistic 交通统计数据实体
     */
    public void addTrafficStatistic(TrafficStatistic trafficStatistic) {
        trafficStatistics.add(trafficStatistic);
        trafficStatistic.setRoadSection(this);
    }

    /**
     * 移除交通统计数据
     * @param trafficStatistic 交通统计数据实体
     */
    public void removeTrafficStatistic(TrafficStatistic trafficStatistic) {
        trafficStatistics.remove(trafficStatistic);
        trafficStatistic.setRoadSection(null);
    }

    /**
     * 添加高峰期规则
     * @param peakPeriodRule 高峰期规则实体
     */
    public void addPeakPeriodRule(PeakPeriodRule peakPeriodRule) {
        peakPeriodRules.add(peakPeriodRule);
        peakPeriodRule.setRoadSection(this);
    }

    /**
     * 移除高峰期规则
     * @param peakPeriodRule 高峰期规则实体
     */
    public void removePeakPeriodRule(PeakPeriodRule peakPeriodRule) {
        peakPeriodRules.remove(peakPeriodRule);
        peakPeriodRule.setRoadSection(null);
    }

    /**
     * 计算路段的高程差
     * @return 高程差（米）
     */
    public Double calculateElevationDifference() {
        if (startElevation != null && endElevation != null) {
            return Math.abs(endElevation - startElevation);
        }
        return null;
    }

    /**
     * 判断路段是否为陡坡路段
     * 根据恩施地区山区道路标准，平均坡度大于8%的路段视为陡坡路段
     * @return 是否为陡坡路段
     */
    public boolean isSteepSlope() {
        return averageSlope != null && averageSlope > 8.0;
    }

    /**
     * 判断路段是否为急弯路段
     * 根据恩施地区山区道路标准，平均曲率大于0.1的路段视为急弯路段
     * @return 是否为急弯路段
     */
    public boolean isSharpCurve() {
        return averageCurvature != null && averageCurvature > 0.1;
    }

    /**
     * 评估路段的通行能力
     * 根据设计通行能力、坡度、曲率等因素综合评估
     * @return 评估后的实际通行能力（辆/小时）
     */
    public Integer evaluateActualCapacity() {
        if (designCapacity == null) {
            return null;
        }

        double capacityFactor = 1.0;
        
        // 考虑坡度因素
        if (averageSlope != null) {
            if (averageSlope > 10.0) {
                capacityFactor *= 0.6;  // 陡坡严重影响通行能力
            } else if (averageSlope > 5.0) {
                capacityFactor *= 0.8;  // 中等坡度影响通行能力
            }
        }
        
        // 考虑曲率因素
        if (averageCurvature != null) {
            if (averageCurvature > 0.15) {
                capacityFactor *= 0.7;  // 急弯严重影响通行能力
            } else if (averageCurvature > 0.05) {
                capacityFactor *= 0.9;  // 一般弯道轻微影响通行能力
            }
        }
        
        // 考虑路面状况
        if (surfaceCondition != null) {
            if (surfaceCondition.equals("较差")) {
                capacityFactor *= 0.8;
            }
        }
        
        return (int)(designCapacity * capacityFactor);
    }

    /**
     * 获取当前路段的实时交通流量
     * 从监测点的最新记录中获取
     * @return 当前交通流量（辆/小时）或null如果没有数据
     */
    public Integer getCurrentTrafficFlow() {
        // 查找最新的交通流量记录
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);
        
        // 收集所有监测点在过去一小时内的最新记录
        Set<TrafficFlowRecord> recentRecords = new HashSet<>();
        for (MonitoringPoint point : monitoringPoints) {
            point.getTrafficFlowRecords().stream()
                .filter(record -> record.getRecordTime().isAfter(oneHourAgo) && record.getRecordTime().isBefore(now))
                .max((r1, r2) -> r1.getRecordTime().compareTo(r2.getRecordTime()))
                .ifPresent(recentRecords::add);
        }
        
        if (recentRecords.isEmpty()) {
            return null;
        }
        
        // 计算平均流量
        return (int) recentRecords.stream()
            .mapToInt(record -> record.getFlowRate() != null ? record.getFlowRate() : 0)
            .average()
            .orElse(0);
    }

    /**
     * 获取当前路段的实时平均速度
     * @return 当前平均速度（公里/小时）或null如果没有数据
     */
    public Double getCurrentAverageSpeed() {
        // 查找最新的交通流量记录
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);
        
        // 收集所有监测点在过去一小时内的最新记录
        Set<TrafficFlowRecord> recentRecords = new HashSet<>();
        for (MonitoringPoint point : monitoringPoints) {
            point.getTrafficFlowRecords().stream()
                .filter(record -> record.getRecordTime().isAfter(oneHourAgo) && record.getRecordTime().isBefore(now))
                .max((r1, r2) -> r1.getRecordTime().compareTo(r2.getRecordTime()))
                .ifPresent(recentRecords::add);
        }
        
        if (recentRecords.isEmpty()) {
            return null;
        }
        
        // 计算平均速度
        return recentRecords.stream()
            .filter(record -> record.getAverageSpeed() != null)
            .mapToDouble(TrafficFlowRecord::getAverageSpeed)
            .average()
            .orElse(0);
    }

    /**
     * 获取路段的完整名称，包含所属道路名称
     * @return 完整名称
     */
    public String getFullName() {
        if (road != null) {
            return road.getName() + " - " + name;
        }
        return name;
    }

    /**
     * 获取当前路段的活跃交通事件
     * @return 活跃交通事件列表
     */
    public Set<TrafficEvent> getActiveTrafficEvents() {
        return trafficEvents.stream()
            .filter(event -> !event.isEnded())
            .collect(Collectors.toSet());
    }

    /**
     * 获取当前路段的活跃交通事件数量
     * @return 活跃交通事件数量
     */
    public int getActiveTrafficEventCount() {
        return (int) trafficEvents.stream()
            .filter(event -> !event.isEnded())
            .count();
    }

    /**
     * 获取路段中心点经度
     * 如果起点和终点经度都存在，则取平均值；否则返回null
     * @return 中心点经度
     */
    public Double getLongitude() {
        if (startLongitude != null && endLongitude != null) {
            return (startLongitude + endLongitude) / 2;
        }
        return null;
    }

    /**
     * 获取路段中心点纬度
     * 如果起点和终点纬度都存在，则取平均值；否则返回null
     * @return 中心点纬度
     */
    public Double getLatitude() {
        if (startLatitude != null && endLatitude != null) {
            return (startLatitude + endLatitude) / 2;
        }
        return null;
    }

    /**
     * 计算当前路段的拥堵指数
     * 0-10：0表示畅通，10表示严重拥堵
     * @return 拥堵指数
     */
    public Double getCurrentCongestionIndex() {
        Integer currentFlow = getCurrentTrafficFlow();
        Integer capacity = evaluateActualCapacity();
        
        if (currentFlow == null || capacity == null || capacity == 0) {
            return null;
        }
        
        // 计算饱和度
        double saturation = (double) currentFlow / capacity;
        
        // 基于饱和度计算拥堵指数
        if (saturation < 0.5) {
            // 畅通
            return saturation * 2; // 0-1范围
        } else if (saturation < 0.8) {
            // 轻度拥堵
            return 1.0 + (saturation - 0.5) * (4.0 / 0.3); // 1-5范围
        } else if (saturation < 1.0) {
            // 中度拥堵
            return 5.0 + (saturation - 0.8) * (2.0 / 0.2); // 5-7范围
        } else {
            // 重度拥堵
            return Math.min(7.0 + (saturation - 1.0) * 3.0, 10.0); // 7-10范围，最大10
        }
    }
    
    /**
     * 获取路段拥堵状态描述
     * @return 拥堵状态描述：畅通、轻微拥堵、中度拥堵、严重拥堵
     */
    public String getCongestionStatus() {
        Double congestionIndex = getCurrentCongestionIndex();
        
        if (congestionIndex == null) {
            return "数据不足";
        }
        
        if (congestionIndex < 2.0) {
            return "畅通";
        } else if (congestionIndex < 5.0) {
            return "轻微拥堵";
        } else if (congestionIndex < 7.0) {
            return "中度拥堵";
        } else {
            return "严重拥堵";
        }
    }
    
    /**
     * 计算基于天气条件的推荐限速
     * 根据恩施地区多雨多雾的特点
     * @param weatherCondition 天气状况：晴、阴、小雨、中雨、大雨、雾等
     * @param visibility 能见度，单位：米
     * @return 推荐限速（公里/小时）
     */
    public Integer calculateWeatherAdjustedSpeedLimit(String weatherCondition, Integer visibility) {
        // 获取道路的基本限速
        Integer baseSpeedLimit = road.getSpeedLimit();
        
        if (baseSpeedLimit == null) {
            return null;
        }
        
        int adjustedLimit = baseSpeedLimit;
        
        // 根据天气状况调整限速
        switch (weatherCondition) {
            case "大雨":
                adjustedLimit = (int)(baseSpeedLimit * 0.6);
                break;
            case "中雨":
                adjustedLimit = (int)(baseSpeedLimit * 0.7);
                break;
            case "小雨":
                adjustedLimit = (int)(baseSpeedLimit * 0.8);
                break;
            case "大雾":
                adjustedLimit = (int)(baseSpeedLimit * 0.5);
                break;
            case "雾":
                adjustedLimit = (int)(baseSpeedLimit * 0.7);
                break;
            case "雪":
            case "冰雪":
                adjustedLimit = (int)(baseSpeedLimit * 0.5);
                break;
        }
        
        // 根据能见度进一步调整限速
        if (visibility != null) {
            if (visibility < 50) {
                adjustedLimit = 20; // 能见度极低时，限速20km/h
            } else if (visibility < 100) {
                adjustedLimit = Math.min(adjustedLimit, 30);
            } else if (visibility < 200) {
                adjustedLimit = Math.min(adjustedLimit, 40);
            } else if (visibility < 500) {
                adjustedLimit = Math.min(adjustedLimit, 60);
            }
        }
        
        // 根据路段特性进一步调整
        // 陡坡路段
        if (isSteepSlope()) {
            adjustedLimit = (int)(adjustedLimit * 0.8);
        }
        
        // 急弯路段
        if (isSharpCurve()) {
            adjustedLimit = (int)(adjustedLimit * 0.7);
        }
        
        return adjustedLimit;
    }
    
    /**
     * 判断当前是否为该路段的高峰期
     * @return 是否为高峰期
     */
    public boolean isCurrentlyPeakPeriod() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        int dayOfWeek = now.getDayOfWeek().getValue(); // 1-7, Monday-Sunday
        
        // 检查所有高峰期规则
        for (PeakPeriodRule rule : peakPeriodRules) {
            if (rule.appliesTo(dayOfWeek, hour, minute)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 计算路段的综合风险指数，综合考虑地质条件、坡度、曲率等因素
     * @return 风险指数（0-10）
     */
    public Double calculateRiskIndex() {
        double riskIndex = 0.0;
        
        // 坡度风险（最大40%）
        if (maxSlope != null) {
            if (maxSlope > 15.0) {
                riskIndex += 4.0;
            } else if (maxSlope > 10.0) {
                riskIndex += 3.0;
            } else if (maxSlope > 8.0) {
                riskIndex += 2.0;
            } else if (maxSlope > 5.0) {
                riskIndex += 1.0;
            }
        } else if (averageSlope != null) {
            if (averageSlope > 10.0) {
                riskIndex += 3.0;
            } else if (averageSlope > 8.0) {
                riskIndex += 2.0;
            } else if (averageSlope > 5.0) {
                riskIndex += 1.0;
            }
        }
        
        // 曲率风险（最大30%）
        if (averageCurvature != null) {
            if (averageCurvature > 0.15) {
                riskIndex += 3.0;
            } else if (averageCurvature > 0.1) {
                riskIndex += 2.0;
            } else if (averageCurvature > 0.05) {
                riskIndex += 1.0;
            }
        }
        
        // 地质风险（最大30%）
        if (geologicalRisk != null && !geologicalRisk.isEmpty()) {
            if (geologicalRisk.contains("滑坡") || geologicalRisk.contains("泥石流") || 
                geologicalRisk.contains("高风险")) {
                riskIndex += 3.0;
            } else if (geologicalRisk.contains("中风险")) {
                riskIndex += 2.0;
            } else if (geologicalRisk.contains("低风险")) {
                riskIndex += 1.0;
            }
        }
        
        return riskIndex;
    }
    
    /**
     * 根据风险指数更新路段风险等级
     */
    public void updateRiskLevel() {
        Double riskIndex = calculateRiskIndex();
        
        if (riskIndex == null) {
            this.riskLevel = "未知";
            return;
        }
        
        if (riskIndex >= 8.0) {
            this.riskLevel = "极高风险";
        } else if (riskIndex >= 6.0) {
            this.riskLevel = "高风险";
        } else if (riskIndex >= 4.0) {
            this.riskLevel = "中风险";
        } else if (riskIndex >= 2.0) {
            this.riskLevel = "低风险";
        } else {
            this.riskLevel = "极低风险";
        }
    }
    
    /**
     * 获取此路段过去24小时内的所有交通事件
     * @return 交通事件列表
     */
    public List<TrafficEvent> getRecentTrafficEvents() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        
        return trafficEvents.stream()
            .filter(event -> event.getStartTime().isAfter(yesterday))
            .sorted((e1, e2) -> e2.getStartTime().compareTo(e1.getStartTime())) // 按时间降序
            .collect(Collectors.toList());
    }
    
    /**
     * 检查路段是否存在安全隐患
     * @return 安全隐患描述，如果没有则返回null
     */
    public String getSafetyHazardDescription() {
        StringBuilder hazards = new StringBuilder();
        
        // 检查坡度
        if (maxSlope != null && maxSlope > 10.0) {
            hazards.append("陡坡路段，坡度").append(maxSlope).append("%；");
        }
        
        // 检查曲率
        if (averageCurvature != null && averageCurvature > 0.1) {
            hazards.append("急弯路段，平均曲率").append(averageCurvature).append("；");
        }
        
        // 检查地质风险
        if (geologicalRisk != null && !geologicalRisk.isEmpty()) {
            hazards.append("地质风险：").append(geologicalRisk).append("；");
        }
        
        // 检查路面状况
        if ("较差".equals(surfaceCondition)) {
            hazards.append("路面状况较差；");
        }
        
        // 检查活跃交通事件
        int activeEvents = getActiveTrafficEventCount();
        if (activeEvents > 0) {
            hazards.append("当前存在").append(activeEvents).append("起交通事件；");
        }
        
        return hazards.length() > 0 ? hazards.toString() : null;
    }
} 