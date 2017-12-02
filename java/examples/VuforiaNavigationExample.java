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

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;

import team25core.Alliance;
import team25core.FourWheelDirectDrivetrain;
import team25core.NavigateToTargetTask;
import team25core.Robot;
import team25core.RobotEvent;

@Autonomous(name = "Vuforia Target Finding")
@Disabled
public class VuforiaNavigationExample extends Robot
{
    NavigateToTargetTask nttt;
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor rearLeft;
    DcMotor rearRight;
    FourWheelDirectDrivetrain drivetrain;

    @Override
    public void init()
    {
        frontLeft   = hardwareMap.dcMotor.get("rearRight");
        frontRight  = hardwareMap.dcMotor.get("rearLeft");
        rearLeft    = hardwareMap.dcMotor.get("frontRight");
        rearRight   = hardwareMap.dcMotor.get("frontLeft");
        drivetrain = new FourWheelDirectDrivetrain(frontRight, rearRight, frontLeft, rearLeft);
        nttt = new NavigateToTargetTask(this, drivetrain, NavigateToTargetTask.Targets.RED_NEAR, 300000, gamepad1, Alliance.RED);
        super.init();
    }

    @Override
    public void start()
    {
        super.start();
        addTask(nttt);
    }

    @Override
    public void handleEvent(RobotEvent e)
    {
        NavigateToTargetTask.NavigateToTargetEvent event = (NavigateToTargetTask.NavigateToTargetEvent) e;
        if (event.kind == NavigateToTargetTask.EventKind.FOUND_TARGET) {
            RobotLog.i("Found target");
        } else if (event.kind == NavigateToTargetTask.EventKind.TIMEOUT) {
            RobotLog.i("Timeout");
        }
    }
}
