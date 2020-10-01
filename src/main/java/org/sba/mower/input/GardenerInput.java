package org.sba.mower.input;

import org.sba.mower.core.Coordinates;

import java.util.List;

public class GardenerInput {
    public Coordinates upperRightCellCoordinates;
    public List<MowerInput> mowerInputs;

    public GardenerInput(Coordinates upperRightCellCoordinates, List<MowerInput> mowerInputs) {
        this.upperRightCellCoordinates = upperRightCellCoordinates;
        this.mowerInputs = mowerInputs;
    }
}
