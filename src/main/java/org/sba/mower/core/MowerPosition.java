package org.sba.mower.core;

import static org.sba.mower.core.Orientation.*;

public class MowerPosition {
    public Coordinates coordinates;
    public Orientation orientation;

    public MowerPosition(Coordinates coordinates, Orientation orientation) {
        this.coordinates = coordinates;
        this.orientation = orientation;
    }

    public void rotateRight() {
        this.orientation = switch (this.orientation) {
            case NORTH -> EAST;
            case SOUTH -> WEST;
            case WEST -> NORTH;
            case EAST -> SOUTH;
        };
    }

    public void rotateLeft() {
        this.orientation = switch (this.orientation) {
            case NORTH -> WEST;
            case SOUTH -> EAST;
            case WEST -> SOUTH;
            case EAST -> NORTH;
        };
    }

    public Coordinates getNextCell() {
        return switch (this.orientation) {
            case NORTH -> new Coordinates(this.coordinates.x(), this.coordinates.y() + 1);
            case SOUTH -> new Coordinates(this.coordinates.x(), this.coordinates.y() - 1);
            case WEST -> new Coordinates(this.coordinates.x() - 1, this.coordinates.y());
            case EAST -> new Coordinates(this.coordinates.x() + 1, this.coordinates.y());
        };
    }

    public void move(Coordinates cell) {
        if (cell != null && !cell.equals(getNextCell())) {
            throw new IllegalStateException("The order is not consistent");
        }
        this.coordinates = getNextCell();
    }

    @Override
    public String toString() {
        return coordinates.toString() + ", " + orientation.name();
    }
}
