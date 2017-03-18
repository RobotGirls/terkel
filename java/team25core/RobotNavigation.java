package team25core;

import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

/**
 * This is NOT an opmode.
 *
 * This class is used to define all the specific navigation tasks for the Target Tracking Demo
 * It focuses on setting up and using the Vuforia Library, which is part of the 2016-2017 FTC SDK
 *
 * Once a target is identified, its information is displayed as telemetry data.
 * To approach the target, three motion priorities are created:
 * - Priority #1 Rotate so the robot is pointing at the target (for best target retention).
 * - Priority #2 Drive laterally based on distance from target center-line
 * - Priority #3 Drive forward based on the desired target standoff distance
 *
 */

public class RobotNavigation
{
    // Constants
    private static final int     MAX_TARGETS    =   4;
    private static double        ON_AXIS        =   1;      // Within 4.0 cm of target center-line
    private static final double  CLOSE_ENOUGH   =   7;      // Within 7.0 cm of final target standoff
    private static final double  HEAD_ON   =  3;            // Within 3.0 cm of final target standoff

    private  double yawGain     =  0.002;   // Rate at which we respond to heading error
    private  double lateralGain =  0.0015;  // Rate at which we respond to off-axis error
    private  double axialGain   =  0.0006;  // Rate at which we respond to target distance errors

    /* Private class members. */
    private Robot               myOpMode;       // Access to the OpMode object
    private Drivetrain          drivetrain;
    private VuforiaTrackables   targets;        // List of active targets

    // Navigation data is only valid if targetFound == true;
    private boolean             targetFound;    // set to true if Vuforia is currently tracking a target
    private String              targetName;     // Name of the currently tracked target
    private double              robotX;         // X displacement from target center
    private double              robotY;         // Y displacement from target center
    private double              robotBearing;   // Robot's rotation around the Z axis (CCW is positive)
    private double              targetRange;    // Range from robot's center to target in mm
    private double              targetBearing;  // Heading of the target , relative to the robot's unrotated center
    private double              relativeBearing;// Heading to the target from the robot's current bearing.
                                                //   eg: a Positive RelativeBearing means the robot must turn CCW to point at the target image.

    public RobotNavigation(Robot robot, Drivetrain drivetrain) {

        // Save reference to OpMode and Hardware map
        this.myOpMode = robot;
        this.drivetrain = drivetrain;

        targetFound = false;
        targetName = null;
        targets = null;

        robotX = 0;
        robotY = 0;
        targetRange = 0;
        targetBearing = 0;
        robotBearing = 0;
        relativeBearing = 0;
    }

    /***
     * Send telemetry data to indicate navigation status
     */
    public void addNavTelemetry() {
        if (targetFound)
        {
            // Display the current visible target name, robot info, target info, and required robot action.
            myOpMode.telemetry.addData("Visible", targetName);
            myOpMode.telemetry.addData("Robot", "[X]:[Y] (B) [%5.0fmm]:[%5.0fmm] (%4.0f°)",
                    robotX, robotY, robotBearing);
            myOpMode.telemetry.addData("Target", "[R] (B):(RB) [%5.0fmm] (%4.0f°):(%4.0f°)",
                    targetRange, targetBearing, relativeBearing);
            myOpMode.telemetry.addData("- Turn    ", "%s %4.0f°",  relativeBearing < 0 ? ">>> CW " : "<<< CCW", Math.abs(relativeBearing));
            myOpMode.telemetry.addData("- Strafe  ", "%s %5.0fmm", robotY < 0 ? "LEFT" : "RIGHT", Math.abs(robotY));
            myOpMode.telemetry.addData("- Distance", "%5.0fmm", Math.abs(robotX));
            myOpMode.telemetry.addData("- Relative bearing", relativeBearing);
            myOpMode.telemetry.addData("- Target bearing", targetBearing);
            myOpMode.telemetry.addData("- Robot bearing", robotBearing);
        }
        else
        {
            myOpMode.telemetry.addData("Visible", "- - - -" );
        }
    }

    /***
     * Start tracking Vuforia images
     */
    public void activateTracking()
    {

        // Start tracking any of the defined targets
        if (targets != null)
            targets.activate();
    }

    public double getRelativeBearing()
    {
        return relativeBearing;
    }

    public double getRobotBearing()
    {
        return robotBearing;
    }

    public double getDistance()
    {
        return Math.abs(robotX);
    }

    public double getStrafe()
    {
        return robotY;
    }

    public void setGainParams(double yaw, double axial, double lateral)
    {
        yawGain = yaw;
        axialGain = axial;
        lateralGain = lateral;
    }

    public void setMaxLateralOffset(int offset)
    {
        ON_AXIS = offset;
    }

