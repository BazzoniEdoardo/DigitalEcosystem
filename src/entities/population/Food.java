package entities.population;

import core.App;
import entities.SimulationEntity;
import entities.movement.Position;
import managers.EntityManager;
import render.entities.AbstractRenderedEntity;
import settings.categories.FoodSettings;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Food extends AbstractRenderedEntity implements SimulationEntity, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int id;
    protected float nutrition;
    protected boolean expired;

    protected Position position;

    public Food(final Position position, final float nutrition) {
        setPosition(position);
        setNutrition(nutrition);
        setExpired(false);
        this.id = EntityManager.nextId();
    }

    public Food() {
        final FoodSettings settings = App.getSimManager().getSettings().getFoodSettings();

        this(new Position(0, 0), settings.baseNutrition());
    }

    @Override
    public SimulationEntity getEntity() {
        return this;
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
        final FoodSettings settings = App.getSimManager().getSettings().getFoodSettings();

        this.nutrition = (nutrition > 0) ? nutrition : settings.baseNutrition();
    }

    private void setExpired(final boolean expired) {
        this.expired = expired;
    }

    private void setPosition(final Position position) {
        this.position = (position != null) ? position : new Position(0, 0);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void update() {
        if (isExpired()) return;

        final FoodSettings settings = App.getSimManager().getSettings().getFoodSettings();

        this.nutrition -= settings.decaymentPerTick();

        if (this.nutrition <= 0) {
            setExpired(true);
        }
    }

    @Override
    public Food clone() {
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

    @Override
    public Map<String, String> getInfo() {
        final Map<String, String> info = new LinkedHashMap<>();
        info.put("ID",        String.valueOf(id));
        info.put("Position",  String.format("%.0f, %.0f", (double) position.x(), (double) position.y()));
        info.put("Nutrition", String.format("%.2f", nutrition));
        info.put("Expired",   String.valueOf(expired));
        return info;
    }

    @Override
    public String getEntityTypeName() {
        return "Food";
    }
}
