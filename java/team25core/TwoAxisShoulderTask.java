package team25core;

import com.qualcomm.hardware.hitechnic.HiTechnicNxtServoController;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

//import opmodes.HisaishiCalibration;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.RobotTask;

/**
 * A relic recovery specific task for moving a 2 servo, 2 axis, shoulder to displace
 * a jewel.  This task manages the necessary delays for servo movements and ensures that
 * the shoulder is in the stowed position prior to sending it's only event, DONE.
 *
 * Users should instantiate the task, call init() to ensure the shoulder is stowed.  This
 * should be done in the robot init.  And then in start add the task the task queue.
 */

public class TwoAxisShoulderTask extends RobotTask {

    public enum EventKind {
        DONE,
    }

    public class TwoAxisShoulderEvent extends RobotEvent {

        public EventKind kind;

        public TwoAxisShoulderEvent(RobotTask task, EventKind kind)
        {
            super(task);
            this.kind = kind;
        }

        public String toString()
        {
            return kind.toString();
        }
    }

    private enum ShoulderState {
        STOWED,
        DEPLOYED,
        PUSHED,
        DONE,
    }

    public enum ShoulderDirection {
        NONE,
        FORWARD,
        BACKWARD,
    }

    private static final String SHOULDER_TAG = "ShoulderTask";

    /**
     * TODO: Add real constants here.
     */

    private static final double Y_AXIS_STOWED = 0;
    private static final double Y_AXIS_DEPLOYED = 0;
    private static final double X_AXIS_NEUTRAL = 0;
    private static final double X_AXIS_FORWARD = 0;
    private static final double X_AXIS_BACK = 0;

/*
    As of December 14th.
    public static final double JEWEL_Y_AXIS_DEPLOYED = (double)(121/256);    // y-axis
    public static final double JEWEL_X_AXIS_NEUTRAL = (double)(62/256);    // x-axis
    public static final double JEWEL_Y_AXIS_STOWED = (double)(0/256);      // y-axis
    public static final double JEWEL_X_AXIS_FORWARD = (double)(25/256);    // x-axis
    public static final double JEWEL_X_AXIS_BACK = (double)(119/256);      // x-axis
 */

    private static final int SERVO_DELAY = 750;

    private Servo xAxis;
    private Servo yAxis;
    private ShoulderState state;
    private ShoulderDirection direction;
    private ElapsedTime timer;

    public TwoAxisShoulderTask(Robot robot, Servo xAxis, Servo yAxis)
    {
        super(robot);
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    public void init()
    {
        state = ShoulderState.STOWED;
        direction = ShoulderDirection.NONE;
        xAxis.setPosition(X_AXIS_NEUTRAL);
        yAxis.setPosition(Y_AXIS_STOWED);
    }

    public void setDirection(ShoulderDirection direction)
    {
        this.direction = direction;
    }

    @Override
    public void start()
    {
        timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    }

    @Override
    public void stop()
    {
        xAxis.setPosition(X_AXIS_NEUTRAL);
        yAxis.setPosition(Y_AXIS_STOWED);
        robot.removeTask(this);
    }

    private boolean delay()
    {
        if (timer.time() > SERVO_DELAY) {
            timer.reset();
            return false;
        } else {
            return true;
        }
    }

    private void push()
    {
        switch (direction) {
            case NONE:
                break;
            case FORWARD:
                xAxis.setPosition(X_AXIS_FORWARD);
                break;
            case BACKWARD:
                xAxis.setPosition(X_AXIS_BACK);
                break;
        }
    }

    @Override
    public boolean timeslice()
    {
        if (direction == ShoulderDirection.NONE) {
            RobotLog.ii(SHOULDER_TAG, "No direction, doing nothing");
            robot.queueEvent(new TwoAxisShoulderEvent(this, EventKind.DONE));
            return true;
        }

        switch (state) {
            case STOWED:
                RobotLog.ii(SHOULDER_TAG, "Stowed");
                yAxis.setPosition(Y_AXIS_DEPLOYED);
                state = ShoulderState.DEPLOYED;
                break;
            case DEPLOYED:
                if (delay()) {
                    return false;
                } else {
                    RobotLog.ii(SHOULDER_TAG, "Deployed");
                    push();
                    state = ShoulderState.PUSHED;
                }
                break;
            case PUSHED:
                if (delay()) {
                    return false;
                } else {
                    RobotLog.ii(SHOULDER_TAG, "Pushed");
                    yAxis.setPosition(Y_AXIS_STOWED);
                    xAxis.setPosition(X_AXIS_NEUTRAL);
                    state = ShoulderState.DONE;
                }
                break;
            case DONE:
                if (delay()) {
                    return false;
                } else {
                    RobotLog.ii(SHOULDER_TAG, "Done");
                    state = ShoulderState.STOWED;
                    robot.queueEvent(new TwoAxisShoulderEvent(this, EventKind.DONE));
                    return true;
                }
            default:
                break;
        }

        return false;
    }
}
