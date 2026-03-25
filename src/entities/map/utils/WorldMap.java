package entities.map.utils;

import entities.movement.MoveResult;
import entities.population.living.Creature;
import entities.population.enviroment.Food;
import entities.population.SimulationEntity;
import entities.movement.Position;
import entities.population.living.genetics.genes.Gene;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class WorldMap implements Serializable {

    private final int width;
    private final int height;
    private final WorldPosition[][] map;

    public WorldMap(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.map = new WorldPosition[width][height];

        init();
    }

    protected void init() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                map[i][j] = new WorldPosition(new Position(i, j));
            }
        }
    }

    public void clear() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                map[i][j].clear();
            }
        }
    }

    public void setCellEntity(final int x, final int y, final SimulationEntity entity) {
        map[x][y].setEntity(entity);
    }

    public void setCellEntity(final Position position, final SimulationEntity entity) {
        map[position.x()][position.y()].setEntity(entity);
    }

    public SimulationEntity getCellEntity(final int x, final int y) {
        return map[x][y].getEntity();
    }

    public SimulationEntity getCellEntity(final Position position) {
        return map[position.x()][position.y()].getEntity();
    }

    public boolean isInBounds(final Position position) {
        return position.x() >= 0 && position.x() < width
                && position.y() >= 0 && position.y() < height;

    }

    public boolean isPositionFree(final Position position) {
        return isInBounds(position) && map[position.x()][position.y()].getEntity() == null;
    }

    public void updateEntitiesPositions(final List<? extends SimulationEntity>... entities) {
        clear();

        Arrays.stream(entities).forEach(ls -> ls.forEach(e -> {
            final Position position = e.getPosition();
            map[position.x()][position.y()].setEntity(e);
        }));
    }

    public MoveResult isMovementAllowed(final Position position) {
        if (!isInBounds(position)) return new MoveResult(false, null);

        final SimulationEntity entity = map[position.x()][position.y()].getEntity();

        if (entity == null) return new MoveResult(true, null);
        if (entity instanceof Creature) return new MoveResult(false, null);
        if (entity instanceof Food f) return new MoveResult(true, f);

        return new MoveResult(true, null);

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public WorldPosition[][] getMap() {
        return map;
    }

}
