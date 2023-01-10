
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

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Locale;

public class IMUGyroDriveTask extends RobotTask {

    public enum EventKind {
        THRESHOLD_80,
        THRESHOLD_90,
        THRESHOLD_95,
        HIT_TARGET,
        PAST_TARGET,
        ERROR_UPDATE,
    }

    public class IMUGyroEvent extends RobotEvent {
        public EventKind kind;
        public float val;

        public IMUGyroEvent(RobotTask task, EventKind kind) {
            super(task);
            this.kind = kind;
            this.val = 0.0f;
        }

        public IMUGyroEvent(RobotTask task, EventKind kind, float val) {
            super(task);
            this.kind = kind;
            this.val = val;
        }
    }

    BNO055IMU imu;

    protected int targetHeading = 0;
    protected int pt = 5;
    protected float slop = 3.0f;
    protected boolean showHeading = true;
    protected static float lastRead = 0.0f;


    protected IMUGyroEvent t_80;
    protected IMUGyroEvent t_90;
    protected IMUGyroEvent t_95;

    private Telemetry.Item headingTlm;
    private Telemetry.Item secondAngleTlm;
    private Telemetry.Item thirdAngleTlm;

    String myHeadingTlm;

    private Orientation angles;
    private Acceleration gravity;

    Telemetry.Item imuStatusTlm;
    Telemetry.Item imuCalibTlm;
    Telemetry.Item imuHeadingTlm;
    Telemetry.Item imuYawRateTlm;
    Telemetry.Item imuRollTlm;
    Telemetry.Item imuPitchTlm;
    Telemetry.Item imuGravTlm;

    private double heading;
    private double yawRate;
    private double roll;
    private double pitch;

    private int i = 0;
    private int j = 0;
    Telemetry.Item whereAmIGyro;

    private FourWheelDirectIMUDrivetrain drivetrain;

    public IMUGyroDriveTask(Robot robot, BNO055IMU imu, int targetHeading, boolean showHeading, Telemetry.Item heading)
    {
        super(robot);
        this.targetHeading = targetHeading;  // Think cardinal: negative is ccw, positive is cw.
        this.showHeading = showHeading;
        this.imu = imu;
        this.headingTlm = heading;

        // added this to get rid of error regarding telemetry
        this.headingTlm = robot.telemetry.addData("Current/target heading is: ", "none");
        this.secondAngleTlm = robot.telemetry.addData("Second angle is: ", "none");
        this.thirdAngleTlm = robot.telemetry.addData("Third angle is: ", "none");

    }
    public IMUGyroDriveTask(Robot robot, BNO055IMU imu, int targetHeading, boolean showHeading)
    {
        super(robot);
        this.targetHeading = targetHeading;  // Think cardinal: negative is ccw, positive is cw.
        this.showHeading = showHeading;
        this.imu = imu;

        this.headingTlm = robot.telemetry.addData("Current/target heading is: ", "none");
        this.secondAngleTlm = robot.telemetry.addData("Second angle is: ", "none");
        this.thirdAngleTlm = robot.telemetry.addData("Third angle is: ", "none");

    }

    public void setDrivetrain(FourWheelDirectIMUDrivetrain usedDrivetrain) {

        drivetrain = usedDrivetrain;

    }

    public void init()
    {
        initializeImu();
    }

    public void initializeImu()
    {
        // Set up the parameters with which we will use our IMU. Note that integration
        // algorithm here just reports accelerations to the logcat log; it doesn't actually
        // provide positional information.
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        // cindy commented out the following
        // parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.loggingEnabled      = true;
        // cindy added
        parameters.useExternalCrystal   = true;
        parameters.loggingTag          = "IMU";

        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu.initialize(parameters);

        angles  = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        gravity = imu.getGravity();

       // telemetry.setMsTransmissionInterval(100);
    }

    @Override
    public void start()
    {
    }

    @Override
    public void stop()
    {
    }

