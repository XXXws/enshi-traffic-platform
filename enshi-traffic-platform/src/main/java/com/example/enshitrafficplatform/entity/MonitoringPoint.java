package com.example.enshitrafficplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 交通监测点实体类
 * 表示固定在道路上的交通监测设备位置
 */
@Entity
@Table(name = "monitoring_points", indexes = {
    @Index(name = "idx_monitoring_point_location", columnList = "longitude,latitude"),
    @Index(name = "idx_monitoring_point_type", columnList = "type"),
    @Index(name = "idx_monitoring_point_status", columnList = "status")
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
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    /**
     * 监测点纬度
     */
    @NotNull(message = "监测点纬度不能为空")
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
    @Column(name = "installation_date")
    private LocalDateTime installationDate;

    /**
     * 最近维护日期
     */
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
    @Column(name = "data_collection_frequency")
    private Integer dataCollectionFrequency;

    /**
     * IP地址
     */
    @Size(max = 50, message = "IP地址长度不能超过50个字符")
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * 监测点所属路段
     */
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
} 