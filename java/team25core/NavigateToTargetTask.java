///*
// * Copyright (c) September 2017 FTC Teams 25/5218
// *
// *  All rights reserved.
// *
// *  Redistribution and use in source and binary forms, with or without modification,
// *  are permitted (subject to the limitations in the disclaimer below) provided that
// *  the following conditions are met:
// *
// *  Redistributions of source code must retain the above copyright notice, this list
// *  of conditions and the following disclaimer.
// *
// *  Redistributions in binary form must reproduce the above copyright notice, this
// *  list of conditions and the following disclaimer in the documentation and/or
// *  other materials provided with the distribution.
// *
// *  Neither the name of FTC Teams 25/5218 nor the names of their contributors may be used to
// *  endorse or promote products derived from this software without specific prior
// *  written permission.
// *
// *  NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
// *  LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// *  AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
// *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
// *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
// *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
// *  TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
// *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// */
//
//package team25core;
//
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.Gamepad;
//import com.qualcomm.robotcore.util.ElapsedTime;
//import com.qualcomm.robotcore.util.RobotLog;
//
//import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
//import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
//import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
//
//public class NavigateToTargetTask extends RobotTask {
//
//    enum TargetState {
//        WAITING,
//        FIND_TARGET,
//        LOST_TARGET,
//        INITIAL_APPROACH_AXIAL,
//        INITIAL_APPROACH_LATERAL,
//        INITIAL_APPROACH_ROTATIONAL,
//        FINAL_APPROACH,
//        AT_TARGET,
//        ALIGNED,
//    };
//
//    public enum FindMethod {
//        APPROACH_STRAIGHT,
//        ROTATE_RIGHT,
//        ROTATE_LEFT,
//    };
//
//    public enum Targets {
//        BLUE_NEAR(0),
//        RED_FAR(1),
//        BLUE_FAR(2),
//        RED_NEAR(3);
//
//        public int targetId;
//        Targets(int id) {
//            this.targetId = id;
//        }
//    }
//
//    public enum EventKind {
//        FOUND_TARGET,
//        INITIAL_APPROACH_AXIAL,
//        INITIAL_APPROACH_ROTATIONAL,
//        AT_TARGET,
//        TIMEOUT,
//    }
//
//    public class NavigateToTargetEvent extends RobotEvent {
//
//        public EventKind kind;
//
//        public NavigateToTargetEvent(RobotTask task, EventKind k)
//        {
//            super(task);
//            kind = k;
//        }
//
//        @Override
//        public String toString()
//        {
//            return (super.toString() + "NavigateToTarget Event " + kind);
//        }
//    }
//
//    protected RobotNavigation nav;
//    protected Drivetrain drivetrain;
//    protected ElapsedTime timer;
//    protected int timeout;
//    protected Gamepad gamepad;
//    protected boolean pressed;
//    protected Targets target;
//    protected TargetState state;
//    protected FindMethod findMethod;
//
//    protected double relativeBearing;
//    protected double robotBearing;
//    protected double linearDistanceFromTarget;
//    protected double strafeDistanceFromTarget;
//    protected int firstRotation;
//
//    private Alliance alliance;
//
//    boolean visible;
//
//    protected float mmPerInch        = 25.4f;
//    protected float mmBotWidth       = 18 * mmPerInch;            // ... or whatever is right for your robot
//    protected float mmFTCFieldWidth  = (12*12 - 2) * mmPerInch;   // the FTC field is ~11'10" center-to-center of the glass panels
//
//    protected final static String ROBOT_TAG = "Nav: ";
//
//    public NavigateToTargetTask(Robot robot, Drivetrain drivetrain, Targets target, int timeout, Gamepad gamepad, Alliance alliance)
//    {
//        super(robot);
//
//        this.timeout = timeout;
//        this.gamepad = gamepad;
//        this.drivetrain = drivetrain;
//        this.target = target;
//        this.findMethod = FindMethod.APPROACH_STRAIGHT;
//        this.firstRotation = 0;
//        this.alliance = alliance;
//
//        robotBearing = 0;
//        linearDistanceFromTarget = 0;
//        strafeDistanceFromTarget = 0;
//    }
//
//    public NavigateToTargetTask(Robot robot, Drivetrain drivetrain, int timeout, Gamepad gamepad, Alliance alliance)
//    {
//        super(robot);
//
//        this.timeout = timeout;
//        this.gamepad = gamepad;
//        this.drivetrain = drivetrain;
//        this.target = null;
//        this.findMethod = FindMethod.APPROACH_STRAIGHT;
//        this.firstRotation = 0;
//        this.alliance = alliance;
//
//        robotBearing = 0;
//        linearDistanceFromTarget = 0;
//        strafeDistanceFromTarget = 0;
//    }
//
//    public void init(VuforiaTrackables targets, VuforiaLocalizer.Parameters parameters, OpenGLMatrix phoneLocationOnRobot)
//    {
//        nav = new RobotNavigation(this.robot, drivetrain);
//        nav.initVuforia(targets, parameters, phoneLocationOnRobot);
//        nav.activateTracking();
//        nav.targetsAreVisible();
//        nav.addNavTelemetry();
//
//        // Uncomment this to tune the gain parameters if the defaults are unacceptable.
//        // nav.setGainParams(0.0, 0.0, 0.0);
//
//        setState(TargetState.WAITING);
//    }
//
//    protected void setState(TargetState state)
//    {
//        this.state = state;
//        RobotLog.i("POS Entering state: " + state.toString());
//        drivetrain.logEncoderCounts();
//    }
//
//    public void reset()
//    {
//        this.state = TargetState.FIND_TARGET;
//        firstRotation = 0;
//        robotBearing = 0;
//        linearDistanceFromTarget = 0;
//        strafeDistanceFromTarget = 0;
//    }
//
//    public void setTarget(Targets target)
//    {
//        this.target = target;
//    }
//
//    public void setAlliance(Alliance alliance)
//    {
//        this.alliance = alliance;
//    }
//
//    @Override
//    public void start()
//    {
//        // No-op
//    }
//
//    @Override
//    public void stop()
//    {
//        drivetrain.stop();
//    }
//
//    /**
//     * Must call when you want the robot to actively start moving to the target.
//     * Prior to this, the robot is just sending telemetry back to the driver station.
//     * This allows drivers to know if the robot can see a target during the init phase.
//     */
//    public void findTarget()
//    {
//        timer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
//        setState(TargetState.FIND_TARGET);
//    }
//
//    public void setFindMethod(FindMethod findMethod)
//    {
//        this.findMethod = findMethod;
//    }
//
//    protected void doFindMethod()
//    {
//        switch (findMethod) {
//        case APPROACH_STRAIGHT:
//            drivetrain.straight(0.3);
//            break;
//        case ROTATE_RIGHT:
//            drivetrain.turn(0.2);
//            break;
//        case ROTATE_LEFT:
//            drivetrain.turn(-0.2);
//            break;
//        }
//    }
//
//    @Override
//    public boolean timeslice()
//    {
//        if (target == null) {
//            visible = nav.targetsAreVisible();
//        } else {
//            visible = nav.targetIsVisible(target.targetId);
//        }
//
//        if ((timer != null) && (timer.time() > timeout)) {
//            robot.queueEvent(new NavigateToTargetEvent(this, EventKind.TIMEOUT));
//            return true;
//        }
//
//        if (visible) {
//            /*
//             * Cache the last known robot position.
//             */
//            relativeBearing = nav.getRelativeBearing();
//            robotBearing = nav.getRobotBearing();
//            linearDistanceFromTarget = nav.getDistance();
//            strafeDistanceFromTarget = nav.getStrafe();
//        }
//
//        // Begin with LATERAL motion in initial approach to prevent losing target.
//        // Then, go to ROTATIONAL motion, and finally, AXIAL motion.
//        // NOTE: Change the values of how much the robot is off depending on personal/robot preference.
//
//        //TODO: CAP MOTOR POWERS AT 0.7!!!
//        switch (state) {
//            case WAITING:
//                RobotLog.i("141 Case: Waiting");
//                if (visible) {
//                    drivetrain.stop();
//                    nav.addNavTelemetry();
//                } else {
//                    robot.telemetry.addLine("Can't see the target");
//                }
//                break;
//            case FIND_TARGET:
//                RobotLog.i("141 Case: Find Target");
//                if (visible) {
//                    drivetrain.stop();
//                    nav.addNavTelemetry();
//                    setState(TargetState.INITIAL_APPROACH_ROTATIONAL);
//                } else {
//                    doFindMethod();
//                }
//                break;
//            case LOST_TARGET:
//                RobotLog.i("141 Case: Lost Target");
//                if (visible) {
//                    setState(TargetState.INITIAL_APPROACH_ROTATIONAL);
//                } else if (relativeBearing > 0){
//                    drivetrain.turn(0.06);
//                } else {
//                    drivetrain.turn(-0.06);
//                }
//                break;
//            case INITIAL_APPROACH_LATERAL:
//                RobotLog.i("141 Case: Initial Approach LATERAL");
//                nav.setGainParams(0, 0, 0.004);
//                RobotLog.i("141 Robot Strafe %f", nav.getStrafe());
//
//                if (!visible) {
//                    state = TargetState.LOST_TARGET;
//                } else if (nav.cruiseControl(300) && alliance == Alliance.RED) {
//                    setState(TargetState.INITIAL_APPROACH_ROTATIONAL);
//                } else if (nav.cruiseControl(300) && alliance == Alliance.BLUE) {
//                    setState(TargetState.INITIAL_APPROACH_ROTATIONAL);
//                }
//                break;
//            case INITIAL_APPROACH_ROTATIONAL:
//                RobotLog.i("141 Case: Initial Approach ROTATIONAL");
//                RobotLog.i("141 Relative Bearing %f", nav.getRelativeBearing());
//                RobotLog.i("141 Robot Bearing %f", nav.getRobotBearing());
//                nav.setGainParams(0.027, 0, 0);
//                if (!visible) {
//                    state = TargetState.LOST_TARGET;
//                } else if (nav.cruiseControl(300)) {
//                    if (firstRotation < 2) {
//                        robot.queueEvent(new NavigateToTargetEvent(this, EventKind.INITIAL_APPROACH_ROTATIONAL));
//                        setState(TargetState.INITIAL_APPROACH_LATERAL);
//                        firstRotation++;
//                    } else {
//                        robot.queueEvent(new NavigateToTargetEvent(this, EventKind.INITIAL_APPROACH_AXIAL));
//                        setState(TargetState.INITIAL_APPROACH_AXIAL);
//                    }
//                }
//                break;
//            case INITIAL_APPROACH_AXIAL:
//                RobotLog.i("141 Case: Initial Approach AXIAL");
//                RobotLog.i("141 Robot Distance %f", nav.getDistance());
//                nav.setGainParams(0, 0.003, 0);
//                if (!visible) {
//                    state = TargetState.LOST_TARGET;
//                } else if (nav.cruiseControl(235) || nav.getDistance() <= 235) {  // 75?
//                    robot.queueEvent(new NavigateToTargetEvent(this, EventKind.AT_TARGET));
//                    RobotLog.i("141 Queueing AT_TARGET event");
//                    return true;
//                }
//                break;
//            case FINAL_APPROACH:
//                if (visible) {
//                    if (nav.cruiseControl(200)) {
//                        setState(TargetState.AT_TARGET);
//                    }
//                } else {
//                    RobotLog.i("Lost target %f", nav.getRelativeBearing());
//                    setState(TargetState.LOST_TARGET);
//                }
//                break;
//            case AT_TARGET:
//                if ((robotBearing < 1.5) && (robotBearing > -1.5)) {
//                    setState(TargetState.ALIGNED);
//                } else if (nav.getRobotBearing() > 0) {
//                    drivetrain.turn(-0.10);
//                } else {
//                    drivetrain.turn(0.10);
//                }
//                break;
//            case ALIGNED:
//                nav.addNavTelemetry();
//                drivetrain.stop();
//                break;
//        }
//        return false;
//    }
//}
