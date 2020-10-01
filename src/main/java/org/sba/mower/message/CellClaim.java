package org.sba.mower.message;

import akka.actor.typed.ActorRef;
import org.sba.mower.core.Coordinates;

public class CellClaim implements MowerMessage {

    public final String requesterId;
    public final Coordinates cell;
    public final ActorRef<MowerMessage> requester;

    public CellClaim(String requesterId,
                     Coordinates cell,
                     ActorRef<MowerMessage> requester
    ) {
        this.requesterId = requesterId;
        this.cell = cell;
        this.requester = requester;
    }
}
