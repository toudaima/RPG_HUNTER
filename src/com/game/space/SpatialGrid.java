package com.game.space;

import com.game.object.biology.NPC;
import com.game.physics.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liling
 * @date 2025/7/4 13:40
 * @description 空间网格
 */
public class SpatialGrid {

    private int cellSize;
    private int width, height;
    private Map<String, List<NPC>> grid;

    public SpatialGrid(int cellSize, int width, int height) {
        this.cellSize = cellSize;
        this.width = width;
        this.height = height;
        this.grid = new HashMap<>();
    }

    public void update(List<NPC> npcs) {
        grid.clear();
        for(NPC npc : npcs) {
            String key = getKey(npc.getPosition());
            grid.computeIfAbsent(key, k -> new ArrayList<>()).add(npc);
        }
    }

    public List<NPC> getNeighbors(NPC npc, float radius) {
        List<NPC> result = new ArrayList<>();
        Vector2 pos = npc.getPosition();

        int startX = (int)((pos.x - radius) / cellSize);
        int startY = (int)((pos.y - radius) / cellSize);
        int endX = (int)((pos.x + radius) / cellSize);
        int endY = (int)((pos.y + radius) / cellSize);

        for(int x = startX; x <= endX; x++) {
            for(int y = startY; y <= endY; y++) {
                String key = x + "," + y;
                if(grid.containsKey(key)) {
                    for(NPC other : grid.get(key)) {
                        if(other != npc && pos.distance(other.getPosition()) <= radius) {
                            result.add(other);
                        }
                    }
                }
            }
        }

        return result;
    }

    private String getKey(Vector2 position) {
        int x = (int)(position.x / cellSize);
        int y = (int)(position.y / cellSize);
        return x + "," + y;
    }
}
