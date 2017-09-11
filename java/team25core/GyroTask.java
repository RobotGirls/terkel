
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

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.RobotLog;

public class GyroTask extends RobotTask {

    public enum EventKind {
        THRESHOLD_80,
        THRESHOLD_90,
        THRESHOLD_95,
        HIT_TARGET,
        PAST_TARGET,
        ERROR_UPDATE,
    }

    public class GyroEvent extends RobotEvent {
        public EventKind kind;
        public int val;

        public GyroEvent(RobotTask task, EventKind kind) {
            super(task);
            this.kind = kind;
            this.val = 0;
        }

        public GyroEvent(RobotTask task, EventKind kind, int val) {
            super(task);
            this.kind = kind;
            this.val = val;
        }
    }

    protected int targetHeading = 0;
    protected int pt = 5;
    protected GyroSensor sensor;
    protected boolean showHeading = false;

    protected GyroEvent t_80;
    protected GyroEvent t_90;
    protected GyroEvent t_95;

    public GyroTask(Robot robot, GyroSensor sensor, int targetHeading, boolean showHeading)
    {
        super(robot);
        this.targetHeading = targetHeading;  // Think cardinal: negative is ccw, positive is cw.
        this.sensor = sensor;
        this.showHeading = showHeading;
    }

    @Override
    public void start() {
        // sensor.resetZAxisIntegrator();

        if (targetHeading > 0) {
            // Clockwise rotation.
            ((ModernRoboticsI2cGyro)sensor).setHeadingMode(ModernRoboticsI2cGyro.HeadingMode.HEADING_CARDINAL);
        } else {
            // Counter-clockwise rotation.
            ((ModernRoboticsI2cGyro)sensor).setHeadingMode(ModernRoboticsI2cGyro.HeadingMode.HEADING_CARTESIAN);
        }
    }

    @Override
    public void stop()
    {
        robot.removeTask(this);
    }

    @Override
    public boolean timeslice() {
        int currentHeading = sensor.getHeading();
        int absTarget = Math.abs(targetHeading);
        int error;

        if (showHeading) {
            robot.telemetry.addData("Current/target heading is: ", currentHeading + "/" + targetHeading);
        }

        error = (absTarget - currentHeading + 360) % 360;

        GyroEvent errorUpdate = new GyroEvent(this, EventKind.ERROR_UPDATE, error);
        robot.queueEvent(errorUpdate);

        if (targetHeading > 0) {
            // Clockwise rotation.
            if (error > 5) {
                GyroEvent pastTarget = new GyroEvent(this, EventKind.PAST_TARGET);
                robot.queueEvent(pastTarget);
                return true;
            }
        } else {
            // Counter-clockwise rotation.
            if (error > 5) {
                GyroEvent pastTarget = new GyroEvent(this, EventKind.PAST_TARGET);
                robot.queueEvent(pastTarget);
                return true;
            }
        }

        if (error == 0) {
            GyroEvent hitTarget = new GyroEvent(this, EventKind.HIT_TARGET);
            robot.queueEvent(hitTarget);
            return true;
        }/*else if (error <= (absTarget * 0.80) && t_80 == null) {
            t_80 = new GyroEvent(this, EventKind.THRESHOLD_80);
            robot.queueEvent(t_80);
            return false;
        } else if (error >= (absTarget * 0.90) && t_90 == null) {
            t_90 = new GyroEvent(this, EventKind.THRESHOLD_90);
            robot.queueEvent(t_90);
            return false;
        } else if (error >= (absTarget * 0.95) && t_95 == null) {
            t_95 = new GyroEvent(this, EventKind.THRESHOLD_95);
            robot.queueEvent(t_95);
            return false;
        } else if (error >= pt) {
            if (error > 180) {
                pt = 5;
            } else {
                pt = 355;
            }
            GyroEvent pastTarget = new GyroEvent(this, EventKind.PAST_TARGET);
            robot.queueEvent(pastTarget);
            return true;
        }*/ else {
            return false;
        }


    }
}
