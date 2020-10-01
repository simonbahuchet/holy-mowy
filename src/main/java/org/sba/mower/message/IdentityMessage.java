package org.sba.mower.message;

import akka.actor.typed.ActorRef;

public class IdentityMessage implements GardenerMessage {
    public final String mowerId;
    public final ActorRef<MowerMessage> mowerActorRef;

    public IdentityMessage(String mowerId, ActorRef<MowerMessage> mowerActorRef) {
        this.mowerId = mowerId;
        this.mowerActorRef = mowerActorRef;
    }
}
