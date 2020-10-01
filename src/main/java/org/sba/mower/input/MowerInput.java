package org.sba.mower.input;

import org.sba.mower.core.Coordinates;
import org.sba.mower.core.MowerInstruction;
import org.sba.mower.core.MowerPosition;
import org.sba.mower.core.Orientation;

import java.util.List;
import java.util.stream.Collectors;

import static org.sba.mower.core.MowerInstruction.*;
import static org.sba.mower.core.Orientation.*;

public class MowerInput {

    public MowerPosition initialPosition;
    public List<MowerInstruction> instructions;

    public MowerInput(MowerPosition initialPosition, List<MowerInstruction> instructions) {
        this.initialPosition = initialPosition;
        this.instructions = instructions;
    }

    public MowerInput(String position, String instructions) {
        this.initialPosition = convertPosition(position);
        this.instructions = instructions.chars().mapToObj(c -> (char) c)
                .map(character -> convertInstruction(character)).collect(Collectors.toList());
    }

    public static final String DELIMITER = " ";

    public static MowerPosition convertPosition(String line) {
        var items = line.split(DELIMITER);
        return new MowerPosition(
                convertCoordinates(items[0], items[1]),
                convertOrientation(items[2])
        );
    }

    public static Coordinates convertCoordinates(String line) {
        var items = line.split(DELIMITER);
        return convertCoordinates(items[0], items[1]);
    }

    public static Coordinates convertCoordinates(String first, String second) {
        try {
            var x = Integer.parseInt(first);
            var y = Integer.parseInt(second);
            return new Coordinates(x, y);
        } catch (NumberFormatException nfe) {
            return new Coordinates(0, 0);
        }
    }

    public static Orientation convertOrientation(String s) {
        return switch (s) {
            case "W" -> WEST;
            case "E" -> EAST;
            case "S" -> SOUTH;
            default -> NORTH;
        };
    }

    public static MowerInstruction convertInstruction(Character s) {
        return switch (s) {
            case 'R' -> RIGHT;
            case 'L' -> LEFT;
            default -> FORWARD;
        };
    }
}
