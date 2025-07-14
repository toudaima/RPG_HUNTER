package com.game.view;

import com.game.camera.Camera;
import com.game.enums.GameStatusEnum;
import com.game.land.TerrainMap;
import com.game.listener.Keyboard;
import com.game.object.biology.NPC;
import com.game.object.obstacle.Build;
import com.game.object.player.Player;
import com.game.space.SpatialGrid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author liling
 * @date 2025/7/4 14:55
 * @description
 */
public class GamePanel extends JPanel implements Runnable {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private static final int FPS = 60;

    private GameStatusEnum gameStatus = GameStatusEnum.READY;
    //运行状态
    private boolean running;
    //线程
    private Thread gameThread;

    private Camera camera;

    private Player player;

    private List<Build> obstacleList = new ArrayList<>();

    private Random random = new Random();

    private SpatialGrid spatialGrid;

    private List<NPC> npcList = new ArrayList<>();

    private TerrainMap terrainMap;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(30, 30, 40));
        setDoubleBuffered(true);



        // 初始化相机(世界大小设为2000x2000)
        camera = new Camera(2000, 2000);
        //初始化空间网格
        spatialGrid = new SpatialGrid(50, 2000, 2000);


        // 添加键盘监听
        addKeyListener(new Keyboard());
        setFocusable(true);
    }

    public void startGameLoop() {
        if(running) return;
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void initializeGameObjects() {
        // 创建玩家(放在世界中心)
        player = new Player(1000, 1000);

        // 创建NPC
        for(int i = 0; i < 1; i++) {
            npcList.add(new NPC(
                    random.nextInt(1800) + 100,
                    random.nextInt(1800) + 100,
                    Color.getHSBColor(random.nextFloat(), 0.8f, 0.8f)
            ));
        }

        // 创建障碍物
        for(int i = 0; i < 30; i++) {
            obstacleList.add(new Build(
                    random.nextInt(1800) + 100,
                    random.nextInt(1800) + 100,
                    random.nextInt(50) + 30,
                    random.nextInt(50) + 30
            ));
        }
        //初始化地图
        terrainMap = new TerrainMap(2000, 2000, 50);
    }

    @Override
    public void run() {

        //刷新间隔
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(running) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1) {
                if (gameStatus == GameStatusEnum.READY) {
                    if (Keyboard.isKeyDown(KeyEvent.VK_ENTER)) {
                        gameStatus = GameStatusEnum.START;
                        // 初始化游戏对象
                        initializeGameObjects();
                    }
                } else {

                    update();
                }
                repaint();
                delta --;
            }
        }
    }

    private void update() {

        // 更新玩家
        player.update();

        // 更新相机
        camera.update(player.getPosition(), getWidth(), getHeight());

        // 更新空间分区
        spatialGrid.update(npcList);

        // 更新NPC
        for(NPC npc : npcList) {
            npc.update(player, npcList, obstacleList, spatialGrid);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        if (gameStatus ==GameStatusEnum.READY) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("宋体", Font.BOLD, 20));
            g2.drawString("点击开始", WIDTH / 2, HEIGHT / 2);
        } else {
            // 抗锯齿
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 应用相机变换(优先)
            camera.applyTransform(g2);

            terrainMap.draw(g2, camera);

            // 绘制障碍物
            for(Build build : obstacleList) {
                build.draw(g2);
            }

            // 绘制NPC
            for(NPC npc : npcList) {
                npc.draw(g2);
            }

            // 绘制玩家
            player.draw(g2);
            // 重置变换以绘制UI元素
            g2.translate(camera.getPosition().x, camera.getPosition().y);

            // 绘制UI
            drawUI(g2);
        }

        //释放资源
        g2.dispose();
    }

    private void drawUI(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.drawString("玩家位置: " + player.getPosition(), 10, 20);
//        g2.drawString("NPC数量: " + npcs.size(), 10, 40);
        g2.drawString("WASD键移动", 10, 60);
    }
}