    /***
     * use target position to determine the best way to approach it.
     * Set the Axial, Lateral and Yaw axis motion values to get us there.
     *
     * @return true if we are close to target
     * @param standOffDistance how close do we get the center of the robot to target (in mm)
     */
    public boolean cruiseControl(double standOffDistance)
    {
        boolean closeEnough;

        // Priority #1 Rotate to always be pointing at the target (for best target retention).
        double Y  = (relativeBearing * yawGain);

        // Priority #2  Drive laterally based on distance from X axis (same as y value)
        double L  = (robotY * lateralGain);

        // Priority #3 Drive forward based on the desiredHeading target standoff distance
        double A  = (-(robotX + standOffDistance) * axialGain);

        /*
        // Determine if we are close enough to the target for action.
        closeEnough = ((Math.abs(robotX + standOffDistance) < CLOSE_ENOUGH) &&
                       (Math.abs(robotY) < ON_AXIS));
        */

        if (L != 0) {
            closeEnough = Math.abs(robotY) < ON_AXIS;
            RobotLog.i("141 L!= 0 Lateral");
        } else if (A != 0) {
           closeEnough = Math.abs(robotX + standOffDistance) < CLOSE_ENOUGH;
            RobotLog.i("141 A!= 0 Axial");
        } else {
            closeEnough = Math.abs(relativeBearing) < HEAD_ON;
            // closeEnough = Math.abs(robotBearing) < HEAD_ON;
            RobotLog.i("141 Y!= 0 Rotational");
        }

        if (!closeEnough) {
            RobotLog.i("141 Axial power %f", A);
            RobotLog.i("141 Lateral power %f", L);
            RobotLog.i("141 Yaw power %f", Y);
            drivetrain.move(A, L, Y);
        } else {
            RobotLog.i("141 Close Enough");
            drivetrain.stop();
        }
        return (closeEnough);
    }

    public boolean onTarget()
    {
        if ((relativeBearing <= 1) && (relativeBearing >= -1)) {
            return true;
        } else {
            return false;
        }
    }

    /***
     * Initialize the Target Tracking and navigation interface
     */
    public void initVuforia(VuforiaTrackables targets, VuforiaLocalizer.Parameters parameters, OpenGLMatrix phoneLocationOnRobot)
    {
        this.targets = targets;

        /** For convenience, gather together all the trackable objects in one easily-iterable collection */
        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targets);

        // create an image translation/rotation matrix to be used for all images
        // Essentially put all the image centers 6" above the 0:0:0 origin,
        // but rotate them so they along the -X axis.
        OpenGLMatrix targetOrientation = OpenGLMatrix
                .translation(0, 0, 150)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ,
                        AngleUnit.DEGREES, 90, 0, -90));

        // Set the all the targets to have the same location and camera orientation
        for (VuforiaTrackable trackable : allTrackables)
        {
            trackable.setLocation(targetOrientation);
            ((VuforiaTrackableDefaultListener)trackable.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        }
    }


    /***
     * See if any of the vision targets are in sight.
     *
     * @return true if any target is found
     */
    public boolean targetsAreVisible()
    {
        int targetTestID = 0;

        // Check each target in turn, but stop looking when the first target is found.
        while ((targetTestID < MAX_TARGETS) && !targetIsVisible(targetTestID)) {
            targetTestID++ ;
        }

        return (targetFound);
    }

    /***
     * Determine if specified target ID is visible and
     * If it is, retreive the relevant data, and then calculate the Robot and Target locations
     *
     * @param   targetId
     * @return  true if the specified target is found
     */
    public boolean targetIsVisible(int targetId)
    {
        VuforiaTrackable target = targets.get(targetId);
        VuforiaTrackableDefaultListener listener = (VuforiaTrackableDefaultListener)target.getListener();
        OpenGLMatrix location  = null;

        // if we have a target, look for an updated robot position
        if ((target != null) && (listener != null) && listener.isVisible()) {
            targetFound = true;
            targetName = target.getName();

            // If we have an updated robot location, update all the relevant tracking information
            location  = listener.getUpdatedRobotLocation();
            if (location != null) {

                // Create a translation and rotation vector for the robot.
                VectorF trans = location.getTranslation();
                Orientation rot = Orientation.getOrientation(location, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);

                // Robot position is defined by the standard Matrix translation (x and y)
                robotX = trans.get(0);
                robotY = trans.get(1);

                // Robot bearing (in +vc CCW cartesian system) is defined by the standard Matrix z rotation
                robotBearing = rot.thirdAngle;

                // target range is based on distance from robot position to origin.
                targetRange = Math.hypot(robotX, robotY);

                // target bearing is based on angle formed between the X axis to the target range line
                targetBearing = Math.toDegrees(-Math.asin(robotY / targetRange));

                // Target relative bearing is the target Heading relative to the direction the robot is pointing.
                relativeBearing = targetBearing - robotBearing;

                RobotLog.v("141 Bearings: Relative %f, Target %f, Robot %f", relativeBearing, targetBearing, robotBearing);
                RobotLog.v("141 Robot Position: X %f, Y %f", robotX, robotY);
            }
            targetFound = true;
        }
        else  {
            // Indicate that there is no target visible
            targetFound = false;
            targetName = "None";
        }

        return targetFound;
    }
}

