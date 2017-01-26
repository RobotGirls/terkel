package team25core;

/*
 * FTC Team 25: cmacfarl, January 23, 2017
 */

import com.qualcomm.robotcore.util.RobotLog;

import java.util.List;

import static team25core.AlignWithWhiteLineTask.AlignmentState.BACK_OFF_RIGHT;
import static team25core.AlignWithWhiteLineTask.AlignmentState.DONE;

public class AlignWithWhiteLineTask extends RobotTask {

    public enum EventKind {
        ALIGNED,
        GOOD_ENOUGH,
        ABORTED,
    }

    public class AlignWithWhiteLineEvent extends RobotEvent {

        public EventKind kind;

        public AlignWithWhiteLineEvent(RobotTask task, EventKind k)
        {
            super(task);
            kind = k;
        }

        @Override
        public String toString()
        {
            return (super.toString() + "AlignWithWhiteLine Event " + kind);
        }
    }

    public enum AlignmentState {
        WILD_ABANDON,           // Really fast, needs to use encoder data to enter LOOK state otherwise it will overshoot the white line
        LOOK,                   // Initial straight fast move to find the white line
        SCAN,                   // Slow straight move to find the white line
        BACK_OFF_LEFT,
        BACK_OFF_RIGHT,
        PIVOT_RIGHT_OVER_RIGHT,
        PIVOT_LEFT_OVER_LEFT,
        PIVOT_RIGHT_OVER_LEFT,
        PIVOT_LEFT_OVER_RIGHT,
        FINE_TUNE,
        FINE_TUNE_LEFT,
        FINE_TUNE_RIGHT,
        DONE,
    }

    AlignmentState state;
    Drivetrain drivetrain;
    SensorCriteria leftSeeBlack;
    SensorCriteria leftSeeWhite;
    SensorCriteria rightSeeBlack;
    SensorCriteria rightSeeWhite;

    private final static double FAST_SPEED   = 0.2;
    private final static double MEDIUM_SPEED = 0.06;
    private final static double SLOW_SPEED   = 0.03;
    private int pivotRightCycle = 0;
    private int pivotLeftCycle = 0;

    private final static String LOG_TAG = "EdgeFind ";

    public AlignWithWhiteLineTask(Robot robot, Drivetrain drivetrain, SensorCriteria leftSeeBlack, SensorCriteria leftSeeWhite,
                           SensorCriteria rightSeeBlack, SensorCriteria rightSeeWhite)
    {
        super(robot);

        this.state = AlignmentState.LOOK;
        this.drivetrain = drivetrain;
        this.leftSeeBlack = leftSeeBlack;
        this.leftSeeWhite = leftSeeWhite;
        this.rightSeeBlack = rightSeeBlack;
        this.rightSeeWhite = rightSeeWhite;
    }

    @Override
    public void start()
    {
        RobotLog.i(LOG_TAG + "================= START EDGE FIND ================== ");
    }

    @Override
    public void stop()
    {
        drivetrain.stop();
        robot.removeTask(this);
        RobotLog.i(LOG_TAG + "================= STOP EDGE FIND ================== ");
    }

    protected void setState(AlignmentState state)
    {
        RobotLog.i(LOG_TAG + "Entering alignment state " + state.toString());

        /*
         * A cheap attempt at cycle detection.
         */
        switch (state) {
        case LOOK:
        case SCAN:
            pivotRightCycle = 0;
            pivotLeftCycle = 0;
            break;
        case PIVOT_RIGHT_OVER_RIGHT:
            pivotRightCycle++;
            break;
        case PIVOT_LEFT_OVER_LEFT:
            pivotLeftCycle++;
            break;
        case PIVOT_RIGHT_OVER_LEFT:
        case PIVOT_LEFT_OVER_RIGHT:
        case DONE:
            break;
        }

        this.state = state;
    }

