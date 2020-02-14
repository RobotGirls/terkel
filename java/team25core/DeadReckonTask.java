/*
 * Copyright (c) September 2017 FTC Teams 25/5218
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted (subject to the limitations in the disclaimer below) provided that
 *  the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this list
 *  of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice, this
 *  list of conditions and the following disclaimer in the documentation and/or
 *  other materials provided with the distribution.
 *
 *  Neither the name of FTC Teams 25/5218 nor the names of their contributors may be used to
 *  endorse or promote products derived from this software without specific prior
 *  written permission.
 *
 *  NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 *  LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 *  TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package team25core;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import static team25core.DeadReckonPath.SegmentState.STOP_MOTORS;
import static team25core.DeadReckonPath.SegmentState.WAIT;
import static team25core.DeadReckonPath.SegmentType.LEFT_DIAGONAL;
import static team25core.DeadReckonPath.SegmentType.RIGHT_DIAGONAL;
import static team25core.DeadReckonPath.SegmentType.SIDEWAYS;
import static team25core.DeadReckonPath.SegmentType.STRAIGHT;
import static team25core.DeadReckonPath.SegmentType.PAUSE;
import static team25core.DeadReckonPath.SegmentType.TURN;

public class DeadReckonTask extends RobotTask {

    private final static String TAG = "DeadREckonTask";

    public enum EventKind {
        SEGMENT_DONE,
        SENSOR_SATISFIED,
        BOTH_SENSORS_SATISFIED,
        RIGHT_SENSOR_SATISFIED,
        LEFT_SENSOR_SATISFIED,
        PATH_DONE,
        PAUSING,
    }

    protected enum DoneReason {
        ENCODER_REACHED,
        SENSOR_SATISFIED,
        BOTH_SENSORS_SATISFIED,
        RIGHT_SENSOR_SATISFIED,
        LEFT_SENSOR_SATISFIED,
    };

    public class DeadReckonEvent extends RobotEvent {

        public EventKind kind;
        public int segment_num;

        public DeadReckonEvent(RobotTask task, EventKind k, int segment_num)
        {
            super(task);
            kind = k;
            this.segment_num = segment_num;
        }

        @Override
        public String toString()
        {
            return (super.toString() + "DeadReckonPath Event " + kind + " " + segment_num);
        }
    }

    protected class LimitSwitchListener implements RobotEventListener {
        @Override
        public void handleEvent(RobotEvent event)
        {
            DeadReckonPath.Segment segment;

            segment = dr.getCurrentSegment();
            if (segment != null) {
                segment.state = DeadReckonPath.SegmentState.DONE;
            }
        }
    }

    protected enum SensorsInstalled {
        SENSORS_NONE,
        SENSORS_ONE,
        SENSORS_TWO,
    };

    protected SensorsInstalled sensorsInstalled;
    protected DeadReckonPath dr;
    protected int num;
    protected boolean waiting;
    protected SensorCriteria leftCriteria;
    protected SensorCriteria rightCriteria;
    protected DoneReason reason;
    protected Drivetrain drivetrain;
    protected ElapsedTime timer;
    protected boolean isStrafing;
    protected boolean isStraight;

    SingleShotTimerTask sst;
    int waitState = 0;

    public DeadReckonTask(Robot robot, DeadReckonPath dr, Drivetrain drivetrain)
    {
        super(robot);

        this.sensorsInstalled = SensorsInstalled.SENSORS_NONE;
        this.num = 0;
        this.dr = dr;
        this.waiting = false;
        this.waitState = 0;
        this.leftCriteria = null;
        this.rightCriteria = null;
        this.drivetrain = drivetrain;
    }

    public DeadReckonTask(Robot robot, DeadReckonPath dr, Drivetrain drivetrain, SensorCriteria criteria)
    {
        super(robot);

        this.sensorsInstalled = SensorsInstalled.SENSORS_ONE;
        this.num = 0;
        this.dr = dr;
        this.waiting = false;
        this.waitState = 0;
        this.leftCriteria = criteria;
        this.rightCriteria = null;
        this.drivetrain = drivetrain;
    }

    public DeadReckonTask(Robot robot, DeadReckonPath dr, Drivetrain drivetrain, SensorCriteria leftCriteria, SensorCriteria rightCriteria)
    {
        super(robot);

        this.sensorsInstalled = SensorsInstalled.SENSORS_TWO;
        this.num = 0;
        this.dr = dr;
        this.waiting = false;
        this.waitState = 0;
        this.leftCriteria = leftCriteria;
        this.rightCriteria = rightCriteria;
        this.drivetrain = drivetrain;
    }

    @Override
    public void start()
    {
        RobotLog.i(TAG, "Start");
    }

    @Override
    public void stop()
    {
        RobotLog.i(TAG, "Stop");
        drivetrain.stop();
    }

    public void setTarget(DeadReckonPath.Segment segment)
    {
        switch (segment.type) {
        case STRAIGHT:
        case RIGHT_DIAGONAL:
        case LEFT_DIAGONAL:
        case SIDEWAYS:
            drivetrain.setTargetInches(segment.distance);
            break;
        case TURN:
            drivetrain.setTargetRotation(segment.distance);
            break;
        }
    }

    public boolean hitTarget()
    {
        if (drivetrain.isBusy()) {
            return false;
        } else {
            return true;
        }
    }

    public void disableSensors() {
        sensorsInstalled = SensorsInstalled.SENSORS_NONE;
    }

    protected void setupWaitState(DeadReckonPath.Segment segment, boolean sendEvent)
    {
        if (sendEvent == true) {
            robot.queueEvent(new DeadReckonEvent(this, EventKind.PAUSING, num));
        }
        segment.state = WAIT;
        timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    }

    @Override
    public boolean timeslice()
    {
        DeadReckonPath.Segment segment;

        /*
         * Get current segment
         */
        segment = dr.getCurrentSegment();

        if (segment == null) {
            if (reason == DoneReason.ENCODER_REACHED) {
                RobotLog.e("251 Dead reckon path done");
                robot.queueEvent(new DeadReckonEvent(this, EventKind.PATH_DONE, num));
            } else if (reason == DoneReason.SENSOR_SATISFIED) {
                RobotLog.e("251 Dead reckon sensor criteria satisfied");
                robot.queueEvent(new DeadReckonEvent(this, EventKind.SENSOR_SATISFIED, num));
            } else if (reason == DoneReason.BOTH_SENSORS_SATISFIED) {
                RobotLog.e("251 Dead reckon both sensor criteria satisfied");
                robot.queueEvent(new DeadReckonEvent(this, EventKind.BOTH_SENSORS_SATISFIED, num));
            } else if (reason == DoneReason.LEFT_SENSOR_SATISFIED) {
                RobotLog.e("251 Dead reckon left sensor criteria segment %d satisfied", num);
                robot.queueEvent(new DeadReckonEvent(this, EventKind.LEFT_SENSOR_SATISFIED, num));
            } else if (reason == DoneReason.RIGHT_SENSOR_SATISFIED) {
                RobotLog.e("251 Dead reckon right sensor criteria segment %d satisfied", num);
                robot.queueEvent(new DeadReckonEvent(this, EventKind.RIGHT_SENSOR_SATISFIED, num));
            } else {
                RobotLog.e("Oops, unknown reason for dead reckon stop");
                robot.queueEvent(new DeadReckonEvent(this, EventKind.PATH_DONE, num));
            }
            /*
             * Make sure it's stopped.
             */
            RobotLog.i("251 Done with path, stopping all");
            dr.stop();
            drivetrain.stop();
            return true;
        } else if (segment.state == DeadReckonPath.SegmentState.DONE) {
            if (reason == DoneReason.ENCODER_REACHED) {
                RobotLog.e("251 Dead reckon segment %d done", num);
                robot.queueEvent(new DeadReckonEvent(this, EventKind.SEGMENT_DONE, num));
            } else if (reason == DoneReason.SENSOR_SATISFIED) {
                RobotLog.e("251 Dead reckon sensor criteria segment %d satisfied", num);
                robot.queueEvent(new DeadReckonEvent(this, EventKind.SENSOR_SATISFIED, num));
            } else if (reason == DoneReason.BOTH_SENSORS_SATISFIED) {
                RobotLog.e("251 Dead reckon sensor criteria segment %d satisfied", num);
                robot.queueEvent(new DeadReckonEvent(this, EventKind.SENSOR_SATISFIED, num));
            } else if (reason == DoneReason.LEFT_SENSOR_SATISFIED) {
                RobotLog.e("251 Dead reckon left sensor criteria segment %d satisfied", num);
                robot.queueEvent(new DeadReckonEvent(this, EventKind.LEFT_SENSOR_SATISFIED, num));
            } else if (reason == DoneReason.RIGHT_SENSOR_SATISFIED) {
                RobotLog.e("251 Dead reckon right sensor criteria segment %d satisfied", num);
                robot.queueEvent(new DeadReckonEvent(this, EventKind.RIGHT_SENSOR_SATISFIED, num));
            } else {
                RobotLog.e("251 Dead reckon segment %d done - no reason", num);
            }
        }

        switch (segment.state) {
        case INITIALIZE:
            isStrafing = false;
            isStraight = false;
            drivetrain.resetEncoders();
            segment.state = DeadReckonPath.SegmentState.ENCODER_RESET;
            break;
        case ENCODER_RESET:
            drivetrain.resetEncoders();
            segment.state = DeadReckonPath.SegmentState.SET_TARGET;
            break;
        case SET_TARGET:
            drivetrain.encodersOn();
            setTarget(segment);
            segment.state = DeadReckonPath.SegmentState.CONSUME_SEGMENT;
            break;
        case CONSUME_SEGMENT:
            if (segment.type == PAUSE) {
                setupWaitState(segment, true);
                break;
            }

            if (segment.type == STRAIGHT) {
                isStraight = true;
                drivetrain.straight(segment.speed);
            } else if (segment.type == SIDEWAYS) {
                isStrafing = true;
                robot.addTask(new MotorRampTask(robot, MotorRampTask.RampDirection.RAMP_UP, segment.speed) {
                    @Override
                    public void run(double speed) { drivetrain.strafe(speed); }
                });
            } else if (segment.type == LEFT_DIAGONAL) {
                drivetrain.leftDiagonal(segment.speed);
            } else if (segment.type == RIGHT_DIAGONAL) {
                drivetrain.rightDiagonal(segment.speed);
            } else {
                drivetrain.turn(segment.speed);
            }
            segment.state = DeadReckonPath.SegmentState.ENCODER_TARGET;
            break;
        case ENCODER_TARGET:
            if ((sensorsInstalled == SensorsInstalled.SENSORS_ONE) && (leftCriteria.satisfied())) {
                RobotLog.i("5218 Solo sensor criteria satisfied");
                segment.state = STOP_MOTORS;
                reason = DoneReason.SENSOR_SATISFIED;
            } else if (sensorsInstalled == SensorsInstalled.SENSORS_ONE) {
                if ((leftCriteria != null) && leftCriteria.satisfied()) {
                    RobotLog.i("5218 Left criteria satisfied");
                    segment.state = STOP_MOTORS;
                    reason = DoneReason.LEFT_SENSOR_SATISFIED;
                } else if ((rightCriteria != null) && rightCriteria.satisfied()) {
                    RobotLog.i("5218 Right criteria satisfied");
                    segment.state = STOP_MOTORS;
                    reason = DoneReason.RIGHT_SENSOR_SATISFIED;
                }
            } else if (sensorsInstalled == SensorsInstalled.SENSORS_TWO) {
                if (leftCriteria.satisfied() && rightCriteria.satisfied()) {
                    RobotLog.i("5218 Left and right criteria satisfied");
                    segment.state = STOP_MOTORS;
                    reason = DoneReason.BOTH_SENSORS_SATISFIED;
                }
            } else if (hitTarget()) {
                segment.state = STOP_MOTORS;
                reason = DoneReason.ENCODER_REACHED;
            }
            break;
        case STOP_MOTORS:
            if (isStrafing == true) {
                robot.addTask(new MotorRampTask(robot, MotorRampTask.RampDirection.RAMP_DOWN, segment.speed) {
                    @Override
                    public void run(double speed) { drivetrain.strafe(speed); }
                });
            } else if (isStraight == true) {
                robot.addTask(new MotorRampTask(robot, MotorRampTask.RampDirection.RAMP_DOWN, segment.speed) {
                    @Override
                    public void run(double speed) { drivetrain.straight(speed); }
                });
            } else {
                drivetrain.stop();
            }
            setupWaitState(segment, false);
            break;
        case WAIT:
            if (timer.time() >= segment.millisecond_pause) {
                segment.state = DeadReckonPath.SegmentState.DONE;
            }
            break;
        case DONE:
            num++;
            dr.nextSegment();
            segment.state = DeadReckonPath.SegmentState.INITIALIZE;
            break;
        }

        robot.telemetry.addData("Segment: ", num);
        robot.telemetry.addData("State: ", segment.state.toString());

        return false;
    }
}
