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

import static team25core.DeadReckonPath.SegmentState.STOP_MOTORS;
import static team25core.DeadReckonPath.SegmentState.WAIT;
import static team25core.DeadReckonPath.SegmentType.LEFT_DIAGONAL;
import static team25core.DeadReckonPath.SegmentType.PAUSE;
import static team25core.DeadReckonPath.SegmentType.RIGHT_DIAGONAL;
import static team25core.DeadReckonPath.SegmentType.SIDEWAYS;
import static team25core.DeadReckonPath.SegmentType.STRAIGHT;
import static team25core.DeadReckonPath.SegmentType.TURN;
import static team25core.DeadReckonPath.SegmentType.TURN_WITH_IMU;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Locale;

public class DeadReckonTaskWithIMUTurn extends RobotTask {

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

    // For IMU
    private BNO055IMU imu;
    private Telemetry.Item headingTlm;

    Telemetry myTelemetry;

    private Telemetry.Item secondAngleTlm;
    private Telemetry.Item thirdAngleTlm;

    String myHeadingTlm;

    protected double targetHeading = 0.0;
    protected boolean showHeading = true;


    private Orientation angles;
    private Acceleration gravity;

    Telemetry.Item imuStatusTlm;
    Telemetry.Item imuCalibTlm;
    Telemetry.Item imuHeadingTlm;
    Telemetry.Item imuYawRateTlm;
    Telemetry.Item imuRollTlm;
    Telemetry.Item imuPitchTlm;
    Telemetry.Item imuGravTlm;

    Telemetry.Item segmentTypeTlm;
    Telemetry.Item whereAmI;
    Telemetry.Item codeLocation;
    Telemetry.Item deltaHeadingTlm;
    Telemetry.Item hitTargetHeadingTlm;
    Telemetry.Item hitHeadingTlm;

    private double heading;
    private double yawRate;
    private double roll;
    private double pitch;

    private static final double YAW_MARGIN = 3;

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
    protected DrivetrainWithIMU drivetrain;
    protected ElapsedTime timer;
    protected boolean isStrafing;
    protected boolean isStraight;
    protected boolean smoothStart;
    protected boolean isUsingImuTurns = false;

    SingleShotTimerTask sst;
    int waitState = 0;

