package com.game.object.biology;

import com.game.object.obstacle.Build;
import com.game.object.player.Player;
import com.game.physics.Vector2;
import com.game.space.SpatialGrid;

import java.awt.*;
import java.util.List;

/**
 * @author liling
 * @date 2025/7/4 13:39
 * @description
 */
public class NPC {
    private static final float MAX_SPEED = 2.5f;
    private static final float MAX_FORCE = 0.5f;

    private Vector2 position;
    private Vector2 velocity;
    private Vector2 acceleration;
    private float radius;
    private Color color;

    // 行为参数
    private float wanderAngle = 0;
    private float health = 1.0f;
    private float energy = 1.0f;

    // 状态系统
    private enum State { WANDER, SEEK, FLEE, REST }
    private State currentState = State.WANDER;
    private float stateTimer = 0;
    private float stateDuration = 0;

    public NPC(float x, float y, Color color) {
        this.position = new Vector2(x, y);
        this.velocity = Vector2.random().normalize().mul(MAX_SPEED * 0.5f);
        this.acceleration = new Vector2();
        this.radius = 12 + (float)Math.random() * 6;
        this.color = color;

        setRandomStateDuration();
    }

    public void update(Player player, List<NPC> npcs, List<Build> obstacles, SpatialGrid grid) {
        // 状态计时器
        stateTimer++;
        if(stateTimer > stateDuration) {
            changeState();
        }

        // 根据状态选择行为
        switch(currentState) {
            case WANDER:
                wander();
                break;
            case SEEK:
                seek(player.getPosition());
                break;
            case FLEE:
                flee(player.getPosition());
                break;
            case REST:
                rest();
                break;
        }

        // 群体行为
        flock(npcs, grid);

        // 避障
        avoidObstacles(obstacles);

        // 边界检查
        checkBounds(800, 600);

        // 更新物理
        updatePhysics();

        // 更新能量系统
        updateEnergy();
    }

    private void updatePhysics() {
        // 限制最大力
        acceleration.limit(MAX_FORCE);

        // 应用加速度
        velocity.add(acceleration);

        // 限制速度
        velocity.limit(MAX_SPEED * energy);

        // 更新位置
        position.add(velocity);

        // 重置加速度
        acceleration.mul(0);
    }

    private void updateEnergy() {
        // 移动消耗能量
        energy = Math.max(0, energy - velocity.length() * 0.001f);

        // 静止恢复能量
        if(velocity.length() < 0.1f) {
            energy = Math.min(1, energy + 0.002f);
        }

        // 低能量时切换到休息状态
        if(energy < 0.3f && currentState != State.REST) {
            changeState(State.REST);
        }
    }

    private void changeState() {
        float rand = (float)Math.random();

        if(energy < 0.4f) {
            currentState = State.REST;
        } else if(rand < 0.5f) {
            currentState = State.WANDER;
        } else if(rand < 0.8f) {
            currentState = State.SEEK;
        } else {
            currentState = State.FLEE;
        }

        setRandomStateDuration();
    }

    private void changeState(State newState) {
        currentState = newState;
        setRandomStateDuration();
    }

    private void setRandomStateDuration() {
        stateTimer = 0;
        stateDuration = 60 + (float)(Math.random() * 300);
    }

    // 行为实现...
    private void wander() {
        float wanderRadius = 30;
        float wanderDistance = 50;
        float wanderJitter = 0.3f;

        wanderAngle += (float)(Math.random() - 0.5) * wanderJitter;

        Vector2 circlePos = velocity.copy().normalize().mul(wanderDistance);
        Vector2 displacement = new Vector2(
                (float)Math.cos(wanderAngle) * wanderRadius,
                (float)Math.sin(wanderAngle) * wanderRadius
        );

        Vector2 wanderForce = circlePos.add(displacement);
        applyForce(wanderForce.limit(MAX_FORCE * 0.5f));
    }

    private void seek(Vector2 target) {
        Vector2 desired = target.sub(position);
        desired.normalize().mul(MAX_SPEED);

        Vector2 steer = desired.sub(velocity);
        applyForce(steer.limit(MAX_FORCE));
    }

    private void flee(Vector2 target) {
        Vector2 desired = position.sub(target);
        if(desired.length() > 100) return; // 只在近距离逃离

        desired.normalize().mul(MAX_SPEED);
        Vector2 steer = desired.sub(velocity);
        applyForce(steer.limit(MAX_FORCE * 1.2f));
    }

