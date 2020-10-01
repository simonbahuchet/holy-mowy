package org.sba.mower;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.sba.mower.core.MowerPosition;
import org.sba.mower.input.GardenerInput;
import org.sba.mower.input.MowerInput;
import org.sba.mower.message.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Gardener extends AbstractBehavior<GardenerMessage> {

    private List<ActorRef<MowerMessage>> mowers;
    private Map<String, ActorRef<MowerMessage>> idToActor = new HashMap<>();

    private ActorContext<GardenerMessage> context;

    private List<MowerPosition> mowerPositions = new ArrayList<>();

    public Gardener(ActorContext<GardenerMessage> context) {
        super(context);
        this.context = context;
    }

    public static Behavior<GardenerMessage> create() {
        return Behaviors.setup(Gardener::new);
    }

    @Override
    public Receive<GardenerMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(MowCommand.class, this::handleMowCommand)
                .onMessage(IdentityMessage.class, this::handleMowerIdentity)
                .onMessage(CourseStarted.class, this::handleMowerStarted)
                .onMessage(CourseCompleted.class, this::handleMowerStopped)
                .build();
    }

    private Behavior<GardenerMessage> handleMowCommand(MowCommand message) {
        getContext().getLog().info("[Gardener] Let's mow this lawn");
        this.mow(message.inputs);
        return this;
    }

    private Behavior<GardenerMessage> handleMowerIdentity(IdentityMessage message) {
        getContext().getLog().info("[Gardener] {} has identified itself", message.mowerId);
        idToActor.put(message.mowerId, message.mowerActorRef);

        if (idToActor.size() == this.mowers.size()) {
            this.mowers.forEach(mower -> {
                mower.tell(new Registration(mowers.stream()
                        .filter(actorRef -> actorRef != mower)
                        .collect(Collectors.toList()))
                );
            });

            this.mowers.forEach(mowerActorRef -> {
                mowerActorRef.tell(new StartCommand(getContext().getSelf()));
            });
        }

        return this;
    }

    private Behavior<GardenerMessage> handleMowerStarted(CourseStarted message) {
        getContext().getLog().info("[Gardener] {} just started his journey", message.mowerId);
        return this;
    }

    private Behavior<GardenerMessage> handleMowerStopped(CourseCompleted message) {
        getContext().getLog().info("[Gardener] {} just finished at {}", message.mowerId(), message.position());
        this.mowerPositions.add(message.position());
        return this;
    }

    void mow(GardenerInput inputs) {

        //create actors
        int i = 0;
        List<ActorRef<MowerMessage>> actors = new ArrayList<>();
        for (MowerInput mowerInput : inputs.mowerInputs) {
            ActorRef<MowerMessage> spawn = this.context.spawn(Mower.create(), "mower-" + i++);
            actors.add(spawn);
            spawn.tell(new IdentifyCommand(inputs.upperRightCellCoordinates, mowerInput, getContext().getSelf()));
        }
        mowers = actors;
    }
}
