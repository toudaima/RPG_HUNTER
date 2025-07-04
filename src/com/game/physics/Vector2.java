package com.game.physics;

/**
 * @author liling
 * @date 2025/7/4 15:00
 * @description
 */
public class Vector2 {

    public float x, y;

    public static Vector2 random() {
        return new Vector2(
                (float)(Math.random() * 2 - 1),
                (float)(Math.random() * 2 - 1)
        );
    }

    public Vector2() {
        this(0, 0);
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 add(Vector2 other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public Vector2 sub(Vector2 other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    public Vector2 mul(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    public Vector2 div(float scalar) {
        this.x /= scalar;
        this.y /= scalar;
        return this;
    }

    public float length() {
        return (float)Math.sqrt(x*x + y*y);
    }

    public Vector2 normalize() {
        float len = length();
        if(len != 0) {
            x /= len;
            y /= len;
        }
        return this;
    }

    public Vector2 limit(float max) {
        if(length() > max) {
            normalize().mul(max);
        }
        return this;
    }

    public float distance(Vector2 other) {
        float dx = x - other.x;
        float dy = y - other.y;
        return (float)Math.sqrt(dx*dx + dy*dy);
    }

    public Vector2 copy() {
        return new Vector2(x, y);
    }

    @Override
    public String toString() {
        return String.format("(%.1f, %.1f)", x, y);
    }
}