    String formatAngle(AngleUnit angleUnit, double angle) {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle));
    }

    String formatDegrees(double degrees){
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
    }


    public void getIMUValues() {
        heading = -imu.getAngularOrientation().firstAngle;
        roll = -imu.getAngularOrientation().secondAngle;
        pitch = -imu.getAngularOrientation().thirdAngle;
        yawRate = -imu.getAngularVelocity().yRotationRate;
        drivetrain.setCurrentYaw(heading);
    }
    public void displayTelemetry() {
        //telemetry.setAutoClear(false);
        //imuStatusTlm.setValue(imu.getSystemStatus().toString());
       // imuCalibTlm.setValue(imu.getCalibrationStatus().toString());

        i = i++;
        whereAmIGyro.setValue("displayTelemetry" + i);

        this.imuHeadingTlm.setValue(heading);

        this.imuRollTlm.setValue(roll);

        this.imuPitchTlm.setValue(pitch);

        this.imuYawRateTlm.setValue( yawRate );
        //imuGravTlm.setValue(gravity.toString());


    }

    public void initTelemetry(Telemetry telemetry) {
        telemetry.setAutoClear(false);
        imuStatusTlm = telemetry.addData("Status", imu.getSystemStatus().toString());
        imuCalibTlm = telemetry.addData("Calib", imu.getCalibrationStatus().toString());
        imuHeadingTlm = telemetry.addData("Heading", formatAngle(angles.angleUnit, angles.firstAngle));
        imuYawRateTlm = telemetry.addData("YawRate", formatAngle(angles.angleUnit, angles.firstAngle));

        imuRollTlm = telemetry.addData("Roll", formatAngle(angles.angleUnit, angles.secondAngle));
        imuPitchTlm = telemetry.addData("Pitch", formatAngle(angles.angleUnit, angles.thirdAngle));
        imuGravTlm = telemetry.addData("Grav", gravity.toString());
        whereAmIGyro = telemetry.addData("whereAmIGyro", "initTelemetry" );
        telemetry.setMsTransmissionInterval(100);
    }

    @Override
    public boolean timeslice()
    {
        boolean ret = false;
        Orientation currentHeading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        float fHeading = AngleUnit.DEGREES.fromUnit(currentHeading.angleUnit, currentHeading.firstAngle);
        double secondAngle = -imu.getAngularOrientation().secondAngle;
        double thirdAngle = -imu.getAngularOrientation().thirdAngle;

        // float thirdAngle = AngleUnit.DEGREES.fromUnit(currentHeading.angleUnit, currentHeading.thirdAngle);

        this.secondAngleTlm.setValue(secondAngle);
        this.thirdAngleTlm.setValue(thirdAngle);
        j = j + 1;
        String foo = "displayTelemetry" + j;
        whereAmIGyro.setValue(foo);
        getIMUValues();
        displayTelemetry();

        myHeadingTlm = currentHeading.toString();
//        if (showHeading) {
//            //robot.telemetry.addData("Current/target heading is: ", fHeading);
//            headingTlm.setValue(fHeading);
//        }

        // this.headingTlm.setValue(fHeading);
        this.headingTlm.setValue(myHeadingTlm);

        // IMUGyroEvent errorUpdate = new IMUGyroEvent(this, EventKind.ERROR_UPDATE, fHeading);
        // robot.queueEvent(errorUpdate);

//        if (fHeading <= Math.abs(slop)) {
//            IMUGyroEvent hitTarget = new IMUGyroEvent(this, EventKind.HIT_TARGET);
//            robot.queueEvent(hitTarget);
//            ret = true;
//        } else {
//            // Counter Clockwise rotation.
//            if ((lastRead > 0) && (fHeading < 0)) {
//                IMUGyroEvent pastTarget = new IMUGyroEvent(this, EventKind.PAST_TARGET);
//                robot.queueEvent(pastTarget);
//                ret = false;
//            } else if ((lastRead < 0) && (fHeading > 0)) {
//                // Clockwise rotation.
//                IMUGyroEvent pastTarget = new IMUGyroEvent(this, EventKind.PAST_TARGET);
//                robot.queueEvent(pastTarget);
//                ret = false;
//            }
//        }

        lastRead = fHeading;
        //return ret;
        return false;
    }
}
