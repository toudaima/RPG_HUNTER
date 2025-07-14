package com.game.enums;

import java.awt.*;

/**
 * @author liling
 * @date 2025/7/11 16:36
 * @description
 */
public enum TerrainTypeEnum {

    GRASS(1.0f, Color.GREEN),      // 草地: 正常速度
    SAND(0.7f, Color.YELLOW),      // 沙地: 减速30%
    WATER(0.4f, Color.BLUE),       // 水域: 减速60%
    SWAMP(0.5f, new Color(100, 70, 40)), // 沼泽
    ROAD(1.3f, Color.GRAY),        // 道路: 加速30%
    MOUNTAIN(0.3f, Color.LIGHT_GRAY); // 山地

    public final float speedModifier; // 速度修正系数
    public final Color color;         // 绘制颜色

    TerrainTypeEnum(float speedModifier, Color color) {
        this.speedModifier = speedModifier;
        this.color = color;
    }
}
