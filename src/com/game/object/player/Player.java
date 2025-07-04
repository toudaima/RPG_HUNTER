package com.game.object.player;

import com.game.listener.Keyboard;
import com.game.physics.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author liling
 * @date 2025/7/4 15:14
 * @description
 */
public class Player {
    //位置
    private final Vector2 position;
    //速度
    private final Vector2 velocity;

    private final Color color = Color.WHITE;

    private final float speed = 5f;
    private final float radius = 15f;

    public Player(float x, float y) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
    }

    public void draw(Graphics2D g2) {
        g2.setColor(color);
        //颜色填充
        g2.fillOval(
                (int)(position.x - radius),
                (int)(position.y - radius),
                (int)(radius * 2),
                (int)(radius * 2)
        );

        // 绘制方向指示
        if(velocity.length() > 0) {
            g2.setColor(Color.RED);
            //绘制与用户同长的向量
            Vector2 heading = velocity.copy().normalize().mul(radius);
            g2.drawLine(
                    (int)position.x,
                    (int)position.y,
                    (int)(position.x + heading.x),
                    (int)(position.y + heading.y)
            );
        }
    }

    public void update() {
        // 重置速度
        velocity.set(0, 0);

        // 键盘控制移动
        if(Keyboard.isKeyDown(KeyEvent.VK_W)){
            velocity.y -= speed;
        }
        if(Keyboard.isKeyDown(KeyEvent.VK_S)) {
            velocity.y += speed;
        }
        if(Keyboard.isKeyDown(KeyEvent.VK_A)) {
            velocity.x -= speed;
        }
        if(Keyboard.isKeyDown(KeyEvent.VK_D)) {
            velocity.x += speed;
        }

        // 对角线移动速度归一化
        if(velocity.x != 0 && velocity.y != 0) {
            velocity.normalize().mul(speed);
        }

        // 更新位置
        position.add(velocity);
    }

    public Vector2 getPosition() {
        return position;
    }
}
