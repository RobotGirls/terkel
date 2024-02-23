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

import android.util.EventLog;

import com.qualcomm.robotcore.hardware.Gamepad;

public class GamepadTask extends RobotTask {

    public enum GamepadNumber {
        GAMEPAD_1,
        GAMEPAD_2,
    };

    public enum EventKind {
        BUTTON_A_DOWN,
        BUTTON_A_UP,
        BUTTON_B_DOWN,
        BUTTON_B_UP,
        BUTTON_X_DOWN,
        BUTTON_X_UP,
        BUTTON_Y_DOWN,
        BUTTON_Y_UP,
        LEFT_BUMPER_DOWN,
        LEFT_BUMPER_UP,
        RIGHT_BUMPER_DOWN,
        RIGHT_BUMPER_UP,
        LEFT_TRIGGER_DOWN,
        LEFT_TRIGGER_UP,
        RIGHT_TRIGGER_DOWN,
        RIGHT_TRIGGER_UP,
        DPAD_LEFT_DOWN,
        DPAD_LEFT_UP,
        DPAD_RIGHT_DOWN,
        DPAD_RIGHT_UP,
        DPAD_UP_DOWN,
        DPAD_UP_UP,
        DPAD_DOWN_DOWN,
        DPAD_DOWN_UP,
        LEFT_STICK_DOWN,
        LEFT_STICK_UP,
        LEFT_STICK_RIGHT,
        LEFT_STICK_LEFT,
        RIGHT_STICK_DOWN,
        RIGHT_STICK_UP,
        RIGHT_STICK_RIGHT,
        RIGHT_STICK_LEFT,
        LEFT_STICK_NEUTRAL,
        RIGHT_STICK_NEUTRAL;

    };

    public class GamepadEvent extends RobotEvent {

        public EventKind kind;

        public GamepadEvent(RobotTask task, EventKind k)
        {
            super(task);
            kind = k;
        }

        @Override
        public String toString()
        {
            return (super.toString() + "Gamepad Event " + kind);
        }
    }

    protected class ButtonState {
        public boolean a_pressed;
        public boolean b_pressed;
        public boolean x_pressed;
        public boolean y_pressed;
        public boolean left_bumper_pressed;
        public boolean right_bumper_pressed;
        public boolean left_trigger_pressed;
        public boolean right_trigger_pressed;
        public boolean dpad_left_pressed;
        public boolean dpad_right_pressed;
        public boolean dpad_up_pressed;
        public boolean dpad_down_pressed;
        public boolean left_stick_down;
        public boolean left_stick_up;
        public boolean left_stick_right;
        public boolean left_stick_left;
        public boolean right_stick_down;
        public boolean right_stick_up;
        public boolean right_stick_left;
        public boolean right_stick_right;
        public boolean right_stick_neutral;
        public boolean left_stick_neutral;


    }

    protected GamepadNumber gamepadNum;
    protected ButtonState buttonState;

    protected final static float TRIGGER_DEADZONE = 0.3f;

    public GamepadTask(Robot robot, GamepadNumber gamepadNum)
    {
        super(robot);

        this.buttonState = new ButtonState();
        this.buttonState.a_pressed = false;
        this.buttonState.b_pressed = false;
        this.buttonState.x_pressed = false;
        this.buttonState.y_pressed = false;
        this.buttonState.left_bumper_pressed   = false;
        this.buttonState.right_bumper_pressed  = false;
        this.buttonState.left_trigger_pressed  = false;
        this.buttonState.right_trigger_pressed = false;

        this.buttonState.dpad_up_pressed    = false;
        this.buttonState.dpad_down_pressed    = false;
        this.buttonState.dpad_left_pressed    = false;
        this.buttonState.dpad_right_pressed    = false;

        this.buttonState.left_stick_down = false;
        this.buttonState.left_stick_up = false;
        this.buttonState.left_stick_left = false;
        this.buttonState.left_stick_right = false;
        this.buttonState.right_stick_down = false;
        this.buttonState.right_stick_up = false;
        this.buttonState.right_stick_left = false;
        this.buttonState.right_stick_right = false;
        this.buttonState.left_stick_neutral = false;
        this.buttonState.right_stick_neutral = false;

        this.gamepadNum = gamepadNum;
    }

