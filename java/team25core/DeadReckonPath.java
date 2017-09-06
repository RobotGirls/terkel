package team25core;

/*
 * FTC Team 25: cmacfarl, August 21, 2015
 */

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.RobotLog;

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
    protected int encoderTicksPerInch;
    protected double encoderTicksPerDegree;
    protected GyroSensor gyro;
    protected DcMotor masterMotor;
    protected Segment currSegment;
    protected Robot robot;
    protected boolean turning;
    protected boolean setup;
    protected int lastHeading;
    protected int target;
    protected TurnKind turnKind;
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

    public void setTarget()
    {
        switch (getCurrentSegment().type) {
        case STRAIGHT:
        case BACK_RIGHT_DIAGONAL:
        case BACK_LEFT_DIAGONAL:
        case SIDEWAYS:
            this.target = Math.abs((int)(getCurrentSegment().distance * encoderTicksPerInch));
            break;
        case TURN:
            this.target = Math.abs((int)(getCurrentSegment().distance * encoderTicksPerDegree));
            break;
        }
        ptt.addData("Target: ", this.target);
    }

    public int getTarget()
    {
        return target;
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

