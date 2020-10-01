package org.sba.mower;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.sba.mower.core.Coordinates;
import org.sba.mower.core.MowerInstruction;
import org.sba.mower.core.MowerPosition;
import org.sba.mower.message.*;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Mower extends AbstractBehavior<MowerMessage> {

    String id;
    boolean waitingForMovementAck = false;

    Coordinates lawnUpperRightCell;
    List<MowerInstruction> instructions;
    List<ActorRef<MowerMessage>> otherMowers;
    ActorRef<GardenerMessage> gardener;

    MowerPosition position;
    Iterator<MowerInstruction> instructionsIterator;

    // In milliseconds
    Long waitDuration = 300L;
    Long forwardDelay = 100L;
    Long rotationDelay = 200L;

    public Mower(ActorContext<MowerMessage> context) {
        super(context);

        Random random = new Random();
        this.id = "mower-" + random.ints(1, 100).findFirst().getAsInt();
    }

    public static Behavior<MowerMessage> create() {
        return Behaviors.setup(Mower::new);
    }

    @Override
    public Receive<MowerMessage> createReceive() {
        return newReceiveBuilder()
                // Messages from Gardener
                .onMessage(IdentifyCommand.class, this::handleIdentifyCommand)
                .onMessage(Registration.class, this::handleRegistration)
                .onMessage(StartCommand.class, this::handleStartCommand)
                .onMessage(StopCommand.class, this::handleStopCommand)

                // Messages from other mowers
                .onMessage(CellClaim.class, this::handleCellClaim)
                .onMessage(CellOccupation.class, this::handleCellOccupation)
                .build();
    }

    private Behavior<MowerMessage> handleIdentifyCommand(IdentifyCommand message) {
        getContext().getLog().info("[{}] Gardener wants me to identify myself", this.id);
        this.gardener = message.gardener;
        this.lawnUpperRightCell = message.upperRightCellCoordinates;
        this.position = message.input.initialPosition;
        this.instructions = message.input.instructions;
        this.gardener.tell(new IdentityMessage(this.id, getContext().getSelf()));
        return this;
    }

    private Behavior<MowerMessage> handleRegistration(Registration message) {
        getContext().getLog().info("[{}] Gardener sent me the list of mowers", this.id);
        this.otherMowers = message.mowers;
        return this;
    }

    private Behavior<MowerMessage> handleStartCommand(StartCommand message) {
        getContext().getLog().info("[{}] Gardener just ordered to start", this.id);
        message.gardener.tell(new CourseStarted(this.id));

        this.instructionsIterator = this.instructions.iterator();

        // Start with the first instruction
        getContext().getLog().info("[{}] Start with the 1st instruction", this.id);
        handleInstruction();
        return this;
    }

    private Behavior<MowerMessage> handleStopCommand(StopCommand message) {
        getContext().getLog().info("[{}] Gardener just ordered to stop", this.id);
        return this;
    }

    private Behavior<MowerMessage> handleCellClaim(CellClaim message) {
        getContext().getLog().info("[{}] {} claims cell {}", this.id, message.requesterId, message.cell);
        CellOccupation answer;
        if (this.position.coordinates != message.cell) {
            answer = new UnOccupiedCell(this.id, message.cell, getContext().getSelf());
        } else {
            answer = new OccupiedCell(this.id, message.cell, getContext().getSelf());
        }
        message.requester.tell(answer);
        return this;
    }

    private Behavior<MowerMessage> handleCellOccupation(CellOccupation message) {
        if (message instanceof OccupiedCell) {
            var m = ((OccupiedCell) message);
            getContext().getLog().info("[{}] {} replied it occupies {}. Discard the move", this.id, m.mowerId, m.cell);
            this.waitingForMovementAck = false;

        } else if (message instanceof UnOccupiedCell) {
            var m = ((UnOccupiedCell) message);
            getContext().getLog().info("[{}] {} replied it DOES NOT occupy {}. Path is clear", this.id, m.mowerId, m.cell);
            this.waitingForMovementAck = false;
            move(m.cell);
        }
        handleInstruction();
        return this;
    }


    private void handleInstruction() {
        if (waitingForMovementAck) {
            try {
                TimeUnit.MILLISECONDS.sleep(waitDuration);
            } catch (InterruptedException e) {
                getContext().getLog().info(e.getMessage());
            }
            return;
        }

        if (instructionsIterator.hasNext()) {
            var instruction = instructionsIterator.next();
            switch (instruction) {
                case FORWARD -> {
                    var nextCell = position.getNextCell();
                    if (nextCell.liesOffField(this.lawnUpperRightCell)) {
                        getContext().getLog().info("[{}] Discard FORWARD instruction since we would get off the lawn", this.id);
                        handleInstruction();
                    } else {
                        requestCell(nextCell);
                    }
                }
                default -> {
                    rotate(instruction);
                    handleInstruction();
                }
            }

        } else {
            getContext().getLog().info("[{}] Run out of instructions. Job completed", this.id);
            gardener.tell(new CourseCompleted(this.id, this.position));
        }
    }

    private void requestCell(Coordinates cell) {

        // Switch to "waiting confirmations" state
        this.waitingForMovementAck = true;

        // Query all the other mowers
        otherMowers.forEach(mowerRef -> {
            getContext().getLog().info("[{}] Querying {} to request {}", this.id, mowerRef, cell);
            mowerRef.tell(new CellClaim(id, cell, getContext().getSelf()));

        });
    }

    private void move(Coordinates cell) {
        getContext().getLog().info("[{}] Moving to {}", this.id, cell);
        try {
            TimeUnit.MILLISECONDS.sleep(forwardDelay);
        } catch (InterruptedException e) {
            getContext().getLog().info(e.getMessage());
        }
        position.move(cell);
        getContext().getLog().info("[{}] now on {}", this.id, cell);
    }

    private void rotate(MowerInstruction rotationInstruction) {
        getContext().getLog().info("[{}] rotating {}", this.id, rotationInstruction);
        try {
            TimeUnit.MILLISECONDS.sleep(rotationDelay);
        } catch (InterruptedException e) {
            getContext().getLog().info(e.getMessage());
        }
        switch (rotationInstruction) {
            case RIGHT -> this.position.rotateRight();
            case LEFT -> this.position.rotateLeft();
        }
        getContext().getLog().info("[{}] now heading {}", this.id, this.position.orientation);
    }
}
