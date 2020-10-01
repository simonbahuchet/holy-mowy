package org.sba.mower.message;

import akka.actor.typed.ActorRef;
import org.sba.mower.core.Coordinates;
import org.sba.mower.input.MowerInput;

public class IdentifyCommand implements MowerMessage {
    public final MowerInput input;
    public final Coordinates upperRightCellCoordinates;
    public final ActorRef<GardenerMessage> gardener;

    public IdentifyCommand(Coordinates upperRightCellCoordinates,
                           MowerInput input,
                           ActorRef<GardenerMessage> gardener) {
        this.input = input;
        this.upperRightCellCoordinates = upperRightCellCoordinates;
        this.gardener = gardener;
    }
}
