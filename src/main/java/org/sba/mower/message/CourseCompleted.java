package org.sba.mower.message;

import org.sba.mower.core.MowerPosition;

public record CourseCompleted(String mowerId, MowerPosition position) implements GardenerMessage {
}

//public class CourseCompleted implements GardenerMessage {
//    public final String mowerId;
//
//    public CourseCompleted(String mowerId) {
//        this.mowerId = mowerId;
//    }
//}
