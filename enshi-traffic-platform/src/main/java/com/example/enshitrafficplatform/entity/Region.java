package com.example.enshitrafficplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * 行政区域实体类
 * 表示恩施市的行政区划，包括城区、县城和乡镇等
 */
@Entity
@Table(name = "regions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Region {

    /**
     * 行政区域ID，主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 行政区域名称，例如：恩施市、利川市、建始县等
     */
    @NotBlank(message = "区域名称不能为空")
    @Size(max = 50, message = "区域名称长度不能超过50个字符")
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /**
     * 行政区域代码，参照国家行政区划代码标准
     */
    @NotBlank(message = "区域代码不能为空")
    @Size(max = 20, message = "区域代码长度不能超过20个字符")
    @Column(name = "code", nullable = false, length = 20, unique = true)
    private String code;

    /**
     * 行政区域级别，例如：市、县、区、乡、镇等
     */
    @NotBlank(message = "区域级别不能为空")
    @Size(max = 20, message = "区域级别长度不能超过20个字符")
    @Column(name = "level", nullable = false, length = 20)
    private String level;

    /**
     * 行政区域中心点的经度
     */
    @Column(name = "longitude", nullable = true)
    private Double longitude;

    /**
     * 行政区域中心点的纬度
     */
    @Column(name = "latitude", nullable = true)
    private Double latitude;

    /**
     * 地理边界的GeoJSON数据，用于在地图上绘制区域边界
     */
    @Column(name = "geo_boundary", columnDefinition = "TEXT")
    private String geoBoundary;

    /**
     * 区域的总人口数
     */
    @Column(name = "population")
    private Integer population;

    /**
     * 区域的总面积（平方公里）
     */
    @Column(name = "area")
    private Double area;

    /**
     * 父级行政区域，例如：恩施市的父级是恩施州
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Region parent;

    /**
     * 子级行政区域列表
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Region> children = new HashSet<>();

    /**
     * 区域内的道路列表
     */
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    private Set<Road> roads = new HashSet<>();

    /**
     * 区域内的天气记录
     */
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    private Set<WeatherRecord> weatherRecords = new HashSet<>();

    /**
     * 区域内的高峰期规则
     */
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    private Set<PeakPeriodRule> peakPeriodRules = new HashSet<>();

    /**
     * 添加子区域
     * @param child 子区域
     */
    public void addChild(Region child) {
        children.add(child);
        child.setParent(this);
    }

    /**
     * 移除子区域
     * @param child 子区域
     */
    public void removeChild(Region child) {
        children.remove(child);
        child.setParent(null);
    }

    /**
     * 添加道路
     * @param road 道路
     */
    public void addRoad(Road road) {
        roads.add(road);
        road.setRegion(this);
    }

    /**
     * 移除道路
     * @param road 道路
     */
    public void removeRoad(Road road) {
        roads.remove(road);
        road.setRegion(null);
    }

    /**
     * 添加天气记录
     * @param weatherRecord 天气记录
     */
    public void addWeatherRecord(WeatherRecord weatherRecord) {
        weatherRecords.add(weatherRecord);
        weatherRecord.setRegion(this);
    }

    /**
     * 移除天气记录
     * @param weatherRecord 天气记录
     */
    public void removeWeatherRecord(WeatherRecord weatherRecord) {
        weatherRecords.remove(weatherRecord);
        weatherRecord.setRegion(null);
    }

    /**
     * 添加高峰期规则
     * @param peakPeriodRule 高峰期规则
     */
    public void addPeakPeriodRule(PeakPeriodRule peakPeriodRule) {
        peakPeriodRules.add(peakPeriodRule);
        peakPeriodRule.setRegion(this);
    }

    /**
     * 移除高峰期规则
     * @param peakPeriodRule 高峰期规则
     */
    public void removePeakPeriodRule(PeakPeriodRule peakPeriodRule) {
        peakPeriodRules.remove(peakPeriodRule);
        peakPeriodRule.setRegion(null);
    }
} 