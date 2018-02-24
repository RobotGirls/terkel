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

public class OneWheelDirectDrivetrain extends DrivetrainBaseImpl implements Drivetrain {

    DcMotor frontLeft;

    double multiplier;
    boolean alternate;

    public OneWheelDirectDrivetrain(DcMotor frontLeft)
    {
        super();

        this.frontLeft = frontLeft;

        this.encoderTarget = 0;
        this.multiplier = 1.0;
        this.alternate = true;

        /**
         * Set a default master.  This is the wheel/motor that will be used to track distance
         * travelled when following a dead reckon path.
         */
        setMasterMotor(frontLeft);
    }

    public OneWheelDirectDrivetrain(double pivotMultiplier, DcMotor frontLeft)
    {
        this.frontLeft = frontLeft;
        this.encoderTarget = 0;
        this.multiplier = pivotMultiplier;
        this.alternate = true;

        /**
         * Set a default master.  This is the wheel/motor that will be used to track distance
         * travelled when following a dead reckon path.
         */
        setMasterMotor(frontLeft);
    }

    @Override
    public void resetEncoders()
    {
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    public void encodersOn()
    {
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void logEncoderCounts() {

    }

    @Override
    public void straight(double speed)
    {
        /*
         * Alternate which motor gets power first in an attempt to reduce startup jerk.
         */
        if (alternate) {
            alternate = false;
            frontLeft.setPower(speed);
        } else {
            alternate = true;
            frontLeft.setPower(speed);
        }
    }

    @Override
    public void strafe(double speed)
    {

    }

    @Override
    public void leftDiagonal(double speed)
    {

    }

    @Override
    public void rightDiagonal(double speed)
    {

    }

    @Override
    public void turn(double speed)
    {
        frontLeft.setPower(speed);
    }

    @Override
    public void pivotTurn(PivotSide side, double speed)
    {
        switch (side) {
        case LEFT_OVER_RIGHT:
            frontLeft.setPower(-speed);
            break;
        case LEFT_OVER_LEFT:
            frontLeft.setPower((1/multiplier) * -speed);
            break;
        case RIGHT_OVER_RIGHT:
            frontLeft.setPower(speed);
            break;
        case RIGHT_OVER_LEFT:
            frontLeft.setPower((1/multiplier) * speed);
            break;
        }
    }

    @Override
    public void setPivotMultiplier(double pivotMultiplier)
    {

    }

    @Override
    public void setPowerLeft(double speed)
    {

    }

    @Override
    public void setPowerRight(double speed)
    {

    }

    @Override
    public void stop()
    {
        frontLeft.setPower(0.0);
    }

    @Override
    public void move(double axial, double lateral, double yaw)
    {

    }
}
