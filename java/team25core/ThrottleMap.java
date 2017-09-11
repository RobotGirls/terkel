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
import com.qualcomm.robotcore.hardware.Gamepad;

public class ThrottleMap {
    Robot robot;
    DcMotor left;
    DcMotor right;

    public float rightPower;
    public float leftPower;

    public ThrottleMap(Robot robot, DcMotor leftMotor, DcMotor rightMotor) {
        this.left = leftMotor;
        this.right = rightMotor;
    }

    private static double logarithmicPower(double power) {
        boolean negative = false;

        // Convert power (decimal) to whole number.
        //power = power * 100;
        if (power < 0) {
            negative = true;
        } else if (power == 0) {
            return 0;
        }

        // Assign value of "e" to a variable.
        double e = Math.exp(1.0);

        double joystick = Math.abs(power);
        double returnPower = Math.pow(e, (4.6 * joystick)) / 100;
        if (negative) {
            return -1 * returnPower;
        } else {
            return returnPower;
        }
    }

    public void applyPower() {
        Gamepad gamepad;
        gamepad = robot.gamepad1;

        leftPower  = -gamepad.left_stick_y;
        rightPower = gamepad.right_stick_y;

        left.setPower(logarithmicPower(leftPower));
        right.setPower(logarithmicPower(rightPower));
    }
}