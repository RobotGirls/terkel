
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

import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.teamcode.R;

import static team25core.SingleShotColorSensorTask.OperatingMode.UNDEFINED;

public class SingleShotColorSensorTask extends RobotTask
{

    public enum OperatingMode {
        COMPARE,
        SINGLE,
        UNDEFINED,
    }

    public enum TargetColor {
        RED,
        GREEN,
        BLUE,
    }

    public enum EventKind {
        RED,
        BLUE,
        PURPLE,
        YES,
        NO,
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

    protected OperatingMode mode;
    protected View relativeLayout;
    protected boolean reflectColor;
    protected ColorSensor colorSensor;
    protected boolean bEnabled;
    protected int channelNumber;
    protected int count;
    protected int threshold = 287;
    protected int msDelay = 0;
    protected ElapsedTime delayTimer;
    protected TargetColor targetColor;
    protected DigitalChannelController cdim;

    public SingleShotColorSensorTask(Robot robot, ColorSensor colorSensor, DigitalChannelController cdim, boolean bEnabled, int channelNumber)
    {
        super(robot);
        this.colorSensor = colorSensor;
        this.cdim = cdim;
        this.bEnabled = bEnabled;
        this.channelNumber = channelNumber;
        this.mode = UNDEFINED;
    }

    public void setModeCompare(int threshold)
    {
        this.mode = OperatingMode.COMPARE;
        this.threshold = threshold;
    }

    public void setModeSingle(TargetColor color, int threshold)
    {
        this.mode = OperatingMode.SINGLE;
        this.targetColor = color;
        this.threshold = threshold;
    }

    public void setReflectColor(boolean on, HardwareMap hardwareMap)
    {
        this.relativeLayout = ((Activity) hardwareMap.appContext).findViewById(R.id.RelativeLayout);
        this.reflectColor = on;
    }

    public void setMsDelay(int msDelay)
    {
        this.msDelay = msDelay;
    }

    @Override
    public void start()
    {
        delayTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        count = 0;
        if (bEnabled) {
            cdim.setDigitalChannelState(channelNumber, bEnabled);
        }
    }

    @Override
    public void stop()
    {
    }

    protected void doReflectColor(int red, int green, int blue)
    {
        float hsvValues[] = {0F,0F,0F};
        final float values[] = hsvValues;

        RobotLog.i("251 Blue: " + blue);
        RobotLog.i("251 Red: " + red);

        Color.RGBToHSV(red * 8, green * 8, blue * 8, hsvValues);

        relativeLayout.post(new Runnable() {
            public void run() {
                relativeLayout.setBackgroundColor(Color.HSVToColor(0xff, values));
            }
        });
    }

    protected boolean doCompare(int red, int blue)
    {
        if (blue > red) {
            ColorSensorEvent blueEvent = new ColorSensorEvent(this, EventKind.BLUE);
            robot.queueEvent(blueEvent);
            return true;
        } else if (red > blue) {
            ColorSensorEvent redEvent = new ColorSensorEvent(this, EventKind.RED);
            robot.queueEvent(redEvent);
            return true;
        } else {
            ColorSensorEvent purpleEvent = new ColorSensorEvent(this, EventKind.PURPLE);
            robot.queueEvent(purpleEvent);
            return true;
        }
    }

    protected boolean doSingle(TargetColor color)
    {
        int colorVal = 0;
        ColorSensorEvent event;

        switch (targetColor) {
        case RED:
            colorVal = colorSensor.red();
            break;
        case GREEN:
            colorVal = colorSensor.green();
            break;
        case BLUE:
            colorVal = colorSensor.blue();
            break;
        }

        if (colorVal > threshold) {
            event = new ColorSensorEvent(this, EventKind.YES);
            robot.queueEvent(event);
            return true;
        } else {
            event = new ColorSensorEvent(this, EventKind.NO);
            robot.queueEvent(event);
            return true;
        }
    }

    @Override
    public boolean timeslice()
    {
        /**
         * The delay here is to allow a full integration cycle once the sensor reaches the physical read point.
         */
        if (delayTimer.time() <= msDelay) {
            return false;
        }

        int red = colorSensor.red();
        int green = colorSensor.red();
        int blue = colorSensor.blue();

        if (reflectColor) {
            doReflectColor(red, green, blue);
        }

        switch (mode) {
        case COMPARE:
            return doCompare(red, blue);
        case SINGLE:
            return doSingle(targetColor);
        case UNDEFINED:
            throw new IllegalArgumentException("Must specify operating mode");
        }

        /**
         * Should never get here.  If so, stop the task
         */
        RobotLog.e("Color sensor task unknown mode.  Stopping");
        return true;
    }
}
