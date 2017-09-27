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

package examples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import team25core.Alliance;
import team25core.FourWheelDirectDrivetrain;
import team25core.NavigateToTargetTask;
import team25core.Robot;
import team25core.RobotEvent;

@Autonomous(name = "VuforiaTargetTracking")
@Disabled
public class VuforiaTargetTrackingExample extends Robot {

    NavigateToTargetTask nttt;
    FourWheelDirectDrivetrain drivetrain;
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor rearLeft;
    DcMotor rearRight;

    // Select which camera you want use.  The FRONT camera is the one on the same side as the screen.  Alt. is BACK
    private static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = VuforiaLocalizer.CameraDirection.FRONT;

    @Override
    public void init()
    {
        super.init();

        frontLeft   = hardwareMap.dcMotor.get("rearRight");
        frontRight  = hardwareMap.dcMotor.get("rearLeft");
        rearLeft    = hardwareMap.dcMotor.get("frontRight");
        rearRight   = hardwareMap.dcMotor.get("frontLeft");
        drivetrain = new FourWheelDirectDrivetrain(frontRight, rearRight, frontLeft, rearLeft);

        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);  // Use this line to see camera display
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();                             // OR... Use this line to improve performance

        // Get your own Vuforia key at  https://developer.vuforia.com/license-manager
        // and paste it here...
        parameters.vuforiaLicenseKey = "Afbu2Uv/////AAAAGVouNdSAD0P8la+sq37vCdQ6uLVH8NWrBLnfZ1R5rObJQpVVHJzqvIgMZO5gTqXG6DYJZcgwtSVZXU2g20FAJobxCog9Wc5vtqgJJmrsJ0NOABRbi9vy4Y9IzBVfaDoRsQTmjxxFf62Z9slttsb44KopGpVGTQ83iHnTo/wDvnZBWRhmckG6IKuqkbRYCFD+w1hHvVLuDoIYLgfpa1Rw1Pc7rszP/CDzUfeO9KwodFpEsfZHIZI8KHIYzfRIOhg1Tg0T4eRsLCO8s9vfZd6vfTuUA/sZkID3N7BsrlLaL6vUqheGPvsbPuQQsMqgPNYTqbhvv3KI/SR5WxUaccuVHnpVMhAjkdpruWVliCCZqp1t";

        parameters.cameraDirection = CAMERA_CHOICE;
        parameters.useExtendedTracking = false;
        VuforiaLocalizer vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        /**
         * Load the data sets that for the trackable objects we wish to track.
         * These particular data sets are stored in the 'assets' part of our application
         * They represent the four image targets used in the 2016-17 FTC game.
         */
        VuforiaTrackables targets = vuforia.loadTrackablesFromAsset("FTC_2016-17");
        targets.get(0).setName("Blue Near");
        targets.get(1).setName("Red Far");
        targets.get(2).setName("Blue Far");
        targets.get(3).setName("Red Near");

        /**
         * Create a transformation matrix describing where the phone is on the robot.
         *
         * The coordinate frame for the robot looks the same as the field.
         * The robot's "forward" direction is facing out along X axis, with the LEFT side facing out along the Y axis.
         * Z is UP on the robot.  This equates to a bearing angle of Zero degrees.
         *
         * The phone starts out lying flat, with the screen facing Up and with the physical top of the phone
         * pointing to the LEFT side of the Robot.  If we consider that the camera and screen will be
         * in "Landscape Mode" the upper portion of the screen is closest to the front of the robot.
         *
         * If using the rear (High Res) camera:
         * We need to rotate the camera around it's long axis to bring the rear camera forward.
         * This requires a negative 90 degree rotation on the Y axis
         *
         * If using the Front (Low Res) camera
         * We need to rotate the camera around it's long axis to bring the FRONT camera forward.
         * This requires a Positive 90 degree rotation on the Y axis
         *
         * Next, translate the camera lens to where it is on the robot.
         */

        final int CAMERA_FORWARD_DISPLACEMENT  = 146;   // Distance in mm in front of robot center
        final int CAMERA_VERTICAL_DISPLACEMENT = 190;   // Distance in mm above ground
        final int CAMERA_LEFT_DISPLACEMENT     = 1;     // Distance to the left of the robots center line

        OpenGLMatrix phoneLocationOnRobot = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.YZX,
                        AngleUnit.DEGREES, CAMERA_CHOICE == VuforiaLocalizer.CameraDirection.FRONT ? 90 : -90, 0, 0));

        nttt = new NavigateToTargetTask(this, drivetrain, NavigateToTargetTask.Targets.RED_NEAR, 300000, gamepad1, Alliance.RED);
        nttt.init(targets, parameters, phoneLocationOnRobot);
        addTask(nttt);

        /**
         * If we can't see the target we will drive straight until we do.
         */
        nttt.setFindMethod(NavigateToTargetTask.FindMethod.APPROACH_STRAIGHT);
    }

    @Override
    public void start()
    {
        nttt.findTarget();
    }

    @Override
    public void handleEvent(RobotEvent e)
    {
        NavigateToTargetTask.NavigateToTargetEvent event = (NavigateToTargetTask.NavigateToTargetEvent) e;
        if (event.kind == NavigateToTargetTask.EventKind.FOUND_TARGET) {
            RobotLog.i("141 Found target");
        } else if (event.kind == NavigateToTargetTask.EventKind.TIMEOUT) {
            RobotLog.i("141 Timeout");
        }
    }
}
