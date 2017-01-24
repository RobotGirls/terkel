package team25core;

/*
 * FTC Team 25: cmacfarl, January 23, 2017
 */

import com.qualcomm.robotcore.util.RobotLog;

import java.util.List;

public class AlignWithWhiteLineTask extends RobotTask {

    public enum EventKind {
        ALIGNED,
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
        LOOK,                   // Initial straight fast move to find the white line
        SCAN,                   // Slow straight move to find the white line
        PIVOT_RIGHT_OVER_RIGHT,
        PIVOT_LEFT_OVER_LEFT,
        PIVOT_RIGHT_OVER_LEFT,
        PIVOT_LEFT_OVER_RIGHT,
        DONE,
    }

    AlignmentState state;
    Drivetrain drivetrain;
    SensorCriteria leftSeeBlack;
    SensorCriteria leftSeeWhite;
    SensorCriteria rightSeeBlack;
    SensorCriteria rightSeeWhite;

    private final static double FAST_SPEED   = 1.0;
    private final static double MEDIUM_SPEED = 0.3;
    private final static double SLOW_SPEED   = 0.1;

    private final static String LOG_TAG = "EdgeFind";

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
        RobotLog.i(LOG_TAG + " ================= START EDGE FIND ================== ");
    }

    @Override
    public void stop()
    {
        drivetrain.stop();
        RobotLog.i(LOG_TAG + " ================= STOP EDGE FIND ================== ");
    }

    protected void setState(AlignmentState state)
    {
        RobotLog.i(LOG_TAG + "Setting alignment state to " + state.toString());
        this.state = state;
    }

    @Override
    public boolean timeslice()
    {
        switch (state) {
        case LOOK:
            drivetrain.straight(FAST_SPEED);
            if (leftSeeWhite.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.PIVOT_LEFT_OVER_LEFT);
            } else if (rightSeeWhite.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.PIVOT_RIGHT_OVER_RIGHT);
            }
            break;
        case SCAN:
            drivetrain.straight(MEDIUM_SPEED);
            if (leftSeeWhite.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.PIVOT_LEFT_OVER_LEFT);
            } else if (rightSeeWhite.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.PIVOT_RIGHT_OVER_RIGHT);
            }
            break;
        case PIVOT_RIGHT_OVER_RIGHT:
            drivetrain.pivotTurn(Drivetrain.PivotSide.RIGHT, MEDIUM_SPEED);
            if (leftSeeWhite.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.PIVOT_RIGHT_OVER_LEFT);
            }  else if (rightSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.SCAN);
            }
            break;
        case PIVOT_LEFT_OVER_LEFT:
            drivetrain.pivotTurn(Drivetrain.PivotSide.LEFT, MEDIUM_SPEED);
            if (rightSeeWhite.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.PIVOT_LEFT_OVER_RIGHT);
            } else if (leftSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.SCAN);
            }
            break;
        case PIVOT_RIGHT_OVER_LEFT:
            drivetrain.pivotTurn(Drivetrain.PivotSide.LEFT, MEDIUM_SPEED);
            if (leftSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.PIVOT_RIGHT_OVER_RIGHT);
            } else if (rightSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.DONE);
            }
            break;
        case PIVOT_LEFT_OVER_RIGHT:
            drivetrain.pivotTurn(Drivetrain.PivotSide.RIGHT, MEDIUM_SPEED);
            if (rightSeeBlack.satisfied()) {
                drivetrain.stop();
                setState(AlignmentState.PIVOT_LEFT_OVER_LEFT);
            } else if (leftSeeBlack.satisfied()) {
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
