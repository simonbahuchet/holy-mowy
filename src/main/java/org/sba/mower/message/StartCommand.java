package org.sba.mower.message;

import akka.actor.typed.ActorRef;

public class StartCommand implements MowerMessage {
    public final ActorRef<GardenerMessage> gardener;

    public StartCommand(ActorRef<GardenerMessage> gardener) {
        this.gardener = gardener;
    }
}
