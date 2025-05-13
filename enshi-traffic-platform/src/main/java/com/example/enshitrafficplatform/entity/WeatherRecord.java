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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 天气记录实体类
 * 表示特定时间和地点的天气状况
 */
@Entity
@Table(name = "weather_records", indexes = {
    @Index(name = "idx_weather_record_time", columnList = "record_time"),
    @Index(name = "idx_weather_condition", columnList = "weather_condition"),
    @Index(name = "idx_weather_location", columnList = "longitude,latitude")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherRecord {

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
     * 天气状况：晴、多云、阴、小雨、中雨、大雨、暴雨、雪、雾等
     */
    @NotBlank(message = "天气状况不能为空")
    @Size(max = 50, message = "天气状况长度不能超过50个字符")
    @Column(name = "weather_condition", nullable = false, length = 50)
    private String weatherCondition;

    /**
     * 温度（摄氏度）
     */
    @Column(name = "temperature")
    private Double temperature;

    /**
     * 湿度（%）
     */
    @Column(name = "humidity")
    private Double humidity;

    /**
     * 风向（度，0-360）
     */
    @Column(name = "wind_direction")
    private Integer windDirection;

    /**
     * 风向描述（东、南、西、北等）
     */
    @Size(max = 50, message = "风向描述长度不能超过50个字符")
    @Column(name = "wind_direction_desc", length = 50)
    private String windDirectionDesc;

    /**
     * 风速（米/秒）
     */
    @Column(name = "wind_speed")
    private Double windSpeed;

    /**
     * 风力等级（0-17级）
     */
    @Column(name = "wind_force")
    private Integer windForce;

    /**
     * 气压（百帕）
     */
    @Column(name = "pressure")
    private Double pressure;

    /**
     * 降水量（毫米）
     */
    @Column(name = "precipitation")
    private Double precipitation;

    /**
     * 能见度（米）
     */
    @Column(name = "visibility")
    private Double visibility;

    /**
     * 是否为冰雪天气
     */
    @Column(name = "is_snow_ice", nullable = false)
    private Boolean isSnowIce = false;

    /**
     * 是否为雾天
     */
    @Column(name = "is_foggy", nullable = false)
    private Boolean isFoggy = false;

    /**
     * 是否有雷暴
     */
    @Column(name = "has_thunderstorm", nullable = false)
    private Boolean hasThunderstorm = false;

    /**
     * 空气质量指数（AQI）
     */
    @Column(name = "aqi")
    private Integer aqi;

    /**
     * 云量（%，0-100）
     */
    @Column(name = "cloud_cover")
    private Integer cloudCover;

    /**
     * 天气警告信息
     */
    @Column(name = "warning_info", columnDefinition = "TEXT")
    private String warningInfo;

    /**
     * 经度
     */
    @Column(name = "longitude")
    private Double longitude;

    /**
     * 纬度
     */
    @Column(name = "latitude")
    private Double latitude;

    /**
     * 数据来源
     */
    @Size(max = 100, message = "数据来源长度不能超过100个字符")
    @Column(name = "data_source", length = 100)
    private String dataSource;

    /**
     * 所属行政区域
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    /**
     * 获取当前天气年龄（分钟）
     * @return 天气记录的年龄（分钟）
     */
    public long getAgeInMinutes() {
        return ChronoUnit.MINUTES.between(recordTime, LocalDateTime.now());
    }

    /**
     * 获取风向的文字描述
     * @return 风向文字描述
     */
    public String getWindDirectionText() {
        if (windDirection == null) {
            return "未知";
        }
        
        if (windDirection >= 337.5 || windDirection < 22.5) {
            return "北风";
        } else if (windDirection >= 22.5 && windDirection < 67.5) {
            return "东北风";
        } else if (windDirection >= 67.5 && windDirection < 112.5) {
            return "东风";
        } else if (windDirection >= 112.5 && windDirection < 157.5) {
            return "东南风";
        } else if (windDirection >= 157.5 && windDirection < 202.5) {
            return "南风";
        } else if (windDirection >= 202.5 && windDirection < 247.5) {
            return "西南风";
        } else if (windDirection >= 247.5 && windDirection < 292.5) {
            return "西风";
        } else {
            return "西北风";
        }
    }

    /**
     * 获取风力等级（蒲福风级）
     * @return 风力等级
     */
    public int getWindForce() {
        if (windSpeed == null) {
            return 0;
        }
        
        if (windSpeed < 0.3) {
            return 0; // 无风
        } else if (windSpeed < 1.6) {
            return 1; // 软风
        } else if (windSpeed < 3.4) {
            return 2; // 轻风
        } else if (windSpeed < 5.5) {
            return 3; // 微风
        } else if (windSpeed < 8.0) {
            return 4; // 和风
        } else if (windSpeed < 10.8) {
            return 5; // 清风
        } else if (windSpeed < 13.9) {
            return 6; // 强风
        } else if (windSpeed < 17.2) {
            return 7; // 疾风
        } else if (windSpeed < 20.8) {
            return 8; // 大风
        } else if (windSpeed < 24.5) {
            return 9; // 烈风
        } else if (windSpeed < 28.5) {
            return 10; // 狂风
        } else if (windSpeed < 32.7) {
            return 11; // 暴风
        } else {
            return 12; // 台风
        }
    }

    /**
     * 获取空气质量等级描述
     * @return 空气质量等级描述
     */
    public String getAqiLevel() {
        if (aqi == null) {
            return "未知";
        }
        
        if (aqi <= 50) {
            return "优";
        } else if (aqi <= 100) {
            return "良";
        } else if (aqi <= 150) {
            return "轻度污染";
        } else if (aqi <= 200) {
            return "中度污染";
        } else if (aqi <= 300) {
            return "重度污染";
        } else {
            return "严重污染";
        }
    }

    /**
     * 获取降水等级描述
     * @return 降水等级描述
     */
    public String getPrecipitationLevel() {
        if (precipitation == null) {
            return "无降水";
        }
        
        if (precipitation < 0.1) {
            return "无降水";
        } else if (precipitation < 10.0) {
            return "小雨";
        } else if (precipitation < 25.0) {
            return "中雨";
        } else if (precipitation < 50.0) {
            return "大雨";
        } else if (precipitation < 100.0) {
            return "暴雨";
        } else if (precipitation < 250.0) {
            return "大暴雨";
        } else {
            return "特大暴雨";
        }
    }

    /**
     * 获取能见度等级描述
     * @return 能见度等级描述
     */
    public String getVisibilityLevel() {
        if (visibility == null) {
            return "未知";
        }
        
        if (visibility < 50) {
            return "特浓雾";
        } else if (visibility < 200) {
            return "浓雾";
        } else if (visibility < 500) {
            return "大雾";
        } else if (visibility < 1000) {
            return "雾";
        } else if (visibility < 2000) {
            return "轻雾";
        } else if (visibility < 5000) {
            return "霾";
        } else if (visibility < 10000) {
            return "轻度霾";
        } else {
            return "良好";
        }
    }

    /**
     * 判断天气是否可能影响交通
     * @return 是否影响交通
     */
    public boolean isAffectingTraffic() {
        // 大雨、暴雨等降水影响交通
        boolean heavyRain = precipitation != null && precipitation >= 25.0;
        
        // 能见度低影响交通
        boolean lowVisibility = visibility != null && visibility < 1000;
        
        // 大风影响交通
        boolean strongWind = windSpeed != null && windSpeed >= 10.8; // 6级以上大风
        
        // 冰雪天气影响交通（基于天气状况描述）
        boolean snowOrIce = weatherCondition != null && 
                          (weatherCondition.contains("雪") || 
                           weatherCondition.contains("冰") || 
                           weatherCondition.contains("霜"));
        
        // 温度过低可能导致道路结冰
        boolean lowTemperature = temperature != null && temperature <= 0;
        
        return heavyRain || lowVisibility || strongWind || snowOrIce || lowTemperature;
    }

    /**
     * 获取天气对交通影响程度（0-10，越大影响越严重）
     * @return 交通影响程度
     */
    public int getTrafficImpactLevel() {
        int impactLevel = 0;
        
        // 降水影响
        if (precipitation != null) {
            if (precipitation >= 250.0) impactLevel += 10;
            else if (precipitation >= 100.0) impactLevel += 8;
            else if (precipitation >= 50.0) impactLevel += 6;
            else if (precipitation >= 25.0) impactLevel += 4;
            else if (precipitation >= 10.0) impactLevel += 2;
            else if (precipitation >= 0.1) impactLevel += 1;
        }
        
        // 能见度影响
        if (visibility != null) {
            if (visibility < 50) impactLevel += 10;
            else if (visibility < 200) impactLevel += 8;
            else if (visibility < 500) impactLevel += 6;
            else if (visibility < 1000) impactLevel += 4;
            else if (visibility < 2000) impactLevel += 2;
        }
        
        // 风速影响
        if (windSpeed != null) {
            if (windSpeed >= 32.7) impactLevel += 10;
            else if (windSpeed >= 28.5) impactLevel += 8;
            else if (windSpeed >= 24.5) impactLevel += 6;
            else if (windSpeed >= 20.8) impactLevel += 5;
            else if (windSpeed >= 17.2) impactLevel += 4;
            else if (windSpeed >= 13.9) impactLevel += 3;
            else if (windSpeed >= 10.8) impactLevel += 2;
        }
        
        // 温度影响（特别是结冰风险）
        if (temperature != null) {
            if (temperature <= -10) impactLevel += 5;
            else if (temperature <= -5) impactLevel += 4;
            else if (temperature <= 0) impactLevel += 3;
            else if (temperature <= 2 && precipitation != null && precipitation > 0) {
                // 接近冰点且有降水，可能导致路面结冰
                impactLevel += 4;
            }
        }
        
        // 考虑天气状况描述
        if (weatherCondition != null) {
            if (weatherCondition.contains("暴雪") || weatherCondition.contains("冰雹")) impactLevel += 8;
            else if (weatherCondition.contains("大雪")) impactLevel += 7;
            else if (weatherCondition.contains("中雪")) impactLevel += 5;
            else if (weatherCondition.contains("小雪")) impactLevel += 3;
            else if (weatherCondition.contains("冰")) impactLevel += 6;
            else if (weatherCondition.contains("霜")) impactLevel += 3;
            else if (weatherCondition.contains("雷")) impactLevel += 2;
        }
        
        // 确保不超过10
        return Math.min(10, impactLevel);
    }

    /**
     * 获取天气记录的格式化日期时间
     * @param pattern 日期格式
     * @return 格式化后的日期时间
     */
    public String getFormattedDateTime(String pattern) {
        if (recordTime == null) {
            return "";
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return recordTime.format(formatter);
    }

    /**
     * 获取默认格式的日期时间（yyyy-MM-dd HH:mm）
     * @return 格式化后的日期时间
     */
    public String getFormattedDateTime() {
        return getFormattedDateTime("yyyy-MM-dd HH:mm");
    }

    /**
     * 获取天气状况简要描述
     * @return 天气状况简要描述
     */
    public String getWeatherSummary() {
        StringBuilder summary = new StringBuilder();
        
        if (weatherCondition != null) {
            summary.append(weatherCondition);
        }
        
        if (temperature != null) {
            summary.append(" ").append(String.format("%.1f", temperature)).append("°C");
        }
        
        if (precipitation != null && precipitation > 0) {
            summary.append(" 降水:").append(String.format("%.1f", precipitation)).append("mm");
        }
        
        if (windSpeed != null) {
            summary.append(" ").append(getWindDirectionText())
                   .append(getWindForce()).append("级");
        }
        
        if (visibility != null && visibility < 10000) {
            summary.append(" 能见度:").append(String.format("%.0f", visibility)).append("m");
        }
        
        return summary.toString().trim();
    }

    /**
     * 获取所属区域名称
     * @return 区域名称
     */
    public String getRegionName() {
        if (region != null) {
            return region.getName();
        }
        return "未知区域";
    }

    /**
     * 计算体感温度（风寒指数）
     * 基于温度和风速计算体感温度
     * @return 体感温度（摄氏度）
     */
    public Double getFeelsLikeTemperature() {
        if (temperature == null || windSpeed == null) {
            return temperature;
        }
        
        // 使用风寒指数公式（适用于低温环境）
        if (temperature <= 10 && windSpeed > 1.3) {
            double windSpeedKmh = windSpeed * 3.6; // 转换为千米/小时
            return 13.12 + 0.6215 * temperature - 11.37 * Math.pow(windSpeedKmh, 0.16) 
                   + 0.3965 * temperature * Math.pow(windSpeedKmh, 0.16);
        }
        
        // 使用热指数公式（适用于高温环境）
        if (temperature >= 27 && humidity != null) {
            double t = temperature;
            double rh = humidity;
            return -8.784695 + 1.61139411 * t + 2.338549 * rh 
                   - 0.14611605 * t * rh - 0.012308094 * t * t 
                   - 0.016424828 * rh * rh + 0.002211732 * t * t * rh 
                   + 0.00072546 * t * rh * rh - 0.000003582 * t * t * rh * rh;
        }
        
        // 温度适中，直接返回实际温度
        return temperature;
    }

    /**
     * 判断是否为恶劣天气（可能导致交通管制）
     * @return 是否为恶劣天气
     */
    public boolean isSevereWeather() {
        return getTrafficImpactLevel() >= 6;
    }

    /**
     * 判断是否为山区道路特有的复杂天气状况
     * 山区道路在雨雾、大风等条件下特别危险
     * @return 是否为山区复杂天气
     */
    public boolean isMountainComplexWeather() {
        // 山区雨雾组合
        boolean rainAndFog = weatherCondition != null && 
                           weatherCondition.contains("雨") && 
                           visibility != null && visibility < 1000;
        
        // 山区强风
        boolean mountainStrongWind = windSpeed != null && windSpeed >= 8.0 && 
                                   (windDirection != null && 
                                    (windDirection > 200 && windDirection < 340)); // 西南至西北风
        
        // 低云高度，影响山区道路可见度（假设云量大时低云会影响山区道路）
        boolean lowClouds = cloudCover != null && cloudCover > 80;
        
        // 突发强降水
        boolean suddenHeavyRain = precipitation != null && precipitation > 20 && 
                               weatherCondition != null && weatherCondition.contains("雷");
        
        return rainAndFog || mountainStrongWind || lowClouds || suddenHeavyRain;
    }

    /**
     * 计算山区地形放大系数
     * 山区天气影响会被地形放大
     * @return 放大系数（1.0-2.0）
     */
    public double getMountainTerrainFactor() {
        double factor = 1.0;
        
        // 降水影响在山区会加剧（山洪、滑坡等风险）
        if (precipitation != null && precipitation > 10) {
            factor += 0.3;
        }
        
        // 山区雾气影响更大（山路转弯处视线受限）
        if (visibility != null && visibility < 1000) {
            factor += 0.3;
        }
        
        // 山区风的影响（山谷风和山脊风）
        if (windSpeed != null && windSpeed > 8.0) {
            factor += 0.2;
        }
        
        // 山区温度低会加剧结冰风险（海拔每升高100米，温度下降约0.6°C）
        if (temperature != null && temperature < 5) {
            factor += 0.2;
        }
        
        return Math.min(2.0, factor);
    }

    /**
     * 获取适合山区道路的最大安全行驶速度建议
     * 根据当前天气状况计算
     * @param defaultSpeed 默认限速（公里/小时）
     * @return 建议速度（公里/小时）
     */
    public Integer getSuggestedSpeedLimit(int defaultSpeed) {
        int impactLevel = getTrafficImpactLevel();
        double mountainFactor = getMountainTerrainFactor();
        
        // 根据影响等级和山区因素调整速度
        double reductionFactor;
        
        if (impactLevel >= 8) {
            reductionFactor = 0.5; // 极端天气减速50%
        } else if (impactLevel >= 6) {
            reductionFactor = 0.6; // 严重天气减速40%
        } else if (impactLevel >= 4) {
            reductionFactor = 0.7; // 较差天气减速30%
        } else if (impactLevel >= 2) {
            reductionFactor = 0.8; // 轻度天气减速20%
        } else {
            reductionFactor = 1.0; // 良好天气不减速
        }
        
        // 应用山区地形因素
        reductionFactor /= mountainFactor;
        
        // 计算建议速度并取整
        return (int)(defaultSpeed * reductionFactor);
    }

    /**
     * 判断特定天气是否可能引发山区次生灾害
     * @return 是否可能引发次生灾害
     */
    public boolean mayTriggerSecondaryDisaster() {
        // 强降雨可能引发山洪、滑坡
        boolean heavyRainRisk = precipitation != null && precipitation > 50;
        
        // 持续降雨后的地质灾害风险
        boolean continuousRainRisk = precipitation != null && precipitation > 20 && 
                                 weatherCondition != null && weatherCondition.contains("持续");
        
        // 冻融循环导致的岩石风化和崩塌风险
        boolean freezeThawRisk = temperature != null && 
                             temperature > -3 && temperature < 3 && 
                             weatherCondition != null && 
                             (weatherCondition.contains("雨") || weatherCondition.contains("雪"));
        
        return heavyRainRisk || continuousRainRisk || freezeThawRisk;
    }

    /**
     * 评估天气对交通的影响程度
     * 根据各种天气因素综合评估
     * @return 影响程度 (0-10)，10表示影响最严重
     */
    public int evaluateTrafficImpact() {
        int impact = 0;
        
        // 基于天气状况评估
        if (weatherCondition != null) {
            if (weatherCondition.contains("暴雨")) {
                impact += 4;
            } else if (weatherCondition.contains("大雨")) {
                impact += 3;
            } else if (weatherCondition.contains("中雨")) {
                impact += 2;
            } else if (weatherCondition.contains("小雨")) {
                impact += 1;
            }
            
            if (weatherCondition.contains("雪")) {
                impact += 4;
            }
            
            if (weatherCondition.contains("雾")) {
                impact += 3;
            }
            
            if (weatherCondition.contains("雷暴")) {
                impact += 2;
            }
        }
        
        // 基于能见度评估
        if (visibility != null) {
            if (visibility < 50) {
                impact += 5;  // 极低能见度
            } else if (visibility < 100) {
                impact += 4;
            } else if (visibility < 200) {
                impact += 3;
            } else if (visibility < 500) {
                impact += 2;
            } else if (visibility < 1000) {
                impact += 1;
            }
        }
        
        // 基于风力评估
        if (windForce != null) {
            if (windForce >= 10) {
                impact += 3;  // 强风影响车辆通行
            } else if (windForce >= 8) {
                impact += 2;
            } else if (windForce >= 6) {
                impact += 1;
            }
        }
        
        // 特殊条件额外评估
        if (Boolean.TRUE.equals(isSnowIce)) {
            impact += 3;  // 冰雪天气路面湿滑
        }
        
        if (Boolean.TRUE.equals(isFoggy)) {
            impact += 3;  // 雾天能见度低
        }
        
        if (Boolean.TRUE.equals(hasThunderstorm)) {
            impact += 2;  // 雷暴天气影响驾驶
        }
        
        // 确保评分在0-10范围内
        return Math.min(10, Math.max(0, impact));
    }

    /**
     * 判断当前天气是否适合驾驶
     * @return 是否适合驾驶
     */
    public boolean isSafeDriving() {
        int impact = evaluateTrafficImpact();
        // 影响程度大于7视为不适合驾驶
        return impact <= 7;
    }

    /**
     * 获取天气的描述性文本
     * @return 天气描述
     */
    public String getWeatherDescription() {
        StringBuilder desc = new StringBuilder(weatherCondition);
        
        if (temperature != null) {
            desc.append(", 温度").append(temperature).append("°C");
        }
        
        if (humidity != null) {
            desc.append(", 湿度").append(humidity).append("%");
        }
        
        if (windDirectionDesc != null && windSpeed != null) {
            desc.append(", ").append(windDirectionDesc).append("风").append(windSpeed).append("m/s");
            if (windForce != null) {
                desc.append("(").append(windForce).append("级)");
            }
        }
        
        if (visibility != null) {
            desc.append(", 能见度").append(visibility).append("m");
        }
        
        if (precipitation != null && precipitation > 0) {
            desc.append(", 降水量").append(precipitation).append("mm");
        }
        
        return desc.toString();
    }

    /**
     * 获取驾驶建议
     * @return 驾驶建议
     */
    public String getDrivingAdvice() {
        int impact = evaluateTrafficImpact();
        
        if (impact >= 8) {
            return "建议非必要情况下不要驾车出行，路面状况危险";
        } else if (impact >= 6) {
            return "请谨慎驾驶，保持安全车距，减速慢行";
        } else if (impact >= 4) {
            return "注意路面状况，适当减速行驶";
        } else if (impact >= 2) {
            return "天气对驾驶有轻微影响，注意安全";
        } else {
            return "天气良好，适合驾驶";
        }
    }
} 