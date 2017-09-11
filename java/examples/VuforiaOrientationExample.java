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

import opmodes.Daisy;
import team25core.FourWheelDirectDrivetrain;
import team25core.NavigateToTargetTask;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.RobotNavigation;
import team25core.TwoWheelDirectDrivetrain;

@Autonomous(name = "LAMEINGO RETURNS: VUFORIA ORIENTATION", group = "Team 25")
@Disabled
public class VuforiaOrientationExample extends Robot
{
    enum TargetState {
        FIND_TARGET,
        LOST_TARGET,
        INITIAL_APPROACH,
        AT_TARGET,
        ALIGNED,
    };

    DcMotor frontLeft;
    DcMotor frontRight;
    TwoWheelDirectDrivetrain twoWheelDrive;
    RobotNavigation nav;
    TargetState state;
    double bearing;

    private static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = Daisy.CAMERA_CHOICE;

    @Override
    public void init()
    {
        frontLeft   = hardwareMap.dcMotor.get("left");
        frontRight  = hardwareMap.dcMotor.get("right");
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        twoWheelDrive = new TwoWheelDirectDrivetrain(Daisy.TICKS_PER_INCH, frontRight, frontLeft);

        // *rap break*
        // yo yo yo
        // cappuccino
        // lizzie plays the piano
        // hellooooooooo!!!!
        // chipotle from zzzzeeee

        nav = new RobotNavigation(this, twoWheelDrive);
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = Daisy.KEY;
        parameters.cameraDirection = CAMERA_CHOICE;
        parameters.useExtendedTracking = false;

        VuforiaLocalizer vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        VuforiaTrackables targets = vuforia.loadTrackablesFromAsset("FTC_2016-17");
        targets.get(0).setName("Blue Near");
        targets.get(1).setName("Red Far");
        targets.get(2).setName("Blue Far");
        targets.get(3).setName("Red Near");

        OpenGLMatrix phoneLocationOnRobot = Daisy.PHONE_LOCATION_ON_ROBOT;
        nav.initVuforia(targets, parameters, phoneLocationOnRobot);

        nav.activateTracking();
    }

    @Override
    public void start()
    {
        state = TargetState.FIND_TARGET;
    }

    @Override
    public void init_loop()
    {
        nav.targetsAreVisible();
        nav.addNavTelemetry();
    }

    @Override
    public void loop()
    {
        nav.targetIsVisible(3);
    }

    @Override
    public void handleEvent(RobotEvent e)
    {
        //Nothing.
    }
}
