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

public class DeadmanMotorTask extends RobotTask {

    public enum DeadmanButton {
        BUTTON_A,
        BUTTON_B,
        BUTTON_X,
        BUTTON_Y,
        LEFT_BUMPER,
        RIGHT_BUMPER,
        LEFT_TRIGGER,
        RIGHT_TRIGGER,
    };

    public enum EventKind {
        DEADMAN_BUTTON_DOWN,
        DEADMAN_BUTTON_UP,
    }

    public class DeadmanMotorEvent extends RobotEvent {
        public EventKind kind;

        public DeadmanMotorEvent (RobotTask task, EventKind k){
            super(task);
            kind = k;
        }
    }

    GamepadTask.GamepadNumber gamepad;
    DcMotor motor;
    double power;
    DeadmanButton button;
    boolean done;
    boolean buttonDown;
    HoldPositionTask holdPositionTask;
    boolean useHoldMotorPositionTask=false;

    final int MINMAX_POSITION_NOT_SET = 0xdeadbeef;
    int minPosition = MINMAX_POSITION_NOT_SET ;
    int maxPosition = MINMAX_POSITION_NOT_SET ;

    ///Creates a DeadmanMotorTask without HoldMotorPositionTask
    public DeadmanMotorTask(Robot robot, DcMotor motor, double power, GamepadTask.GamepadNumber gamepad, DeadmanButton button)
    {
        super(robot);

        holdPositionTask=null;
        useHoldMotorPositionTask = false;
        this.motor = motor;
        this.gamepad = gamepad;
        this.button = button;
        this.power = power;
        done = false;
    }

    ///Creates a DeadmanMotorTask with option for HoldMotorPositionTask

    public DeadmanMotorTask(Robot robot, DcMotor motor, double power, GamepadTask.GamepadNumber gamepad, DeadmanButton button, boolean useHoldMotorPositionTask)
    {
        super(robot);

        holdPositionTask = new HoldPositionTask(robot, motor, 1);
        this.useHoldMotorPositionTask=useHoldMotorPositionTask;
        this.motor = motor;
        this.gamepad = gamepad;
        this.button = button;
        this.power = power;
        done = false;
    }

    protected boolean isButtonTracked(GamepadTask.EventKind kind)
    {
        boolean ret;

        ret = false;

        if (((kind == GamepadTask.EventKind.BUTTON_A_DOWN) || (kind == GamepadTask.EventKind.BUTTON_A_UP)) && (button == DeadmanButton.BUTTON_A)) {
            ret = true;
        } else if (((kind == GamepadTask.EventKind.BUTTON_B_DOWN) || (kind == GamepadTask.EventKind.BUTTON_B_UP)) && (button == DeadmanButton.BUTTON_B)) {
            ret = true;
        } else if (((kind == GamepadTask.EventKind.BUTTON_X_DOWN) || (kind == GamepadTask.EventKind.BUTTON_X_UP)) && (button == DeadmanButton.BUTTON_X)) {
            ret = true;
        } else if (((kind == GamepadTask.EventKind.BUTTON_Y_DOWN) || (kind == GamepadTask.EventKind.BUTTON_Y_UP)) && (button == DeadmanButton.BUTTON_Y)) {
            ret = true;
        } else if (((kind == GamepadTask.EventKind.LEFT_BUMPER_DOWN) || (kind == GamepadTask.EventKind.LEFT_BUMPER_UP)) && (button == DeadmanButton.LEFT_BUMPER)) {
            ret = true;
        } else if (((kind == GamepadTask.EventKind.RIGHT_BUMPER_DOWN) || (kind == GamepadTask.EventKind.RIGHT_BUMPER_UP)) && (button == DeadmanButton.RIGHT_BUMPER)) {
            ret = true;
        } else if (((kind == GamepadTask.EventKind.LEFT_TRIGGER_DOWN) || (kind == GamepadTask.EventKind.LEFT_TRIGGER_UP)) && (button == DeadmanButton.LEFT_TRIGGER)) {
            ret = true;
        } else if (((kind == GamepadTask.EventKind.RIGHT_TRIGGER_DOWN) || (kind == GamepadTask.EventKind.RIGHT_TRIGGER_UP)) && (button == DeadmanButton.RIGHT_TRIGGER)) {
            ret = true;
        }
        return ret;
    }
    public void setMinMotorPosition(int minPos) {
        this.minPosition = minPos;
    }
    public void setMaxMotorPosition(int maxPos) {
        this.maxPosition = maxPos;
    }

    protected void toggleMotor(GamepadTask.EventKind kind)
    {
        int currPosition;
        boolean positionWithinLimits = true;   //when the position is in limit

        switch (kind) {
        case BUTTON_A_DOWN:
        case BUTTON_B_DOWN:
        case BUTTON_X_DOWN:
        case BUTTON_Y_DOWN:
        case LEFT_BUMPER_DOWN:
        case RIGHT_BUMPER_DOWN:
        case LEFT_TRIGGER_DOWN:
        case RIGHT_TRIGGER_DOWN:

            if (minPosition != MINMAX_POSITION_NOT_SET) {
                currPosition = motor.getCurrentPosition();  //if the position is at a certain number which is the min height then it will stop
                if (currPosition < minPosition) {
                    positionWithinLimits = false;
                }
            } else if (maxPosition != MINMAX_POSITION_NOT_SET) {
                currPosition = motor.getCurrentPosition();
                if (currPosition > maxPosition) {           //if the postion is at the max it stop
                    positionWithinLimits = false;
                }
            } else {
                positionWithinLimits = true;
            }
            if (positionWithinLimits) {  //if the position is within the limits then we give power to the motor
                motor.setPower(power);
                robot.queueEvent(new DeadmanMotorEvent(this, EventKind.DEADMAN_BUTTON_DOWN));
            } else {
                motor.setPower(0.0);
                robot.queueEvent(new DeadmanMotorEvent(this, EventKind.DEADMAN_BUTTON_UP));

            }
            motor.setPower(power);
            robot.queueEvent(new DeadmanMotorEvent(this, EventKind.DEADMAN_BUTTON_DOWN));
            /*if (useHoldMotorPositionTask) {
                robot.removeTask(holdPositionTask);
            }*/
            break;

        case BUTTON_A_UP:
        case BUTTON_B_UP:
        case BUTTON_X_UP:
        case BUTTON_Y_UP:
        case LEFT_BUMPER_UP:
        case RIGHT_BUMPER_UP:
        case LEFT_TRIGGER_UP:
        case RIGHT_TRIGGER_UP:
            motor.setPower(0.0);
            robot.queueEvent(new DeadmanMotorEvent(this, EventKind.DEADMAN_BUTTON_UP));
            if (useHoldMotorPositionTask) {
                robot.addTask(holdPositionTask);
            }
            break;
        }
    }

    @Override
    public void start()
    {
        motor.setPower(1.0); //changed from 0.0

        robot.addTask(new GamepadTask(robot, gamepad) {
            @Override
            public void handleEvent(RobotEvent e)
            {
                GamepadEvent event = (GamepadEvent)e;

                if (isButtonTracked(event.kind)) {
                    toggleMotor(event.kind);
                }

            }
        });
    }

    @Override
    public void stop()
    {
        motor.setPower(0.0);
    }

    @Override
    public boolean timeslice() {
       return done;
    }


}
