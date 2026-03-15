package entities;

import configuration.FoodConfig;
import entities.movement.Position;

import java.io.Serializable;
import java.util.Objects;

public class Food implements SimulationEntity, Serializable {
    private static final long serialVersionUID = 1L;

    protected float nutrition;
    protected boolean expired;

    protected Position position;

    public Food(final Position position, final float nutrition) {
        setPosition(position);
        setNutrition(nutrition);
        setExpired(false);
    }

    public Food() {
        this(new Position(0, 0), FoodConfig.baseNutrition);
    }

    public Food(final Food food) {
        this(food.getPosition(), food.getNutrition());
    }

    public float getNutrition() {
        return nutrition;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isExpired() {
        return expired;
    }

    private void setNutrition(final float nutrition) {
        this.nutrition = (nutrition > 0) ? nutrition : FoodConfig.baseNutrition;
    }

    private void setExpired(final boolean expired) {
        this.expired = expired;
    }

    private void setPosition(final Position position) {
        this.position = (position != null) ? position : new Position(0, 0);
    }

    @Override
    public void update() {
        if (isExpired()) return;

        this.nutrition -= FoodConfig.decaymentPerTick;

        if (this.nutrition <= 0) {
            setExpired(true);
        }
    }

    @Override
    public SimulationEntity clone() {
        return new Food(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Food food)) return false;
        return getNutrition() == food.getNutrition() && isExpired() == food.isExpired() && Objects.equals(getPosition(), food.getPosition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNutrition(), isExpired(), getPosition());
    }

    @Override
    public String toString() {
        return "Food{" +
                "nutrition=" + nutrition +
                ", expired=" + expired +
                ", position=" + position +
                '}';
    }
}
