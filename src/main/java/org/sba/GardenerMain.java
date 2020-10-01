package org.sba;

import akka.actor.typed.ActorSystem;
import org.sba.mower.Gardener;
import org.sba.mower.core.Coordinates;
import org.sba.mower.core.MowerPosition;
import org.sba.mower.input.GardenerInput;
import org.sba.mower.input.MowerInput;
import org.sba.mower.message.GardenerMessage;
import org.sba.mower.message.MowCommand;

import java.io.IOException;
import java.util.List;

import static org.sba.mower.core.MowerInstruction.*;
import static org.sba.mower.core.Orientation.EAST;
import static org.sba.mower.core.Orientation.NORTH;

public class GardenerMain {
    public static void main(String[] args) {

        final ActorSystem<GardenerMessage> gardener = ActorSystem.create(Gardener.create(), "HolyMowy");

        gardener.tell(new MowCommand(mockInputNominal()));

        try {
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ignored) {

        } finally {
            gardener.terminate();
        }
    }

    static GardenerInput mockInputNominal() {
        var mowerInput1 = new MowerInput(
                new MowerPosition(new Coordinates(1, 2), NORTH),
                List.of(LEFT, FORWARD, LEFT, FORWARD, LEFT, FORWARD, LEFT, FORWARD, FORWARD)
        );
        var mowerInput2 = new MowerInput(
                new MowerPosition(new Coordinates(3, 3), EAST),
                List.of(FORWARD, FORWARD, RIGHT, FORWARD, FORWARD, RIGHT, FORWARD, RIGHT, RIGHT, FORWARD)
        );
        return new GardenerInput(
                new Coordinates(5, 5),
                List.of(mowerInput1, mowerInput2)
        );
    }

    static GardenerInput mockInputOffField() {
        var mowerInput1 = new MowerInput(
                new MowerPosition(new Coordinates(0, 0), NORTH),
                List.of(LEFT, LEFT, LEFT, FORWARD)
        );
        var mowerInput2 = new MowerInput(
                new MowerPosition(new Coordinates(1, 0), EAST),
                List.of(RIGHT)
        );
        return new GardenerInput(
                new Coordinates(0, 0),
                List.of(mowerInput1, mowerInput2)
        );
    }
}
