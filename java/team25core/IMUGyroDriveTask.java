
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

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;

import org.firstinspires.ftc.robotcore.external.Telemetry;
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

    public IMUGyroDriveTask(Robot robot, BNO055IMU imu, int targetHeading, boolean showHeading, Telemetry.Item heading)
    {
        super(robot);
        this.targetHeading = targetHeading;  // Think cardinal: negative is ccw, positive is cw.
        this.showHeading = showHeading;
        this.imu = imu;
        this.headingTlm = heading;

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

    public void init()
    {
        initializeImu();
    }

    public void initializeImu()
    {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu.initialize(parameters);
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


        myHeadingTlm = currentHeading.toString();
//        if (showHeading) {
//            //robot.telemetry.addData("Current/target heading is: ", fHeading);
//            headingTlm.setValue(fHeading);
//        }

        // this.headingTlm.setValue(fHeading);
        this.headingTlm.setValue(myHeadingTlm);

        // IMUGyroEvent errorUpdate = new IMUGyroEvent(this, EventKind.ERROR_UPDATE, fHeading);
        // robot.queueEvent(errorUpdate);

        if (fHeading <= Math.abs(slop)) {
            IMUGyroEvent hitTarget = new IMUGyroEvent(this, EventKind.HIT_TARGET);
            robot.queueEvent(hitTarget);
            ret = true;
        } else {
            // Counter Clockwise rotation.
            if ((lastRead > 0) && (fHeading < 0)) {
                IMUGyroEvent pastTarget = new IMUGyroEvent(this, EventKind.PAST_TARGET);
                robot.queueEvent(pastTarget);
                ret = false;
            } else if ((lastRead < 0) && (fHeading > 0)) {
                // Clockwise rotation.
                IMUGyroEvent pastTarget = new IMUGyroEvent(this, EventKind.PAST_TARGET);
                robot.queueEvent(pastTarget);
                ret = false;
            }
        }

        lastRead = fHeading;
        return ret;
    }
}
