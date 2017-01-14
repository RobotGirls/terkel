package team25core;

/*
 * FTC Team 25: cmacfarl, January 12, 2017
 */

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.R;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcontroller.external.samples.ConceptVuforiaNavigation.TAG;

public class NavigateToTargetTask extends RobotTask {

    public enum EventKind {
        FOUND_TARGET,
        TIMEOUT,
    }

    public class NavigateToTargetEvent extends RobotEvent {

        public EventKind kind;

        public NavigateToTargetEvent(RobotTask task, EventKind k)
        {
            super(task);
            kind = k;
        }

        @Override
        public String toString()
        {
            return (super.toString() + "NavigateToTarget Event " + kind);
        }
    }

    Robot_Navigation nav;
    Robot_MecanumDrive drivetrain;
    ElapsedTime timer;
    int timeout;

    float mmPerInch        = 25.4f;
    float mmBotWidth       = 18 * mmPerInch;            // ... or whatever is right for your robot
    float mmFTCFieldWidth  = (12*12 - 2) * mmPerInch;   // the FTC field is ~11'10" center-to-center of the glass panels

    public NavigateToTargetTask(Robot robot, int timeout)
    {
        super(robot);

        this.timeout = timeout;
    }

    protected void placeTargets()
    {
    }

    protected void placePhone()
    {
    }

    String format(OpenGLMatrix transformationMatrix) {
        return transformationMatrix.formatAsTransform();
    }

    public void init(DcMotor leftFront, DcMotor rightFront, DcMotor leftRear, DcMotor rightRear)
    {
        nav = new Robot_Navigation();
        drivetrain = new Robot_MecanumDrive(leftFront, rightFront, leftRear, rightRear);

        nav.initVuforia(this.robot, drivetrain);
        nav.activateTracking();
        nav.targetsAreVisible();
        nav.addNavTelemetry();
    }

    @Override
    public void start()
    {
        timer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
    }

    @Override
    public void stop()
    {
    }

    @Override
    public boolean timeslice()
    {
        if (timer.time() > timeout) {
            robot.queueEvent(new NavigateToTargetEvent(this, EventKind.TIMEOUT));
            return true;
        }

        if (nav.targetsAreVisible()) {
            if (nav.cruiseControl(400)) {
                robot.queueEvent(new NavigateToTargetEvent(this, EventKind.FOUND_TARGET));
                return true;
            }
            drivetrain.moveRobot();
        } else {
            drivetrain.rotateRobot(0.5);
        }
        nav.addNavTelemetry();
        return false;
    }
}
