package render.entities;

import entities.SimulationEntity;

import java.util.Map;

public abstract class AbstractRenderedEntity implements RenderedEntity{

    protected double x;
    protected double y;
    protected double width;
    protected double height;

    protected Shape shape;

    public AbstractRenderedEntity(final double x, final double y, final double width, final double height, final Shape shape) {
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        this.shape = shape;
    }

    public AbstractRenderedEntity() {
        setX(0);
        setY(0);
        setWidth(0);
        setHeight(0);
        this.shape = Shape.CIRCLE;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    public abstract SimulationEntity getEntity();

    public abstract Map<String, String> getInfo();
    public abstract String getEntityTypeName();

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public void setWidth(double width) {
        this.width = width;
    }

    @Override
    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public boolean contains(double mouseX, double mouseY) {
        return switch (shape) {
            case SQUARE: {
                yield mouseX >= x && mouseX <= x + width &&
                        mouseY >= y && mouseY <= y + height;
            }
            case CIRCLE: {
                double cx = x + width / 2.0;
                double cy = y + height / 2.0;
                double r  = width / 2.0;
                double dx = mouseX - cx;
                double dy = mouseY - cy;
                yield dx * dx + dy * dy <= r * r;
            }
        };
    }
}
