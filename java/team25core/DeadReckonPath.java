package team25core;

/*
 * FTC Team 25: cmacfarl, August 21, 2015
 */

import java.util.LinkedList;
import java.util.Queue;

public class DeadReckonPath {

    /*
     * FIXME: Add a pivot turn segment type
     */
    public enum SegmentType {
        STRAIGHT,
        TURN,
        SIDEWAYS,
        BACK_LEFT_DIAGONAL,
        BACK_RIGHT_DIAGONAL,
    }

    public enum SegmentState {
        INITIALIZE,
        ENCODER_RESET,
        SET_TARGET,
        CONSUME_SEGMENT,
        ENCODER_TARGET,
        STOP_MOTORS,
        WAIT,
        DONE,
    }

    protected enum TurnKind {
        TURN_USING_ENCODERS,
        TURN_USING_GYRO,
    }

    public Queue<Segment> segments;
    protected Segment currSegment;
    protected boolean setup;
    protected int target;
    protected PersistentTelemetryTask ptt;

    public class Segment {

        public SegmentType type;
        public SegmentState state;
        public double distance;
        public double speed;

        Segment(SegmentType type, double distance, double speed)
        {
            this.state = SegmentState.INITIALIZE;
            this.distance = distance;
            this.type = type;
            this.speed = speed;
        }
    }

    public DeadReckonPath()
    {
        this.currSegment = null;
        segments = new LinkedList<Segment>();
    }

    public int numSegments()
    {
        return segments.size();
    }

    public void addSegment(SegmentType type, double distance, double speed)
    {
        segments.add(new Segment(type, distance, speed));
    }

    public void nextSegment()
    {
        segments.remove();
    }

    public Segment getCurrentSegment()
    {
        return segments.peek();
    }

    public void stop()
    {
        /*
         * Remove all remaining segments.
         */
        segments.clear();
    }
}

