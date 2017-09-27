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

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import static android.R.attr.end;

public class MonitorMotorTask extends RobotTask {

    public static final char DISPLAY_POSITION = 0x01;
    public static final char DISPLAY_RPM = 0x02;
    public static final char LOG_POSITION = 0x04;

    public enum MotorKind {
        ANDYMARK_20,
        ANDYMARK_40,
        ANDYMARK_60,
        ANDYMARK_3_7,
        ANDYMARK_3_7_1ST_GEN,
    }

    /*
     * Setup for Andymark 20/40/60.  Todo: Add support for other motor types
     *
     * Data from andymark website.
     *
     *    For 20/40/60 motor bodies.
     *      7 pulses per revolution and 4 ticks, edges, per pulse.
     *
     *    20:1 = 7 * 20 * 4 = 560
     *    40:1 = 7 * 40 * 4 = 1120
     *    60:1 = 7 * 60 * 4 = 1680
     *    3.7  = 7 * 3.7 * 4 = 103
     *
     *    For 3.7 motor body (1st generation)
     *      11 pulses per revolution and 4 ticks, edges per pulse
     *
     *    3.7:1 = 11 * 4 = 44
     *
     *    For 3.7 motor body (2nd generation)
     *      Same as mounted to a 20/40/60
     */
    protected int ANDYMARK_20_TPR = 560;
    protected int ANDYMARK_40_TPR = 1120;
    protected int ANDYMARK_60_TPR = 1680;
    protected int ANDYMARK_3_7_2ND_GEN_TPR = 103;
    protected int ANDYMARK_3_7_1ST_GEN_TPR = 44;

    public enum EventKind {
        ERROR_UPDATE,
        TARGET_RPM,
    }

    public class MonitorMotorEvent extends RobotEvent {
        public EventKind kind;
        public int val;

        public MonitorMotorEvent(RobotTask task, EventKind kind, int val) {
            super(task);
            this.kind = kind;
            this.val = val;
        }
    }

    protected int target;
    protected int targetRpm = -1;
    protected char displayProperties = DISPLAY_POSITION;
    protected int rpm;
    protected int lastPosition = -1;
    protected double lastTime;
    protected int position;
    protected ElapsedTime timeSinceLastCall = null;
    protected MotorKind motorKind;
    protected int ticksPerRevolution;
    protected static final int MILLIS_IN_MINUTE = 60000;

    protected Robot robot;
    protected DcMotor motor;

    public MonitorMotorTask(Robot robot, DcMotor motor, MotorKind motorKind, char displayProperties)
    {
        super(robot);

        this.motor = motor;
        this.robot = robot;
        this.target = 0;
        this.motorKind = motorKind;
        this.displayProperties = displayProperties;
    }

    public MonitorMotorTask(Robot robot, DcMotor motor)
    {
        super(robot);

        this.motor = motor;
        this.robot = robot;
        this.target = 0;
        this.motorKind = MotorKind.ANDYMARK_40;
        this.displayProperties = DISPLAY_POSITION;
    }

    public MonitorMotorTask(Robot robot, DcMotor motor, int target)
    {
        super(robot);

        this.motor = motor;
        this.robot = robot;
        this.target = target;
        this.motorKind = MotorKind.ANDYMARK_40;
        this.displayProperties = DISPLAY_POSITION;
    }

    public void setTargetRpm(int targetRpm)
    {
        this.targetRpm = targetRpm;
    }

    protected void setupMotorProperties()
    {
        switch (motorKind) {
        case ANDYMARK_20:
            ticksPerRevolution = ANDYMARK_20_TPR;
            break;
        case ANDYMARK_40:
            ticksPerRevolution = ANDYMARK_40_TPR;
            break;
        case ANDYMARK_60:
            ticksPerRevolution = ANDYMARK_60_TPR;
            break;
        case ANDYMARK_3_7:
            ticksPerRevolution = ANDYMARK_3_7_2ND_GEN_TPR;
            break;
        case ANDYMARK_3_7_1ST_GEN:
            ticksPerRevolution = ANDYMARK_3_7_1ST_GEN_TPR;
            break;
        }
    }

    @Override
    public void start()
    {
        setupMotorProperties();
    }

    @Override
    public void stop()
    {
        robot.removeTask(this);
    }

    protected void calculateRpm()
    {
        int deltaPosition;
        int deltaTime;
        double distanceRotated;
        double percentRotated;
        double oneRotationMultiplier;

        /*
         * Setup for first time through.
         */
        if (lastPosition == -1) {
            lastPosition = position;
            timeSinceLastCall = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
            timeSinceLastCall.reset();
            return;
        }

        RobotLog.i("RPM - start");

        /*
         * Someone should check my math.  The equation could be simplified but I was
         * intentionally verbose in order to attempt to document how we are calculating RPM.
         */
        deltaPosition = position - lastPosition;
        deltaTime = (int)timeSinceLastCall.milliseconds();

        RobotLog.i("RPM - deltaPosition: " + deltaPosition);
        RobotLog.i("RPM - deltaTime: " + deltaTime);

        distanceRotated = (deltaPosition / (double)ticksPerRevolution);
        RobotLog.i("RPM - distanceRotated: " + distanceRotated);
        oneRotationMultiplier = 1 / distanceRotated;

        RobotLog.i("RPM - oneRotationMultiplier: " + oneRotationMultiplier);

        rpm = (int)(MILLIS_IN_MINUTE / (oneRotationMultiplier * deltaTime));
        RobotLog.i("RPM - rpm: " + rpm);

        timeSinceLastCall.reset();
        lastPosition = position;

        RobotLog.i("RPM - end");
    }

    @Override
    public boolean timeslice()
    {
        int error;

        position = motor.getCurrentPosition();
        error = target - position;

        /*
         * Make sure the sample rate is not too fast.
         */
        if ((timeSinceLastCall == null) || (timeSinceLastCall.time() >= 20)) {
            calculateRpm();
        }

        if ((targetRpm != -1) && (rpm >= targetRpm)) {
            robot.queueEvent(new MonitorMotorEvent(this, EventKind.TARGET_RPM, rpm));
            /*
             * Only send once...
             */
            targetRpm = -1;
        }

        robot.queueEvent(new MonitorMotorEvent(this, EventKind.ERROR_UPDATE, error));

        if ((displayProperties & LOG_POSITION) != 0) {
            RobotLog.ii("MonitorMotor", "Position %d", position);
        }
        if ((displayProperties & DISPLAY_POSITION) != 0) {
            robot.telemetry.addData(motor.getConnectionInfo() + " Postion: ", Math.abs(position));
        }
        if ((displayProperties & DISPLAY_RPM) != 0) {
            robot.telemetry.addData(motor.getConnectionInfo() + "RPM: ", rpm);
        }
        robot.telemetry.addData(motor.getConnectionInfo() + " Target: ", Math.abs(target));
        robot.telemetry.addData(motor.getConnectionInfo() + " Error: ", Math.abs(error));
        robot.telemetry.update();
        /*
         * Never stops.
         */
        return false;
    }
}
