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
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.util.RobotLog;

public class LineFollowerTask extends RobotTask {

    protected double lightValue;
    protected double inches;

    // Proportional variables.
    protected double raw;
    protected double lightError;
    protected double adjustedError;
    protected double PROPORTIONAL_K = .7;
    protected double BASE_POWER = 0.5;

    protected boolean zigzag;
    protected int ticksPerInch;

    protected LightSensor light;
    protected DcMotor left;
    protected DcMotor right;

    protected int IDEAL_VALUE = 25;
    protected int maximum = 735;
    protected int minimum = 574;

    public enum EventKind {
        FOLLOWING,
        DONE,
        ERROR,
    }

    public class LineFollowerEvent extends RobotEvent
    {
        public EventKind kind;
        public int val;

        public LineFollowerEvent(RobotTask task, EventKind kind) {
            super(task);
            this.kind = kind;
            this.val = 0;
        }
    }

    public LineFollowerTask(Robot robot, LightSensor light, DcMotor left, DcMotor right, int ticksInch, double inches, boolean zigzag)
    {
        super(robot);
        this.light = light;
        this.inches = inches;
        this.zigzag = zigzag;
        this.ticksPerInch = ticksInch;
        this.left = left;
        this.right = right;
    }

    public LineFollowerTask(Robot robot, LightSensor light, DcMotor left, DcMotor right, int ticksInch, double inches, boolean zigzag,
                            int maximum, int minimum)
    {
        super(robot);
        this.light = light;
        this.inches = inches;
        this.zigzag = zigzag;
        this.ticksPerInch = ticksInch;
        this.left = left;
        this.right = right;
        this.minimum = minimum;
        this.maximum = maximum;
    }
    @Override
    public void start()
    {
        RobotLog.i("251 LF Starting line follower task");
        left.setMode(DcMotor.RunMode.RESET_ENCODERS);
        right.setMode(DcMotor.RunMode.RESET_ENCODERS);
        left.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
        right.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);

        left.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void stop()
    {

    }

    @Override
    public boolean timeslice()
    {
        raw = light.getRawLightDetected();
        lightValue = (1 - ((raw - minimum)/(maximum - minimum))) * 100;

        if (zigzag) {
            // Follows the left side of the line.
            if (left.getCurrentPosition() < inches * ticksPerInch) {
                RobotLog.i("251 LF Status: FOLLOWING");
                RobotLog.i("251 LF Target: %d, Raw: %d", ((int)(inches * ticksPerInch)), raw);
                RobotLog.i("251 LF Light value: %d", ((int)lightValue));
                if (lightValue > IDEAL_VALUE) {
                    RobotLog.i("251 LF Moving LEFT");
                    left.setPower(0.35);
                    right.setPower(0.15);
                } else {
                    RobotLog.i("251 LF Moving RIGHT");
                    left.setPower(0.15);
                    right.setPower(0.35);
                }
                return false;
            } else if (left.getCurrentPosition() >= inches * ticksPerInch) {
                RobotLog.i("251 LF Status: TARGET MET");

                LineFollowerEvent done = new LineFollowerEvent(this, EventKind.DONE);
                robot.queueEvent(done);

                left.setPower(0.0);
                right.setPower(0.0);

                return true;
            }
        } else if (!zigzag) {
            // Proportional line follower.
            if (left.getCurrentPosition() < inches * ticksPerInch) {
                lightError = IDEAL_VALUE - lightValue;
                adjustedError = (lightError * PROPORTIONAL_K);

                right.setPower(BASE_POWER + adjustedError);
                left.setPower(BASE_POWER - adjustedError);

                LineFollowerEvent following = new LineFollowerEvent(this, EventKind.FOLLOWING);
                robot.queueEvent(following);
            } else if (left.getCurrentPosition() >= inches * ticksPerInch) {
                LineFollowerEvent done = new LineFollowerEvent(this, EventKind.DONE);
                robot.queueEvent(done);

                left.setPower(0.0);
                right.setPower(0.0);

                return true;
            }
        }
        return false;
    }
}
