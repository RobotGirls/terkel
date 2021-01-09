package team25core;

import com.qualcomm.robotcore.hardware.Gamepad;

import team25core.JoystickDriveControlScheme;
import team25core.MotorValues;

/*
Modified version of TankMechanumControlSchemeReverse, which was created by Breanna Chan.

Specifications:
must use left joystick to steer, right joystick to turn
 */

//DEV TO DO:
// -diagonal strafing (high priority)
// -speed-sensitive strafing (low priority)

public class SingleGamepadControlScheme implements JoystickDriveControlScheme {

    protected double fr; //front right motor
    protected double fl; //front left motor
    protected double br; //back right motor
    protected double bl; //back left motor
    protected double leftX;
    protected double rightX;
    protected double leftY;
    protected double rightY;
    protected Gamepad gamepad;

    //for use in cases where EITHER the left wheels or right wheels are mounted backward.
    //currently: left wheels are mounted backward, so the left wheel forward/backward values are reversed from normal
    protected final double LEFT_WHEELS_FORWARD = 1;
    protected double LEFT_WHEELS_BACKWARD = -1;
    protected double RIGHT_WHEELS_FORWARD = -1;
    protected double RIGHT_WHEELS_BACKWARD = 1;
    /* use the above variables in conjunction w/ reverse mechanum drive train abilities, as follows:
            STRAFE FORWARD:  all wheels forward
            STRAFE BACKWARD: all wheels backward
            STRAFE LEFT: fl backward, bl forward, fr forward, br backward
            STRAFE RIGHT: fl forward, bl backward, fr backward, br forward
            PIVOT RIGHT: left wheels go forward, right wheels go backward
            PIVOT LEFT: left wheels go backward, right wheels go forward,
            STRAFE DIAGONAL (FORWARD RIGHT): fl forward, br forward, bl and fr both @ 0
            STRAFE DIAGONAL (FORWARD LEFT): bl forward, fr forward, br and fl both @ 0
            STRAFE DIAGONAL (BACKWARD LEFT): fl backward, br backward, bl and fr both @ 0
            STRAFE DIAGONAL (BACKWARD RIGHT): bl backward, fr backward, br and fl both @ 0
     */


    public SingleGamepadControlScheme(Gamepad gamepad)
    {
        this.gamepad = gamepad;
    }

    public MotorValues getMotorPowers()
    {
        leftX = gamepad.left_stick_x;
        rightX = gamepad.right_stick_x;
        leftY = gamepad.left_stick_y;
        rightY = gamepad.right_stick_y;

        /*
        JOYSTICK GRID:
                    JOYSTICK UP: joystick-y < 0
                    JOYSTICK DOWN: joystick-y > 0
                    JOYSTICK LEFT: joystick-x > 0
                    JOYSTICK RIGHT: joystick-x < 0
         */

        if (leftX > 0.5 && rightY < -0.5) { //left joystick right and right joystick forward
            fl = LEFT_WHEELS_FORWARD;
            br = RIGHT_WHEELS_FORWARD;
        } else if (leftX < -0.5 && rightY < -0.5) { //left joystick left and right joystick forward
            fr = RIGHT_WHEELS_FORWARD;
            bl = LEFT_WHEELS_FORWARD;
        } else if (leftX > 0.5 && rightY > 0.5) {   //left joystick right and right joystick down

        } else if (leftY < -0.5) {          //left joystick forward
            //go forward
            fl = LEFT_WHEELS_FORWARD;
            bl = LEFT_WHEELS_FORWARD;
            fr = RIGHT_WHEELS_FORWARD;
            br = RIGHT_WHEELS_FORWARD;
        } else if (leftY > 0.5) {           //left joystick down
            //go backward
            fl = LEFT_WHEELS_BACKWARD;
            bl = LEFT_WHEELS_BACKWARD;
            fr = RIGHT_WHEELS_BACKWARD;
            br = RIGHT_WHEELS_BACKWARD;
        } else if (leftX > 0.5) {           // left joystick to the right
            //strafe right
            fl = LEFT_WHEELS_FORWARD;
            bl = LEFT_WHEELS_BACKWARD;
            fr = RIGHT_WHEELS_BACKWARD;
            br = RIGHT_WHEELS_FORWARD;
        } else if (leftX < -0.5) {          //left joystick to the left
            //strafe left
            fl = LEFT_WHEELS_BACKWARD;
            bl = LEFT_WHEELS_FORWARD;
            fr = RIGHT_WHEELS_FORWARD;
            br = RIGHT_WHEELS_BACKWARD;
        } else if (rightX > 0.5) {          //right joystick right
            //pivot right
            fl = LEFT_WHEELS_FORWARD;
            bl = LEFT_WHEELS_FORWARD;
            fr = RIGHT_WHEELS_BACKWARD;
            br = RIGHT_WHEELS_BACKWARD;
        } else if (rightX < -0.5) {         // right joystick left
            //pivot left
            fl = LEFT_WHEELS_BACKWARD;
            bl = LEFT_WHEELS_BACKWARD;
            fr = RIGHT_WHEELS_FORWARD;
            br = RIGHT_WHEELS_FORWARD;
        } else {                            //robot stops when no input
            fl = 0;
            bl = 0;
            fr = 0;
            br = 0;
        }

        return new MotorValues(fl, fr, bl, br);
    }
}
