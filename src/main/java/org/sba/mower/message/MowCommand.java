package org.sba.mower.message;

import org.sba.mower.input.GardenerInput;

public class MowCommand implements GardenerMessage {
    public final GardenerInput inputs;

    public MowCommand(GardenerInput inputs) {
        this.inputs = inputs;
    }
}