    public DeadReckonTaskWithIMUTurn(Robot robot, DeadReckonPath dr, DrivetrainWithIMU drivetrain)
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
        this.smoothStart = false;
    }
    public DeadReckonTaskWithIMUTurn(Robot robot, DeadReckonPath dr, DrivetrainWithIMU drivetrain, boolean smoothStart, boolean usingImuTurns)
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
        this.isUsingImuTurns = usingImuTurns;
        this.smoothStart = smoothStart;
    }
    public DeadReckonTaskWithIMUTurn(Robot robot, DeadReckonPath dr, DrivetrainWithIMU drivetrain, boolean smoothStart)
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
        this.smoothStart = smoothStart;
    }

    public DeadReckonTaskWithIMUTurn(Robot robot, DeadReckonPath dr, DrivetrainWithIMU drivetrain, SensorCriteria criteria)
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
        this.smoothStart = false;
    }

    public DeadReckonTaskWithIMUTurn(Robot robot, DeadReckonPath dr, DrivetrainWithIMU drivetrain, SensorCriteria leftCriteria, SensorCriteria rightCriteria)
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
        this.smoothStart = false;
    }


    public void initializeImu(BNO055IMU imu, double targetHeading, boolean showHeading, Telemetry.Item heading)
    {
        this.targetHeading = targetHeading;  // Think cardinal: negative is ccw, positive is cw.
        this.showHeading = showHeading;
        this.imu = imu;
        this.headingTlm = heading;

        // added this to get rid of error regarding telemetry
        // FIXME Commenting this out to test color sensor
//        this.headingTlm = robot.telemetry.addData("Current/target heading is: ", "none");
//        this.secondAngleTlm = robot.telemetry.addData("Second angle is: ", "none");
//        this.thirdAngleTlm = robot.telemetry.addData("Third angle is: ", "none");


        // Set up the parameters with which we will use our IMU. Note that integration
        // algorithm here just reports accelerations to the logcat log; it doesn't actually
        // provide positional information.
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        // cindy commented out the following
        // parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.loggingEnabled      = true;
        // cindy added
        parameters.useExternalCrystal   = true;
        parameters.loggingTag          = "IMU";

        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu.initialize(parameters);

        angles  = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        gravity = imu.getGravity();

        // telemetry.setMsTransmissionInterval(100);
    }

    String formatAngle(AngleUnit angleUnit, double angle) {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle));
    }

    String formatDegrees(double degrees){
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
    }


    public void initTelemetry(Telemetry.Item myImuCalibTlm, Telemetry.Item myImuGravTlm, Telemetry.Item myImuRollTlm,
                              Telemetry.Item myImuPitchTlm, Telemetry.Item myImuHeadingTlm,
                              Telemetry.Item myImuStatusTlm, Telemetry.Item myImuYawRateTlm,
                              Telemetry.Item myWhereAmI, Telemetry.Item mySegmentTypeTlm,
                              Telemetry.Item myCodeLocation, Telemetry.Item myDeltaHeadingTlm,
                              Telemetry.Item myHitTargetHeadingTlm, Telemetry.Item myHitHeadingTlm) {
        this.imuCalibTlm = myImuCalibTlm;
        this.imuGravTlm = myImuGravTlm;
        this.imuRollTlm = myImuRollTlm;
        this.imuPitchTlm = myImuPitchTlm;
        this.imuHeadingTlm = myImuHeadingTlm;
        this.imuStatusTlm = myImuStatusTlm;
        this.imuYawRateTlm = myImuYawRateTlm;
        this.whereAmI = myWhereAmI;
        this.segmentTypeTlm = mySegmentTypeTlm;
        this.codeLocation = myCodeLocation;
        this.deltaHeadingTlm = myDeltaHeadingTlm;
        this.hitTargetHeadingTlm = myHitTargetHeadingTlm;
        this.hitHeadingTlm = myHitHeadingTlm;


        imuStatusTlm.setValue( imu.getSystemStatus().toString());
        imuCalibTlm.setValue( imu.getCalibrationStatus().toString());
        imuHeadingTlm.setValue( formatAngle(angles.angleUnit, angles.firstAngle));
        imuYawRateTlm.setValue( formatAngle(angles.angleUnit, angles.firstAngle));

        imuRollTlm.setValue(formatAngle(angles.angleUnit, angles.secondAngle));
        imuPitchTlm.setValue(formatAngle(angles.angleUnit, angles.thirdAngle));
        imuGravTlm.setValue(gravity.toString());
        // whereAmIGyro = telemetry.addData("whereAmIGyro", "initTelemetry" );
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

    public void useSmoothStart(boolean on)
    {
        smoothStart = on;
    }

    public void setTarget(DeadReckonPath.Segment segment)
    {
        whereAmI.setValue("setTarget Begin");
        segmentTypeTlm.setValue(segment.type);
        switch (segment.type) {
            case TURN_WITH_IMU:
                whereAmI.setValue("setTarget in TURN_WITH_IMU");
                drivetrain.setTargetYaw(segment.distance);
                targetHeading = segment.distance;
                break;
            case STRAIGHT:
            case RIGHT_DIAGONAL:
            case LEFT_DIAGONAL:
            case SIDEWAYS:
                whereAmI.setValue("setTarget in STRT,RT,LFT,SIDEWAYS");
                drivetrain.setTargetInches(segment.distance);
                break;
            case TURN:
                whereAmI.setValue("setTarget in TURN");
                drivetrain.setTargetRotation(segment.distance);
                break;
            default:
                whereAmI.setValue("setTarget in default");
        }
        whereAmI.setValue("setTarget End");
    }

    public boolean hitTarget()
    {
        if (drivetrain.isBusy()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean imuTargetHit()
    {
        double deltaHeading;
        whereAmI.setValue("in imuTargetHit");
        // if the heading is less than the acceptable yaw target margin
        // then return true to indicate we are close enough to the target yaw
        deltaHeading = targetHeading - Math.abs(heading);
        deltaHeadingTlm.setValue(deltaHeading);
        hitTargetHeadingTlm.setValue(targetHeading);
        hitHeadingTlm.setValue(heading);
        if (Math.abs(deltaHeading) > YAW_MARGIN) {
            whereAmI.setValue("in imuTargetHit false");
            return false;
        } else {
            whereAmI.setValue("in imuTargetHit true");
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

    public void getIMUValues() {
        heading = -imu.getAngularOrientation().firstAngle;
        roll = -imu.getAngularOrientation().secondAngle;
        pitch = -imu.getAngularOrientation().thirdAngle;
        yawRate = -imu.getAngularVelocity().yRotationRate;
        drivetrain.setCurrentYaw(heading);
        drivetrain.setCurrentYawRate(yawRate);
    }

    public void displayTelemetry() {
        //telemetry.setAutoClear(false);
        //imuStatusTlm.setValue(imu.getSystemStatus().toString());
        // imuCalibTlm.setValue(imu.getCalibrationStatus().toString());

     //   i = i++;
     //   whereAmIGyro.setValue("displayTelemetry" + i);

        this.imuHeadingTlm.setValue(heading);

        this.imuRollTlm.setValue(roll);

        this.imuPitchTlm.setValue(pitch);

        this.imuYawRateTlm.setValue( yawRate );
        //imuGravTlm.setValue(gravity.toString());


    }

    @Override
    public boolean timeslice()
    {
        DeadReckonPath.Segment segment;

        Orientation currentHeading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double secondAngle = -imu.getAngularOrientation().secondAngle;
        double thirdAngle = -imu.getAngularOrientation().thirdAngle;
        // FIXME add TLm.setValue
//        this.secondAngleTlm.setValue(secondAngle);
//        this.thirdAngleTlm.setValue(thirdAngle);

        getIMUValues();
        // FIXME add displaytlm
        displayTelemetry();

        myHeadingTlm = currentHeading.toString();
        // FIXME add headingTLm.setValue
        this.headingTlm.setValue(myHeadingTlm);

        /*
         * Get current segment
         */
        segment = dr.getCurrentSegment();

        if (segment == null) {
            if (reason == DoneReason.ENCODER_REACHED) {
                whereAmI.setValue("ts, segment NULL, ENCODER_REACHED");
                RobotLog.e("251 Dead reckon path done");
                robot.queueEvent(new DeadReckonEvent(this, EventKind.PATH_DONE, num));
            } else if (reason == DoneReason.SENSOR_SATISFIED) {
                whereAmI.setValue("ts, segment NULL, SENSOR_SATISFIED");
                RobotLog.e("251 Dead reckon sensor criteria satisfied");
                robot.queueEvent(new DeadReckonEvent(this, EventKind.SENSOR_SATISFIED, num));
            } else if (reason == DoneReason.BOTH_SENSORS_SATISFIED) {
                RobotLog.e("251 Dead reckon both sensor criteria satisfied");
                robot.queueEvent(new DeadReckonEvent(this, EventKind.BOTH_SENSORS_SATISFIED, num));
            } else if (reason == DoneReason.LEFT_SENSOR_SATISFIED) {
                whereAmI.setValue("ts, segment NULL, LEFT_SENSOR_SATISFIED");
                RobotLog.e("251 Dead reckon left sensor criteria segment %d satisfied", num);
                robot.queueEvent(new DeadReckonEvent(this, EventKind.LEFT_SENSOR_SATISFIED, num));
            } else if (reason == DoneReason.RIGHT_SENSOR_SATISFIED) {
                whereAmI.setValue("ts, segment NULL,RIGHT_SENSOR_SATISFIED");
                RobotLog.e("251 Dead reckon right sensor criteria segment %d satisfied", num);
                robot.queueEvent(new DeadReckonEvent(this, EventKind.RIGHT_SENSOR_SATISFIED, num));
            } else {
                whereAmI.setValue("ts, segment NULL, in else");
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
        } else if ((segment.state == DeadReckonPath.SegmentState.DONE) && (dr.numSegments() > 1)) {
            if (reason == DoneReason.ENCODER_REACHED) {
                whereAmI.setValue("ts, SegmentState.DONE, ENCODER_REACHED");
                RobotLog.e("251 Dead reckon segment %d done", num);
                robot.queueEvent(new DeadReckonEvent(this, EventKind.SEGMENT_DONE, num));
            } else if (reason == DoneReason.SENSOR_SATISFIED) {
                whereAmI.setValue("ts, SegmentState.DONE, SENSOR_SATISFIED");
                RobotLog.e("251 Dead reckon sensor criteria segment %d satisfied", num);
                robot.queueEvent(new DeadReckonEvent(this, EventKind.SENSOR_SATISFIED, num));
            } else if (reason == DoneReason.BOTH_SENSORS_SATISFIED) {
                RobotLog.e("251 Dead reckon sensor criteria segment %d satisfied", num);
                robot.queueEvent(new DeadReckonEvent(this, EventKind.SENSOR_SATISFIED, num));
            } else if (reason == DoneReason.LEFT_SENSOR_SATISFIED) {
                whereAmI.setValue("ts, SegmentState.DONE, LEFT_SENSOR_SATISFIED");
                RobotLog.e("251 Dead reckon left sensor criteria segment %d satisfied", num);
                robot.queueEvent(new DeadReckonEvent(this, EventKind.LEFT_SENSOR_SATISFIED, num));
            } else if (reason == DoneReason.RIGHT_SENSOR_SATISFIED) {
                whereAmI.setValue("ts, SegmentState.DONE, RIGHT_SENSOR_SATISFIED");
                RobotLog.e("251 Dead reckon right sensor criteria segment %d satisfied", num);
                robot.queueEvent(new DeadReckonEvent(this, EventKind.RIGHT_SENSOR_SATISFIED, num));
            } else {
                whereAmI.setValue("ts, SegmentState.DONE, else");
                RobotLog.e("251 Dead reckon segment %d done - no reason", num);
            }
        }

        switch (segment.state) {
            case INITIALIZE:
                whereAmI.setValue("ts, SegmentState.INITIALIZE");
                isStrafing = false;
                isStraight = false;
                drivetrain.resetEncoders();
                segment.state = DeadReckonPath.SegmentState.ENCODER_RESET;
                break;
            case ENCODER_RESET:
                whereAmI.setValue("ts, SegmentState.ENCODER_RESET");
                drivetrain.resetEncoders();
                segment.state = DeadReckonPath.SegmentState.SET_TARGET;
                break;
            case SET_TARGET:
                whereAmI.setValue("ts, SegmentState.SET_TARGET");
                drivetrain.encodersOn();
                setTarget(segment);
                segment.state = DeadReckonPath.SegmentState.CONSUME_SEGMENT;
                break;
            case CONSUME_SEGMENT:
                whereAmI.setValue("ts, SegmentState.CONSUME_SEGMENT");
                if (segment.type == PAUSE) {
                    whereAmI.setValue("ts, SegmentState.CONSUME_SEGMENT PAUSE");
                    setupWaitState(segment, true);
                    break;
                }

                if (segment.type == STRAIGHT) {
                    whereAmI.setValue("ts, SegmentState.CONSUME_SEGMENT STRAIGHT");
                    isStraight = true;
                    if (smoothStart == true) {
                        robot.addTask(new MotorRampTask(robot, MotorRampTask.RampDirection.RAMP_UP, segment.speed) {
                            @Override
                            public void run(double speed) {
                                drivetrain.straight(speed);
                            }
                        });
                    } else {
                        drivetrain.straight(segment.speed);
                    }
                } else if (segment.type == SIDEWAYS) {
                    whereAmI.setValue("ts, SegmentState.CONSUME_SEGMENT SIDEWAYS");
                    isStrafing = true;
                    if (smoothStart == true) {
                        robot.addTask(new MotorRampTask(robot, MotorRampTask.RampDirection.RAMP_UP, segment.speed) {
                            @Override
                            public void run(double speed) {
                                drivetrain.strafe(speed);
                            }
                        });
                    } else {
                        drivetrain.strafe(segment.speed);
                    }
                } else if (segment.type == LEFT_DIAGONAL) {
                    drivetrain.leftDiagonal(segment.speed);
                } else if (segment.type == RIGHT_DIAGONAL) {
                    drivetrain.rightDiagonal(segment.speed);
                } else if (segment.type == TURN_WITH_IMU) {
                    whereAmI.setValue("ts, SegmentState.CONSUME_SEGMENT TURN_WITH_IMU");
                    drivetrain.turnWithIMU(segment.speed);
                } else {
                    whereAmI.setValue("ts, SegmentState.CONSUME_SEGMENT else TURN");
                    drivetrain.turn(segment.speed);
                }
                segment.state = DeadReckonPath.SegmentState.ENCODER_TARGET;
                break;
            case ENCODER_TARGET:
                whereAmI.setValue("ts, SegmentState.ENCODER_TARGET");
                if ((sensorsInstalled == SensorsInstalled.SENSORS_ONE) && (leftCriteria.satisfied())) {
                    RobotLog.i("5218 Solo sensor criteria satisfied");
                    segment.state = STOP_MOTORS;
                    reason = DoneReason.SENSOR_SATISFIED;
                    whereAmI.setValue("ts, SegmentState.ENCODER_TARGET leftCriteria.satisfied");
                } else if (sensorsInstalled == SensorsInstalled.SENSORS_TWO) {
                    if (leftCriteria.satisfied() && rightCriteria.satisfied()) {
                        RobotLog.i("5218 Left and right criteria satisfied");
                        segment.state = STOP_MOTORS;
                        reason = DoneReason.BOTH_SENSORS_SATISFIED;
                    }
//                } else if (isUsingImuTurns && imuTargetHit()) {
                } else if ((segment.type == TURN_WITH_IMU) && imuTargetHit()) {
                    codeLocation.setValue("ts, SegmentState.ENCODER_TARGET imuTargetHit");
                    whereAmI.setValue("ts, SegmentState.ENCODER_TARGET imuTargetHit");
                    segment.state = STOP_MOTORS;
                    reason = DoneReason.ENCODER_REACHED;
//                } else if (!isUsingImuTurns && hitTarget()) {
                } else if ((segment.type != TURN_WITH_IMU) && hitTarget()) {
                    codeLocation.setValue("ts, SegmentState.ENCODER_TARGET hitTarget");
                    whereAmI.setValue("ts, SegmentState.ENCODER_TARGET hitTarget");
                    segment.state = STOP_MOTORS;
                    reason = DoneReason.ENCODER_REACHED;
                }
            break;
        case STOP_MOTORS:
            whereAmI.setValue("ts, SegmentState.STOP_MOTORS");
            if (smoothStart == true) {
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
                }
            } else {
                drivetrain.stop();
            }
            setupWaitState(segment, false);
            break;
        case WAIT:
            whereAmI.setValue("ts, SegmentState.WAIT");
            if (timer.time() >= segment.millisecond_pause) {
                segment.state = DeadReckonPath.SegmentState.DONE;
            }
            break;
        case DONE:
            whereAmI.setValue("ts, SegmentState.DONE");
            num++;
            dr.nextSegment();
            segment.state = DeadReckonPath.SegmentState.INITIALIZE;
            break;
        }

//        robot.telemetry.addData("Segment: ", num);
//        robot.telemetry.addData("State: ", segment.state.toString());

        return false;
    }
}
