package org.sba.mower.core;

public record Coordinates(int x, int y) {
    public boolean liesOffField(Coordinates lawnUpperRightCell) {
        return x() < 0 || x() > lawnUpperRightCell.x() || y() < 0 || y() > lawnUpperRightCell.y();
    }
}

//public class Coordinates {
//    public final int x;
//    public final int y;
//
//    public Coordinates(int x, int y) {
//        this.x = x;
//        this.y = y;
//    }
//}
