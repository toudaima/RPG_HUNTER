package com.game.object.obstacle;

import com.game.physics.Vector2;

import java.awt.*;

/**
 * @author liling
 * @date 2025/7/4 17:30
 * @description
 */
public class Build {

    private Vector2 position;
    private float width, height;
    private Color color = new Color(100, 100, 100, 150);

    public Build(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.width = width;
        this.height = height;
    }

    public boolean contains(Vector2 point) {
        return point.x >= position.x - width/2 &&
                point.x <= position.x + width/2 &&
                point.y >= position.y - height/2 &&
                point.y <= position.y + height/2;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(color);
        g2.fillRect(
                (int)(position.x - width/2),
                (int)(position.y - height/2),
                (int)width,
                (int)height
        );
    }

    public Vector2 getPosition() {
        return position;
    }
}
