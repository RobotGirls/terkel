
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

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.teamcode.R;

import static team25core.SingleShotColorSensorTask.OperatingMode.UNDEFINED;

public class ColorSensorTask extends RobotTask
{

    public enum EventKind {
        RED_DETECTED,
        BLUE_DETECTED,
        NO_COLOR_DETECTED,
    }

    public class ColorSensorEvent extends RobotEvent
    {
        public EventKind kind;

        public ColorSensorEvent(RobotTask task, EventKind kind)
        {
            super(task);
            this.kind = kind;
        }
    }

    protected ColorSensor colorSensor;
    protected int count;
    protected int threshold = 287;
    protected int msDelay = 0;
    protected ElapsedTime delayTimer;

    public ColorSensorTask(Robot robot, ColorSensor colorSensor)
    {
        super(robot);
        this.colorSensor = colorSensor;
    }

    @Override
    public void start()
    {
        delayTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        count = 0;
    }

    @Override
    public void stop()
    {
        robot.removeTask(this);
    }

    @Override
    public boolean timeslice()
    {
        ColorSensorEvent event;

        if (colorSensor.red() > threshold) {
            event = new ColorSensorEvent(this, EventKind.RED_DETECTED);
            robot.queueEvent(event);
        } else if (colorSensor.blue() > threshold){
            event = new ColorSensorEvent(this, EventKind.BLUE_DETECTED);
            robot.queueEvent(event);
        } else {
            event = new ColorSensorEvent(this, EventKind.NO_COLOR_DETECTED);
            robot.queueEvent(event);
        }

        return false;
    }
}
