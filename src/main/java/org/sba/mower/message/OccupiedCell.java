package org.sba.mower.message;

import akka.actor.typed.ActorRef;
import org.sba.mower.core.Coordinates;

public class OccupiedCell implements CellOccupation {
    public final String mowerId;
    public final Coordinates cell;
    public final ActorRef<MowerMessage> mowerActorRef;

    public OccupiedCell(String mowerId, Coordinates cell, ActorRef<MowerMessage> mowerActorRef) {
        this.mowerId = mowerId;
        this.cell = cell;
        this.mowerActorRef = mowerActorRef;
    }
}
