package com.example.enshitrafficplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 交通事件实体类
 * 记录交通事故、施工、管制等事件信息
 */
@Entity
@Table(name = "traffic_events", indexes = {
    @Index(name = "idx_traffic_event_type", columnList = "event_type"),
    @Index(name = "idx_traffic_event_status", columnList = "status"),
    @Index(name = "idx_traffic_event_severity", columnList = "severity"),
    @Index(name = "idx_traffic_event_start_time", columnList = "start_time"),
    @Index(name = "idx_traffic_event_end_time", columnList = "end_time")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrafficEvent {

    /**
     * 事件ID，主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 事件类型：交通事故、道路施工、交通管制、临时封路、自然灾害等
     */
    @NotNull(message = "事件类型不能为空")
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    /**
     * 事件描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 事件发生时间
     */
    @NotNull(message = "开始时间不能为空")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * 事件结束时间（预计或实际）
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 事件状态：待处理、处理中、已解决、已关闭
     */
    @Column(name = "status", length = 20)
    private String status;

    /**
     * 事件严重程度：轻微、一般、严重、极其严重
     */
    @Column(name = "severity", length = 20)
    private String severity;

    /**
     * 位置描述
     */
    @Column(name = "location_description", length = 255)
    private String locationDescription;

    /**
     * 地理位置-经度
     */
    @Column(name = "longitude")
    private Double longitude;

    /**
     * 地理位置-纬度
     */
    @Column(name = "latitude")
    private Double latitude;

    /**
     * 处理人员
     */
    @Column(name = "handler", length = 100)
    private String handler;

    /**
     * 事件影响半径（米）
     */
    @Column(name = "impact_radius")
    private Double impactRadius;

    /**
     * 影响车道数
     */
    @Column(name = "affected_lanes")
    private Integer affectedLanes;

    /**
     * 事件来源：监测系统、人工上报、公安交警等
     */
    @Column(name = "source", length = 50)
    private String source;

    /**
     * 与事件相关的交通流量影响估计（百分比，如减少30%）
     */
    @Column(name = "traffic_impact")
    private Double trafficImpact;

    /**
     * 所属路段
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "road_section_id")
    private RoadSection roadSection;

    /**
     * 获取事件持续时间（分钟）
     * @return 持续时间
     */
    public Long getDurationMinutes() {
        if (startTime == null) {
            return null;
        }
        
        LocalDateTime endDateTime = endTime != null ? endTime : LocalDateTime.now();
        return ChronoUnit.MINUTES.between(startTime, endDateTime);
    }

    /**
     * 判断事件是否活跃
     * @return 是否活跃
     */
    public boolean isActive() {
        if (status == null) {
            return false;
        }
        
        return "待处理".equals(status) || "处理中".equals(status);
    }

    /**
     * 判断事件是否已过期
     * @return 是否已过期
     */
    public boolean isExpired() {
        if (endTime == null) {
            return false;
        }
        
        return LocalDateTime.now().isAfter(endTime);
    }

    /**
     * 关闭事件，设置结束时间和状态
     */
    public void closeEvent() {
        this.status = "已关闭";
        if (this.endTime == null) {
            this.endTime = LocalDateTime.now();
        }
    }

    /**
     * 延长事件预计结束时间
     * @param minutes 延长的分钟数
     */
    public void extendEndTime(int minutes) {
        if (this.endTime == null) {
            this.endTime = LocalDateTime.now().plusMinutes(minutes);
        } else {
            this.endTime = this.endTime.plusMinutes(minutes);
        }
    }

    /**
     * 更新事件位置
     * @param latitude 纬度
     * @param longitude 经度
     * @param locationDescription 位置描述
     */
    public void updateLocation(Double latitude, Double longitude, String locationDescription) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationDescription = locationDescription;
    }

    /**
     * 获取事件对应的路段名称
     * @return 路段名称，如果没有关联则返回"未知路段"
     */
    public String getRoadSectionName() {
        if (roadSection != null) {
            return roadSection.getName();
        }
        return "未知路段";
    }

    /**
     * 获取事件对应的道路名称
     * @return 道路名称，如果没有关联则返回"未知道路"
     */
    public String getRoadName() {
        if (roadSection != null && roadSection.getRoad() != null) {
            return roadSection.getRoad().getName();
        }
        return "未知道路";
    }

    /**
     * 检查事件是否影响指定的地理位置
     * @param lat 纬度
     * @param lng 经度
     * @return 是否影响该位置
     */
    public boolean isAffectingLocation(Double lat, Double lng) {
        if (latitude == null || longitude == null || impactRadius == null || lat == null || lng == null) {
            return false;
        }
        
        // 使用简化的距离计算方法（平面近似，仅适用于小范围）
        double dx = (lng - longitude) * 111320 * Math.cos(latitude * Math.PI / 180);
        double dy = (lat - latitude) * 110540;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        return distance <= impactRadius;
    }

    /**
     * 计算事件严重等级的数值表示
     * @return 严重等级的数值：1-轻微，2-一般，3-严重，4-极其严重
     */
    public int getSeverityLevel() {
        if (severity == null) {
            return 0;
        }
        
        switch (severity) {
            case "轻微": return 1;
            case "一般": return 2;
            case "严重": return 3;
            case "极其严重": return 4;
            default: return 0;
        }
    }

    /**
     * 设置事件路段，同时更新事件的位置信息
     * @param roadSection 路段对象
     */
    public void setRoadSectionAndUpdateLocation(RoadSection roadSection) {
        this.roadSection = roadSection;
        
        // 如果路段有中心点信息，更新事件位置
        if (roadSection != null && roadSection.getLatitude() != null && roadSection.getLongitude() != null) {
            this.latitude = roadSection.getLatitude();
            this.longitude = roadSection.getLongitude();
            this.locationDescription = roadSection.getName() + " " + 
                                     (roadSection.getRoad() != null ? roadSection.getRoad().getName() : "");
        }
    }

    /**
     * 判断事件是否已结束
     * @return 是否已结束
     */
    public boolean isEnded() {
        if (endTime == null) {
            return false;
        }
        
        return LocalDateTime.now().isAfter(endTime) || "已关闭".equals(status) || "已解决".equals(status);
    }
} 