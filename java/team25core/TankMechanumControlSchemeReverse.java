/*
 *  Copyright (c) September 2017 FTC Teams 25/5218
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

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by Breanna Chan on 1/6/2018.
 */


public class TankMechanumControlSchemeReverse implements JoystickDriveControlScheme {

    protected double fr;
    protected double fl;
    protected double rr;
    protected double rl;
    protected double leftX;
    protected double rightX;
    protected double leftY;
    protected double rightY;
    protected Gamepad gamepad;

    protected double leftWheelForward;
    protected double leftWheelBackward;
    protected double rightWheelForward;
    protected double rightWheelBackward;


    public TankMechanumControlSchemeReverse(Gamepad gamepad)
    {
        this.gamepad = gamepad;
    }

    public MotorValues getMotorPowers()
    {
        leftX = gamepad.left_stick_x;
        rightX = gamepad.right_stick_x;
        leftY = gamepad.left_stick_y;
        rightY = gamepad.right_stick_y;

        leftWheelForward   = -1;
        leftWheelBackward  = 1;
        rightWheelForward  = -1;
        rightWheelBackward = 1;

        // If joysticks are pointed left (negative joystick values), counter rotate wheels.
        // Threshold for joystick values in the x may vary.

        if (leftX > 0.5 && rightX > 0.5) {          // both X-sticks sideways left
            //fl = -leftX;
            //rl = leftX;
            //fr = -rightX;
            //rr = rightX;
            fl = leftWheelBackward;
            rl = leftWheelForward;
            fr = rightWheelForward;
            rr = rightWheelBackward;
        } else if (leftX < -0.5 && rightX < -0.5) { // both Y-sticks sideways right
            //fl = -leftX;
            //rl = leftX;
            //fr = -rightX;
            //rr = rightX;
            fl = leftWheelForward;
            rl = leftWheelBackward;
            fr = rightWheelBackward;
            rr = rightWheelForward;
        } else if (gamepad.right_trigger > 0.5) {   // backward diagonal to the right
            //fr = -1.0;
            //rl = 1.0;
            fr = rightWheelBackward;
            rl = leftWheelBackward;
        } else if (gamepad.left_trigger > 0.5) {    // backward diagonal to the left
            //fl = 1.0;
            //rr = -1.0;
            fl = leftWheelBackward;
            rr = rightWheelBackward;
        } else if (gamepad.left_bumper) {           // forward diagonal to the left
            //fr = 1.0;
            //rl = -1.0;
            fr = rightWheelForward;
            rl = leftWheelForward;
        } else if (gamepad.right_bumper) {          // forward diagonal to the right
            //fl = -1.0;
            //rr = 1.0;
            fl = leftWheelForward;
            rr = rightWheelForward;
        } else {                                    // forward or backward
            fl = leftY;
            rl = leftY;


            // CINDY changed the following RoverRuckus,
            //fr = -rightY;
            //rr = -rightY;
            fr = rightY;
            rr = rightY;
        }

        return new MotorValues(fl, fr, rl, rr);
    }
}
