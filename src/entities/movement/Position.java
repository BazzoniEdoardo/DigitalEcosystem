package entities.movement;

import java.io.Serializable;
import java.util.Objects;

public record Position(int x, int y) implements Serializable {


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
