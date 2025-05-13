package com.example.enshitrafficplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 交通统计数据实体类
 * 表示根据原始交通流量数据聚合计算的统计结果
 */
@Entity
@Table(name = "traffic_statistics", indexes = {
    @Index(name = "idx_traffic_statistic_date", columnList = "statistic_date"),
    @Index(name = "idx_traffic_statistic_type", columnList = "statistic_type"),
    @Index(name = "idx_traffic_statistic_time_type", columnList = "time_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrafficStatistic {

    /**
     * 统计ID，主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 统计类型：流量统计、速度统计、拥堵统计等
     */
    @NotBlank(message = "统计类型不能为空")
    @Size(max = 50, message = "统计类型长度不能超过50个字符")
    @Column(name = "statistic_type", nullable = false, length = 50)
    private String statisticType;

    /**
     * 统计日期
     */
    @NotNull(message = "统计日期不能为空")
    @Column(name = "statistic_date", nullable = false)
    private LocalDate statisticDate;

    /**
     * 时间类型：全天、早高峰、晚高峰、平峰期等
     */
    @NotBlank(message = "时间类型不能为空")
    @Size(max = 50, message = "时间类型长度不能超过50个字符")
    @Column(name = "time_type", nullable = false, length = 50)
    private String timeType;

    /**
     * 时间范围起始时间，如果是全天则为空
     */
    @Column(name = "time_range_start")
    private LocalDateTime timeRangeStart;

    /**
     * 时间范围结束时间，如果是全天则为空
     */
    @Column(name = "time_range_end")
    private LocalDateTime timeRangeEnd;

    /**
     * 平均交通流量（辆/小时）
     */
    @Column(name = "average_flow_rate")
    private Double averageFlowRate;

    /**
     * 平均车速（公里/小时）
     */
    @Column(name = "average_speed")
    private Double averageSpeed;

    /**
     * 平均拥堵指数（0-10，10表示最拥堵）
     */
    @Column(name = "average_congestion_index")
    private Double averageCongestionIndex;

    /**
     * 大型车辆比例（%）
     */
    @Column(name = "large_vehicle_percentage")
    private Double largeVehiclePercentage;

    /**
     * 中型车辆比例（%）
     */
    @Column(name = "medium_vehicle_percentage")
    private Double mediumVehiclePercentage;

    /**
     * 小型车辆比例（%）
     */
    @Column(name = "small_vehicle_percentage")
    private Double smallVehiclePercentage;

    /**
     * 交通事件总数
     */
    @Column(name = "event_count")
    private Integer eventCount;

    /**
     * 严重交通事件总数
     */
    @Column(name = "severe_event_count")
    private Integer severeEventCount;

    /**
     * 最大交通流量（辆/小时）
     */
    @Column(name = "max_flow_rate")
    private Integer maxFlowRate;

    /**
     * 最小交通流量（辆/小时）
     */
    @Column(name = "min_flow_rate")
    private Integer minFlowRate;

    /**
     * 最大瞬时车速（公里/小时）
     */
    @Column(name = "max_speed")
    private Double maxSpeed;

    /**
     * 最小瞬时车速（公里/小时）
     */
    @Column(name = "min_speed")
    private Double minSpeed;

    /**
     * 最大拥堵指数（0-10）
     */
    @Column(name = "max_congestion_index")
    private Double maxCongestionIndex;

    /**
     * 峰值系数 = 最大流量 / 平均流量
     */
    @Column(name = "peak_factor")
    private Double peakFactor;

    /**
     * 拥堵时长（分钟）
     */
    @Column(name = "congestion_duration")
    private Integer congestionDuration;

    /**
     * 统计数据的来源描述
     */
    @Column(name = "data_source", columnDefinition = "TEXT")
    private String dataSource;

    /**
     * 统计的样本量（原始数据记录数）
     */
    @Column(name = "sample_count")
    private Integer sampleCount;

    /**
     * 统计额外信息，JSON格式
     */
    @Column(name = "extra_info", columnDefinition = "TEXT")
    private String extraInfo;

    /**
     * 所属路段
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "road_section_id")
    private RoadSection roadSection;

    /**
     * 所属监测点
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitoring_point_id")
    private MonitoringPoint monitoringPoint;

    /**
     * 获取流量变化率（相对于最小值的百分比）
     * @return 流量变化率
     */
    public Double calculateFlowRateVariation() {
        if (minFlowRate == null || minFlowRate == 0 || maxFlowRate == null) {
            return null;
        }
        return ((double) maxFlowRate - minFlowRate) / minFlowRate * 100;
    }

    /**
     * 获取速度变化率（相对于最小值的百分比）
     * @return 速度变化率
     */
    public Double calculateSpeedVariation() {
        if (minSpeed == null || minSpeed == 0 || maxSpeed == null) {
            return null;
        }
        return (maxSpeed - minSpeed) / minSpeed * 100;
    }

    /**
     * 判断是否为拥堵时段
     * 根据平均拥堵指数判断，拥堵指数大于5视为拥堵
     * @return 是否为拥堵时段
     */
    public boolean isCongested() {
        return averageCongestionIndex != null && averageCongestionIndex > 5.0;
    }

    /**
     * 计算该时段交通指数评级
     * 综合考虑流量、速度、拥堵指数等因素
     * @return 评级（A、B、C、D、E、F，A表示最畅通，F表示最拥堵）
     */
    public String calculateServiceLevel() {
        if (averageCongestionIndex == null || averageSpeed == null) {
            return "未知";
        }
        
        // 根据拥堵指数和平均车速判断服务水平
        if (averageCongestionIndex < 2 && averageSpeed > 60) {
            return "A"; // 最畅通
        } else if (averageCongestionIndex < 3 && averageSpeed > 50) {
            return "B"; // 良好
        } else if (averageCongestionIndex < 4 && averageSpeed > 40) {
            return "C"; // 一般
        } else if (averageCongestionIndex < 6 && averageSpeed > 30) {
            return "D"; // 一般拥堵
        } else if (averageCongestionIndex < 8 && averageSpeed > 20) {
            return "E"; // 严重拥堵
        } else {
            return "F"; // 极度拥堵
        }
    }

    /**
     * 获取统计时间范围描述
     * @return 时间范围描述
     */
    public String getTimeRangeDescription() {
        StringBuilder timeRange = new StringBuilder(statisticDate.toString());
        
        if (timeType.equals("全天")) {
            timeRange.append(" 全天");
        } else if (timeRangeStart != null && timeRangeEnd != null) {
            timeRange.append(" ")
                    .append(timeRangeStart.toLocalTime())
                    .append("-")
                    .append(timeRangeEnd.toLocalTime());
        } else {
            timeRange.append(" ").append(timeType);
        }
        
        return timeRange.toString();
    }

    /**
     * 计算统计时段持续时间（小时）
     * @return 持续时间
     */
    public Double calculateTimePeriodHours() {
        if (timeRangeStart == null || timeRangeEnd == null) {
            // 如果是全天，返回24小时
            if ("全天".equals(timeType)) {
                return 24.0;
            }
            return null;
        }
        
        long minutes = java.time.temporal.ChronoUnit.MINUTES.between(timeRangeStart, timeRangeEnd);
        return minutes / 60.0;
    }

    /**
     * 计算峰值系数
     * 峰值系数 = 最大流量 / 平均流量
     * 用于衡量交通流量波动程度
     * @return 峰值系数，若平均流量为0或null，则返回null
     */
    public Double calculatePeakFactor() {
        if (maxFlowRate == null || averageFlowRate == null || averageFlowRate == 0) {
            return null;
        }
        return maxFlowRate.doubleValue() / averageFlowRate;
    }

    /**
     * 更新峰值系数
     * 根据当前最大流量和平均流量计算并设置峰值系数
     */
    public void updatePeakFactor() {
        this.peakFactor = calculatePeakFactor();
    }

    /**
     * 获取统计日期的周几
     * @return 周几（1-7，1代表周一）
     */
    public int getDayOfWeek() {
        return statisticDate.getDayOfWeek().getValue();
    }

    /**
     * 判断统计日期是否为工作日
     * @return 是否为工作日
     */
    public boolean isWeekday() {
        DayOfWeek dayOfWeek = statisticDate.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    /**
     * 获取统计日期的月份
     * @return 月份（1-12）
     */
    public int getMonth() {
        return statisticDate.getMonthValue();
    }

    /**
     * 获取统计日期的季度
     * @return 季度（1-4）
     */
    public int getQuarter() {
        int month = statisticDate.getMonthValue();
        return (month - 1) / 3 + 1;
    }

    /**
     * 获取统计类型的显示名称
     * @return 统计类型显示名称
     */
    public String getStatisticTypeDisplay() {
        if (statisticType == null) {
            return "";
        }
        
        switch (statisticType) {
            case "daily": return "日统计";
            case "weekly": return "周统计";
            case "monthly": return "月统计";
            case "yearly": return "年统计";
            default: return statisticType;
        }
    }

    /**
     * 获取时间类型的显示名称
     * @return 时间类型显示名称
     */
    public String getTimeTypeDisplay() {
        if (timeType == null) {
            return "";
        }
        
        switch (timeType) {
            case "all_day": return "全天";
            case "morning_peak": return "早高峰";
            case "evening_peak": return "晚高峰";
            case "weekday": return "工作日";
            case "weekend": return "周末";
            default: return timeType;
        }
    }

    /**
     * 获取拥堵指数
     * 拥堵指数 = 1 - (平均车速 / 最大车速)，范围0-1
     * @return 拥堵指数
     */
    public Double getCongestionIndex() {
        if (averageSpeed == null || maxSpeed == null || maxSpeed == 0) {
            return null;
        }
        return 1.0 - (averageSpeed / maxSpeed);
    }

    /**
     * 获取流量指数
     * 流量指数 = 平均流量 / 最大流量，范围0-1
     * @return 流量指数
     */
    public Double getFlowIndex() {
        if (averageFlowRate == null || maxFlowRate == null || maxFlowRate == 0) {
            return null;
        }
        return averageFlowRate / maxFlowRate;
    }

    /**
     * 获取日期的字符串表示
     * @param format 日期格式，默认为"yyyy-MM-dd"
     * @return 日期字符串
     */
    public String getFormattedDate(String format) {
        if (statisticDate == null) {
            return "";
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format != null ? format : "yyyy-MM-dd");
        return statisticDate.format(formatter);
    }

    /**
     * 获取日期的字符串表示（默认格式）
     * @return 默认格式的日期字符串
     */
    public String getFormattedDate() {
        return getFormattedDate("yyyy-MM-dd");
    }

    /**
     * 获取统计摘要
     * @return 统计数据摘要
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(getStatisticTypeDisplay());
        summary.append("(").append(getFormattedDate()).append(") ");
        
        if (timeType != null) {
            summary.append(getTimeTypeDisplay()).append(" ");
        }
        
        if (averageFlowRate != null) {
            summary.append("平均流量: ").append(String.format("%.1f", averageFlowRate)).append("辆/小时 ");
        }
        
        if (averageSpeed != null) {
            summary.append("平均车速: ").append(String.format("%.1f", averageSpeed)).append("公里/小时 ");
        }
        
        if (congestionDuration != null && congestionDuration > 0) {
            summary.append("拥堵时长: ").append(congestionDuration).append("分钟 ");
        }
        
        return summary.toString().trim();
    }

    /**
     * 获取路段名称（如果有）
     * @return 路段名称或"未知路段"
     */
    public String getRoadSectionName() {
        if (roadSection != null) {
            return roadSection.getName();
        }
        return "未知路段";
    }

    /**
     * 获取监测点名称（如果有）
     * @return 监测点名称或"未知监测点"
     */
    public String getMonitoringPointName() {
        if (monitoringPoint != null) {
            return monitoringPoint.getName();
        }
        return "未知监测点";
    }

    /**
     * 获取道路名称（如果有）
     * @return 道路名称或"未知道路"
     */
    public String getRoadName() {
        if (roadSection != null && roadSection.getRoad() != null) {
            return roadSection.getRoad().getName();
        }
        return "未知道路";
    }

    /**
     * 判断统计数据是否存在拥堵情况
     * 当拥堵时长大于0或拥堵指数大于0.5时认为存在拥堵
     * @return 是否存在拥堵
     */
    public boolean hasCongestion() {
        if (congestionDuration != null && congestionDuration > 0) {
            return true;
        }
        
        Double congestionIndex = getCongestionIndex();
        return congestionIndex != null && congestionIndex > 0.5;
    }

    /**
     * 计算车流量变化率（相对于前一天/周/月数据）
     * @param previous 前一个时间段的统计数据
     * @return 变化率（百分比，正值表示增加，负值表示减少）
     */
    public Double calculateFlowRateChangeRate(TrafficStatistic previous) {
        if (previous == null || previous.getAverageFlowRate() == null || 
            this.averageFlowRate == null || previous.getAverageFlowRate() == 0) {
            return null;
        }
        
        return (this.averageFlowRate - previous.getAverageFlowRate()) / 
               previous.getAverageFlowRate() * 100;
    }

    /**
     * 计算车速变化率（相对于前一天/周/月数据）
     * @param previous 前一个时间段的统计数据
     * @return 变化率（百分比，正值表示增加，负值表示减少）
     */
    public Double calculateSpeedChangeRate(TrafficStatistic previous) {
        if (previous == null || previous.getAverageSpeed() == null || 
            this.averageSpeed == null || previous.getAverageSpeed() == 0) {
            return null;
        }
        
        return (this.averageSpeed - previous.getAverageSpeed()) / 
               previous.getAverageSpeed() * 100;
    }
} 