    @Override
    public boolean timeslice()
    {
        if ((pivotRightCycle > 8) || (pivotLeftCycle > 8)) {
            RobotLog.i(LOG_TAG + "Calling it good enough");
            robot.queueEvent(new AlignWithWhiteLineEvent(this, EventKind.GOOD_ENOUGH));
            return true;
        }

        switch (state) {
        case WILD_ABANDON:
            // Unimplemented.
        case LOOK:
            drivetrain.straight(FAST_SPEED);
            if (leftSeeWhite.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.BACK_OFF_LEFT);
            } else if (rightSeeWhite.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.BACK_OFF_RIGHT);
            }
            break;
        case BACK_OFF_LEFT:
            drivetrain.straight(-SLOW_SPEED);
            if (leftSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.PIVOT_LEFT_OVER_LEFT);
            }
            break;
        case BACK_OFF_RIGHT:
            drivetrain.straight(-SLOW_SPEED);
            if (rightSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.PIVOT_RIGHT_OVER_RIGHT);
            }
            break;
        case SCAN:
            drivetrain.straight(SLOW_SPEED);
            if (leftSeeWhite.satisfied() && rightSeeWhite.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.DONE);
            } else if (leftSeeWhite.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.PIVOT_LEFT_OVER_LEFT);
            } else if (rightSeeWhite.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.PIVOT_RIGHT_OVER_RIGHT);
            }
            break;
        case PIVOT_RIGHT_OVER_RIGHT:
            drivetrain.pivotTurn(Drivetrain.PivotSide.RIGHT_OVER_RIGHT, MEDIUM_SPEED);
            if (leftSeeWhite.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.PIVOT_RIGHT_OVER_LEFT);
            }  else if (rightSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.SCAN);
            }
            break;
        case PIVOT_LEFT_OVER_LEFT:
            drivetrain.pivotTurn(Drivetrain.PivotSide.LEFT_OVER_LEFT, MEDIUM_SPEED);
            if (rightSeeWhite.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.PIVOT_LEFT_OVER_RIGHT);
            } else if (leftSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.SCAN);
            }
            break;
        case PIVOT_RIGHT_OVER_LEFT:
            drivetrain.pivotTurn(Drivetrain.PivotSide.RIGHT_OVER_LEFT, SLOW_SPEED);
            if (leftSeeBlack.satisfied() && rightSeeBlack.satisfied()) {
                setState(AlignmentState.DONE);
            } else if (leftSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.PIVOT_RIGHT_OVER_RIGHT);
            } else if (rightSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.FINE_TUNE);
                // setState(AlignmentState.PIVOT_LEFT_OVER_RIGHT);
            }
            break;
        case PIVOT_LEFT_OVER_RIGHT:
            drivetrain.pivotTurn(Drivetrain.PivotSide.LEFT_OVER_RIGHT, SLOW_SPEED);
            if (leftSeeBlack.satisfied() && rightSeeBlack.satisfied()) {
                setState(AlignmentState.DONE);
            } else if (rightSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.PIVOT_LEFT_OVER_LEFT);
            } else if (leftSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.FINE_TUNE);
                // setState(AlignmentState.PIVOT_RIGHT_OVER_LEFT);
            }
            break;
        case FINE_TUNE:
            if (leftSeeBlack.satisfied() && rightSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.DONE);
            } else if (leftSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.FINE_TUNE_RIGHT);
            } else if (rightSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.FINE_TUNE_LEFT);
            } else {
                drivetrain.stop();
                setState(AlignmentState.DONE);
            }
            break;
        case FINE_TUNE_LEFT:
            drivetrain.pivotTurn(Drivetrain.PivotSide.LEFT_OVER_RIGHT, SLOW_SPEED);
            if (leftSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.DONE);
            }
            break;
        case FINE_TUNE_RIGHT:
            drivetrain.pivotTurn(Drivetrain.PivotSide.RIGHT_OVER_LEFT, SLOW_SPEED);
            if (rightSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.DONE);
            }
            break;
        case DONE:
            drivetrain.stop();
            robot.queueEvent(new AlignWithWhiteLineEvent(this, EventKind.ALIGNED));
            return true;
        }
        return false;
    }
}