    private void rest() {
        // 减速
        if(velocity.length() > 0.1f) {
            Vector2 brake = velocity.copy().mul(-0.05f);
            applyForce(brake);
        }

        // 能量恢复足够后切换状态
        if(energy > 0.8f) {
            changeState();
        }
    }

    private void flock(List<NPC> npcs, SpatialGrid grid) {
        Vector2 separation = new Vector2();
        Vector2 alignment = new Vector2();
        Vector2 cohesion = new Vector2();

        int neighborCount = 0;
        float neighborRadius = 80;

        // 使用空间分区获取附近NPC
        List<NPC> neighbors = grid.getNeighbors(this, neighborRadius);

        for(NPC other : neighbors) {
            if(other == this) continue;

            float distance = position.distance(other.position);
            if(distance > 0 && distance < neighborRadius) {
                // 分离: 远离太近的邻居
                Vector2 diff = position.sub(other.position);
                diff.normalize().div(distance); // 距离越近权重越大
                separation.add(diff);

                // 对齐: 计算平均方向
                alignment.add(other.velocity);

                // 凝聚: 计算平均位置
                cohesion.add(other.position);

                neighborCount++;
            }
        }

        if(neighborCount > 0) {
            // 分离
            separation.div(neighborCount);
            if(separation.length() > 0) {
                separation.normalize().mul(MAX_SPEED);
                separation.sub(velocity);
                separation.limit(MAX_FORCE);
                applyForce(separation.mul(1.5f));
            }

            // 对齐
            alignment.div(neighborCount);
            alignment.normalize().mul(MAX_SPEED);
            alignment.sub(velocity);
            alignment.limit(MAX_FORCE * 0.8f);
            applyForce(alignment);

            // 凝聚
            cohesion.div(neighborCount);
            seek(cohesion); // 使用seek行为朝向中心
        }
    }

    private void avoidObstacles(List<Build> obstacleList) {
        float lookAhead = velocity.length() + radius;
        if(lookAhead < 10) return;

        Vector2 ahead = position.add(velocity.copy().normalize().mul(lookAhead));

        for(Build build : obstacleList) {
            if(build.contains(ahead)) {
                Vector2 avoidance = position.sub(build.getPosition());
                avoidance.normalize().mul(MAX_FORCE * 2);
                applyForce(avoidance);
                return;
            }
        }
    }

    private void checkBounds(int width, int height) {
        float margin = 50;
        float turnForce = MAX_FORCE * 0.8f;

        if(position.x < margin) {
            applyForce(new Vector2(turnForce, 0));
        } else if(position.x > width - margin) {
            applyForce(new Vector2(-turnForce, 0));
        }

        if(position.y < margin) {
            applyForce(new Vector2(0, turnForce));
        } else if(position.y > height - margin) {
            applyForce(new Vector2(0, -turnForce));
        }
    }

    private void applyForce(Vector2 force) {
        acceleration.add(force);
    }

    public void draw(Graphics2D g2) {
        // 绘制主体
        g2.setColor(color);
        g2.fillOval(
                (int)(position.x - radius),
                (int)(position.y - radius),
                (int)(radius * 2),
                (int)(radius * 2)
        );

        // 绘制方向指示
        g2.setColor(Color.WHITE);
        Vector2 heading = velocity.copy().normalize().mul(radius);
        g2.drawLine(
                (int)position.x,
                (int)position.y,
                (int)(position.x + heading.x),
                (int)(position.y + heading.y)
        );

        // 绘制状态指示
        drawStateIndicator(g2);
    }

    private void drawStateIndicator(Graphics2D g2) {
        Color stateColor;
        switch(currentState) {
            case WANDER: stateColor = Color.BLUE; break;
            case SEEK: stateColor = Color.GREEN; break;
            case FLEE: stateColor = Color.RED; break;
            case REST: stateColor = Color.YELLOW; break;
            default: stateColor = Color.WHITE;
        }

        g2.setColor(stateColor);
        g2.fillOval(
                (int)(position.x - 3),
                (int)(position.y - radius - 6),
                6, 6
        );
    }

    public Vector2 getPosition() {
        return position;
    }
}
