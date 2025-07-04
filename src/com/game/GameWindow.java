package com.game;

import com.game.view.GamePanel;

import javax.swing.*;
import java.awt.*;

/**
 * @author liling
 * @date 2025/7/4 14:54
 * @description
 */
public class GameWindow extends JFrame {

    private final GamePanel gamePanel;

    public GameWindow() {
        setTitle("打鬼专家");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        gamePanel = new GamePanel();
        add(gamePanel);

        pack();
        setLocationRelativeTo(null);
    }

    public void startGame() {
        gamePanel.startGameLoop();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
            window.startGame();
        });
    }
}
