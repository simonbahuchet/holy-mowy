package org.sba.mower.message;

import akka.actor.typed.ActorRef;

import java.util.List;

public class Registration implements MowerMessage {
    public final List<ActorRef<MowerMessage>> mowers;

    public Registration(List<ActorRef<MowerMessage>> mowers) {
        this.mowers = mowers;
    }
}
