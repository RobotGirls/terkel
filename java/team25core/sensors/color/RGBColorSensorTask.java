
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

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class RGBColorSensorTask extends RobotTask
{

    public enum EventKind {
        RED_DETECTED,
        BLUE_DETECTED,
        GREEN_DETECTED,
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
    protected int redthreshold = 287;
    protected int bluethreshold = 287;
    protected int greenthreshold = 287;
    protected int msDelay = 0;
    protected ElapsedTime delayTimer;

    // returns the values for blue, red, and green from the color sensor
    protected int[] colorArray = new int[3];

    public int[] getColors() {
        colorArray[0] = colorSensor.blue();
        colorArray[1] = colorSensor.red();
        colorArray[2] = colorSensor.green();
        return colorArray;
    }

    public RGBColorSensorTask(Robot robot, ColorSensor colorSensor)
    {
        super(robot);
        this.colorSensor = colorSensor;
    }

    // define maximum thresholds for blue, red, and green
    public void setThresholds(int bluethreshold, int redthreshold, int greenthreshold) {
        this.bluethreshold = bluethreshold;
        this.redthreshold = redthreshold;
        this.greenthreshold = greenthreshold;
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
    }

    @Override
    public boolean timeslice()
    {
        ColorSensorEvent event;
        // colorSensor.*** means it returns the value of a color as an integer
        // compares the values of red blue and green. The color with the highest value than the other
        // two colors and is less than it's color's threshold, then that color is returned

        if (colorSensor.red() > colorSensor.green() && colorSensor.red() > colorSensor.blue() && colorSensor.red() < redthreshold) {
            event = new ColorSensorEvent(this, EventKind.RED_DETECTED);
            robot.queueEvent(event);
        } else if (colorSensor.blue() > colorSensor.green() && colorSensor.blue() > colorSensor.red() && colorSensor.blue() < bluethreshold){
            event = new ColorSensorEvent(this, EventKind.BLUE_DETECTED);
            robot.queueEvent(event);
        } else if (colorSensor.green() > colorSensor.red() && colorSensor.green() > colorSensor.blue() && colorSensor.green() < greenthreshold){
            event = new ColorSensorEvent(this, EventKind.GREEN_DETECTED);
            robot.queueEvent(event);
        } else {
            event = new ColorSensorEvent(this, EventKind.NO_COLOR_DETECTED);
            robot.queueEvent(event);
        }

        return false;
    }
}
