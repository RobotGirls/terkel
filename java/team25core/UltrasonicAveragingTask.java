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

import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.RobotLog;

// import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class UltrasonicAveragingTask extends RobotTask {

    protected int setSize;
    UltrasonicSensorArbitratorTask arbitrator;
    // DescriptiveStatistics movingAvg;
    UltrasonicSensor sensor;
    protected final double ULTRASONIC_MAX = 255.0;
    protected double min;

    public UltrasonicAveragingTask(Robot robot, UltrasonicSensor sensor, int setSize)
    {
        super(robot);
        this.setSize = setSize;
        // this.movingAvg = new DescriptiveStatistics(setSize);
        this.sensor = sensor;
        this.min = ULTRASONIC_MAX;
        this.arbitrator = null;
    }

    public UltrasonicAveragingTask(Robot robot, UltrasonicSensorArbitratorTask arbitrator, UltrasonicSensor sensor, int setSize)
    {
        super(robot);
        this.setSize = setSize;
        // this.movingAvg = new DescriptiveStatistics(setSize);
        this.sensor = sensor;
        this.min = ULTRASONIC_MAX;
        this.arbitrator = arbitrator;
    }

    public double getAverage()
    {
        // return movingAvg.getMean();
        return 0.0;
    }

    @Override
    public void start()
    {

    }

    @Override
    public void stop()
    {

    }

    public void resetMin()
    {
        min = ULTRASONIC_MAX;
    }

    public double getMin()
    {
        return min;
    }

    @Override
    public boolean timeslice()
    {
        double val;

        if (arbitrator != null) {
            val = arbitrator.getUltrasonicLevel((Team25UltrasonicSensor)sensor);
        } else {
            val = sensor.getUltrasonicLevel();
        }

        if (val < min) {
            min = val;
            // RobotLog.i(sensor.getConnectionInfo() + " min %3.1f", min);
        }

        robot.telemetry.addData("Avg Task Distance " + sensor.getConnectionInfo(), val);
        // movingAvg.addValue(val);

        /*
         * Never stops
         */
        return false;
    }
}

