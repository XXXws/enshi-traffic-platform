package com.example.enshitrafficplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 交通监测点实体类
 * 表示固定在道路上的交通监测设备位置
 */
@Entity
@Table(name = "monitoring_points", indexes = {
    @Index(name = "idx_monitoring_point_location", columnList = "longitude,latitude"),
    @Index(name = "idx_monitoring_point_type", columnList = "type"),
    @Index(name = "idx_monitoring_point_status", columnList = "status"),
    @Index(name = "idx_monitoring_point_code", columnList = "code")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringPoint {

    /**
     * 监测点ID，主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 监测点编号，唯一标识
     */
    @NotBlank(message = "监测点编号不能为空")
    @Pattern(regexp = "^[A-Z]{2}\\d{4}$", message = "监测点编号格式必须为2位大写字母+4位数字")
    @Size(max = 50, message = "监测点编号长度不能超过50个字符")
    @Column(name = "code", nullable = false, length = 50, unique = true)
    private String code;

    /**
     * 监测点名称，例如：G209某某路段监测点1号
     */
    @NotBlank(message = "监测点名称不能为空")
    @Size(max = 100, message = "监测点名称长度不能超过100个字符")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 监测点类型：摄像头、雷达、线圈、红外等
     */
    @NotBlank(message = "监测点类型不能为空")
    @Size(max = 50, message = "监测点类型长度不能超过50个字符")
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    /**
     * 监测点经度
     */
    @NotNull(message = "监测点经度不能为空")
    @DecimalMin(value = "108.0", message = "经度值必须大于或等于108.0")
    @DecimalMax(value = "110.0", message = "经度值必须小于或等于110.0")
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    /**
     * 监测点纬度
     */
    @NotNull(message = "监测点纬度不能为空")
    @DecimalMin(value = "29.0", message = "纬度值必须大于或等于29.0")
    @DecimalMax(value = "31.0", message = "纬度值必须小于或等于31.0")
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    /**
     * 监测点高程（米）
     */
    @Column(name = "elevation")
    private Double elevation;

    /**
     * 监测方向：上行、下行、双向
     */
    @Size(max = 20, message = "监测方向长度不能超过20个字符")
    @Column(name = "direction", length = 20)
    private String direction;

    /**
     * 安装日期
     */
    @PastOrPresent(message = "安装日期不能是将来的日期")
    @Column(name = "installation_date")
    private LocalDateTime installationDate;

    /**
     * 最近维护日期
     */
    @PastOrPresent(message = "最近维护日期不能是将来的日期")
    @Column(name = "last_maintenance_date")
    private LocalDateTime lastMaintenanceDate;

    /**
     * 监测点状态：正常、故障、维修中等
     */
    @NotBlank(message = "监测点状态不能为空")
    @Size(max = 50, message = "监测点状态长度不能超过50个字符")
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    /**
     * 监测点的详细描述信息
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 设备型号
     */
    @Size(max = 100, message = "设备型号长度不能超过100个字符")
    @Column(name = "device_model", length = 100)
    private String deviceModel;

    /**
     * 设备制造商
     */
    @Size(max = 100, message = "设备制造商长度不能超过100个字符")
    @Column(name = "manufacturer", length = 100)
    private String manufacturer;

    /**
     * 数据采集频率（秒）
     */
    @Positive(message = "数据采集频率必须为正数")
    @Max(value = 3600, message = "数据采集频率不能超过3600秒")
    @Column(name = "data_collection_frequency")
    private Integer dataCollectionFrequency;

    /**
     * IP地址
     */
    @Size(max = 50, message = "IP地址长度不能超过50个字符")
    @Pattern(regexp = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$", message = "IP地址格式不正确")
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * 摄像机角度(度)
     */
    @Min(value = 0, message = "摄像机角度必须大于或等于0度")
    @Max(value = 360, message = "摄像机角度必须小于或等于360度")
    @Column(name = "camera_angle")
    private Integer cameraAngle;

    /**
     * 是否支持夜视
     */
    @Column(name = "night_vision_supported")
    private Boolean nightVisionSupported;

    /**
     * 电源类型（市电/太阳能）
     */
    @Size(max = 50, message = "电源类型长度不能超过50个字符")
    @Column(name = "power_source", length = 50)
    private String powerSource;

    /**
     * 监测点所属路段
     */
    @NotNull(message = "监测点所属路段不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "road_section_id", nullable = false)
    private RoadSection roadSection;

    /**
     * 该监测点记录的交通流量数据
     */
    @OneToMany(mappedBy = "monitoringPoint", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TrafficFlowRecord> trafficFlowRecords = new HashSet<>();

    /**
     * 该监测点的交通统计数据
     */
    @OneToMany(mappedBy = "monitoringPoint", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TrafficStatistic> trafficStatistics = new HashSet<>();

    /**
     * 添加交通流量记录
     * @param trafficFlowRecord 交通流量记录
     */
    public void addTrafficFlowRecord(TrafficFlowRecord trafficFlowRecord) {
        trafficFlowRecords.add(trafficFlowRecord);
        trafficFlowRecord.setMonitoringPoint(this);
    }

    /**
     * 移除交通流量记录
     * @param trafficFlowRecord 交通流量记录
     */
    public void removeTrafficFlowRecord(TrafficFlowRecord trafficFlowRecord) {
        trafficFlowRecords.remove(trafficFlowRecord);
        trafficFlowRecord.setMonitoringPoint(null);
    }

    /**
     * 添加交通统计数据
     * @param trafficStatistic 交通统计数据
     */
    public void addTrafficStatistic(TrafficStatistic trafficStatistic) {
        trafficStatistics.add(trafficStatistic);
        trafficStatistic.setMonitoringPoint(this);
    }

    /**
     * 移除交通统计数据
     * @param trafficStatistic 交通统计数据
     */
    public void removeTrafficStatistic(TrafficStatistic trafficStatistic) {
        trafficStatistics.remove(trafficStatistic);
        trafficStatistic.setMonitoringPoint(null);
    }

    /**
     * 将当前监测点状态更新为故障
     * @param reason 故障原因
     * @return 更新后的状态
     */
    public String markAsMalfunction(String reason) {
        this.status = "故障";
        this.description = "故障原因: " + reason + ". 上次状态更新时间: " + LocalDateTime.now();
        return this.status;
    }

    /**
     * 将当前监测点状态更新为正常
     * @param maintenanceInfo 维护信息
     * @return 更新后的状态
     */
    public String markAsNormal(String maintenanceInfo) {
        this.status = "正常";
        this.lastMaintenanceDate = LocalDateTime.now();
        this.description = maintenanceInfo != null ? maintenanceInfo : "设备正常工作中";
        return this.status;
    }

    /**
     * 将当前监测点状态更新为维修中
     * @param maintenanceInfo 维护信息
     * @return 更新后的状态
     */
    public String markAsMaintenance(String maintenanceInfo) {
        this.status = "维修中";
        this.description = maintenanceInfo;
        return this.status;
    }

    /**
     * 计算距今安装时间（天）
     * @return 安装至今的天数
     */
    public Long getDaysSinceInstallation() {
        if (installationDate == null) {
            return null;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(installationDate, LocalDateTime.now());
    }

    /**
     * 计算上次维护到现在的天数
     * @return 上次维护至今的天数
     */
    public Long getDaysSinceLastMaintenance() {
        if (lastMaintenanceDate == null) {
            return null;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(lastMaintenanceDate, LocalDateTime.now());
    }

    /**
     * 判断是否需要维护
     * 根据设备类型和上次维护时间判断，默认180天检查一次
     * @return 是否需要维护
     */
    public boolean needsMaintenance() {
        if (lastMaintenanceDate == null) {
            return installationDate != null && 
                   java.time.temporal.ChronoUnit.DAYS.between(installationDate, LocalDateTime.now()) > 180;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(lastMaintenanceDate, LocalDateTime.now()) > 180;
    }

    /**
     * 获取监测点的位置描述
     * @return 位置描述字符串
     */
    public String getLocationDescription() {
        StringBuilder location = new StringBuilder();
        if (roadSection != null && roadSection.getRoad() != null) {
            location.append(roadSection.getRoad().getName())
                    .append(" - ")
                    .append(roadSection.getName());
        }
        location.append(" (")
                .append(latitude)
                .append(", ")
                .append(longitude)
                .append(")");
        return location.toString();
    }

    /**
     * 获取最新交通流量记录
     * @return 最新交通流量记录，如果没有则返回空Optional
     */
    public Optional<TrafficFlowRecord> getLatestTrafficFlowRecord() {
        return trafficFlowRecords.stream()
                .max(Comparator.comparing(TrafficFlowRecord::getRecordTime));
    }

    /**
     * 获取最新交通流量（辆/小时）
     * @return 最新交通流量，如果没有则返回null
     */
    public Integer getLatestTrafficFlow() {
        return getLatestTrafficFlowRecord()
                .map(TrafficFlowRecord::getFlowRate)
                .orElse(null);
    }

    /**
     * 获取最新平均车速（公里/小时）
     * @return 最新平均车速，如果没有则返回null
     */
    public Double getLatestAverageSpeed() {
        return getLatestTrafficFlowRecord()
                .map(TrafficFlowRecord::getAverageSpeed)
                .orElse(null);
    }

    /**
     * 获取最新拥堵等级
     * @return 最新拥堵等级，如果没有则返回"未知"
     */
    public String getLatestCongestionLevel() {
        return getLatestTrafficFlowRecord()
                .map(TrafficFlowRecord::getCongestionLevel)
                .orElse("未知");
    }

    /**
     * 获取指定时间范围内的交通流量记录
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 时间范围内的交通流量记录集合
     */
    public Set<TrafficFlowRecord> getTrafficFlowRecordsInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return trafficFlowRecords.stream()
                .filter(record -> record.getRecordTime().isAfter(startTime) && 
                                 record.getRecordTime().isBefore(endTime))
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * 计算设备健康指数（0-100）
     * 基于年限、维护情况和状态
     * @return 健康指数
     */
    public Integer calculateHealthIndex() {
        int healthIndex = 100;
        
        // 检查安装年限（超过5年扣分）
        if (installationDate != null) {
            long years = installationDate.until(LocalDateTime.now(), ChronoUnit.YEARS);
            if (years > 5) {
                healthIndex -= (years - 5) * 5;
            }
        }
        
        // 检查最后维护时间（超过6个月扣分）
        if (lastMaintenanceDate != null) {
            long months = lastMaintenanceDate.until(LocalDateTime.now(), ChronoUnit.MONTHS);
            if (months > 6) {
                healthIndex -= (months - 6) * 2;
            }
        } else if (installationDate != null) {
            // 如果从未维护过但已安装一年以上，严重扣分
            long months = installationDate.until(LocalDateTime.now(), ChronoUnit.MONTHS);
            if (months > 12) {
                healthIndex -= 30;
            }
        }
        
        // 根据设备状态调整
        if ("故障".equals(status)) {
            healthIndex -= 50;
        } else if ("维修中".equals(status)) {
            healthIndex -= 30;
        } else if ("性能下降".equals(status)) {
            healthIndex -= 20;
        }
        
        // 确保健康指数在0-100范围内
        return Math.max(0, Math.min(100, healthIndex));
    }
    
    /**
     * 获取设备健康状态描述
     * @return 健康状态描述
     */
    public String getHealthStatus() {
        Integer healthIndex = calculateHealthIndex();
        
        if (healthIndex >= 90) {
            return "优良";
        } else if (healthIndex >= 75) {
            return "良好";
        } else if (healthIndex >= 60) {
            return "一般";
        } else if (healthIndex >= 40) {
            return "较差";
        } else {
            return "极差";
        }
    }
    
    /**
     * 判断设备是否适合在恶劣天气下工作
     * 恩施多雨雾，需要考虑设备的防水防雾性能
     * @return 是否适合恶劣天气环境
     */
    public boolean isSuitableForHarshWeather() {
        // 检查设备型号是否含有防水防雾相关关键词
        if (deviceModel != null && 
            (deviceModel.contains("防水") || 
             deviceModel.contains("防雾") || 
             deviceModel.contains("IP67") || 
             deviceModel.contains("全天候"))) {
            return true;
        }
        
        // 根据描述判断
        if (description != null && 
            (description.contains("防水") || 
             description.contains("防雾") || 
             description.contains("适合恶劣环境"))) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 获取设备覆盖和监测范围（米）
     * @return 监测范围
     */
    public Integer getMonitoringRange() {
        if ("摄像头".equals(type)) {
            return 100;  // 视频监控范围约100米
        } else if ("雷达".equals(type)) {
            return 300;  // 雷达监测范围约300米
        } else if ("线圈".equals(type)) {
            return 10;   // 线圈监测范围局限于安装位置
        } else if ("红外".equals(type)) {
            return 50;   // 红外监测范围约50米
        } else {
            return 50;   // 默认监测范围
        }
    }
    
    /**
     * 计算当前监测点的平均日车流量（基于过去30天数据）
     * @return 平均日车流量
     */
    public Integer calculateAverageDailyTrafficFlow() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        // 按天分组数据
        Map<LocalDate, List<Integer>> dailyFlows = trafficFlowRecords.stream()
            .filter(record -> record.getRecordTime().isAfter(thirtyDaysAgo) && record.getFlowRate() != null)
            .collect(Collectors.groupingBy(
                record -> record.getRecordTime().toLocalDate(),
                Collectors.mapping(TrafficFlowRecord::getFlowRate, Collectors.toList())
            ));
        
        if (dailyFlows.isEmpty()) {
            return null;
        }
        
        // 计算每天的平均流量
        List<Integer> averageDailyFlows = new ArrayList<>();
        for (List<Integer> flows : dailyFlows.values()) {
            if (!flows.isEmpty()) {
                int dailyAvg = (int) flows.stream().mapToInt(Integer::intValue).average().orElse(0);
                averageDailyFlows.add(dailyAvg);
            }
        }
        
        // 计算所有天的平均值
        if (averageDailyFlows.isEmpty()) {
            return null;
        }
        
        return (int) averageDailyFlows.stream().mapToInt(Integer::intValue).average().orElse(0);
    }
    
    /**
     * 计算峰谷比（高峰期流量与低谷期流量的比值）
     * @return 峰谷比
     */
    public Double calculatePeakValleyRatio() {
        // 获取过去7天的数据
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<TrafficFlowRecord> recentRecords = trafficFlowRecords.stream()
            .filter(record -> record.getRecordTime().isAfter(sevenDaysAgo) && record.getFlowRate() != null)
            .sorted(Comparator.comparing(TrafficFlowRecord::getRecordTime))
            .collect(Collectors.toList());
        
        if (recentRecords.isEmpty()) {
            return null;
        }
        
        // 按小时分组
        Map<Integer, List<Integer>> hourlyFlows = recentRecords.stream()
            .collect(Collectors.groupingBy(
                record -> record.getRecordTime().getHour(),
                Collectors.mapping(TrafficFlowRecord::getFlowRate, Collectors.toList())
            ));
        
        // 计算每小时的平均流量
        Map<Integer, Double> hourlyAvgFlows = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : hourlyFlows.entrySet()) {
            hourlyAvgFlows.put(entry.getKey(), 
                entry.getValue().stream().mapToInt(Integer::intValue).average().orElse(0));
        }
        
        if (hourlyAvgFlows.isEmpty()) {
            return null;
        }
        
        // 找出最高和最低流量
        double maxFlow = hourlyAvgFlows.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
        double minFlow = hourlyAvgFlows.values().stream().mapToDouble(Double::doubleValue).min().orElse(0);
        
        if (minFlow == 0) {
            return null; // 避免除以零
        }
        
        return maxFlow / minFlow;
    }
    
    /**
     * 识别最近7天内的交通异常事件
     * @return 异常事件列表
     */
    public List<Map<String, Object>> detectTrafficAnomalies() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<TrafficFlowRecord> recentRecords = trafficFlowRecords.stream()
            .filter(record -> record.getRecordTime().isAfter(sevenDaysAgo))
            .sorted(Comparator.comparing(TrafficFlowRecord::getRecordTime))
            .collect(Collectors.toList());
        
        if (recentRecords.size() < 10) { // 数据点太少，无法分析
            return List.of();
        }
        
        List<Map<String, Object>> anomalies = new ArrayList<>();
        
        // 分析流量突变
        for (int i = 1; i < recentRecords.size(); i++) {
            TrafficFlowRecord prev = recentRecords.get(i-1);
            TrafficFlowRecord curr = recentRecords.get(i);
            
            if (prev.getFlowRate() != null && curr.getFlowRate() != null) {
                // 检查流量突增或突降
                double changeRatio = prev.getFlowRate() > 0 ? 
                    (double)(curr.getFlowRate() - prev.getFlowRate()) / prev.getFlowRate() : 0;
                
                if (Math.abs(changeRatio) > 0.5) { // 50%的变化视为异常
                    Map<String, Object> anomaly = new HashMap<>();
                    anomaly.put("time", curr.getRecordTime());
                    anomaly.put("type", changeRatio > 0 ? "流量突增" : "流量突降");
                    anomaly.put("changeRatio", String.format("%.2f%%", changeRatio * 100));
                    anomaly.put("prevFlow", prev.getFlowRate());
                    anomaly.put("currFlow", curr.getFlowRate());
                    anomalies.add(anomaly);
                }
            }
            
            // 检查速度突变
            if (prev.getAverageSpeed() != null && curr.getAverageSpeed() != null) {
                double speedChangeRatio = prev.getAverageSpeed() > 0 ?
                    (curr.getAverageSpeed() - prev.getAverageSpeed()) / prev.getAverageSpeed() : 0;
                
                if (Math.abs(speedChangeRatio) > 0.3) { // 30%的变化视为异常
                    Map<String, Object> anomaly = new HashMap<>();
                    anomaly.put("time", curr.getRecordTime());
                    anomaly.put("type", speedChangeRatio < 0 ? "速度骤降" : "速度骤增");
                    anomaly.put("changeRatio", String.format("%.2f%%", speedChangeRatio * 100));
                    anomaly.put("prevSpeed", prev.getAverageSpeed());
                    anomaly.put("currSpeed", curr.getAverageSpeed());
                    anomalies.add(anomaly);
                }
            }
        }
        
        return anomalies;
    }
} 