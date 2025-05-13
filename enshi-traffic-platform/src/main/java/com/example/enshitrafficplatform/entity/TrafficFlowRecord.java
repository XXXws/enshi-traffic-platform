package com.example.enshitrafficplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 交通流量记录实体类
 * 表示监测点采集的交通流量数据
 */
@Entity
@Table(name = "traffic_flow_records", indexes = {
    @Index(name = "idx_traffic_flow_record_time", columnList = "record_time"),
    @Index(name = "idx_traffic_flow_direction", columnList = "direction"),
    @Index(name = "idx_traffic_flow_congestion_level", columnList = "congestion_level")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrafficFlowRecord {

    /**
     * 记录ID，主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 记录时间
     */
    @NotNull(message = "记录时间不能为空")
    @Column(name = "record_time", nullable = false)
    private LocalDateTime recordTime;

    /**
     * 车流量（辆/小时）
     */
    @Column(name = "flow_rate")
    private Integer flowRate;

    /**
     * 平均车速（公里/小时）
     */
    @Column(name = "average_speed")
    private Double averageSpeed;

    /**
     * 道路占有率（%）
     */
    @Column(name = "occupancy_rate")
    private Double occupancyRate;

    /**
     * 通行方向：上行、下行、双向
     */
    @Column(name = "direction", length = 20)
    private String direction;

    /**
     * 交通拥堵级别：畅通、轻度拥堵、中度拥堵、严重拥堵
     */
    @Column(name = "congestion_level", length = 20)
    private String congestionLevel;

    /**
     * 大型车辆数量
     */
    @Column(name = "large_vehicle_count")
    private Integer largeVehicleCount;

    /**
     * 中型车辆数量
     */
    @Column(name = "medium_vehicle_count")
    private Integer mediumVehicleCount;

    /**
     * 小型车辆数量
     */
    @Column(name = "small_vehicle_count")
    private Integer smallVehicleCount;

    /**
     * 该时段内的最大车速（公里/小时）
     */
    @Column(name = "max_speed")
    private Double maxSpeed;

    /**
     * 该时段内的最小车速（公里/小时）
     */
    @Column(name = "min_speed")
    private Double minSpeed;

    /**
     * 车辆间距（米）
     */
    @Column(name = "headway")
    private Double headway;

    /**
     * 天气状况：晴、阴、雨、雪、雾等
     */
    @Column(name = "weather_condition", length = 50)
    private String weatherCondition;

    /**
     * 能见度（米）
     */
    @Column(name = "visibility")
    private Double visibility;

    /**
     * 数据质量评分（0-100）
     */
    @Column(name = "data_quality")
    private Integer dataQuality;

    /**
     * 所属监测点
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitoring_point_id", nullable = false)
    private MonitoringPoint monitoringPoint;

    /**
     * 所属路段
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "road_section_id")
    private RoadSection roadSection;

    /**
     * 计算车辆总数
     * @return 车辆总数
     */
    public Integer getTotalVehicleCount() {
        int total = 0;
        if (largeVehicleCount != null) total += largeVehicleCount;
        if (mediumVehicleCount != null) total += mediumVehicleCount;
        if (smallVehicleCount != null) total += smallVehicleCount;
        return total;
    }

    /**
     * 计算大型车辆占比
     * @return 大型车辆占比（百分比）
     */
    public Double getLargeVehiclePercentage() {
        Integer total = getTotalVehicleCount();
        if (total == 0 || largeVehicleCount == null) return 0.0;
        return (double) largeVehicleCount / total * 100;
    }

    /**
     * 计算中型车辆占比
     * @return 中型车辆占比（百分比）
     */
    public Double getMediumVehiclePercentage() {
        Integer total = getTotalVehicleCount();
        if (total == 0 || mediumVehicleCount == null) return 0.0;
        return (double) mediumVehicleCount / total * 100;
    }

    /**
     * 计算小型车辆占比
     * @return 小型车辆占比（百分比）
     */
    public Double getSmallVehiclePercentage() {
        Integer total = getTotalVehicleCount();
        if (total == 0 || smallVehicleCount == null) return 0.0;
        return (double) smallVehicleCount / total * 100;
    }

    /**
     * 计算速度标准差，反映车速的离散程度
     * @return 速度标准差
     */
    public Double calculateSpeedVariation() {
        if (maxSpeed == null || minSpeed == null || averageSpeed == null) {
            return null;
        }
        // 使用极差估算标准差（简化计算）
        return (maxSpeed - minSpeed) / 4.0;
    }

    /**
     * 根据车流量、平均车速和占有率自动评估拥堵级别
     * 使用标准交通工程评估方法
     * @return 拥堵级别
     */
    public String evaluateCongestionLevel() {
        // 根据车流量和平均车速判断拥堵程度
        if (averageSpeed == null || occupancyRate == null) {
            return "未知";
        }
        
        // 占有率阈值
        double severeThreshold = 40.0;  // 严重拥堵阈值
        double moderateThreshold = 25.0;  // 中度拥堵阈值
        double lightThreshold = 15.0;  // 轻度拥堵阈值
        
        // 在山区道路条件下，根据占有率和平均车速综合判断
        if (occupancyRate >= severeThreshold || (occupancyRate >= moderateThreshold && averageSpeed <= 20)) {
            return "严重拥堵";
        } else if (occupancyRate >= moderateThreshold || (occupancyRate >= lightThreshold && averageSpeed <= 30)) {
            return "中度拥堵";
        } else if (occupancyRate >= lightThreshold || averageSpeed <= 40) {
            return "轻度拥堵";
        } else {
            return "畅通";
        }
    }

    /**
     * 判断该时段是否为高峰期
     * 根据记录时间（工作日7:00-9:00, 17:00-19:00）判断
     * @return 是否为高峰期
     */
    public boolean isPeakHour() {
        if (recordTime == null) {
            return false;
        }
        
        int hour = recordTime.getHour();
        int dayOfWeek = recordTime.getDayOfWeek().getValue();
        
        // 工作日（周一到周五）
        boolean isWeekday = dayOfWeek >= 1 && dayOfWeek <= 5;
        
        // 早高峰7:00-9:00，晚高峰17:00-19:00
        boolean isMorningPeak = hour >= 7 && hour < 9;
        boolean isEveningPeak = hour >= 17 && hour < 19;
        
        return isWeekday && (isMorningPeak || isEveningPeak);
    }

    /**
     * 获取路段名称（从监测点获取）
     * @return 路段名称
     */
    public String getRoadSectionName() {
        if (monitoringPoint != null && monitoringPoint.getRoadSection() != null) {
            return monitoringPoint.getRoadSection().getName();
        }
        return "未知路段";
    }

    /**
     * 获取道路名称（从监测点获取）
     * @return 道路名称
     */
    public String getRoadName() {
        if (monitoringPoint != null && 
            monitoringPoint.getRoadSection() != null && 
            monitoringPoint.getRoadSection().getRoad() != null) {
            return monitoringPoint.getRoadSection().getRoad().getName();
        }
        return "未知道路";
    }

    /**
     * 更新拥堵级别
     * 根据当前数据自动计算并更新拥堵级别
     */
    public void updateCongestionLevel() {
        this.congestionLevel = evaluateCongestionLevel();
    }

    /**
     * 获取当前记录的年龄（分钟）
     * @return 记录年龄（分钟）
     */
    public long getAgeInMinutes() {
        return java.time.temporal.ChronoUnit.MINUTES.between(recordTime, LocalDateTime.now());
    }
} 