
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
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.RobotLog;

public class TeleopDriveTaskReverse extends RobotTask {
    protected Robot robot;
    protected DcMotor frontLeft;
    protected DcMotor frontRight;
    protected DcMotor rearLeft;
    protected DcMotor rearRight;

    public double slowMultiplier = 1;

    public boolean isSuspended = false;

    protected JoystickDriveControlScheme driveScheme;

    public TeleopDriveTaskReverse(Robot robot, JoystickDriveControlScheme driveScheme, DcMotor frontLeft, DcMotor frontRight, DcMotor rearLeft, DcMotor rearRight)
    {
        super(robot);

        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.rearLeft = rearLeft;
        this.rearRight = rearRight;
        this.robot = robot;
        this.isSuspended = false;
        this.driveScheme = driveScheme;

        // Cindy changed this for RoverRuckus
        //frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        //rearLeft.setDirection(DcMotorSimple.Direction.FORWARD);

        // Left wheels are set to the opposite direction from right
        // wheels since the left motors are mounted in the opposite
        // direction from the right motors
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        rearRight.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void suspendTask(boolean isSuspended)
    {
        this.isSuspended = isSuspended;
    }

    @Override
    public void start()
    {
        // Nothing.
    }

    public void slowDown(boolean slow)
    {
        if (slow) {
            slowMultiplier = 0.5;
        } else {
            slowMultiplier = 1;
        }
    }

    public void slowDown(double mult)
    {
        slowMultiplier = mult;
    }

    @Override
    public void stop()
    {
        robot.removeTask(this);
    }

    @Override
    public boolean timeslice()
    {
        if (isSuspended) {
            RobotLog.i("teleop timeslice suspended");
            return false;
        }

        RobotLog.i("teleop timeslice not suspended");

        MotorValues values = driveScheme.getMotorPowers();

        frontLeft.setPower(values.fl * slowMultiplier);
        rearLeft.setPower(values.rl * slowMultiplier);
        frontRight.setPower(values.fr * slowMultiplier);
        rearRight.setPower(values.rr * slowMultiplier);

        return false;
    }
}
