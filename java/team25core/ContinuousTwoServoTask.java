package team25core;


import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad2;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import team25core.Robot;
import team25core.RobotTask;

public class ContinuousTwoServoTask extends RobotTask {
    protected Robot robot;
    protected Servo servo1;
    protected Servo servo2;

    public double right;
    public double left;
    public double ceiling;

    public boolean slow = false;
    public boolean useRightJoystick = false;
    public boolean ceilingOn = false;

    public double slowMultiplier = 0.5;

    public ContinuousTwoServoTask(Robot robot, Servo servo1, Servo servo2, boolean useRightJoystick) {
        super(robot);
        this.servo1 = servo1;
        this.servo2 = servo2;
        this.robot = robot;
        this.useRightJoystick = useRightJoystick;
    }

    private void getJoystick() {
        Gamepad gamepad = robot.gamepad2;

        left = -gamepad.left_stick_y * slowMultiplier;
        right = -gamepad.right_stick_y * slowMultiplier;
    }

    public void slowDown(boolean slow) {
        if (slow) {
            slowMultiplier = 0.5;
        } else {
            slowMultiplier = 1;
        }
    }
    //range for standard is 0-180

    public void slowDown(double mult) {
        slowMultiplier = mult;
    }

    @Override
    public void start() {
        // Nothing.
    }

    @Override
    public void stop() {
    }

    public void useCeiling(double ceiling) {
        ceilingOn = true;
        this.ceiling = ceiling;
    }
// continuous servo
// as regular servo :  range is 0.5 is not moving the range is 0 to 1
//                      where 0 is full power in one direction and 1 is
//                      full power in the other directions.
    // as a continuous CR servo.
    //
    @Override
    public boolean timeslice() {
        getJoystick();
        double joystickValue = useRightJoystick ? right : left;

        double breakPosition = 0.05;
        double servoPosition;

        if (Math.abs(joystickValue) < breakPosition) {
            servoPosition = 0.5;
        } else {
            servoPosition = 0.5 + (joystickValue * 0.5);
        }

        if (ceilingOn) {
            if (servoPosition > ceiling) {
                servoPosition = ceiling;
            } else if (servoPosition < (1 - ceiling)) {
                servoPosition = 1 - ceiling;
            }
        }

        telemetry.addData("ContinuousTwoServoTask Servo Position", servoPosition);
        servo1.setPosition(servoPosition);
        servo2.setPosition(servoPosition);

        return false;
    }
}
