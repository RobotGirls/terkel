
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

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class BalanceTask extends RobotTask {
    protected Robot robot;
    protected DcMotor frontLeft;
    protected DcMotor frontRight;
    protected DcMotor rearLeft;
    protected DcMotor rearRight;

    public double fr;
    public double fl;
    public double rr;
    public double rl;
    public double slowMultiplier = 1;
    public double leftX;
    public double rightX;
    public double leftY;
    public double rightY;

    public boolean yForward = true;
    public boolean isSuspended = false;

    BNO055IMU imu;
    Orientation angles;
    Acceleration gravity;

    public BalanceTask(Robot robot, DcMotor frontLeft, DcMotor frontRight, DcMotor rearLeft, DcMotor rearRight)
    {
        super(robot);

        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.rearLeft = rearLeft;
        this.rearRight = rearRight;
        this.robot = robot;
        this.isSuspended = false;

        //frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        //rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        rearRight.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    private void getJoystick() {
        Gamepad gamepad;
        gamepad = robot.gamepad1;

        if (yForward) {
            leftX = gamepad.left_stick_x;
            rightX = gamepad.right_stick_x;
            leftY = gamepad.left_stick_y;
            rightY = gamepad.right_stick_y;
        } else {
            leftX = -gamepad.left_stick_y;
            rightX = -gamepad.right_stick_y;
            leftY = -gamepad.left_stick_x;
            rightY = -gamepad.right_stick_x;
        }

        // If joysticks are pointed left (negative joystick values), counter rotate wheels.
        // Threshold for joystick values in the x may vary.

        if (leftX > 0.5 && rightX > 0.5) {
            fl = -leftX;
            rl = leftX;
            fr = -rightX;
            rr = rightX;
        } else if (leftX < -0.5 && rightX < -0.5) {
            fl = -leftX;
            rl = leftX;
            fr = -rightX;
            rr = rightX;
        } else if (gamepad.right_trigger > 0.5) {
            fr = -1.0;
            rl = 1.0;
        } else if (gamepad.left_trigger > 0.5) {
            fl = 1.0;
            rr = -1.0;
        } else if (gamepad.left_bumper) {
            fr = 1.0;
            rl = -1.0;
        } else if (gamepad.right_bumper) {
            rr = 1.0;
            fl = -1.0;
        } else {
            fl = leftY;
            rl = leftY;
            fr = -rightY;
            rr = -rightY;
        }
    }

    public void suspendTask(boolean isSuspended)
    {
        this.isSuspended = isSuspended;
    }

    public void changeDirection()
    {
       if (yForward) {
           yForward = false;
       } else {
           yForward = true;
       }
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
        getJoystick();

        frontLeft.setPower(fl * slowMultiplier);
        rearLeft.setPower(rl * slowMultiplier);
        frontRight.setPower(fr * slowMultiplier);
        rearRight.setPower(rr * slowMultiplier);

        return false;
    }

}