    @Override
    public void start()
    {
        // TODO: ??
    }

    @Override
    public void stop()
    {
    }

    /*
     * Process gamepad actions and send them to the robot as events.
     *
     * Note that these are not state changes, but is designed to send a
     * continual stream of events as long as the button is pressed (hmmm,
     * this may not be a good idea if software can't keep up).
     */
    @Override
    public boolean timeslice()
    {
        Gamepad gamepad;

        /*
         * I thought Java passed objects by reference, but oddly enough if you cache
         * the gamepad in the task's contstructor, it will never update.  Hence this.
         */
        if (gamepadNum == GamepadNumber.GAMEPAD_1) {
            gamepad = robot.gamepad1;
        } else {
            gamepad = robot.gamepad2;
        }

        if ((gamepad.a) && (buttonState.a_pressed == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.BUTTON_A_DOWN));
            buttonState.a_pressed = true;
        } else if ((!gamepad.a) && (buttonState.a_pressed == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.BUTTON_A_UP));
            buttonState.a_pressed = false;
        }

        if ((gamepad.b) && (buttonState.b_pressed == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.BUTTON_B_DOWN));
            buttonState.b_pressed = true;
        } else if ((!gamepad.b) && (buttonState.b_pressed == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.BUTTON_B_UP));
            buttonState.b_pressed = false;
        }

        if ((gamepad.x) && (buttonState.x_pressed == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.BUTTON_X_DOWN));
            buttonState.x_pressed = true;
        } else if ((!gamepad.x) && (buttonState.x_pressed == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.BUTTON_X_UP));
            buttonState.x_pressed = false;
        }

        if ((gamepad.y) && (buttonState.y_pressed == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.BUTTON_Y_DOWN));
            buttonState.y_pressed = true;
        } else if ((!gamepad.y) && (buttonState.y_pressed == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.BUTTON_Y_UP));
            buttonState.y_pressed = false;
        }

        if ((gamepad.left_bumper) && (buttonState.left_bumper_pressed == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.LEFT_BUMPER_DOWN));
            buttonState.left_bumper_pressed = true;
        } else if ((!gamepad.left_bumper) && (buttonState.left_bumper_pressed == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.LEFT_BUMPER_UP));
            buttonState.left_bumper_pressed = false;
        }

        if ((gamepad.right_bumper) && (buttonState.right_bumper_pressed == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.RIGHT_BUMPER_DOWN));
            buttonState.right_bumper_pressed = true;
        } else if ((!gamepad.right_bumper) && (buttonState.right_bumper_pressed == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.RIGHT_BUMPER_UP));
            buttonState.right_bumper_pressed = false;
        }

        if ((gamepad.left_trigger >= TRIGGER_DEADZONE) && (buttonState.left_trigger_pressed == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.LEFT_TRIGGER_DOWN));
            buttonState.left_trigger_pressed = true;
        } else if ((gamepad.left_trigger < TRIGGER_DEADZONE) && (buttonState.left_trigger_pressed == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.LEFT_TRIGGER_UP));
            buttonState.left_trigger_pressed = false;
        }

        if ((gamepad.right_trigger >= TRIGGER_DEADZONE) && (buttonState.right_trigger_pressed == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.RIGHT_TRIGGER_DOWN));
            buttonState.right_trigger_pressed = true;
        } else if ((gamepad.right_trigger < TRIGGER_DEADZONE) && (buttonState.right_trigger_pressed == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.RIGHT_TRIGGER_UP));
            buttonState.right_trigger_pressed = false;
        }

        if ((gamepad.dpad_left) && (buttonState.dpad_left_pressed == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.DPAD_LEFT_DOWN));
            buttonState.dpad_left_pressed = true;
        } else if ((!gamepad.dpad_left) && (buttonState.dpad_left_pressed == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.DPAD_LEFT_UP));
            buttonState.dpad_left_pressed = false;
        }

        if ((gamepad.dpad_right) && (buttonState.dpad_right_pressed == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.DPAD_RIGHT_DOWN));
            buttonState.dpad_right_pressed = true;
        } else if ((!gamepad.dpad_right) && (buttonState.dpad_right_pressed == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.DPAD_RIGHT_UP));
            buttonState.dpad_right_pressed = false;
        }

        if ((gamepad.dpad_up) && (buttonState.dpad_up_pressed == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.DPAD_UP_DOWN));
            buttonState.dpad_up_pressed = true;
        } else if ((!gamepad.dpad_up) && (buttonState.dpad_up_pressed == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.DPAD_UP_UP));
            buttonState.dpad_up_pressed = false;
        }

        if ((gamepad.dpad_down) && (buttonState.dpad_down_pressed == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.DPAD_DOWN_DOWN));
            buttonState.dpad_down_pressed = true;
        } else if ((!gamepad.dpad_down) && (buttonState.dpad_down_pressed == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.DPAD_DOWN_UP));
            buttonState.dpad_down_pressed = false;
        }

        //left side -----------------------------------------------------------------
        if ((gamepad.left_stick_x > 0.5 ) && (buttonState.left_stick_right == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.LEFT_STICK_RIGHT));
            buttonState.left_stick_right = true;
        } else if (((gamepad.left_stick_x < 0.5)) && (buttonState.left_stick_right == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.LEFT_STICK_NEUTRAL));
            buttonState.left_stick_right = false;
        }

        if ((gamepad.left_stick_x < -0.5 ) && (buttonState.left_stick_left == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.LEFT_STICK_LEFT));
            buttonState.left_stick_left = true;
        } else if (((gamepad.left_stick_x > -0.5)) && (buttonState.left_stick_left == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.LEFT_STICK_NEUTRAL));
            buttonState.left_stick_left = false;
        }

        if ((gamepad.left_stick_y < -0.5 ) && (buttonState.left_stick_up == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.LEFT_STICK_UP));
            buttonState.left_stick_up = true;
        } else if (((gamepad.left_stick_y > -0.5)) && (buttonState.left_stick_up == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.LEFT_STICK_NEUTRAL));
            buttonState.left_stick_up = false;
        }

        if ((gamepad.left_stick_y > 0.5 ) && (buttonState.left_stick_down == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.LEFT_STICK_DOWN));
            buttonState.left_stick_down = true;
        } if (((gamepad.left_stick_y < 0.5)) && (buttonState.left_stick_down == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.LEFT_STICK_NEUTRAL));
            buttonState.left_stick_down = false;
        }

        //right side -----------------------------------------------------------------
        if ((gamepad.right_stick_x > 0.5 ) && (buttonState.right_stick_right == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.RIGHT_STICK_RIGHT));
            buttonState.right_stick_right = true;
        } else if (((gamepad.right_stick_x < 0.5)) && (buttonState.right_stick_right == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.RIGHT_STICK_NEUTRAL));
            buttonState.right_stick_right = false;
        }

        if ((gamepad.right_stick_x < -0.5 ) && (buttonState.right_stick_left == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.RIGHT_STICK_LEFT));
            buttonState.right_stick_left = true;
        } else if (((gamepad.right_stick_x > -0.5)) && (buttonState.right_stick_left == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.RIGHT_STICK_NEUTRAL));
            buttonState.right_stick_left = false;
        }

        if ((gamepad.right_stick_y < -0.5) && (buttonState.right_stick_up == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.RIGHT_STICK_UP));
            buttonState.right_stick_up = true;
        } else if (((gamepad.right_stick_y > -0.5)) && (buttonState.right_stick_up == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.RIGHT_STICK_NEUTRAL));
            buttonState.right_stick_up = false;
        }

        if ((gamepad.right_stick_y > 0.5) && (buttonState.right_stick_down == false)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.RIGHT_STICK_DOWN));
            buttonState.right_stick_down = true;
        } else if (((gamepad.right_stick_y < 0.5)) && (buttonState.right_stick_down == true)) {
            robot.queueEvent(new GamepadEvent(this, EventKind.RIGHT_STICK_NEUTRAL));
            buttonState.right_stick_down = false;
        }
        /*
         * This task lives forever.
         */
        return false;
    }
}
