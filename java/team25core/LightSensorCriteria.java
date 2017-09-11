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

import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.util.RobotLog;

/*
 * Looks for a white line.
 */
public class LightSensorCriteria implements SensorCriteria {

    public enum LightPolarity {
        BLACK,
        WHITE,
    }

    protected int light;

    protected double min;
    protected double max;
    protected double threshold;
    protected LightSensor sensor;
    protected LightPolarity polarity;

    public LightSensorCriteria(LightSensor sensor, double min, double max)
    {
        this.sensor = sensor;
        this.polarity = LightPolarity.WHITE;
        this.min = min;
        this.max = max;
        this.threshold = (min + ((max - min)/2));
    }

    public LightSensorCriteria(LightSensor sensor, LightPolarity polarity, double min, double max)
    {
        this.sensor = sensor;
        this.polarity = polarity;
        this.min = min;
        this.max = max;
        this.threshold = (min + ((max - min)/2));
    }

    public void setThreshold(double percent) {
        this.threshold = max - ((max - min) * percent);
    }

    @Override
    public boolean satisfied()
    {
        if (polarity == LightPolarity.WHITE) {
            if (sensor.getRawLightDetected() > threshold) {
                return true;
            }
        } else if (polarity == LightPolarity.BLACK) {
            if (sensor.getRawLightDetected() < threshold) {
                return true;
            }
        }
        return false;
    }
}

