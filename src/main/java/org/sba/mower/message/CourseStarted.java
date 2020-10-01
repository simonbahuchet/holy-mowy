package org.sba.mower.message;

public class CourseStarted implements GardenerMessage {
    public final String mowerId;

    public CourseStarted(String mowerId) {
        this.mowerId = mowerId;
    }
}
