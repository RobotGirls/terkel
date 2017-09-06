
package team25core;

/*
 * FTC Team 25: izzielau, September 27, 2015
 */

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

public class TankDriveTask extends RobotTask {

    protected Robot robot;
    protected Drivetrain drivetrain;

    public double right;
    public double left;
    public double slowMultiplier = 1;

    public TankDriveTask(Robot robot, Drivetrain drivetrain)
    {
        super(robot);

        this.robot = robot;
        this.drivetrain = drivetrain;
    }

    private void getJoystick()
    {
        Gamepad gamepad = robot.gamepad1;

        left  = -gamepad.left_stick_y * slowMultiplier;
        right = gamepad.right_stick_y * slowMultiplier;
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
        robot.removeTask(this);
    }

    @Override
    public boolean timeslice()
    {
        getJoystick();

        drivetrain.setPowerLeft(left);
        drivetrain.setPowerRight(right);
        return false;
    }



}