package render.entities;

import entities.SimulationEntity;

import java.util.Map;

public interface RenderedEntity {

    double getX();
    double getY();
    double getWidth();
    double getHeight();

    void setX(final double x);
    void setY(final double y);
    void setWidth(final double width);
    void setHeight(final double height);

    Shape getShape();
    SimulationEntity getEntity();
    Map<String, String> getInfo();
    String getEntityTypeName();

    boolean contains(final double mouseX, final double mouseY);

    enum Shape {
        SQUARE,
        CIRCLE
    }
}
