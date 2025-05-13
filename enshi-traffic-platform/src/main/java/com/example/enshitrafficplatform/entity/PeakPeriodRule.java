package com.example.enshitrafficplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 高峰期规则实体类
 * 定义特定区域或路段的交通高峰期时段
 */
@Entity
@Table(name = "peak_period_rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeakPeriodRule {

    /**
     * 规则ID，主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 规则名称
     */
    @NotNull(message = "规则名称不能为空")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 规则描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /**
     * 适用星期（用二进制位表示，如0111110表示工作日）
     */
    @Column(name = "applicable_days")
    private Integer applicableDays;

    /**
     * 规则开始日期
     */
    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    /**
     * 规则结束日期
     */
    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    /**
     * 规则优先级（数值越大优先级越高）
     */
    @Column(name = "priority")
    private Integer priority;

    /**
     * 流量阈值（辆/小时）
     */
    @Column(name = "flow_threshold")
    private Integer flowThreshold;

    /**
     * 规则状态：活跃、非活跃
     */
    @Column(name = "status", length = 20)
    private String status;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 所属区域
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    /**
     * 所属路段
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "road_section_id")
    private RoadSection roadSection;

    /**
     * 所属路段集合（多对多关系）
     */
    @ManyToMany
    @JoinTable(
        name = "peak_rule_road_sections",
        joinColumns = @JoinColumn(name = "peak_rule_id"),
        inverseJoinColumns = @JoinColumn(name = "road_section_id")
    )
    private Set<RoadSection> roadSections = new HashSet<>();

    /**
     * 添加路段到规则
     * @param roadSection 路段
     * @return 是否添加成功
     */
    public boolean addRoadSection(RoadSection roadSection) {
        if (roadSections == null) {
            roadSections = new HashSet<>();
        }
        return roadSections.add(roadSection);
    }

    /**
     * 移除路段从规则
     * @param roadSection 路段
     * @return 是否移除成功
     */
    public boolean removeRoadSection(RoadSection roadSection) {
        if (roadSections == null) {
            return false;
        }
        return roadSections.remove(roadSection);
    }

    /**
     * 判断指定时间是否在高峰期内
     * @param dateTime 要检查的时间
     * @return 是否在高峰期内
     */
    public boolean isInPeakPeriod(LocalDateTime dateTime) {
        if (dateTime == null || startTime == null || endTime == null) {
            return false;
        }
        
        // 检查日期是否在有效期内
        if (effectiveFrom != null && dateTime.toLocalDate().isBefore(effectiveFrom)) {
            return false;
        }
        if (effectiveTo != null && dateTime.toLocalDate().isAfter(effectiveTo)) {
            return false;
        }
        
        // 检查星期是否适用
        if (applicableDays != null) {
            int dayBit = 1 << (dateTime.getDayOfWeek().getValue() - 1);
            if ((applicableDays & dayBit) == 0) {
                return false;
            }
        }
        
        // 检查时间是否在范围内
        LocalTime time = dateTime.toLocalTime();
        
        // 如果结束时间小于开始时间，表示跨天
        if (endTime.isBefore(startTime)) {
            return time.isAfter(startTime) || time.equals(startTime) || time.isBefore(endTime);
        } else {
            return (time.isAfter(startTime) || time.equals(startTime)) && 
                   (time.isBefore(endTime) || time.equals(endTime));
        }
    }

    /**
     * 判断现在是否在高峰期内
     * @return 是否在高峰期内
     */
    public boolean isNowInPeakPeriod() {
        return isInPeakPeriod(LocalDateTime.now());
    }

    /**
     * 获取适用星期的文本描述
     * @return 适用星期的文本描述
     */
    public String getApplicableDaysText() {
        if (applicableDays == null) {
            return "所有日期";
        }
        
        StringBuilder result = new StringBuilder();
        String[] days = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        
        for (int i = 0; i < 7; i++) {
            if ((applicableDays & (1 << i)) != 0) {
                if (result.length() > 0) {
                    result.append(", ");
                }
                result.append(days[i]);
            }
        }
        
        if (result.length() == 0) {
            return "无适用日期";
        }
        
        return result.toString();
    }

    /**
     * 设置适用星期
     * @param daysOfWeek 适用的星期几集合（1-7，1表示周一）
     */
    public void setApplicableDaysOfWeek(Set<Integer> daysOfWeek) {
        if (daysOfWeek == null || daysOfWeek.isEmpty()) {
            this.applicableDays = 0;
            return;
        }
        
        int result = 0;
        for (Integer day : daysOfWeek) {
            if (day >= 1 && day <= 7) {
                result |= (1 << (day - 1));
            }
        }
        
        this.applicableDays = result;
    }

    /**
     * 设置工作日适用
     */
    public void setWorkdaysOnly() {
        // 工作日为周一至周五
        this.applicableDays = 0b1111100; // 二进制表示的0000000000011111
    }

    /**
     * 设置周末适用
     */
    public void setWeekendsOnly() {
        // 周末为周六和周日
        this.applicableDays = 0b0000011; // 二进制表示的0000000000000011
    }

    /**
     * 设置规则为活跃状态
     */
    public void activate() {
        this.status = "活跃";
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 设置规则为非活跃状态
     */
    public void deactivate() {
        this.status = "非活跃";
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 获取时间段的格式化表示
     * @return 时间段格式化表示
     */
    public String getTimeRangeFormatted() {
        if (startTime == null || endTime == null) {
            return "";
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return startTime.format(formatter) + " - " + endTime.format(formatter);
    }

    /**
     * 获取规则生效日期的格式化表示
     * @return 规则生效日期格式化表示
     */
    public String getEffectiveDateRangeFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        if (effectiveFrom == null && effectiveTo == null) {
            return "永久有效";
        }
        
        if (effectiveFrom != null && effectiveTo == null) {
            return effectiveFrom.format(formatter) + " 起生效";
        }
        
        if (effectiveFrom == null && effectiveTo != null) {
            return "生效至 " + effectiveTo.format(formatter);
        }
        
        return effectiveFrom.format(formatter) + " 至 " + effectiveTo.format(formatter);
    }

    /**
     * 获取规则运行状态描述
     * @return 规则运行状态描述
     */
    public String getStatusDescription() {
        if ("非活跃".equals(status)) {
            return "规则当前未生效";
        }
        
        LocalDate today = LocalDate.now();
        
        if (effectiveFrom != null && today.isBefore(effectiveFrom)) {
            return "规则将在 " + effectiveFrom.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 开始生效";
        }
        
        if (effectiveTo != null && today.isAfter(effectiveTo)) {
            return "规则已于 " + effectiveTo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 结束";
        }
        
        if (isNowInPeakPeriod()) {
            return "当前正在高峰期内";
        } else {
            return "当前不在高峰期内";
        }
    }

    /**
     * 获取下次高峰期的开始时间
     * @return 下次高峰期的开始时间，如果规则不适用则返回null
     */
    public LocalDateTime getNextPeakPeriodStart() {
        if (startTime == null || "非活跃".equals(status)) {
            return null;
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        
        // 检查日期是否在有效期内
        if (effectiveFrom != null && today.isBefore(effectiveFrom)) {
            LocalDateTime nextStart = LocalDateTime.of(effectiveFrom, startTime);
            // 找到effectiveFrom后第一个适用的星期几
            while (!isDayApplicable(nextStart.getDayOfWeek())) {
                nextStart = nextStart.plusDays(1);
            }
            return nextStart;
        }
        
        if (effectiveTo != null && today.isAfter(effectiveTo)) {
            return null; // 规则已过期
        }
        
        // 先检查今天的高峰期
        LocalDateTime todayStart = LocalDateTime.of(today, startTime);
        if (now.isBefore(todayStart) && isDayApplicable(today.getDayOfWeek())) {
            return todayStart;
        }
        
        // 查找未来7天内的下一个高峰期
        for (int i = 1; i <= 7; i++) {
            LocalDate nextDate = today.plusDays(i);
            if (effectiveTo != null && nextDate.isAfter(effectiveTo)) {
                return null; // 规则在此期间结束
            }
            
            if (isDayApplicable(nextDate.getDayOfWeek())) {
                return LocalDateTime.of(nextDate, startTime);
            }
        }
        
        return null; // 未来7天内没有适用的高峰期
    }

    /**
     * 判断指定星期几是否适用于此规则
     * @param dayOfWeek 星期几
     * @return 是否适用
     */
    private boolean isDayApplicable(DayOfWeek dayOfWeek) {
        if (applicableDays == null) {
            return true;
        }
        
        int dayBit = 1 << (dayOfWeek.getValue() - 1);
        return (applicableDays & dayBit) != 0;
    }

    /**
     * 获取规则的简要描述
     * @return 规则简要描述
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(name);
        
        if (startTime != null && endTime != null) {
            summary.append(" (").append(getTimeRangeFormatted()).append(")");
        }
        
        summary.append(" ").append(getApplicableDaysText());
        
        if (status != null) {
            summary.append(" - ").append(status);
        }
        
        return summary.toString();
    }

    /**
     * 获取区域名称
     * @return 区域名称或"全局"
     */
    public String getRegionName() {
        if (region != null) {
            return region.getName();
        }
        return "全局";
    }

    /**
     * 计算两个时间点之间的重叠高峰期时长（分钟）
     * @param from 开始时间
     * @param to 结束时间
     * @return 重叠的高峰期时长（分钟）
     */
    public long calculateOverlappingPeakPeriodMinutes(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null || from.isAfter(to)) {
            return 0;
        }
        
        long totalMinutes = 0;
        LocalDateTime current = from;
        
        while (!current.isAfter(to)) {
            if (isInPeakPeriod(current)) {
                totalMinutes++;
            }
            current = current.plusMinutes(1);
        }
        
        return totalMinutes;
    }

    /**
     * 判断指定星期几、小时和分钟是否在高峰期规则内
     * @param dayOfWeek 星期几（1-7，1表示周一）
     * @param hour 小时（0-23）
     * @param minute 分钟（0-59）
     * @return 是否符合规则
     */
    public boolean appliesTo(int dayOfWeek, int hour, int minute) {
        if (startTime == null || endTime == null) {
            return false;
        }
        
        // 检查星期是否适用
        if (applicableDays != null) {
            int dayBit = 1 << (dayOfWeek - 1);
            if ((applicableDays & dayBit) == 0) {
                return false;
            }
        }
        
        // 检查时间是否在范围内
        LocalTime time = LocalTime.of(hour, minute);
        
        // 如果结束时间小于开始时间，表示跨天
        if (endTime.isBefore(startTime)) {
            return time.isAfter(startTime) || time.equals(startTime) || time.isBefore(endTime);
        } else {
            return (time.isAfter(startTime) || time.equals(startTime)) && 
                   (time.isBefore(endTime) || time.equals(endTime));
        }
    }
} 