package com.game.camera;

import com.game.physics.Vector2;

import java.awt.*;

/**
 * @author liling
 * @date 2025/7/4 14:59
 * @description
 */
public class Camera {

    private Vector2 position;
    private Vector2 target;
    private float smoothSpeed = 0.1f;
    private int worldWidth, worldHeight;

    public Camera(int worldWidth, int worldHeight) {
        this.position = new Vector2();
        this.target = new Vector2();
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void update(Vector2 targetPosition, int screenWidth, int screenHeight) {
        // 计算目标位置（使目标位于屏幕中心）
        target.x = targetPosition.x - screenWidth/2;
        target.y = targetPosition.y - screenHeight/2;

        // 平滑移动
        position.x += (target.x - position.x) * smoothSpeed;
        position.y += (target.y - position.y) * smoothSpeed;

        // 限制相机不超出世界边界
        position.x = Math.max(0, Math.min(worldWidth - screenWidth, position.x));
        position.y = Math.max(0, Math.min(worldHeight - screenHeight, position.y));
    }

    public void applyTransform(Graphics2D g2) {
        g2.translate(-position.x, -position.y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 screenToWorld(Vector2 screenPos) {
        return new Vector2(screenPos.x + position.x, screenPos.y + position.y);
    }
}
