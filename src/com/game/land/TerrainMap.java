package com.game.land;

import com.game.camera.Camera;
import com.game.enums.TerrainTypeEnum;

import java.awt.*;
import java.util.Random;

/**
 * @author liling
 * @date 2025/7/11 16:38
 * @description
 */
public class TerrainMap {

    private TerrainTypeEnum[][] terrainGrid;
    private int tileSize;

    public TerrainMap(int width, int height, int tileSize) {
        this.terrainGrid = new TerrainTypeEnum[width][height];
        this.tileSize = tileSize;
        generateRandomTerrain();
    }

    private void generateRandomTerrain() {
        Random random = new Random();
        for(int x = 0; x < terrainGrid.length; x++) {
            for(int y = 0; y < terrainGrid[0].length; y++) {
                float rand = random.nextFloat();
                if(rand < 0.6f) terrainGrid[x][y] = TerrainTypeEnum.GRASS;
                else if(rand < 0.75f) terrainGrid[x][y] = TerrainTypeEnum.SAND;
                else if(rand < 0.85f) terrainGrid[x][y] = TerrainTypeEnum.WATER;
                else if(rand < 0.92f) terrainGrid[x][y] = TerrainTypeEnum.SWAMP;
                else terrainGrid[x][y] = TerrainTypeEnum.ROAD;
            }
        }
    }

    public TerrainTypeEnum getTerrainAt(float worldX, float worldY) {
        int tileX = (int)(worldX / tileSize);
        int tileY = (int)(worldY / tileSize);

        if(tileX >= 0 && tileX < terrainGrid.length &&
                tileY >= 0 && tileY < terrainGrid[0].length) {
            return terrainGrid[tileX][tileY];
        }
        return TerrainTypeEnum.GRASS; // 默认地形
    }

    public void draw(Graphics2D g, Camera camera) {
        Rectangle viewBounds = camera.getViewBounds();

        int startX = Math.max(0, (int)(viewBounds.x / tileSize));
        int startY = Math.max(0, (int)(viewBounds.y / tileSize));
        int endX = Math.min(terrainGrid.length,
                (int)((viewBounds.x + viewBounds.width) / tileSize + 1));
        int endY = Math.min(terrainGrid[0].length,
                (int)((viewBounds.y + viewBounds.height) / tileSize + 1));

        for(int x = startX; x < endX; x++) {
            for(int y = startY; y < endY; y++) {
                g.setColor(terrainGrid[x][y].color);
                g.fillRect(
                        x * tileSize,
                        y * tileSize,
                        tileSize,
                        tileSize
                );
            }
        }
    }
}
