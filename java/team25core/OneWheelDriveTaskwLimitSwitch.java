
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
import com.qualcomm.robotcore.hardware.DigitalChannel;

public class OneWheelDriveTaskwLimitSwitch extends RobotTask
{
    protected Robot robot;
    protected DcMotor motor;

    public double right;
    public double left;
    public double ceiling;

    public boolean slow = false;
    public boolean useLeftJoystick = false;
    public boolean ceilingOn = false;

    public double slowMultiplier = 1;

    private DigitalChannel LimitSwitch;
    private boolean bottomLimit;

    // Enumeration: events.
    public enum EventKind {
        PUSH,
        UNKNOWN,
    }

    public class OneWheelDriveTaskwLimitSwitchEvent extends RobotEvent {
        public OneWheelDriveTaskwLimitSwitch.EventKind kind;

        public OneWheelDriveTaskwLimitSwitchEvent(RobotTask task, OneWheelDriveTaskwLimitSwitch.EventKind k)
        {
            super(task);
            kind = k;
        }
    }

    public OneWheelDriveTaskwLimitSwitch(Robot robot, DcMotor motor, boolean useLeftJoystick, DigitalChannel LimitSwitch, boolean bottomLimit)
    {
        super(robot);

        this.motor = motor;
        this.robot = robot;
        this.useLeftJoystick = useLeftJoystick;
        this.LimitSwitch = LimitSwitch;
        this.bottomLimit = bottomLimit;
    }

    private void getJoystick()
    {
        Gamepad gamepad = robot.gamepad2;

        left = -gamepad.left_stick_y * slowMultiplier;
        right = -gamepad.right_stick_y * slowMultiplier;
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
    public void start()
    {
        // Nothing.
    }

    @Override
    public void stop()
    {
    }

    public void useCeiling(double ceiling)
    {
        ceilingOn = true;
        this.ceiling = ceiling;
    }



    @Override
    public boolean timeslice()
    {
        getJoystick();

        if (useLeftJoystick) {
            if((bottomLimit&&LimitSwitch.getState() == false&&left<0) || (!bottomLimit&&LimitSwitch.getState() == false&&left>0)){
                motor.setPower(0);
                robot.queueEvent(new OneWheelDriveTaskwLimitSwitchEvent(this, OneWheelDriveTaskwLimitSwitch.EventKind.PUSH));
            } else{
               if (ceilingOn) {
                   if (left > ceiling) {
                       motor.setPower(ceiling);
                   } else {
                       motor.setPower(left);
                   }
               } else {
                   motor.setPower(left);
               }
        }} else {
            if((bottomLimit&&LimitSwitch.getState() == false&&right<0) || (!bottomLimit&&LimitSwitch.getState() == false&&right>0)){
                motor.setPower(0);
                robot.queueEvent(new OneWheelDriveTaskwLimitSwitchEvent(this, OneWheelDriveTaskwLimitSwitch.EventKind.PUSH));
            } else {
                if (ceilingOn) {
                    if (right > ceiling) {
                        motor.setPower(ceiling);
                    } else {
                        motor.setPower(right);
                    }
                } else {
                    motor.setPower(right);
                }
            }
        }

        return false;
    }
}
